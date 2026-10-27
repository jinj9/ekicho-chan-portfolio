package com.ekichochan.app.data.repository

import com.ekichochan.app.data.model.StationMasterResponse

interface StationRepository {
    // STEP 8ではローカルデータだけを扱う。通信導入時に非同期の契約へ変更する。
    fun getStation(): StationMasterResponse
}
