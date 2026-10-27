package com.ekichochan.app.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SampleStationRepositoryTest {
    @Test
    fun getStationReturnsLearningData() {
        val repository = SampleStationRepository()

        val station = repository.getStation()

        assertEquals(1, station.id)
        assertEquals("00000001", station.stationCd)
        assertEquals("学習用の駅", station.stationName)
        assertEquals("Sample Station", station.stationEnglishName)
        assertNull(station.latitude)
        assertNull(station.longitude)
        assertEquals(0, station.prefectureCd)
        assertEquals("学習用の地域", station.locationName)
        assertEquals("1", station.appearanceRateCd)
        assertEquals(
            "この駅はViewModelの役割を学ぶための架空のデータです。",
            station.stationInformationText
        )
        assertNull(station.imageData)
        assertNull(station.imageType)
        assertEquals(false, station.isObtained)
    }
}
