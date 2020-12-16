package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Resource
import com.udacity.asteroidradar.base.BaseViewModel
import kotlinx.coroutines.launch

enum class AsteroidFilter {
    WEEK,
    TODAY,
    SAVED
}

class MainViewModel (
    private val repository: MainRepository
) : BaseViewModel(repository) {

    private val _setAsteroidFilter = MutableLiveData(AsteroidFilter.SAVED)

    val asteroids = Transformations.switchMap(_setAsteroidFilter) {
        it?.let { filter ->
            when(filter) {
                AsteroidFilter.WEEK -> repository.weekAsteroids
                AsteroidFilter.TODAY -> repository.todayAsteroids
                AsteroidFilter.SAVED -> repository.asteroids
            }
        }
    }

    val photoOTD = repository.photoOTD

    private val _dataState = MutableLiveData<Resource<Any>>()
    val dateState: LiveData<Resource<Any>>
        get() = _dataState

    fun displayNetworkErrorCompleted() {
        _dataState.value = Resource.Success(Any())
    }

    init {
        viewModelScope.launch {
            _dataState.value = Resource.Loading
            _dataState.value = repository.refreshPhotoOTD()
            _dataState.value = repository.refreshFeed()
        }
    }

    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetail: LiveData<Asteroid>
        get() = _navigateToAsteroidDetail

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetail.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToAsteroidDetail.value = null
    }

    fun updateAsteroidFilter(filter: AsteroidFilter) {
        _setAsteroidFilter.value = filter
    }

    fun refreshAll() {
        viewModelScope.launch {
            _dataState.value = Resource.Loading
            _dataState.value = repository.refreshPhotoOTD()
            _dataState.value = repository.refreshFeed()
        }
    }

}