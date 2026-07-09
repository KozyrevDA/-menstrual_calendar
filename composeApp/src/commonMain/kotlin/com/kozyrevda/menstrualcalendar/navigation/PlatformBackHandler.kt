package com.kozyrevda.menstrualcalendar.navigation

import androidx.compose.runtime.Composable

/**
 * Обработчик системной кнопки «Назад».
 * Android — перехватывает back-жест/кнопку; iOS — системной кнопки нет, no-op.
 */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
