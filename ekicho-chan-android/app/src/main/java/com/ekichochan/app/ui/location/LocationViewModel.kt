package com.ekichochan.app.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekichochan.app.data.repository.LocationStation
import com.ekichochan.app.data.repository.LocationStationRepository
import com.ekichochan.app.data.repository.RemoteAccountStationRepository
import com.ekichochan.app.data.model.StationMasterResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LocationStationState(
    val prefectureIndex: Int = -1,
    val loading: Boolean = false,
    val failed: Boolean = false,
    val stations: List<LocationStation> = emptyList(),
    val obtainState: LocationObtainState = LocationObtainState.Idle
)

sealed interface LocationObtainState {
    data object Idle : LocationObtainState
    data object Locating : LocationObtainState
    data object Loading : LocationObtainState
    data class Success(val station: StationMasterResponse) : LocationObtainState
    data class Error(val message: String) : LocationObtainState
}

class LocationViewModel(private val repository: LocationStationRepository,
    private val accountStations: RemoteAccountStationRepository? = null) : ViewModel() {
    private val mutableState = MutableStateFlow(LocationStationState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null
    private var generation = 0

    fun selectPrefecture(index: Int) {
        require(index in 0..6)
        request?.cancel()
        val current = ++generation
        mutableState.value = LocationStationState(prefectureIndex = index, loading = true)
        request = viewModelScope.launch {
            try {
                val stations = repository.getStations(index + 40)
                if (generation == current) mutableState.value = LocationStationState(index, stations = stations)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (generation == current) mutableState.value = LocationStationState(index, failed = true)
            }
        }
    }

    fun retry() {
        val index = state.value.prefectureIndex
        if (index >= 0) selectPrefecture(index)
    }

    fun locating() {
        if (mutableState.value.obtainState is LocationObtainState.Locating ||
            mutableState.value.obtainState is LocationObtainState.Loading) return
        mutableState.value = mutableState.value.copy(obtainState = LocationObtainState.Locating)
    }

    fun locationUnavailable(message: String) {
        mutableState.value = mutableState.value.copy(obtainState = LocationObtainState.Error(message))
    }

    fun obtain(stationId: Int, latitude: Double, longitude: Double) {
        if (mutableState.value.obtainState is LocationObtainState.Loading) return
        val remote = accountStations ?: return locationUnavailable("位置情報GETは利用できません。")
        // 通信開始前に状態を変え、同一フレーム内の連打も二重送信にしない。
        mutableState.value = mutableState.value.copy(obtainState = LocationObtainState.Loading)
        viewModelScope.launch {
            mutableState.value = try {
                mutableState.value.copy(obtainState = LocationObtainState.Success(
                    remote.obtainAtLocation(stationId, latitude, longitude)))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: retrofit2.HttpException) {
                val message = if (error.code() == 400) "駅から100m以内でGETしてください。"
                    else if (error.code() == 404) "駅または位置情報GET用画像が登録されていません。"
                    else "位置情報GETに失敗しました。"
                mutableState.value.copy(obtainState = LocationObtainState.Error(message))
            } catch (_: Exception) {
                mutableState.value.copy(obtainState = LocationObtainState.Error(
                    "現在地を送信できませんでした。サーバーとの接続を確認してください。"))
            }
        }
    }

    fun consumeResult() {
        mutableState.value = mutableState.value.copy(obtainState = LocationObtainState.Idle)
    }
}
