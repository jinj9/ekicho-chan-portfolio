package com.ekichochan.app.ui.collection

import com.ekichochan.app.data.sample.SampleCollectionData
import org.junit.Assert.assertEquals
import org.junit.Test

class CollectionFilterTest {
    @Test fun blankQueryReturnsAll() {
        assertEquals(3, filterCollection(SampleCollectionData.stations, "  ").size)
    }
    @Test fun matchesStationNameAndTrimsQuery() {
        assertEquals(102, filterCollection(SampleCollectionData.stations, " 海辺 ").single().id)
    }
    @Test fun matchesRegion() {
        assertEquals(3, filterCollection(SampleCollectionData.stations, "架空の地域").size)
    }
    @Test fun handlesNullFieldsAndNoMatch() {
        val missing = SampleCollectionData.stations[0].copy(stationName = null, locationName = null)
        assertEquals(0, filterCollection(listOf(missing), "駅").size)
        assertEquals(0, filterCollection(SampleCollectionData.stations, "存在しない駅").size)
    }
    @Test fun doesNotChangeSource() {
        val before = SampleCollectionData.stations.toList()
        filterCollection(SampleCollectionData.stations, "海辺")
        assertEquals(before, SampleCollectionData.stations)
    }
}
