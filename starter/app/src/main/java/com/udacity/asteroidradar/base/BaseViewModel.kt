package com.udacity.asteroidradar.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel (
    private val repository: BaseRepository
): ViewModel()