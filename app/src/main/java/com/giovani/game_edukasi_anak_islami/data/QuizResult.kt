package com.giovani.game_edukasi_anak_islami.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class QuizResult(
    val id: Int,
    val categoryId: Int,
    val score: Int? = 0,
    val dateTime: Instant?
)
