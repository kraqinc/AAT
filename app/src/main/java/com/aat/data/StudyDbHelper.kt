package com.aat.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StudyDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                role TEXT NOT NULL,
                content TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE reminders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                subject TEXT,
                trigger_at INTEGER NOT NULL,
                created_at INTEGER NOT NULL,
                done INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        val welcome = ContentValues().apply {
            put("role", "assistant")
            put("content", "Hola, soy AAT. Escríbeme algo como: 'Tengo un examen mañana a las 4:40 de biología, recuérdame'.")
            put("created_at", System.currentTimeMillis())
        }
        db.insert("messages", null, welcome)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS messages")
        db.execSQL("DROP TABLE IF EXISTS reminders")
        onCreate(db)
    }

    fun insertMessage(role: String, content: String) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put("role", role)
                put("content", content)
                put("created_at", System.currentTimeMillis())
            }
            db.insert("messages", null, values)
        }
    }

    fun getMessages(limit: Int = 100): List<ChatMessage> {
        val result = mutableListOf<ChatMessage>()
        readableDatabase.rawQuery(
            "SELECT id, role, content, created_at FROM messages ORDER BY id ASC LIMIT ?",
            arrayOf(limit.toString())
        ).use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow("id")
            val roleCol = cursor.getColumnIndexOrThrow("role")
            val contentCol = cursor.getColumnIndexOrThrow("content")
            val createdCol = cursor.getColumnIndexOrThrow("created_at")
            while (cursor.moveToNext()) {
                result += ChatMessage(
                    id = cursor.getLong(idCol),
                    role = cursor.getString(roleCol),
                    content = cursor.getString(contentCol),
                    createdAt = cursor.getLong(createdCol)
                )
            }
        }
        return result
    }

    fun insertReminder(title: String, subject: String?, triggerAt: Long): Long {
        val values = ContentValues().apply {
            put("title", title)
            put("subject", subject)
            put("trigger_at", triggerAt)
            put("created_at", System.currentTimeMillis())
            put("done", 0)
        }
        return writableDatabase.insert("reminders", null, values)
    }

    fun getReminders(limit: Int = 40): List<ReminderItem> {
        val result = mutableListOf<ReminderItem>()
        readableDatabase.rawQuery(
            "SELECT id, title, subject, trigger_at, created_at, done FROM reminders ORDER BY trigger_at ASC LIMIT ?",
            arrayOf(limit.toString())
        ).use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow("id")
            val titleCol = cursor.getColumnIndexOrThrow("title")
            val subjectCol = cursor.getColumnIndexOrThrow("subject")
            val triggerCol = cursor.getColumnIndexOrThrow("trigger_at")
            val createdCol = cursor.getColumnIndexOrThrow("created_at")
            val doneCol = cursor.getColumnIndexOrThrow("done")
            while (cursor.moveToNext()) {
                result += ReminderItem(
                    id = cursor.getLong(idCol),
                    title = cursor.getString(titleCol),
                    subject = cursor.getString(subjectCol),
                    triggerAt = cursor.getLong(triggerCol),
                    createdAt = cursor.getLong(createdCol),
                    done = cursor.getInt(doneCol) == 1
                )
            }
        }
        return result
    }

    fun markReminderDone(reminderId: Long) {
        writableDatabase.execSQL(
            "UPDATE reminders SET done = 1 WHERE id = ?",
            arrayOf(reminderId.toString())
        )
    }

    companion object {
        private const val DB_NAME = "aat.db"
        private const val DB_VERSION = 1
    }
}
