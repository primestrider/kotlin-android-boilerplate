package com.primestrider.kotlinstarter.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import androidx.navigation.compose.rememberNavController
import com.primestrider.kotlinstarter.app.navigation.MainShell
import com.primestrider.kotlinstarter.core.crash.CrashFallbackScreen
import com.primestrider.kotlinstarter.core.designsystem.component.LocalMessenger
import com.primestrider.kotlinstarter.core.designsystem.component.Messenger
import com.primestrider.kotlinstarter.core.designsystem.theme.AppTheme
import com.primestrider.kotlinstarter.core.designsystem.theme.ThemeMode
import com.primestrider.kotlinstarter.core.designsystem.theme.resolveDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: AppViewModel by viewModels()

    @Inject
    lateinit var messenger: Messenger

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        splash.setKeepOnScreenCondition { !viewModel.ready.value }
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            // singleTop: a deep link delivered to the running activity arrives here instead of recreating it.
            DisposableEffect(navController) {
                val listener = Consumer<Intent> { navController.handleDeepLink(it) }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }
            val mode = viewModel.themeMode.collectAsState().value ?: ThemeMode.SYSTEM
            val darkTheme = resolveDarkTheme(mode, isSystemInDarkTheme())
            LaunchedEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(LIGHT_SCRIM, DARK_SCRIM) { darkTheme },
                )
            }
            AppTheme(darkTheme = darkTheme) {
                CompositionLocalProvider(LocalMessenger provides messenger) {
                    val crash = viewModel.lastCrash.collectAsState().value
                    if (crash != null) CrashFallbackScreen(crash, onContinue = viewModel::dismissCrash) else MainShell(navController)
                }
            }
        }
    }

    private companion object {
        val LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
        val DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}
