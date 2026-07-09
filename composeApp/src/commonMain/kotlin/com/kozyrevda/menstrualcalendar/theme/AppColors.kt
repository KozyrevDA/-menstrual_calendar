package com.kozyrevda.menstrualcalendar.theme

import androidx.compose.ui.graphics.Color

/**
 * Палитра из HTML-прототипа «Луна».
 * Мягкая, тёплая, пастельная гамма.
 */
object AppColors {
    // базовые
    val bg = Color(0xFFFFF9F6)          // фон приложения
    val surface = Color.White           // карточки
    val ink = Color(0xFF45262C)         // основной текст
    val sub = Color(0xFFA17A80)         // вторичный текст
    val subLight = Color(0xFFC29AA0)    // подписи, оверлайны
    val inkSoft = Color(0xFF6B4A50)     // текст чипов
    val roseInk = Color(0xFF8E5F67)     // текст на розовых подложках

    // акценты
    val rose = Color(0xFFE4677F)        // акцент
    val roseDark = Color(0xFFC9506B)    // тёмный акцент
    val roseLight = Color(0xFFFBE3E9)   // розовый фон
    val roseMuted = Color(0xFFF3C1CC)   // приглушённый розовый (графики)
    val roseDisabled = Color(0xFFE9BFC8)

    // персиковая ветка (овуляция / фертильность)
    val peach = Color(0xFFE08A4E)
    val peachLight = Color(0xFFFCEBDC)
    val peachInk = Color(0xFFB4632F)
    val peachSoft = Color(0xFFF6C79E)
    val peachText = Color(0xFF8E5F42)

    // успех (таблетка принята, серии)
    val green = Color(0xFF3E8B60)
    val greenLight = Color(0xFFDFF0E6)
    val greenBorder = Color(0xFFBCE0CB)

    // границы и разделители
    val border = Color(0xFFF0DCD6)
    val borderSoft = Color(0xFFF6E3DE)
    val divider = Color(0xFFF9EDE9)
    val ghost = Color(0xFFDFCAC6)
    val dashed = Color(0xFFE9CFC9)
    val chevron = Color(0xFFD9C2BE)

    // тени
    val cardShadow = Color(0x1FC48E84)      // rgba(196,142,132,.12)
    val roseShadow = Color(0x59E4677F)      // rgba(228,103,127,.35)

    // маскот «Луна»
    val mascotFace1 = Color(0xFFFFF6E8)
    val mascotFace2 = Color(0xFFFFE8D6)
    val mascotInk = Color(0xFF5A3A40)
    val mascotBlush = Color(0xFFF6B8C4)
    val sparklePink = Color(0xFFF3C1CC)
    val sparklePeach = Color(0xFFF6C79E)
}
