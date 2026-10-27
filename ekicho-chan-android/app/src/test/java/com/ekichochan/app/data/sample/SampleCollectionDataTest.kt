package com.ekichochan.app.data.sample

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SampleCollectionDataTest {
    @Test
    fun findsSelectedStationById() {
        assertEquals("はじまり駅（学習用）", SampleCollectionData.findStation(101)?.stationName)
        assertEquals("海辺駅（学習用）", SampleCollectionData.findStation(102)?.stationName)
        assertEquals("山あい駅（学習用）", SampleCollectionData.findStation(103)?.stationName)
    }

    @Test
    fun unknownIdReturnsNull() {
        assertNull(SampleCollectionData.findStation(-1))
    }

    @Test
    fun missingIdReturnsNull() {
        assertNull(SampleCollectionData.findStation(null))
    }

    @Test
    fun lookupDoesNotChangeSampleData() {
        val before = SampleCollectionData.stations.toList()
        SampleCollectionData.findStation(102)
        assertEquals(before, SampleCollectionData.stations)
    }
}
