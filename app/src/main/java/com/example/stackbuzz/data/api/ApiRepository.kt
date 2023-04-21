package com.example.stackbuzz.data.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stackbuzz.data.model.QuestionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepository {
    var questionsLiveData = MutableLiveData<Response<QuestionResponse>>()

    fun getQuestions(): LiveData<Response<QuestionResponse>> {

        val agentLeadsService: Call<QuestionResponse> =
            ApiService.Companion.create().getQuestions()
        agentLeadsService.enqueue(object : Callback<QuestionResponse> {
            override fun onResponse(
                call: Call<QuestionResponse>,
                response: Response<QuestionResponse>
            ) {
                questionsLiveData.setValue(response)
            }

            override fun onFailure(call: Call<QuestionResponse>, t: Throwable) {
                Log.d("Repo Failure", t.toString())
            }
        })
        return questionsLiveData
    }

}