package com.kozyrevda.menstrualcalendar.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Формы «Луны»: карточки 22–24 dp, кнопки-pill 100 dp. */
object AppShapes {
    val cardLarge = RoundedCornerShape(28.dp)   // карточка кольца цикла
    val card = RoundedCornerShape(24.dp)
    val cardSmall = RoundedCornerShape(22.dp)
    val pill = RoundedCornerShape(100.dp)       // кнопки и чипы
    val tile = RoundedCornerShape(18.dp)        // плитки интенсивности, заметка
    val day = RoundedCornerShape(14.dp)         // ячейка календаря
    val fab = RoundedCornerShape(20.dp)
    val bubbleLuna = RoundedCornerShape(18.dp, 18.dp, 18.dp, 6.dp)   // реплика Луны
    val bubbleUser = RoundedCornerShape(18.dp, 18.dp, 6.dp, 18.dp)   // реплика пользовательницы
}

private val LightColorScheme = lightColorScheme(
    primary = AppColors.rose,
    onPrimary = Color.White,
    primaryContainer = AppColors.roseLight,
    onPrimaryContainer = AppColors.roseDark,
    secondary = AppColors.peach,
    onSecondary = Color.White,
    secondaryContainer = AppColors.peachLight,
    onSecondaryContainer = AppColors.peachInk,
    tertiary = AppColors.green,
    tertiaryContainer = AppColors.greenLight,
    background = AppColors.bg,
    onBackground = AppColors.ink,
    surface = AppColors.surface,
    onSurface = AppColors.ink,
    surfaceVariant = AppColors.roseLight,
    onSurfaceVariant = AppColors.sub,
    outline = AppColors.border,
    outlineVariant = AppColors.divider,
    error = AppColors.roseDark,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content,
    )
}
