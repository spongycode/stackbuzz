package com.example.stackbuzz.util

import android.util.Log

object HelperFunctions {

    fun getTimeAgo(unixTime: Long): String {
        val now = System.currentTimeMillis() / 1000
        Log.d("time", now.toString())
        val seconds = now - unixTime

        return when {
            seconds < 60 -> "${seconds}s"
            seconds < 60 * 60 -> "${seconds / 60}m"
            seconds < 60 * 60 * 24 -> "${seconds / (60 * 60)}h"
            seconds < 60 * 60 * 24 * 30 -> "${seconds / (60 * 60 * 24)}d"
            seconds < 60 * 60 * 24 * 365 -> "${seconds / (60 * 60 * 24 * 30)}mth"
            else -> "${seconds / (60 * 60 * 24 * 365)}yr"
        }
    }

}