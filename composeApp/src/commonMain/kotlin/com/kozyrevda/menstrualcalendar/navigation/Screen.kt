package com.kozyrevda.menstrualcalendar.navigation

/** Все экраны приложения. */
sealed interface Screen {
    // корневые вкладки нижней навигации
    data object Home : Screen
    data object Calendar : Screen
    data object Stats : Screen
    data object Settings : Screen

    // полноэкранные сценарии
    data object Onboarding : Screen
    data class LogSymptoms(val isoDate: String? = null) : Screen
    data object Pills : Screen
    data object LunaChat : Screen
    data object Paywall : Screen

    companion object {
        /** Вкладки нижней навигации в порядке отображения. */
        val tabs: List<Screen> = listOf(Home, Calendar, Stats, Settings)
    }
}

val Screen.tabLabel: String
    get() = when (this) {
        Screen.Home -> "Сегодня"
        Screen.Calendar -> "Календарь"
        Screen.Stats -> "Статистика"
        Screen.Settings -> "Настройки"
        else -> ""
    }
