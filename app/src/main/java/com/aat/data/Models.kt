package com.aat.data

data class ChatMessage(
    val id: Long,
    val role: String,
    val content: String,
    val createdAt: Long
)

data class ReminderItem(
    val id: Long,
    val title: String,
    val subject: String?,
    val triggerAt: Long,
    val createdAt: Long,
    val done: Boolean
)

data class ReminderDraft(
    val title: String,
    val subject: String?,
    val triggerAtMillis: Long
)

data class AiReply(
    val text: String,
    val suggestions: List<String>,
    val reminder: ReminderDraft? = null
)

data class ProcessResult(
    val reply: AiReply,
    val reminderId: Long? = null
)
