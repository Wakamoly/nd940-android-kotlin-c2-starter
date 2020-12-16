@file:Suppress("UNCHECKED_CAST")

package com.udacity.asteroidradar.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.main.MainRepository
import com.udacity.asteroidradar.main.MainViewModel

class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository as MainRepository) as T
            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}