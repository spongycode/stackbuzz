package com.example.stackbuzz.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object HelperFunctions {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeAgo(unixTimestamp: Long): String {
        val now = LocalDateTime.now()
        val dateTime =
            LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp), ZoneId.systemDefault())
        val duration = Duration.between(dateTime, now)

        return "asked " + when {
            duration.toDays() > 2 -> {
                val pattern =
                    if (dateTime.year == now.year) "MMM d 'at' HH:mm" else "MMM d, yyyy 'at' HH:mm"
                val formatter = DateTimeFormatter.ofPattern(pattern)
                dateTime.format(formatter)
            }

            duration.toDays() >= 1 -> if (duration.toDays() > 1) "${duration.toDays()} days" else "yesterday"
            duration.toHours() >= 1 -> "${duration.toHours()} hour${if (duration.toHours() > 1) "s" else ""} ago"
            duration.toMinutes() >= 1 -> "${duration.toMinutes()} min${if (duration.toMinutes() > 1) "s" else ""} ago"
            else -> "${duration.seconds} sec${if (duration.seconds > 1) "s" else ""} ago"
        }
    }

}