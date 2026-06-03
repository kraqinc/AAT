package com.aat.data

import android.content.Context
import com.aat.notification.NotificationUtils

class StudyRepository(context: Context) {
    private val db = StudyDbHelper(context.applicationContext)
    private val ai = MockEduAiService()

    fun loadMessages(): List<ChatMessage> = db.getMessages()
    fun loadReminders(): List<ReminderItem> = db.getReminders()

    fun sendUserMessage(context: Context, input: String): ProcessResult {
        val clean = input.trim().ifBlank { "Quiero estudiar" }
        db.insertMessage("user", clean)

        val reply = ai.reply(clean)
        db.insertMessage("assistant", reply.text)

        val reminderId = reply.reminder?.let { draft ->
            val insertedId = db.insertReminder(draft.title, draft.subject, draft.triggerAtMillis)
            val subjectLabel = draft.subject ?: "tu tema"
            NotificationUtils.scheduleReminder(
                context = context,
                reminderId = insertedId,
                triggerAtMillis = draft.triggerAtMillis,
                title = draft.title,
                body = "Recuerda estudiar $subjectLabel"
            )
            insertedId
        }

        return ProcessResult(
            reply = reply,
            reminderId = reminderId
        )
    }

    fun markReminderDone(reminderId: Long) {
        db.markReminderDone(reminderId)
    }
}
