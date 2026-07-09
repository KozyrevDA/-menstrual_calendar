package com.kozyrevda.menstrualcalendar.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(AppColors.bg)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "Луна — календарь цикла",
                style = MaterialTheme.typography.headlineLarge,
                color = AppColors.ink,
                textAlign = TextAlign.Center,
            )
            Text(
                "Забота о вас каждый день цикла",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.sub,
                textAlign = TextAlign.Center,
            )
        }
    }
}
