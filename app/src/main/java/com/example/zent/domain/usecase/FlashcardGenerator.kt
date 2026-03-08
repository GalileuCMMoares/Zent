package com.example.zent.domain.usecase

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

data class GeneratedCard(
    val question: String,
    val correctAnswer: String,
    val options: String // JSON String com as opções erradas
)

class FlashcardGenerator {

    // Lembre-se de substituir pela sua chave real!
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = "AIzaSyAIQML5LmC-DkywfeGNm3IermFHGOAzAuU"
    )

    suspend fun generateCards(
        materialText: String,
        difficultyLevel: String,
        image: Bitmap? = null,
        pdfBytes: ByteArray? = null
    ): List<GeneratedCard> = withContext(Dispatchers.IO) {

        // NOVO PROMPT: 10 perguntas, dificuldades variadas, alinhadas ao nível escolar.
        val prompt = """
            Você é um especialista em educação focado em repetição espaçada (SRS).
            PÚBLICO-ALVO: "$difficultyLevel".
            
            Sua missão é gerar EXATAMENTE 10 questões de múltipla escolha INÉDITAS baseadas estritamente no material fornecido.
            
            DIRETRIZES OBRIGATÓRIAS:
            1. VARIAÇÃO DE DIFICULDADE: Dentro do nível "$difficultyLevel", divida as 10 perguntas em:
               - 3 questões FÁCEIS (Focadas em conceitos básicos e memorização direta).
               - 4 questões MÉDIAS (Exigem interpretação e conexão de ideias do texto).
               - 3 questões DIFÍCEIS (Exigem raciocínio analítico, detalhes cruciais ou "pegadinhas").
            2. ADAPTAÇÃO: A linguagem deve ser ideal para uma pessoa estudando para "$difficultyLevel".
            3. FOCO: Não faça perguntas de gramática. Foque exclusivamente no conteúdo da matéria.
            
            Retorne EXATAMENTE e APENAS um Array JSON puro neste formato:
            [{"question": "?", "correctAnswer": "Correta", "wrongOptions": ["Errada 1", "Errada 2", "Errada 3"]}]
            
            Texto/Contexto:
            $materialText
        """.trimIndent()

        try {
            val inputContent = content {
                if (image != null) image(image)
                if (pdfBytes != null) blob("application/pdf", pdfBytes)
                text(prompt)
            }

            val response = generativeModel.generateContent(inputContent)
            val jsonText = response.text?.trim()?.removePrefix("```json")?.removeSuffix("```")?.trim() ?: "[]"

            val jsonArray = JSONArray(jsonText)
            val cards = mutableListOf<GeneratedCard>()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val question = item.getString("question")
                val correctAnswer = item.getString("correctAnswer")
                val wrongOptionsArray = item.getJSONArray("wrongOptions")

                val optionsList = mutableListOf<String>()
                for (j in 0 until wrongOptionsArray.length()) optionsList.add(wrongOptionsArray.getString(j))

                cards.add(GeneratedCard(question, correctAnswer, JSONArray(optionsList).toString()))
            }
            return@withContext cards

        } catch (e: Exception) {
            throw Exception("Falha ao gerar cartas com a IA: ${e.message}")
        }
    }
}