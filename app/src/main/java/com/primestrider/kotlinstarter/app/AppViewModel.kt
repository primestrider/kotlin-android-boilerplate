package com.primestrider.kotlinstarter.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemeMode
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemePreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel
    @Inject
    constructor(
        repository: ThemePreferenceRepository,
    ) : ViewModel() {
        /** Null until the stored preference has loaded; the splash screen stays up meanwhile. */
        val themeMode: StateFlow<ThemeMode?> =
            repository.themeMode.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    }
