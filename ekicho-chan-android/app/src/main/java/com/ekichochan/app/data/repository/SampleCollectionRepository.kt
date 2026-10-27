package com.ekichochan.app.data.repository

import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.sample.SampleCollectionData

/** APIや取得履歴には接続せず、架空の一覧を返す。 */
class SampleCollectionRepository : CollectionRepository {
    override fun getStations(): List<StationMasterResponse> =
        SampleCollectionData.stations.toList()
}
