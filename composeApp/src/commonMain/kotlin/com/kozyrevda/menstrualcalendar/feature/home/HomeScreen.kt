package com.kozyrevda.menstrualcalendar.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.data.AppStateHolder
import com.kozyrevda.menstrualcalendar.core.logic.CyclePredictor
import com.kozyrevda.menstrualcalendar.core.logic.MONTHS_SHORT
import com.kozyrevda.menstrualcalendar.core.logic.daysWord
import com.kozyrevda.menstrualcalendar.core.logic.rus
import com.kozyrevda.menstrualcalendar.core.logic.today
import com.kozyrevda.menstrualcalendar.core.logic.weekdayFull
import com.kozyrevda.menstrualcalendar.core.model.CyclePhase
import com.kozyrevda.menstrualcalendar.feature.common.LunaCard
import com.kozyrevda.menstrualcalendar.feature.common.noRippleClick
import com.kozyrevda.menstrualcalendar.navigation.Screen
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes

@Composable
fun HomeScreen(onOpen: (Screen) -> Unit) {
    val t = today()
    val settings = AppStateHolder.cycleSettings
    val info = settings?.let { CyclePredictor.getDayInfo(t, it) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column {
            Text(
                "${t.weekdayFull()}, ${t.rus()}".uppercase(),
                style = MaterialTheme.typography.labelSmall, color = AppColors.subLight,
            )
            Text("Привет", style = MaterialTheme.typography.headlineMedium, color = AppColors.ink)
        }

        if (info != null && settings != null) {
            // ── карточка «сегодня» (кольцо цикла придёт на этапе главного экрана) ──
            LunaCard {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ДЕНЬ ЦИКЛА ${info.cycleDay}", style = MaterialTheme.typography.labelSmall, color = AppColors.subLight)
                    Text(info.phase.rusTitle(), style = MaterialTheme.typography.titleLarge, color = AppColors.rose)
                    Text(info.phaseSubtitle(), style = MaterialTheme.typography.bodyMedium, color = AppColors.sub)
                }
            }
            // ── прогноз следующих месячных ──
            val next = CyclePredictor.getNextPeriodStart(settings, t)
            LunaCard(corner = AppShapes.cardSmall) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(Modifier.size(46.dp).clip(CircleShape).background(AppColors.roseLight), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${next.dayOfMonth}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AppColors.roseDark)
                            Text(MONTHS_SHORT[next.monthNumber - 1].lowercase(), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.roseDark)
                        }
                    }
                    Column {
                        Text(
                            "Месячные — через ${info.daysUntilNextPeriod} ${daysWord(info.daysUntilNextPeriod)}",
                            style = MaterialTheme.typography.titleSmall, color = AppColors.ink,
                        )
                        Text("Прогноз с точностью ±2 дня", style = MaterialTheme.typography.bodySmall, color = AppColors.sub)
                    }
                }
            }
        }

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

private fun CyclePhase.rusTitle(): String = when (this) {
    CyclePhase.Menstruation -> "Месячные"
    CyclePhase.Follicular -> "Фолликулярная фаза"
    CyclePhase.Ovulation -> "Овуляция"
    CyclePhase.Luteal -> "Лютеиновая фаза"
}

private fun com.kozyrevda.menstrualcalendar.core.model.CycleDayInfo.phaseSubtitle(): String = when {
    isPeriod -> "День $cycleDay месячных — берегите себя"
    isOvulation || isFertile -> "Высокая вероятность зачатия"
    phase == CyclePhase.Follicular -> "Низкая вероятность зачатия"
    else -> "Месячные через $daysUntilNextPeriod ${com.kozyrevda.menstrualcalendar.core.logic.daysWord(daysUntilNextPeriod)}"
}
