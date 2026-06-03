package com.aat.data

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object StudyPromptParser {
    private val timeRegex = Regex("""\b([01]?\d|2[0-3]):([0-5]\d)\b""")
    private val subjectRegex = Regex(
        """\bde\s+([a-záéíóúñ0-9\s]+?)(?:\s+(?:recu[eé]rdame|recuerdame|recordarme|para|mañana|hoy)|$)""",
        RegexOption.IGNORE_CASE
    )

    fun parse(input: String, now: LocalDateTime = LocalDateTime.now()): ReminderDraft? {
        val match = timeRegex.find(input) ?: return null
        val hour = match.groupValues[1].toInt()
        val minute = match.groupValues[2].toInt()

        val subject = subjectRegex.find(input)?.groupValues?.getOrNull(1)?.trim()
            ?.takeIf { it.isNotBlank() }

        val dayOffset = when {
            input.contains("pasado mañana", ignoreCase = true) -> 2L
            input.contains("mañana", ignoreCase = true) -> 1L
            else -> 0L
        }

        var scheduled = now.toLocalDate().plusDays(dayOffset).atTime(hour, minute)
        if (scheduled.isBefore(now)) {
            scheduled = scheduled.plusDays(1)
        }

        val triggerAtMillis = scheduled
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val subjectLabel = subject ?: "estudio"

        return ReminderDraft(
            title = "Examen de $subjectLabel · ${scheduled.format(formatter)}",
            subject = subject,
            triggerAtMillis = triggerAtMillis
        )
    }

    fun humanTime(millis: Long): String {
        val dt = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(millis),
            ZoneId.systemDefault()
        )
        return dt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
    }

    fun remainingMillis(triggerAtMillis: Long): Long {
        return Duration.between(
            LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant(),
            java.time.Instant.ofEpochMilli(triggerAtMillis)
        ).toMillis().coerceAtLeast(0L)
    }
}
