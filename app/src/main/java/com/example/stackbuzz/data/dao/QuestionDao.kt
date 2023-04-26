package com.example.stackbuzz.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stackbuzz.data.model.Question
import kotlinx.coroutines.flow.Flow


@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions")
    fun getAllQuestions(): Flow<List<Question>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()
}