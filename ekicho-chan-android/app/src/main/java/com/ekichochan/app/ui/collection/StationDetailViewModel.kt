package com.ekichochan.app.ui.collection

import androidx.lifecycle.ViewModel
import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.repository.CollectionRepository

class StationDetailViewModel(
    stationId: Int?,
    repository: CollectionRepository
) : ViewModel() {
    // メモリ内の同期取得だけ。通信はここに直接追加しない。
    // IDがない場合は取得せず、既存の「駅が見つかりません」表示へ渡す。
    val station: StationMasterResponse? = if (stationId == null) {
        null
    } else {
        repository.getStations().find { station -> station.id == stationId }
    }
}
