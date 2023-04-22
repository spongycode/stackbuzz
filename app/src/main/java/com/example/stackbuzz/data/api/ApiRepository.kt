package com.example.stackbuzz.data.api

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.room.withTransaction
import com.example.stackbuzz.data.local.QuestionDatabase
import com.example.stackbuzz.data.model.QuestionResponse
import com.example.stackbuzz.util.networkBoundResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepository(context: Context) {
    var searchResultsLiveData = MutableLiveData<Response<QuestionResponse>>()

    private val db =
        Room.databaseBuilder(context, QuestionDatabase::class.java, "question_database")
            .build()
    private val questionDao = db.questionDao()
    fun getQuestions() = networkBoundResource(
        query = {
            questionDao.getAllQuestions()
        },
        fetch = {
            ApiService.Companion.create().getQuestions()
        },
        saveFetchResult = { questions ->
            db.withTransaction {
                questionDao.deleteAllQuestions()
                questionDao.insertQuestions(questions.items!!)
            }

        }
    )

    fun getSearchResults(queryText: String): LiveData<Response<QuestionResponse>> {

        val agentLeadsService: Call<QuestionResponse> =
            ApiService.Companion.create().getSearchResults(queryText)
        agentLeadsService.enqueue(object : Callback<QuestionResponse> {
            override fun onResponse(
                call: Call<QuestionResponse>,
                response: Response<QuestionResponse>
            ) {
                searchResultsLiveData.value = response
            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                Log.d("Repo Failure", t.toString())
            }
        })
        return searchResultsLiveData
    }
}