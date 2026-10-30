package com.primestrider.kotlinstarter.core.auth

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
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

/** AES-256/GCM with a non-exportable key in the AndroidKeyStore; the 12-byte IV is prepended. */
class KeystoreSessionCipher
    @Inject
    constructor() : SessionCipher {
        override fun encrypt(plain: ByteArray): ByteArray {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key())
            return cipher.iv + cipher.doFinal(plain)
        }

        override fun decrypt(cipher: ByteArray): ByteArray {
            require(cipher.size > IV_SIZE) { "Ciphertext too short" }
            val c = Cipher.getInstance(TRANSFORMATION)
            c.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(TAG_BITS, cipher, 0, IV_SIZE))
            return c.doFinal(cipher, IV_SIZE, cipher.size - IV_SIZE)
        }

        @Synchronized
        private fun key(): SecretKey {
            val store = KeyStore.getInstance(PROVIDER).apply { load(null) }
            (store.getKey(ALIAS, null) as? SecretKey)?.let { return it }
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
