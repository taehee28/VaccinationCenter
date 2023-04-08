package com.thk.vaccinationcenter.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.map.CameraPosition
import com.thk.vaccinationcenter.data.repository.MapRepository
import com.thk.vaccinationcenter.models.VaccinationCenter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: MapRepository
) : ViewModel() {
    val centerList: StateFlow<List<VaccinationCenter>> =
        repository.getCenterList().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var keepLastCameraPosition: Boolean = false
}