package com.kozyrevda.menstrualcalendar.feature.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.data.AppStateHolder
import com.kozyrevda.menstrualcalendar.core.logic.PillLogic
import com.kozyrevda.menstrualcalendar.core.logic.daysWord
import com.kozyrevda.menstrualcalendar.core.logic.plural
import com.kozyrevda.menstrualcalendar.core.logic.rus
import com.kozyrevda.menstrualcalendar.core.logic.today
import com.kozyrevda.menstrualcalendar.feature.common.LunaCard
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes

@Composable
fun StatsScreen() {
    val settings = AppStateHolder.cycleSettings
    val logs = AppStateHolder.dayLogs
    val course = AppStateHolder.pillCourse
    val t = today()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            "Статистика", style = MaterialTheme.typography.headlineMedium, color = AppColors.ink,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp),
        )

        // ── параметры цикла ──
        if (settings != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Modifier.weight(1f), "${settings.cycleLengthDays}", daysWord(settings.cycleLengthDays), "цикл", AppColors.rose)
                StatCard(Modifier.weight(1f), "${settings.periodLengthDays}", daysWord(settings.periodLengthDays), "месячные", AppColors.peach)
            }
        }

        // ── дневник ──
        LunaCard(corner = AppShapes.cardSmall) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    Text("Дневник самочувствия", style = MaterialTheme.typography.titleMedium, color = AppColors.ink, modifier = Modifier.weight(1f))
                    Text(
                        "${logs.size} ${plural(logs.size, "запись", "записи", "записей")}",
                        style = MaterialTheme.typography.labelMedium, color = AppColors.sub,
                    )
                }
                if (logs.isEmpty()) {
                    Text(
                        "Пока пусто. Отмечайте настроение и симптомы — здесь появится история.",
                        style = MaterialTheme.typography.bodySmall, color = AppColors.sub, lineHeight = 18.sp,
                    )
                } else {
                    val recent = logs.entries.sortedByDescending { it.key }.take(5)
                    recent.forEachIndexed { i, (date, log) ->
                        Column {
                            Text(date.rus().uppercase(), style = MaterialTheme.typography.labelSmall, color = AppColors.subLight)
                            val summary = (log.mood + log.symptoms).joinToString(" · ")
                                .ifBlank { "заметка" }
                            Text(summary, style = MaterialTheme.typography.bodySmall, color = AppColors.inkSoft)
                            if (log.note.isNotBlank()) {
                                Text("«${log.note}»", style = MaterialTheme.typography.labelMedium, color = AppColors.sub)
                            }
                        }
                        if (i != recent.lastIndex) {
                            Box(Modifier.fillMaxWidth().height(1.dp).background(AppColors.divider))
                        }
                    }
                }
            }
        }

        // ── таблетки ──
        LunaCard(corner = AppShapes.cardSmall) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Таблетки", style = MaterialTheme.typography.titleMedium, color = AppColors.ink)
                if (course == null) {
                    Text(
                        "Курс не настроен — включите трекер на экране «Таблетки».",
                        style = MaterialTheme.typography.bodySmall, color = AppColors.sub,
                    )
                } else {
                    val streak = PillLogic.streak(course, AppStateHolder.pillsTaken, t)
                    Text(
                        "Серия: $streak ${daysWord(streak)} · всего принято: ${AppStateHolder.pillsTaken.size}",
                        style = MaterialTheme.typography.bodySmall, color = AppColors.inkSoft,
                    )
                    Text(
                        "Начало упаковки — ${course.startDate.rus()}, напоминание в ${course.reminderTime}",
                        style = MaterialTheme.typography.labelMedium, color = AppColors.sub,
                    )
                }
            }
        }

        Text(
            "Развёрнутая аналитика — график длины циклов и сравнение месяцев — появится в следующих версиях.",
            style = MaterialTheme.typography.labelMedium, color = AppColors.subLight,
            modifier = Modifier.padding(horizontal = 6.dp), lineHeight = 18.sp,
        )
    }
}

@Composable
private fun StatCard(modifier: Modifier, value: String, unit: String, label: String, color: Color) {
    LunaCard(modifier = modifier, corner = AppShapes.cardSmall) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 30.sp, fontWeight = FontWeight.Black, color = color)
                Text(" $unit", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color, modifier = Modifier.padding(bottom = 4.dp))
            }
            Text(label, style = MaterialTheme.typography.bodySmall, color = AppColors.sub)
        }
    }
}
