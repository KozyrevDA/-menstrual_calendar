package com.kozyrevda.menstrualcalendar.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.theme.LunaColors
import com.kozyrevda.menstrualcalendar.theme.LunaTheme

@Composable
fun App() {
    LunaTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(LunaColors.bg)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "Луна — календарь цикла",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = LunaColors.ink,
                textAlign = TextAlign.Center,
            )
            Text(
                "Забота о вас каждый день цикла",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = LunaColors.sub,
                textAlign = TextAlign.Center,
            )
        }
    }
}
