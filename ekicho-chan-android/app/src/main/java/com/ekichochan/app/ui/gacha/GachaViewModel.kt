package com.ekichochan.app.ui.gacha

import androidx.lifecycle.ViewModel
import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.repository.StationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GachaViewModel(
    private val repository: StationRepository
) : ViewModel() {
    // このSTEPの取得は同期のローカル処理。通信はここでは実行しない。
    private val initialStation = repository.getStation()

    // 更新できる入口をViewModelの内部に限定する。
    private val _station = MutableStateFlow(initialStation)
    val station: StateFlow<StationMasterResponse> = _station.asStateFlow()

    fun toggleSampleStationName() {
        _station.update { current ->
            val nextName = if (current.stationName == initialStation.stationName) {
                "変更後の学習用の駅"
            } else {
                initialStation.stationName
            }
            // 元のインスタンスを書き換えず、新しいデータを通知する。
            current.copy(stationName = nextName)
        }
    }
}
