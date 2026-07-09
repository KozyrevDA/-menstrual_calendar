package com.kozyrevda.menstrualcalendar.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.kozyrevda.menstrualcalendar.core.data.AppStateHolder
import com.kozyrevda.menstrualcalendar.feature.calendar.CalendarScreen
import com.kozyrevda.menstrualcalendar.feature.chat.LunaChatScreen
import com.kozyrevda.menstrualcalendar.feature.common.LunaBottomBar
import com.kozyrevda.menstrualcalendar.feature.home.HomeScreen
import com.kozyrevda.menstrualcalendar.feature.log.LogSymptomsScreen
import com.kozyrevda.menstrualcalendar.feature.onboarding.OnboardingScreen
import com.kozyrevda.menstrualcalendar.feature.paywall.PaywallScreen
import com.kozyrevda.menstrualcalendar.feature.pills.PillsScreen
import com.kozyrevda.menstrualcalendar.feature.settings.SettingsScreen
import com.kozyrevda.menstrualcalendar.feature.stats.StatsScreen

/** State-based навигация: when по Navigator.current. */
@Composable
fun AppNavigation() {
    val navigator = remember {
        Navigator(start = if (AppStateHolder.isOnboarded) Screen.Home else Screen.Onboarding)
    }
    val current = navigator.current
    val showBottomBar = current in Screen.tabs

    // системный «Назад» (Android): закрываем оверлей, а не приложение
    PlatformBackHandler(enabled = navigator.canGoBack) { navigator.back() }

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            when (current) {
                Screen.Onboarding -> OnboardingScreen(
                    onFinish = { settings ->
                        AppStateHolder.saveCycleSettings(settings)
                        navigator.replaceAll(Screen.Home)
                    }
                )
                Screen.Home -> HomeScreen(onOpen = { screen ->
                    if (screen in Screen.tabs) navigator.switchTab(screen) else navigator.navigate(screen)
                })
                Screen.Calendar -> CalendarScreen(onLogDay = { date ->
                    navigator.navigate(Screen.LogSymptoms(isoDate = date.toString()))
                })
                Screen.Stats -> StatsScreen()
                Screen.Settings -> SettingsScreen(onDataDeleted = {
                    navigator.replaceAll(Screen.Onboarding)
                })
                is Screen.LogSymptoms -> LogSymptomsScreen(isoDate = current.isoDate, onBack = navigator::back)
                Screen.Pills -> PillsScreen(onBack = navigator::back)
                Screen.LunaChat -> LunaChatScreen(onBack = navigator::back)
                Screen.Paywall -> PaywallScreen(onBack = navigator::back)
            }
        }
        if (showBottomBar) {
            LunaBottomBar(
                labels = Screen.tabs.map { it.tabLabel to (it == current) },
                onSelect = { navigator.switchTab(Screen.tabs[it]) },
            )
        }
    }
}
