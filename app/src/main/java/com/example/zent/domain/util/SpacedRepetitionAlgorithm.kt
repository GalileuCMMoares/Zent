package com.example.zent.domain.util

import java.util.Calendar

/**
 * Pesos de dificuldade para calcular a acurácia ponderada.
 * Questões HARD valem mais que EASY — acertar questões difíceis
 * indica retenção mais forte do conteúdo.
 */
enum class QuestionDifficulty(val weight: Float) {
    EASY(1.0f),
    MEDIUM(1.5f),
    HARD(2.0f);

    companion object {
        fun fromString(value: String): QuestionDifficulty {
            return when (value.uppercase()) {
                "EASY" -> EASY
                "HARD" -> HARD
                else -> MEDIUM
            }
        }
    }
}

/**
 * Resultado individual de uma questão no quiz.
 */
data class QuestionResult(
    val difficulty: QuestionDifficulty,
    val wasCorrect: Boolean
)

/**
 * Qualidade do quiz derivada da acurácia ponderada por dificuldade.
 * Mapeia a acurácia contínua (0.0–1.0) para a escala discreta do SM-2.
 */
enum class QuizQuality(val value: Int) {
    BLACKOUT(0),   // weightedAccuracy < 0.40  — esqueceu a maior parte
    HARD(1),       // weightedAccuracy < 0.60  — lembrou com muito esforço
    GOOD(2),       // weightedAccuracy < 0.85  — boa recordação
    EASY(3);       // weightedAccuracy >= 0.85 — recordação forte

    companion object {
        fun fromWeightedAccuracy(weightedAccuracy: Float): QuizQuality {
            return when {
                weightedAccuracy < 0.40f -> BLACKOUT
                weightedAccuracy < 0.60f -> HARD
                weightedAccuracy < 0.85f -> GOOD
                else -> EASY
            }
        }
    }
}

data class SrsResult(
    val nextReviewDate: Long,
    val intervalDays: Int,
    val easeFactor: Float,
    val repetitions: Int
)

object SpacedRepetitionAlgorithm {

    /**
     * Calcula a acurácia ponderada por dificuldade.
     *
     * Fórmula:
     *   weightedAccuracy = soma(peso_i * acerto_i) / soma(peso_i)
     *
     * Acertar questão HARD contribui mais para a nota.
     * Errar questão HARD penaliza mais.
     *
     * Exemplo com 10 questões (3E + 4M + 3D), peso total = 15.0:
     * - Acertou tudo: 15/15 = 1.0 → EASY
     * - Acertou fáceis + médias, errou difíceis: 9/15 = 0.60 → HARD
     * - Acertou só fáceis: 3/15 = 0.20 → BLACKOUT
     */
    fun computeWeightedAccuracy(results: List<QuestionResult>): Float {
        if (results.isEmpty()) return 0f
        val totalWeight = results.sumOf { it.difficulty.weight.toDouble() }.toFloat()
        val earnedWeight = results
            .filter { it.wasCorrect }
            .sumOf { it.difficulty.weight.toDouble() }.toFloat()
        return if (totalWeight > 0f) earnedWeight / totalWeight else 0f
    }

    /**
     * Cálculo principal do SRS baseado no SM-2 (Curva de Esquecimento).
     *
     * O ease factor controla a velocidade de crescimento dos intervalos.
     * A progressão padrão do SM-2:
     *   repetição 1 → 1 dia
     *   repetição 2 → 6 dias
     *   repetição n → intervalo_anterior × easeFactor
     *
     * Para desempenho ruim (qualidade < 2), as repetições são resetadas
     * e o aluno revisa novamente em 1 dia — modelando a parte íngreme
     * da curva de esquecimento de Ebbinghaus.
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

        if (quality.value < 2) {
            // Falha na recordação: volta ao início da curva de esquecimento
            repetitions = 0
            interval = 1
        } else {
            // Recordação bem-sucedida: avança na curva
            repetitions += 1
            interval = when {
                repetitions == 1 && quality == QuizQuality.EASY -> 3  // Primeira vez, excelente: 3 dias
                repetitions == 1 && quality == QuizQuality.GOOD -> 1  // Primeira vez, bom: 1 dia
                repetitions == 2 -> 6      // Segunda revisão: 6 dias (padrão SM-2)
                else -> Math.round(interval * easeFactor).coerceAtLeast(1)
            }
        }

        // Atualiza o ease factor usando a fórmula SM-2
        val q = quality.value
        easeFactor += (0.1f - (3 - q) * (0.08f + (3 - q) * 0.02f))

        // Limites: nunca abaixo de 1.3 (evita intervalos muito curtos)
        // e nunca acima de 3.0 (evita intervalos excessivamente longos)
        easeFactor = easeFactor.coerceIn(1.3f, 3.0f)

        // Calcula a data da próxima revisão
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

    /**
     * Método principal: calcula tudo a partir dos resultados brutos do quiz.
     * Este é o ponto de entrada usado pelo ViewModel.
     */
    fun processQuizResults(
        results: List<QuestionResult>,
        currentInterval: Int,
        currentEaseFactor: Float,
        currentRepetitions: Int
    ): SrsResult {
        val weightedAccuracy = computeWeightedAccuracy(results)
        val quality = QuizQuality.fromWeightedAccuracy(weightedAccuracy)
        return calculateNextReview(quality, currentInterval, currentEaseFactor, currentRepetitions)
    }
}
