package com.example.stackbuzz.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stackbuzz.data.dao.QuestionDao
import com.example.stackbuzz.data.utils.DataConverter
import com.example.stackbuzz.data.model.Question

@Database(entities = [Question::class], version = 1)
@TypeConverters(DataConverter::class)
abstract class QuestionDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
}