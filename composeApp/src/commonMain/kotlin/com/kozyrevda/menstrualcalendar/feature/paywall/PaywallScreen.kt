package com.kozyrevda.menstrualcalendar.feature.paywall

import androidx.compose.runtime.Composable
import com.kozyrevda.menstrualcalendar.feature.common.StubScaffold

@Composable
fun PaywallScreen(onBack: (() -> Unit)? = null) {
    StubScaffold(
        title = "Луна Плюс",
        subtitle = "Подписка 299 ₽/мес · 2 490 ₽/год — этап «Paywall»",
        onBack = onBack,
    )
}
