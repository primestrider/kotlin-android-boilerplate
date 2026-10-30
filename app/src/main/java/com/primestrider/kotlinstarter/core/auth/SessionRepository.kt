package com.primestrider.kotlinstarter.core.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.primestrider.kotlinstarter.core.common.ApplicationScope
import com.primestrider.kotlinstarter.core.storage.SessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Single source of truth for the signed-in session, persisted encrypted. Signed in iff a session
 * (and so tokens) exists. Unreadable stored data counts as signed out and is removed.
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

        init {
            scope.launch {
                try {
                    val restored = read()
                    // A sign-in that raced the initial read wins.
                    _session.compareAndSet(null, restored)
                } finally {
                    _loaded.value = true
                }
            }
        }

        fun accessToken(): String? = _session.value?.tokens?.accessToken

        suspend fun signIn(session: Session) {
            _signOutReason.value = null
            write(session)
            _session.value = session
        }

        suspend fun updateTokens(tokens: AuthTokens) {
            val updated = _session.value?.copy(tokens = tokens) ?: return
            write(updated)
            _session.value = updated
        }

        suspend fun signOut(reason: SignOutReason) {
            _session.value = null
            _signOutReason.value = reason
            dataStore.edit { it.remove(KEY) }
        }

        fun clearSignOutReason() {
            _signOutReason.value = null
        }

        private suspend fun write(session: Session) {
            val encrypted = cipher.encrypt(json.encodeToString(Session.serializer(), session).toByteArray())
            dataStore.edit { it[KEY] = Base64.encode(encrypted) }
        }

        @Suppress("TooGenericExceptionCaught")
        private suspend fun read(): Session? {
            val stored = dataStore.data.first()[KEY] ?: return null
            return try {
                val plain = cipher.decrypt(Base64.decode(stored))
                json.decodeFromString(Session.serializer(), String(plain))
            } catch (e: Exception) {
                dataStore.edit { it.remove(KEY) }
                null
            }
        }

        private companion object {
            val KEY = stringPreferencesKey("auth.session")
        }
    }
