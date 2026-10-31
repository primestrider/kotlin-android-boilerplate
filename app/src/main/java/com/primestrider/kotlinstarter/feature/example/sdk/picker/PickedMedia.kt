package com.primestrider.kotlinstarter.feature.example.sdk.picker

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import java.util.Locale

/** A picked or captured image/video: where it is, what it is, its pixel size and its size in bytes. */
data class PickedMedia(
    val uri: Uri,
    val mime: String,
    val width: Int,
    val height: Int,
    val bytes: Long,
) {
    /** `4000 × 3000`. */
    val dimensions: String get() = "$width × $height"

    val isVideo: Boolean get() = mime.startsWith("video/")
}

/** A file chosen in the document picker. */
data class PickedDocument(
    val name: String,
    val bytes: Long,
)

/** [bytes] in kilobytes (1024 bytes) with one decimal in [locale]'s separator: `1.5 KB`. */
fun formatKb(
    bytes: Long,
    locale: Locale = Locale.getDefault(),
): String = String.format(locale, "%.1f KB", bytes / 1024.0)

/** Size of [uri] in bytes, from the provider's `_size` column or, failing that, its file descriptor; 0 when unknown. */
internal fun ContentResolver.sizeOf(uri: Uri): Long {
    runCatching {
        query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst() && !cursor.isNull(0)) return cursor.getLong(0)
        }
    }
    return runCatching { openAssetFileDescriptor(uri, "r")?.use { it.length } }.getOrNull()?.takeIf { it >= 0 } ?: 0L
}

/** Reads [uri] as a video: MIME from the resolver, pixel size from `MediaMetadataRetriever`; null when it cannot be opened. */
internal fun ContentResolver.readVideo(uri: Uri): PickedMedia? {
    val retriever = MediaMetadataRetriever()
    return try {
        openAssetFileDescriptor(uri, "r")?.use { retriever.setDataSource(it.fileDescriptor, it.startOffset, it.length) } ?: return null
        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
        PickedMedia(uri, getType(uri) ?: "video/*", width, height, sizeOf(uri))
    } catch (e: RuntimeException) {
        null
    } finally {
        retriever.release()
    }
}

/** Reads [uri] as an image without decoding it (`inJustDecodeBounds`); null when it is not an image. */
internal fun ContentResolver.readImage(uri: Uri): PickedMedia? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    // With inJustDecodeBounds decodeStream returns null by design; only the stream being openable matters.
    (openInputStream(uri) ?: return null).use { BitmapFactory.decodeStream(it, null, bounds) }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    return PickedMedia(uri, getType(uri) ?: bounds.outMimeType ?: "image/*", bounds.outWidth, bounds.outHeight, sizeOf(uri))
}

/** Name and size of a document [uri]; the name falls back to the last path segment. */
internal fun ContentResolver.readDocument(uri: Uri): PickedDocument {
    var name: String? = null
    runCatching {
        query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst() && !cursor.isNull(0)) name = cursor.getString(0)
        }
    }
    return PickedDocument(name ?: uri.lastPathSegment.orEmpty(), sizeOf(uri))
}
