package com.primestrider.kotlinstarter.feature.example.sdk.music

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.primestrider.kotlinstarter.feature.devices.impl.R

/**
 * Plays the demo track as a **foreground service** of type `mediaPlayback`, so it keeps playing with the app in the
 * background or the screen off. Media3 does the foreground work: while the player plays, [MediaSessionService] shows
 * the media notification (lock screen and quick settings controls included) and promotes itself to the foreground;
 * when playback pauses it leaves the foreground so the user can swipe the notification away.
 *
 * The screen talks to it only through a `MediaController` (see [MusicController]), the same way the system media
 * controls, a Bluetooth headset or Android Auto do; that is why the service is exported with the session action.
 */
class MusicService : MediaSessionService() {
    private var session: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val player =
            ExoPlayer
                .Builder(this)
                // Takes audio focus (other apps pause or duck) and pauses when headphones are unplugged.
                .setAudioAttributes(
                    AudioAttributes
                        .Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                    true,
                ).setHandleAudioBecomingNoisy(true)
                .build()
        player.setMediaItem(sampleTrack(this))
        player.prepare()
        // At the end, rewind and pause: the service leaves the foreground and Play starts over.
        player.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        player.seekTo(0)
                        player.pause()
                    }
                }
            },
        )
        val builder = MediaSession.Builder(this, player)
        // Tapping the notification opens the app.
        packageManager.getLaunchIntentForPackage(packageName)?.let { launch ->
            builder.setSessionActivity(
                PendingIntent.getActivity(this, 0, launch, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT),
            )
        }
        session = builder.build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    /** Swiping the app away keeps music that is playing, but a paused player has no reason to stay alive. */
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = session?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) stopSelf()
    }

    override fun onDestroy() {
        session?.run {
            player.release()
            release()
        }
        session = null
        super.onDestroy()
    }
}

/** The bundled 10-second track (`res/raw/sdk_music_sample.wav`): works offline and has no licence to check. */
private fun sampleTrack(context: Context): MediaItem =
    MediaItem
        .Builder()
        .setMediaId("sample")
        .setUri("android.resource://${context.packageName}/${R.raw.sdk_music_sample}")
        .setMediaMetadata(
            MediaMetadata
                .Builder()
                .setTitle(context.getString(R.string.sdk_music_track_title))
                .setArtist(context.getString(R.string.sdk_music_track_artist))
                .build(),
        ).build()
