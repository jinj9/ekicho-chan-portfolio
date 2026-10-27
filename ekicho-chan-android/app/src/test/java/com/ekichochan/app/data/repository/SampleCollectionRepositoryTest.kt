package com.ekichochan.app.data.repository

import com.ekichochan.app.data.sample.SampleCollectionData
import org.junit.Assert.assertEquals
import org.junit.Test

class SampleCollectionRepositoryTest {
    @Test
    fun returnsThreeSampleStations() {
        val stations = SampleCollectionRepository().getStations()
        assertEquals(3, stations.size)
        assertEquals(SampleCollectionData.stations, stations)
    }

    @Test
    fun editingCopiedResultDoesNotChangeSource() {
        val repository = SampleCollectionRepository()
        val edited = repository.getStations().toMutableList()
        edited[0] = edited[0].copy(stationName = "変更したコピー")
        assertEquals("はじまり駅（学習用）", repository.getStations()[0].stationName)
        assertEquals("はじまり駅（学習用）", SampleCollectionData.stations[0].stationName)
    }
}
