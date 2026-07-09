package com.kozyrevda.menstrualcalendar.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kozyrevda.menstrualcalendar.feature.common.LunaCard
import com.kozyrevda.menstrualcalendar.feature.common.noRippleClick
import com.kozyrevda.menstrualcalendar.navigation.Screen
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes

@Composable
fun HomeScreen(onOpen: (Screen) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Привет", style = MaterialTheme.typography.headlineMedium, color = AppColors.ink)
        Text(
            "Главный экран с кольцом цикла появится после подключения домена. Пока — переходы:",
            style = MaterialTheme.typography.bodyMedium, color = AppColors.sub,
        )
        listOf(
            "Логирование симптомов" to Screen.LogSymptoms(),
            "Таблетки" to Screen.Pills,
            "Чат с Луной" to Screen.LunaChat,
            "Paywall" to Screen.Paywall,
        ).forEach { (label, screen) ->
            LunaCard(corner = AppShapes.cardSmall) {
                Row(
                    Modifier.fillMaxWidth().noRippleClick { onOpen(screen) }
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(label, style = MaterialTheme.typography.titleSmall, color = AppColors.ink, modifier = Modifier.weight(1f))
                    Text("›", style = MaterialTheme.typography.titleMedium, color = AppColors.chevron)
                }
            }
        }
    }
}
