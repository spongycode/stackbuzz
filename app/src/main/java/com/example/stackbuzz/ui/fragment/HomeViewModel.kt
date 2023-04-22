package com.example.stackbuzz.ui.fragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.stackbuzz.data.api.ApiRepository

class HomeViewModel(repository: ApiRepository) : ViewModel() {

    val questions = repository.getQuestions().asLiveData()

    init {
        Log.d("INIT", questions.value.toString())
    }
}

class HomeViewModelFactory(private val repository: ApiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}