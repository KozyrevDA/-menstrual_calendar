package com.kozyrevda.menstrualcalendar.feature.pills

import androidx.compose.runtime.Composable
import com.kozyrevda.menstrualcalendar.feature.common.StubScaffold

@Composable
fun PillsScreen(onBack: (() -> Unit)? = null) {
    StubScaffold(
        title = "Таблетки",
        subtitle = "Блистер 21+7, серия и напоминания — этап «Таблетки»",
        onBack = onBack,
    )
}
