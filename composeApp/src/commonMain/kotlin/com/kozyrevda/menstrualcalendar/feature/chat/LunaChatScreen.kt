package com.kozyrevda.menstrualcalendar.feature.chat

import androidx.compose.runtime.Composable
import com.kozyrevda.menstrualcalendar.feature.common.StubScaffold

@Composable
fun LunaChatScreen(onBack: (() -> Unit)? = null) {
    StubScaffold(
        title = "Луна",
        subtitle = "ИИ-подружка и психолог — этап «Чат»",
        onBack = onBack,
    )
}
