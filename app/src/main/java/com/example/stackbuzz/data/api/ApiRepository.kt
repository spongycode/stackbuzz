package com.example.stackbuzz.data.api

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import com.example.stackbuzz.data.local.QuestionDatabase
import com.example.stackbuzz.data.model.Question
import com.example.stackbuzz.util.Resource
import com.example.stackbuzz.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ApiRepository(context: Context) {
    private val apiService = ApiService.Companion.create()

    private val db =
        Room.databaseBuilder(context, QuestionDatabase::class.java, "question_database")
            .build()
    private val questionDao = db.questionDao()
    fun getQuestions() = networkBoundResource(
        query = {
            questionDao.getAllQuestions()
        },
        fetch = {
            apiService.getQuestions()
        },
        saveFetchResult = { questions ->
            db.withTransaction {
                questionDao.deleteAllQuestions()
                questionDao.insertQuestions(questions.items!!)
            }

        }
    )

    fun getSearchResults(queryText: String): Flow<Resource<List<Question>>> = flow {
        emit(Resource.Loading())
        try {
            val searchResults = apiService.getSearchResults(queryText).items
            emit(Resource.Success(searchResults!!))
        } catch (throwable: Throwable) {
            emit(Resource.Error(throwable))
        }
    }
}