package com.example.stackbuzz.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stackbuzz.data.model.Question

@Database(entities = [Question::class], version = 1)
@TypeConverters(DataConverter::class)
abstract class QuestionDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
}