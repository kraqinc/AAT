package com.aat.data

class MockEduAiService {

    fun reply(input: String): AiReply {
        val reminder = StudyPromptParser.parse(input)

        val base = when {
            input.contains("quiz", ignoreCase = true) -> {
                "Claro. Te preparo un quiz rápido con preguntas de práctica y luego ajustamos el nivel."
            }
            input.contains("flash", ignoreCase = true) -> {
                "Perfecto. Voy a convertir el tema en flashcards para memorizar más rápido."
            }
            input.contains("imagen", ignoreCase = true) || input.contains("dibuj", ignoreCase = true) -> {
                "Puedo generar una imagen educativa de apoyo. En esta base lista para repo, el motor real se conecta después con una API pública."
            }
            reminder != null -> {
                "Claro. Ya detecté tu examen y armé el recordatorio. ¿Quieres practicar con un quiz corto o repasar con flashcards?"
            }
            else -> {
                "Te ayudo a estudiar. Puedo resumir, hacer quiz, crear flashcards, generar imágenes y programar recordatorios."
            }
        }

        return AiReply(
            text = base,
            suggestions = listOf(
                "Hacer quiz rápido",
                "Crear flashcards",
                "Resumir el tema",
                "Generar imagen de apoyo"
            ),
            reminder = reminder
        )
    }
}
