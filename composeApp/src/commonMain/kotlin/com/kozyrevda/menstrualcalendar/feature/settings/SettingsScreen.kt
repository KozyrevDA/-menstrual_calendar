package com.kozyrevda.menstrualcalendar.feature.settings

import androidx.compose.runtime.Composable
import com.kozyrevda.menstrualcalendar.feature.common.StubScaffold

@Composable
fun SettingsScreen(onBack: (() -> Unit)? = null) {
    StubScaffold(
        title = "Настройки",
        subtitle = "Напоминания, приватность, PIN и параметры цикла — этап «Настройки»",
        onBack = onBack,
    )
}
