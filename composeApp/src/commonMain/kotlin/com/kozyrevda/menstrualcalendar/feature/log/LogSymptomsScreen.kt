package com.kozyrevda.menstrualcalendar.feature.log

import androidx.compose.runtime.Composable
import com.kozyrevda.menstrualcalendar.feature.common.StubScaffold

@Composable
fun LogSymptomsScreen(onBack: (() -> Unit)? = null) {
    StubScaffold(
        title = "Сегодня",
        subtitle = "Интенсивность, симптомы, настроение и заметка — этап «Логирование»",
        onBack = onBack,
    )
}
