package com.example.stackbuzz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.stackbuzz.data.local.DataConverter

data class QuestionResponse(
    val items: List<Question>? = null,
    val has_more: Boolean? = null,
    val quota_max: Int? = null,
    val quota_remaining: Int? = null
)

@Entity(tableName = "questions")
data class Question(
    @TypeConverters(DataConverter::class)
    val tags: List<String>? = null,
    val owner: Owner? = null,
    val is_answered: Boolean? = null,
    val view_count: Int? = null,
    val answer_count: Int? = null,
    val score: Int? = null,
    val last_activity_date: Long? = null,
    val creation_date: Long? = null,
    val question_id: Long? = null,
    val content_license: String? = null,
    val link: String? = null,
    @PrimaryKey val title: String
)

data class Owner(
    val account_id: Int? = null,
    val reputation: Int? = null,
    val user_id: Int? = null,
    val user_type: String? = null,
    val profile_image: String? = null,
    val display_name: String? = null,
    val link: String? = null,
    val accept_rate: Int? = null
)
