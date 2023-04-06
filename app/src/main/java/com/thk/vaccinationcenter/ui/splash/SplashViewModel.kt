package com.thk.vaccinationcenter.ui.splash

import androidx.lifecycle.ViewModel
import com.thk.vaccinationcenter.data.repository.CachingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: CachingRepository
) : ViewModel() {
    fun cacheCenterData() = repository.cacheCenterData()
}