package com.primestrider.kotlinstarter.core.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.primestrider.kotlinstarter.core.common.ApplicationScope
import com.primestrider.kotlinstarter.core.storage.SessionStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Single source of truth for the signed-in session, persisted encrypted. Signed in iff a session
 * (and so tokens) exists. Unreadable stored data counts as signed out and is removed.
 * All mutations are serialized by a mutex so memory and disk cannot diverge.
 */
@OptIn(ExperimentalEncodingApi::class)
@Singleton
class SessionRepository
    @Inject
    constructor(
        @SessionStore private val dataStore: DataStore<Preferences>,
        private val cipher: SessionCipher,
        private val json: Json,
        @ApplicationScope scope: CoroutineScope,
    ) {
        private val _session = MutableStateFlow<Session?>(null)
        val session: StateFlow<Session?> = _session.asStateFlow()

        private val _loaded = MutableStateFlow(false)

        /** True once the stored session has been read for the first time. */
        val loaded: StateFlow<Boolean> = _loaded.asStateFlow()

        private val _signOutReason = MutableStateFlow<SignOutReason?>(null)
        val signOutReason: StateFlow<SignOutReason?> = _signOutReason.asStateFlow()

        val isSignedIn: Boolean get() = _session.value != null

        private val lock = Mutex()

        init {
            scope.launch {
                try {
                    lock.withLock {
                        // A sign-in that raced the initial read wins.
                        _session.compareAndSet(null, read())
                    }
                } finally {
                    _loaded.value = true
                }
            }
        }

        fun accessToken(): String? = _session.value?.tokens?.accessToken

        suspend fun signIn(session: Session) {
            lock.withLock {
                _signOutReason.value = null
                write(session)
                _session.value = session
            }
        }

        suspend fun updateTokens(tokens: AuthTokens) {
            lock.withLock {
                val updated = _session.value?.copy(tokens = tokens) ?: return
                write(updated)
                _session.value = updated
            }
        }

        suspend fun signOut(reason: SignOutReason) {
            lock.withLock {
                // Storage first: if it fails, memory and disk both still say signed in.
                dataStore.edit { it.remove(KEY) }
                _session.value = null
                _signOutReason.value = reason
            }
        }

        fun clearSignOutReason() {
            _signOutReason.value = null
        }

        private suspend fun write(session: Session) {
            val plain = json.encodeToString(Session.serializer(), session).toByteArray(Charsets.UTF_8)
            dataStore.edit { it[KEY] = Base64.encode(cipher.encrypt(plain)) }
        }

        /** A failed storage read means signed out for this run; a value that cannot be decrypted or parsed is dropped. */
        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        private suspend fun read(): Session? =
            try {
                val stored = dataStore.data.first()[KEY]
                if (stored == null) {
                    null
                } else {
                    val plain = cipher.decrypt(Base64.decode(stored))
                    json.decodeFromString(Session.serializer(), String(plain, Charsets.UTF_8))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                // Storage unreadable right now: signed out for this run, stored data kept.
                null
            } catch (e: Exception) {
                try {
                    dataStore.edit { it.remove(KEY) }
                } catch (removal: CancellationException) {
                    throw removal
                } catch (removal: Exception) {
                    // Storage is unusable; staying signed out in memory is the best we can do.
                }
                null
            }

        private companion object {
            val KEY = stringPreferencesKey("auth.session")
        }
    }
