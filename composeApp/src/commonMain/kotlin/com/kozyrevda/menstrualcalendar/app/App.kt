package com.kozyrevda.menstrualcalendar.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kozyrevda.menstrualcalendar.navigation.AppNavigation
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(AppColors.bg)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            AppNavigation()
        }
    }
}
