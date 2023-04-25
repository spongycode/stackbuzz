package com.example.stackbuzz.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.stackbuzz.data.api.ApiRepository
import com.example.stackbuzz.data.model.Question
import com.example.stackbuzz.util.Resource

class HomeViewModel(private val repository: ApiRepository) : ViewModel() {

    private val _questions = MediatorLiveData<Resource<List<Question>>>()
    val questions: LiveData<Resource<List<Question>>> = _questions

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> = _refreshing

    init {
        _questions.addSource(repository.getQuestions().asLiveData()) { result ->
            _questions.value = result
        }
    }

    fun updateQuestions() {
        _refreshing.value = true
        _questions.removeSource(questions)
        _questions.addSource(repository.getQuestions().asLiveData()) { result ->
            _questions.value = result
            _refreshing.value = false
        }
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