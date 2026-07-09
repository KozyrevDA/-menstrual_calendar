package com.kozyrevda.menstrualcalendar.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Типографика в духе Nunito из прототипа: округлая, жирная, дружелюбная.
 * Пока используется системный шрифт; чтобы подключить Nunito, положите
 * nunito_*.ttf в composeResources/font и замените [AppFontFamily].
 */
val AppFontFamily: FontFamily = FontFamily.Default

val AppTypography = Typography(
    // «Привет, Аня» / заголовки экранов (26–28sp, 800–900)
    headlineLarge = TextStyle(fontFamily = AppFontFamily, fontSize = 28.sp, fontWeight = FontWeight.Black, lineHeight = 34.sp),
    headlineMedium = TextStyle(fontFamily = AppFontFamily, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 33.sp),
    // заголовки карточек и секций
    titleLarge = TextStyle(fontFamily = AppFontFamily, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold),
    titleMedium = TextStyle(fontFamily = AppFontFamily, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold),
    titleSmall = TextStyle(fontFamily = AppFontFamily, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold),
    // основной текст
    bodyLarge = TextStyle(fontFamily = AppFontFamily, fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = AppFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = AppFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold),
    // подписи и оверлайны
    labelLarge = TextStyle(fontFamily = AppFontFamily, fontSize = 13.5.sp, fontWeight = FontWeight.Bold),
    labelMedium = TextStyle(fontFamily = AppFontFamily, fontSize = 12.5.sp, fontWeight = FontWeight.Bold),
    labelSmall = TextStyle(fontFamily = AppFontFamily, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
)
