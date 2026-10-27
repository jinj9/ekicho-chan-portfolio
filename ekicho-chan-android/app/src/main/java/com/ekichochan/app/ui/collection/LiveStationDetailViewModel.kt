package com.ekichochan.app.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.repository.RemoteAccountStationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LiveStationDetailState {
    data object Loading : LiveStationDetailState
    data class Success(val station: StationMasterResponse) : LiveStationDetailState
    data object Error : LiveStationDetailState
}

class LiveStationDetailViewModel(stationId: Int?, repository: RemoteAccountStationRepository) : ViewModel() {
    private val mutableState = MutableStateFlow<LiveStationDetailState>(LiveStationDetailState.Loading)
    val state = mutableState.asStateFlow()
    init {
        viewModelScope.launch {
            mutableState.value = if (stationId == null) LiveStationDetailState.Error else
                runCatching { repository.detail(stationId) }.fold(
                    LiveStationDetailState::Success, { LiveStationDetailState.Error })
        }
    }
}
