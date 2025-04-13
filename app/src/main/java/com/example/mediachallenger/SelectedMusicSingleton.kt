package com.example.mediachallenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object SelectedMusicSingleton {
    private val _selectedMusic = MutableLiveData<String>("Música da lhama")
    val selectedMusic: LiveData<String> get() = _selectedMusic

    fun setSelectedMusic(music: String) {
        _selectedMusic.value = music
    }
}