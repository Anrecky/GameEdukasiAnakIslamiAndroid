package com.giovani.game_edukasi_anak_islami.data

import kotlinx.serialization.*

@Serializable
data class Question(
    val id: Int,
    val question: String,
    val optionOne: String,
    val optionTwo: String,
    val optionThree: String,
    val optionFour: String,
    val answer: String
)
