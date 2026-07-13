package com.kozyrevda.menstrualcalendar.core.data

import com.kozyrevda.menstrualcalendar.core.data.LunaSafety.Category
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LunaSafetyTest {

    @Test
    fun selfHarmDetected() {
        assertEquals(Category.SelfHarm, LunaSafety.classify("иногда мне кажется, что я не хочу жить"))
        assertEquals(Category.SelfHarm, LunaSafety.classify("думаю про суицид"))
    }

    @Test
    fun violenceDetected() {
        assertEquals(Category.Violence, LunaSafety.classify("муж меня ударил вчера"))
        assertEquals(Category.Violence, LunaSafety.classify("это домашнее насилие?"))
    }

    @Test
    fun acutePainDetected() {
        assertEquals(Category.AcutePain, LunaSafety.classify("у меня острая боль внизу живота"))
        assertEquals(Category.AcutePain, LunaSafety.classify("почти потеряла сознание от боли"))
    }

    @Test
    fun heavyBleedingDetected() {
        assertEquals(Category.HeavyBleeding, LunaSafety.classify("очень сильное кровотечение, что делать"))
    }

    @Test
    fun possiblePregnancyDetected() {
        assertEquals(Category.PossiblePregnancy, LunaSafety.classify("у меня задержка 5 дней"))
        assertEquals(Category.PossiblePregnancy, LunaSafety.classify("кажется беременна, страшно"))
        // Луна не утверждает беременность
        assertTrue("не могу определить беременность" in LunaSafety.replyFor(Category.PossiblePregnancy))
    }

    @Test
    fun missedPillDetected() {
        assertEquals(Category.MissedPill, LunaSafety.classify("забыла выпить таблетку вчера"))
        // ответ не содержит медицинской инструкции «прими две»
        assertTrue("инструкци" in LunaSafety.replyFor(Category.MissedPill))
    }

    @Test
    fun medicationRequestDetected() {
        assertEquals(Category.MedicationRequest, LunaSafety.classify("посоветуй таблетки от боли"))
        assertEquals(Category.MedicationRequest, LunaSafety.classify("какая дозировка нужна?"))
        assertEquals(Category.MedicationRequest, LunaSafety.classify("хочу перестать принимать назначенное"))
    }

    @Test
    fun seeDoctorDetected() {
        assertEquals(Category.SeeDoctor, LunaSafety.classify("что со мной происходит, это болезнь?"))
        assertEquals(Category.SeeDoctor, LunaSafety.classify("поставь диагноз по симптомам"))
    }

    @Test
    fun selfHarmHasPriorityOverOtherTopics() {
        // в одном сообщении и боль, и суицидальные мысли → кризисный сценарий важнее
        assertEquals(Category.SelfHarm, LunaSafety.classify("боль не проходит и я не хочу жить"))
    }

    @Test
    fun ordinaryMessagesPass() {
        assertNull(LunaSafety.classify("сегодня грустно и тревожно перед месячными"))
        assertNull(LunaSafety.classify("привет, как дела?"))
        assertNull(LunaSafety.classify("плохо сплю последнее время"))
    }

    @Test
    fun everyCategoryHasCalmReplyWithHelpPointer() {
        Category.entries.forEach { c ->
            val r = LunaSafety.replyFor(c)
            assertTrue(r.length > 80, "ответ $c слишком короткий")
            assertTrue(
                listOf("112", "103", "102", "врач", "специалист", "инструкци", "8 800")
                    .any { it in r.lowercase() },
                "ответ $c не направляет к помощи",
            )
        }
    }
}
