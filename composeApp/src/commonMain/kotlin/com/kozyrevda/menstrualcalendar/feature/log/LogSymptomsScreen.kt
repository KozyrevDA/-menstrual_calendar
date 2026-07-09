package com.kozyrevda.menstrualcalendar.feature.log

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.data.AppStateHolder
import com.kozyrevda.menstrualcalendar.core.logic.rus
import com.kozyrevda.menstrualcalendar.core.logic.today
import com.kozyrevda.menstrualcalendar.core.model.DayLog
import com.kozyrevda.menstrualcalendar.core.model.LogOptions
import com.kozyrevda.menstrualcalendar.feature.common.BackChevron
import com.kozyrevda.menstrualcalendar.feature.common.Cta
import com.kozyrevda.menstrualcalendar.feature.common.FlowChips
import com.kozyrevda.menstrualcalendar.feature.common.noRippleClick
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes
import kotlinx.datetime.LocalDate

@Composable
fun LogSymptomsScreen(isoDate: String? = null, onBack: () -> Unit) {
    val date = isoDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: today()
    val existing = AppStateHolder.logFor(date)

    var mood by remember { mutableStateOf(existing?.mood?.toSet() ?: emptySet()) }
    var symptoms by remember { mutableStateOf(existing?.symptoms?.toSet() ?: emptySet()) }
    var note by remember { mutableStateOf(existing?.note ?: "") }

    fun save() {
        AppStateHolder.saveDayLog(date, DayLog(mood.toList(), symptoms.toList(), note.trim()))
        onBack()
    }

    Column(Modifier.fillMaxSize()) {
        // ── шапка ──
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackChevron(onBack)
            Text(
                (if (date == today()) "Сегодня, " else "") + date.rus(),
                style = MaterialTheme.typography.titleMedium, color = AppColors.ink,
                textAlign = TextAlign.Center, modifier = Modifier.weight(1f),
            )
            Text(
                "Готово", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.rose,
                modifier = Modifier.noRippleClick { save() },
            )
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp).padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Section("Настроение") {
                FlowChips(LogOptions.moods, mood, perRow = 3) { s ->
                    mood = if (s in mood) mood - s else mood + s
                }
            }
            Section("Симптомы") {
                FlowChips(LogOptions.symptoms, symptoms, perRow = 3) { s ->
                    symptoms = if (s in symptoms) symptoms - s else symptoms + s
                }
            }
            // ── заметка ──
            BasicTextField(
                note, { note = it },
                textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppColors.ink),
                modifier = Modifier.fillMaxWidth().clip(AppShapes.tile).background(AppColors.surface)
                    .border(1.5.dp, AppColors.dashed, AppShapes.tile)
                    .padding(18.dp).heightIn(min = 72.dp),
                decorationBox = { inner ->
                    if (note.isEmpty()) Text(
                        "Добавить заметку…", fontSize = 14.sp,
                        fontWeight = FontWeight.Bold, color = AppColors.subLight,
                    )
                    inner()
                },
            )
            Cta("Сохранить") { save() }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = AppColors.ink)
        content()
    }
}
