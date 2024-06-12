package com.example.compasstest

import android.accounts.NetworkErrorException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compasstest.data.ApiService
import com.example.compasstest.model.AboutData
import com.example.compasstest.model.AboutDataDao
import com.example.compasstest.utils.RequestState
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val apiService: ApiService,
    private val aboutDataDao: AboutDataDao
) : ViewModel() {


    private val _every10thCharacter = MutableStateFlow<List<String>>(emptyList())
    val every10thCharacter: StateFlow<List<String>> = _every10thCharacter

    private val _wordCount = MutableStateFlow<Map<String, Int>>(emptyMap())
    val wordCount: StateFlow<Map<String, Int>> = _wordCount

    private val _requestState = MutableStateFlow<RequestState<String>>(RequestState.Idle)
    val requestState: StateFlow<RequestState<String>> = _requestState


    fun loadAboutData() {
        _requestState.value = RequestState.Loading
        viewModelScope.launch {
            try {
                aboutDataDao.getAboutData(1).collect { aboutData ->
                    _requestState.value = RequestState.Success(aboutData.content)
                    _every10thCharacter.value = every10thCharacterRequest(aboutData.content)
                    _wordCount.value = countWords(aboutData.content)
                }
            } catch (e: Exception) {
                _requestState.value = RequestState.Error("An error occurred with your network connection")
            }
        }
    }

    fun fetchData() {
        _requestState.value = RequestState.Loading
        viewModelScope.launch {
            try {
                val response = apiService.fetchContent()
                _requestState.value = RequestState.Success(response)
                executeDataProcessing(response)
                aboutData(response)
            } catch (e: Exception) {
                _requestState.value = RequestState.Error("An error occurred")
                loadAboutData()
            }
        }
    }

    private suspend fun executeDataProcessing(content: String) {
        coroutineScope {
            val every10thCharacterDeferred = async { every10thCharacterRequest(content) }
            val wordCountDeferred = async { countWords(content) }

            _every10thCharacter.value = every10thCharacterDeferred.await()
            _wordCount.value = wordCountDeferred.await()
        }
    }

    private suspend fun aboutData(content: String) {
        aboutDataDao.insertAboutData(AboutData(1, content))
    }

    private fun every10thCharacterRequest(text: String): List<String> {
        return text.filter { !it.isWhitespace() }
            .mapIndexedNotNull { index, char ->
                if ((index + 1) % 10 == 0) char.toString() else null
            }
    }

    private fun countWords(text: String): Map<String, Int> {
        val words = text.split(Regex("\\s+"))
        return words.groupingBy { it }.eachCount()
    }
}
