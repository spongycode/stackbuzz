package com.example.stackbuzz.data.model

data class QuestionResponse(
    val items: List<QuestionItem>? = null,
    val has_more: Boolean? = null,
    val quota_max: Int? = null,
    val quota_remaining: Int? = null
)

data class QuestionItem(
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
    val title: String? = null
)

data class Owner(
    val account_id: Int,
    val reputation: Int,
    val user_id: Int,
    val user_type: String,
    val profile_image: String,
    val display_name: String,
    val link: String,
    val accept_rate: Int? = null
)
