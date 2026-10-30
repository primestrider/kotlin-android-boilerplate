package com.primestrider.kotlinstarter.core.auth

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.UnrecoverableKeyException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

/** Encrypts the persisted session. A seam so JVM tests can avoid the Android Keystore. */
interface SessionCipher {
    fun encrypt(plain: ByteArray): ByteArray

    fun decrypt(cipher: ByteArray): ByteArray
}

/**
 * AES-256/GCM with a non-exportable key in the AndroidKeyStore; the 12-byte IV is prepended.
 * If the key is unusable it is deleted and regenerated once; data encrypted under the old key
 * then fails to decrypt, which the repository treats as signed out.
 */
class KeystoreSessionCipher
    @Inject
    constructor() : SessionCipher {
        @Volatile
        private var cachedKey: SecretKey? = null

        override fun encrypt(plain: ByteArray): ByteArray =
            withKey { key ->
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.ENCRYPT_MODE, key)
                cipher.iv + cipher.doFinal(plain)
            }

        override fun decrypt(cipher: ByteArray): ByteArray {
            require(cipher.size > IV_SIZE) { "Ciphertext too short" }
            return withKey { key ->
                val c = Cipher.getInstance(TRANSFORMATION)
                c.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, cipher, 0, IV_SIZE))
                c.doFinal(cipher, IV_SIZE, cipher.size - IV_SIZE)
            }
        }

        private fun <T> withKey(operation: (SecretKey) -> T): T =
            try {
                operation(key())
            } catch (e: UnrecoverableKeyException) {
                operation(regenerateKey())
            } catch (e: KeyStoreException) {
                operation(regenerateKey())
            } catch (e: InvalidKeyException) {
                operation(regenerateKey())
            }

        @Synchronized
        private fun regenerateKey(): SecretKey {
            cachedKey = null
            runCatching { keyStore().deleteEntry(ALIAS) }
            return key()
        }

        private fun keyStore(): KeyStore = KeyStore.getInstance(PROVIDER).apply { load(null) }

        @Synchronized
        private fun key(): SecretKey {
            cachedKey?.let { return it }
            val existing = keyStore().getKey(ALIAS, null) as? SecretKey
            val key = existing ?: generateKey()
            cachedKey = key
            return key
        }

        private fun generateKey(): SecretKey {
            val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER)
            generator.init(
                KeyGenParameterSpec
                    .Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(KEY_BITS)
                    .build(),
            )
            return generator.generateKey()
        }

        private companion object {
            const val PROVIDER = "AndroidKeyStore"
            const val ALIAS = "kotlinstarter_session"
            const val TRANSFORMATION = "AES/GCM/NoPadding"
            const val KEY_BITS = 256
            const val IV_SIZE = 12
            const val TAG_BITS = 128
        }
    }
