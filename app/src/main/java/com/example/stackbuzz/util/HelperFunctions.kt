package com.example.stackbuzz.util

import android.util.Log

object HelperFunctions {

    fun getTimeAgo(unixTime: Long): String {
        val now = System.currentTimeMillis() / 1000
        Log.d("time", now.toString())
        val seconds = now - unixTime

        return when {
            seconds < 60 -> "$seconds seconds ago"
            seconds < 60 * 2 -> "1 minute ago"
            seconds < 60 * 60 -> "${seconds / 60} minutes ago"
            seconds < 60 * 60 * 2 -> "1 hour ago"
            seconds < 60 * 60 * 24 -> "${seconds / (60 * 60)} hours ago"
            seconds < 60 * 60 * 24 * 2 -> "1 day ago"
            seconds < 60 * 60 * 24 * 30 -> "${seconds / (60 * 60 * 24)} days ago"
            seconds < 60 * 60 * 24 * 30 * 2 -> "1 month ago"
            seconds < 60 * 60 * 24 * 365 -> "${seconds / (60 * 60 * 24 * 30)} months ago"
            else -> "${seconds / (60 * 60 * 24 * 365)} years ago"
        }
    }

}