package com.thk.vaccinationcenter.ui.map

import androidx.lifecycle.ViewModel
import com.thk.vaccinationcenter.data.repository.MapRepository
import com.thk.vaccinationcenter.models.VaccinationCenter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: MapRepository
) : ViewModel() {
    fun getCenterList(): Flow<List<VaccinationCenter>> = repository.getCenterList()
}