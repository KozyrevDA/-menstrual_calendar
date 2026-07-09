package com.kozyrevda.menstrualcalendar.feature.stats

import androidx.compose.runtime.Composable
import com.kozyrevda.menstrualcalendar.feature.common.StubScaffold

@Composable
fun StatsScreen(onBack: (() -> Unit)? = null) {
    StubScaffold(
        title = "Статистика",
        subtitle = "Средний цикл, график длины и история — этап «Статистика»",
        onBack = onBack,
    )
}
