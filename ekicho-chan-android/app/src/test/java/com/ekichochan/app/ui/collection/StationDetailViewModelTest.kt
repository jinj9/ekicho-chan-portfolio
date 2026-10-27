package com.ekichochan.app.ui.collection

import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.repository.CollectionRepository
import com.ekichochan.app.data.sample.SampleCollectionData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StationDetailViewModelTest {
    @Test
    fun findsSelectedStationFromRepository() {
        val repository = FakeCollectionRepository(SampleCollectionData.stations)
        val viewModel = StationDetailViewModel(102, repository)
        assertEquals("海辺駅（学習用）", viewModel.station?.stationName)
    }

    @Test
    fun usesInjectedDataInsteadOfBuiltInSample() {
        val station = SampleCollectionData.stations[0].copy(id = 999, stationName = "差し替えた駅")
        val viewModel = StationDetailViewModel(999, FakeCollectionRepository(listOf(station)))
        assertEquals(station, viewModel.station)
    }

    @Test
    fun unknownIdReturnsNull() {
        val viewModel = StationDetailViewModel(-1, FakeCollectionRepository(SampleCollectionData.stations))
        assertNull(viewModel.station)
    }

    @Test
    fun emptyRepositoryReturnsNull() {
        val viewModel = StationDetailViewModel(102, FakeCollectionRepository(emptyList()))
        assertNull(viewModel.station)
    }

    @Test
    fun missingIdDoesNotReadRepository() {
        val repository = FakeCollectionRepository(SampleCollectionData.stations)
        val viewModel = StationDetailViewModel(null, repository)
        assertNull(viewModel.station)
        assertEquals(0, repository.callCount)
    }

    @Test
    fun readingStationAgainDoesNotFetchAgain() {
        val repository = FakeCollectionRepository(SampleCollectionData.stations)
        val viewModel = StationDetailViewModel(102, repository)
        assertEquals(102, viewModel.station?.id)
        assertEquals("海辺駅（学習用）", viewModel.station?.stationName)
        assertEquals(1, repository.callCount)
    }

    @Test
    fun separateViewModelsKeepTheirOwnSelectedStations() {
        val repository = FakeCollectionRepository(SampleCollectionData.stations)
        val first = StationDetailViewModel(101, repository)
        val second = StationDetailViewModel(103, repository)
        assertEquals(101, first.station?.id)
        assertEquals(103, second.station?.id)
    }

    private class FakeCollectionRepository(
        private val stations: List<StationMasterResponse>
    ) : CollectionRepository {
        var callCount = 0
            private set

        override fun getStations(): List<StationMasterResponse> {
            callCount += 1
            return stations
        }
    }
}
