package com.ekichochan.app.data.repository

import com.ekichochan.app.data.model.StationMasterResponse

interface CollectionRepository {
    // このSTEPはメモリ内の同期取得だけ。通信導入時に非同期の契約へ変更する。
    fun getStations(): List<StationMasterResponse>
}
