package com.thk.vaccinationcenter.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thk.vaccinationcenter.data.repository.CachingRepository
import com.thk.vaccinationcenter.data.utils.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: CachingRepository
) : ViewModel() {
    suspend fun isCachingCompleted(): Boolean = repository.isCachingCompleted()

    fun cacheCenterData(): Flow<RequestState> = repository.cacheCenterData()
}