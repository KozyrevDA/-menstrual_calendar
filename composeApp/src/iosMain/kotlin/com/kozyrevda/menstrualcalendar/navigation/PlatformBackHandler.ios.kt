package com.kozyrevda.menstrualcalendar.navigation

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // На iOS системной кнопки «Назад» нет — навигация только через UI.
}
