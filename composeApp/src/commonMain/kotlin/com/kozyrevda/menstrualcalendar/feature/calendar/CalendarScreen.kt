package com.kozyrevda.menstrualcalendar.feature.calendar

import androidx.compose.runtime.Composable
import com.kozyrevda.menstrualcalendar.feature.common.StubScaffold

@Composable
fun CalendarScreen(onBack: (() -> Unit)? = null) {
    StubScaffold(
        title = "Календарь",
        subtitle = "Сетка месяца с месячными, овуляцией и фертильными днями — этап «Календарь»",
        onBack = onBack,
    )
}
