package com.example.zent.domain.util

import java.util.Calendar

// Resposta do usuário após ver o Quiz
enum class QuizQuality(val value: Int) {
    BLACKOUT(0), // Esqueceu completamente
    HARD(1),     // Lembrou com muito esforço
    GOOD(2),     // Lembrou bem
    EASY(3)      // Achou muito fácil
}

data class SrsResult(
    val nextReviewDate: Long,
    val intervalDays: Int,
    val easeFactor: Float,
    val repetitions: Int
)

object SpacedRepetitionAlgorithm {

    /**
     * Calcula quando a carta deve aparecer novamente.
     * Baseado no algoritmo SM-2 da SuperMemo (Curva de Esquecimento).
     */
    fun calculateNextReview(
        quality: QuizQuality,
        currentInterval: Int,
        currentEaseFactor: Float,
        currentRepetitions: Int
    ): SrsResult {
        var repetitions = currentRepetitions
        var interval = currentInterval
        var easeFactor = currentEaseFactor

        // Se o usuário errou feio (Blackout), o progresso zera
        if (quality.value < 2) {
            repetitions = 0
            interval = 1
        } else {
            // Se acertou, aumentamos o número de repetições
            repetitions += 1

            // Calcula o novo intervalo de dias
            interval = when (repetitions) {
                1 -> 1
                2 -> 6
                else -> Math.round(interval * easeFactor)
            }
        }

        // Calcula o novo fator de facilidade (se achou difícil, a carta aparece com mais frequência)
        easeFactor += (0.1f - (3 - quality.value) * (0.08f + (3 - quality.value) * 0.02f))

        // Limites de segurança (Fator de facilidade nunca deve ser menor que 1.3)
        if (easeFactor < 1.3f) easeFactor = 1.3f

        // Calcula a data exata da próxima revisão
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, interval)
        val nextReviewDate = calendar.timeInMillis

        return SrsResult(
            nextReviewDate = nextReviewDate,
            intervalDays = interval,
            easeFactor = easeFactor,
            repetitions = repetitions
        )
    }
}