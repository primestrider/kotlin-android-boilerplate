package com.primestrider.kotlinstarter.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primestrider.kotlinstarter.core.auth.SessionRepository
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemeMode
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemePreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel
    @Inject
    constructor(
        repository: ThemePreferenceRepository,
        sessionRepository: SessionRepository,
    ) : ViewModel() {
        /** Null until the stored preference has loaded. */
        val themeMode: StateFlow<ThemeMode?> =
            repository.themeMode.stateIn(viewModelScope, SharingStarted.Eagerly, null)

        /** True once theme and session are both loaded; the splash screen stays up until then. */
        val ready: StateFlow<Boolean> =
            combine(themeMode, sessionRepository.loaded) { theme, loaded -> theme != null && loaded }
                .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    }
