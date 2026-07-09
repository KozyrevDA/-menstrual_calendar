package com.kozyrevda.menstrualcalendar.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kozyrevda.menstrualcalendar.core.data.AppStateHolder
import com.kozyrevda.menstrualcalendar.core.logic.daysWord
import com.kozyrevda.menstrualcalendar.feature.common.LunaCard
import com.kozyrevda.menstrualcalendar.feature.common.LunaToggle
import com.kozyrevda.menstrualcalendar.feature.common.noRippleClick
import com.kozyrevda.menstrualcalendar.theme.AppColors
import com.kozyrevda.menstrualcalendar.theme.AppShapes

private const val PRIVACY_POLICY_URL = "https://kozyrevda.github.io/-menstrual_calendar/privacy"

@Composable
fun SettingsScreen(onDataDeleted: () -> Unit = {}) {
    val settings = AppStateHolder.cycleSettings
    val clipboard = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    var exported by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            "Настройки", style = MaterialTheme.typography.headlineMedium, color = AppColors.ink,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp),
        )

        // ── цикл ──
        Group("Цикл") {
            SettingRow("Длина цикла", if (settings == null) "Пройдите онбординг" else null) {
                if (settings != null) Stepper(settings.cycleLengthDays, 18, 45) { v ->
                    AppStateHolder.saveCycleSettings(settings.copy(cycleLengthDays = v))
                }
            }
            SettingRow("Длина месячных", null, divider = false) {
                if (settings != null) Stepper(settings.periodLengthDays, 1, 10) { v ->
                    AppStateHolder.saveCycleSettings(settings.copy(periodLengthDays = v))
                }
            }
        }

        // ── напоминания ──
        Group("Напоминания") {
            SettingRow("Приближение месячных", "За 2 дня, в 9:00") {
                LunaToggle(AppStateHolder.remindPeriod) { AppStateHolder.toggleRemindPeriod() }
            }
            SettingRow("День овуляции", "В день овуляции, в 9:00") {
                LunaToggle(AppStateHolder.remindOvulation) { AppStateHolder.toggleRemindOvulation() }
            }
            SettingRow("Таблетки", "Во время из настроек курса", divider = false) {
                LunaToggle(AppStateHolder.remindPills) { AppStateHolder.toggleRemindPills() }
            }
        }

        // ── приватность ──
        Group("Приватность") {
            SettingRow("Приватный режим", "PIN-код при входе (заглушка MVP)", divider = false) {
                LunaToggle(AppStateHolder.privateMode) { AppStateHolder.togglePrivateMode() }
            }
        }

        // ── данные ──
        Group("Данные") {
            ActionRow(if (exported) "Скопировано в буфер ✓" else "Экспорт данных", "Все записи одним JSON") {
                clipboard.setText(AnnotatedString(AppStateHolder.exportJson()))
                exported = true
            }
            ActionRow("Политика приватности", "Открыть в браузере") {
                runCatching { uriHandler.openUri(PRIVACY_POLICY_URL) }
            }
            if (!confirmDelete) {
                ActionRow("Удалить данные", "Стереть всё с устройства", divider = false, danger = true) {
                    confirmDelete = true
                }
            } else {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        "Точно удалить всё?",
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.5.sp),
                        color = AppColors.roseDark, modifier = Modifier.weight(1f),
                    )
                    PillButton("Отмена", filled = false) { confirmDelete = false }
                    PillButton("Удалить", filled = true) {
                        AppStateHolder.clearAll()
                        confirmDelete = false
                        onDataDeleted()
                    }
                }
            }
        }

        Text(
            "Прогнозы носят ориентировочный характер и не являются медицинской " +
                "рекомендацией или методом контрацепции. " +
                "Данные хранятся только на этом устройстве.",
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp), color = AppColors.subLight,
            modifier = Modifier.padding(horizontal = 6.dp),
        )
        Spacer(Modifier.height(4.dp))
    }
}

/* ── строительные блоки ── */

@Composable
private fun Group(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            label.uppercase(), style = MaterialTheme.typography.labelSmall,
            color = AppColors.subLight, modifier = Modifier.padding(horizontal = 4.dp),
        )
        LunaCard(corner = AppShapes.cardSmall) { Column { content() } }
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String?, divider: Boolean = true, trailing: @Composable () -> Unit) {
    Column {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 14.5.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.ink)
                if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.labelMedium, color = AppColors.sub)
            }
            trailing()
        }
        if (divider) Box(Modifier.fillMaxWidth().padding(horizontal = 18.dp).height(1.dp).background(AppColors.divider))
    }
}

@Composable
private fun ActionRow(title: String, subtitle: String, divider: Boolean = true, danger: Boolean = false, onClick: () -> Unit) {
    Column(Modifier.noRippleClick(onClick)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 14.5.sp, fontWeight = FontWeight.ExtraBold, color = if (danger) AppColors.roseDark else AppColors.ink)
                Text(subtitle, style = MaterialTheme.typography.labelMedium, color = AppColors.sub)
            }
            Text("›", style = MaterialTheme.typography.titleMedium, color = AppColors.chevron)
        }
        if (divider) Box(Modifier.fillMaxWidth().padding(horizontal = 18.dp).height(1.dp).background(AppColors.divider))
    }
}

@Composable
private fun Stepper(value: Int, min: Int, max: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StepBtn("−") { onChange((value - 1).coerceAtLeast(min)) }
        Text(
            "$value ${daysWord(value)}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.ink,
            modifier = Modifier.widthIn(min = 62.dp), textAlign = TextAlign.Center,
        )
        StepBtn("+") { onChange((value + 1).coerceAtMost(max)) }
    }
}

@Composable
private fun StepBtn(label: String, onClick: () -> Unit) {
    Box(
        Modifier.size(30.dp).clip(CircleShape).background(AppColors.roseLight).noRippleClick(onClick),
        contentAlignment = Alignment.Center,
    ) { Text(label, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.roseDark) }
}

@Composable
private fun PillButton(label: String, filled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.clip(AppShapes.pill)
            .background(if (filled) AppColors.roseDark else AppColors.roseLight)
            .noRippleClick(onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            label, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
            color = if (filled) androidx.compose.ui.graphics.Color.White else AppColors.roseDark,
        )
    }
}
