package com.ekichochan.app.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.repository.RemoteAccountStationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LiveCollectionViewModel(private val repository: RemoteAccountStationRepository) : ViewModel() {
    private val mutableState = MutableStateFlow<CollectionUiState>(CollectionUiState.Loading)
    val state = mutableState.asStateFlow()
    private val stationCache = mutableMapOf<Int, StationMasterResponse>()

    init { reload() }

    fun reload() = viewModelScope.launch {
        mutableState.value = CollectionUiState.Loading
        try {
            val stations = repository.collection().content
            stationCache.clear()
            stations.forEach { stationCache[it.id] = it }
            mutableState.value = CollectionUiState.Success(stations)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            mutableState.value = CollectionUiState.Error
        }
    }

    fun cached(stationId: Int): StationMasterResponse? = stationCache[stationId]
}
