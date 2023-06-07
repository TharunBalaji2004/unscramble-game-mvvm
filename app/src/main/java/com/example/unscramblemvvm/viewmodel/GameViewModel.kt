package com.example.unscramblemvvm.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.unscramblemvvm.model.MAX_NO_OF_WORDS
import com.example.unscramblemvvm.model.SCORE_INCREASE
import com.example.unscramblemvvm.model.allWordsList

class GameViewModel : ViewModel() {
    private val _score = MutableLiveData(0)
    val score: LiveData<Int> get() = _score // Used for displaying in View

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int> get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<String> get() = _currentScrambledWord

    // List of words used in game
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    init {
        Log.d("GameFragment","GameViewModel created !!")
        getNextWord()
    }

    // Handles sending next word to view
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while(String(tempWord).equals(currentWord,false)){
            tempWord.shuffle()
        }

        if (wordsList.contains(currentWord)){
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = _currentWordCount.value?.inc()
            wordsList.add(currentWord)
        }
    }

    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)){
            _score.value = _score.value!! + SCORE_INCREASE
            return true
        }
        return false
    }

    fun maxWordsReached(): Boolean {
        if (_currentWordCount.value!! < MAX_NO_OF_WORDS){
            getNextWord()
            return false
        }
        return true
    }

    fun reinitdata(){
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }
}