package com.example.stackbuzz.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stackbuzz.data.api.ApiRepository
import com.example.stackbuzz.data.model.Question
import com.example.stackbuzz.util.Resource
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: ApiRepository) : ViewModel() {
    var editTextValue = ""
    private val _allQuestions = MutableLiveData<Resource<List<Question>>>()

    private val _filteredQuestions = MutableLiveData<List<Question>>()
    val filteredQuestions: LiveData<List<Question>> = _filteredQuestions

    private val _tagsList = MutableLiveData<MutableList<String>>()
    val tagsList: LiveData<MutableList<String>> = _tagsList

    val selectedChips = MutableLiveData<MutableSet<String>>(mutableSetOf())

    fun searchQuery(queryText: String) {
        viewModelScope.launch {
            repository.getSearchResults(queryText = queryText).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val newSet = mutableSetOf<String>()
                        for (question in resource.data!!) {
                            for (tag in question.tags!!) {
                                newSet.add(tag)
                            }
                        }
                        val newList = newSet.toMutableList()
                        newList.sort()
                        _tagsList.value = newList
                        _allQuestions.value = Resource.Success(resource.data)
                        _filteredQuestions.value = resource.data!!
                    }

                    is Resource.Error -> {
                    }
                }
            }
        }
    }

    fun filterQuery() {
        if (selectedChips.value == null || selectedChips.value?.isEmpty() == true) {
            if (_allQuestions.value != null) {
                _filteredQuestions.value = _allQuestions.value?.data!!
            }
        } else {
            val updatedQuestions = mutableListOf<Question>()
            for (question in _allQuestions.value?.data!!) {
                for (tag in question.tags!!) {
                    if (selectedChips.value!!.contains(tag)) {
                        updatedQuestions.add(question)
                        break
                    }
                }
            }
            _filteredQuestions.value = updatedQuestions
        }
    }
}

class SearchViewModelFactory(private val repository: ApiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}