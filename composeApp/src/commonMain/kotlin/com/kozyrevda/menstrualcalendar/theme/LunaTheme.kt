package com.kozyrevda.menstrualcalendar.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Дизайн-токены из HTML-прототипа «Луна» (Календарь цикла.dc.html). */
object LunaColors {
    val bg = Color(0xFFFFF9F6)
    val ink = Color(0xFF45262C)
    val sub = Color(0xFFA17A80)
    val subLight = Color(0xFFC29AA0)
    val rose = Color(0xFFE4677F)
    val roseDark = Color(0xFFC9506B)
    val roseLight = Color(0xFFFBE3E9)
    val peach = Color(0xFFE08A4E)
    val peachLight = Color(0xFFFCEBDC)
    val peachInk = Color(0xFFB4632F)
    val green = Color(0xFF3E8B60)
    val greenLight = Color(0xFFDFF0E6)
    val border = Color(0xFFF0DCD6)
    val divider = Color(0xFFF9EDE9)
    val ghost = Color(0xFFDFCAC6)
    val cardShadow = Color(0x1FC48E84)
}

object LunaShapes {
    val card = RoundedCornerShape(24.dp)
    val cardSmall = RoundedCornerShape(22.dp)
    val chip = RoundedCornerShape(100.dp)
    val tile = RoundedCornerShape(18.dp)
    val day = RoundedCornerShape(14.dp)
    val fab = RoundedCornerShape(20.dp)
}

private val LunaColorScheme = lightColorScheme(
    primary = LunaColors.rose,
    onPrimary = Color.White,
    secondary = LunaColors.peach,
    background = LunaColors.bg,
    surface = Color.White,
    onBackground = LunaColors.ink,
    onSurface = LunaColors.ink,
)

@Composable
fun LunaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LunaColorScheme, content = content)
}
