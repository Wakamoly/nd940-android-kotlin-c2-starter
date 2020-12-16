package com.udacity.asteroidradar.main

import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.base.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel (
    private val repository: MainRepository
) : BaseViewModel(repository) {

    init {
        viewModelScope.launch {
            repository.refresh()
        }
    }

    val asteroids = repository.asteroids
    val photoOTD = repository.photoOTD

}