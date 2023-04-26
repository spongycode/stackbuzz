package com.example.stackbuzz.data.utils

import androidx.room.TypeConverter
import com.example.stackbuzz.data.model.Owner
import com.google.gson.GsonBuilder

class DataConverter {
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(string: String): List<String> {
        return string.split(",").map { it.trim() }
    }

    @TypeConverter
    fun ownerToString(owner: Owner): String {
        return GsonBuilder().create().toJson(owner)
    }

    @TypeConverter
    fun stringToOwner(string: String): Owner {
        return GsonBuilder().create().fromJson(string, Owner::class.java)
    }
}