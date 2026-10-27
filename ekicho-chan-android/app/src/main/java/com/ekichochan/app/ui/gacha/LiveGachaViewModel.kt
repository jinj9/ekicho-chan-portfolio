package com.ekichochan.app.ui.gacha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.repository.RemoteAccountStationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LiveGachaState {
    data object Ready : LiveGachaState
    data object Loading : LiveGachaState
    data class Success(val station: StationMasterResponse) : LiveGachaState
    data object Error : LiveGachaState
}

class LiveGachaViewModel(private val repository: RemoteAccountStationRepository) : ViewModel() {
    private val mutableState = MutableStateFlow<LiveGachaState>(LiveGachaState.Ready)
    val state = mutableState.asStateFlow()

    fun draw() {
        if (mutableState.value == LiveGachaState.Loading) return
        // launch前に状態を変え、同一フレーム内の連打も二重送信にしない。
        mutableState.value = LiveGachaState.Loading
        viewModelScope.launch {
            try {
                mutableState.value = LiveGachaState.Success(repository.draw())
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = LiveGachaState.Error
            }
        }
    }
}
