package com.ekichochan.app.ui.collection

import androidx.lifecycle.viewModelScope
import com.ekichochan.app.data.sample.SampleCollectionData
import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.repository.CollectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initiallyLoadsThenShowsStations() = runTest {
        val repository = FakeCollectionRepository(SampleCollectionData.stations)
        val viewModel = CollectionViewModel(repository)
        assertEquals(CollectionUiState.Loading, viewModel.uiState.value)
        assertEquals(0, repository.callCount)
        advanceUntilIdle()
        assertEquals(CollectionUiState.Success(SampleCollectionData.stations), viewModel.uiState.value)
        assertEquals(1, repository.callCount)
    }

    @Test
    fun cyclesThroughErrorEmptyAndSuccessAfterLoading() = runTest {
        val viewModel = CollectionViewModel(FakeCollectionRepository(SampleCollectionData.stations))
        advanceUntilIdle()
        viewModel.showNextSampleState()
        assertEquals(CollectionUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()
        assertEquals(CollectionUiState.Error, viewModel.uiState.value)
        viewModel.showNextSampleState()
        assertEquals(CollectionUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()
        assertEquals(CollectionUiState.Success(emptyList()), viewModel.uiState.value)
        viewModel.showNextSampleState()
        advanceUntilIdle()
        assertEquals(CollectionUiState.Success(SampleCollectionData.stations), viewModel.uiState.value)
    }

    @Test
    fun repositoryFailureCanBeRetried() = runTest {
        val repository = FakeCollectionRepository(SampleCollectionData.stations)
        repository.fail = true
        val viewModel = CollectionViewModel(repository)
        advanceUntilIdle()
        assertEquals(CollectionUiState.Error, viewModel.uiState.value)
        repository.fail = false
        viewModel.restoreSampleStations()
        assertEquals(CollectionUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()
        assertEquals(CollectionUiState.Success(SampleCollectionData.stations), viewModel.uiState.value)
        assertEquals(2, repository.callCount)
    }

    @Test
    fun copiesLoadedList() = runTest {
        val input = SampleCollectionData.stations.toMutableList()
        val viewModel = CollectionViewModel(FakeCollectionRepository(input))
        advanceUntilIdle()
        input.clear()
        assertEquals(CollectionUiState.Success(SampleCollectionData.stations), viewModel.uiState.value)
    }

    @Test
    fun usesInjectedData() = runTest {
        val expected = listOf(SampleCollectionData.stations[0].copy(id = 999))
        val viewModel = CollectionViewModel(FakeCollectionRepository(expected))
        advanceUntilIdle()
        assertEquals(CollectionUiState.Success(expected), viewModel.uiState.value)
    }

    @Test
    fun supportsEmptyRepository() = runTest {
        val viewModel = CollectionViewModel(FakeCollectionRepository(emptyList()))
        advanceUntilIdle()
        assertEquals(CollectionUiState.Success(emptyList()), viewModel.uiState.value)
    }

    @Test
    fun ignoresNextButtonWhileLoading() = runTest {
        val repository = FakeCollectionRepository(SampleCollectionData.stations)
        val viewModel = CollectionViewModel(repository)
        viewModel.showNextSampleState()
        advanceUntilIdle()
        assertEquals(CollectionUiState.Success(SampleCollectionData.stations), viewModel.uiState.value)
        assertEquals(1, repository.callCount)
    }

    @Test
    fun reloadCancelsPreviousJob() = runTest {
        val repository = FakeCollectionRepository(SampleCollectionData.stations)
        val viewModel = CollectionViewModel(repository)
        viewModel.restoreSampleStations()
        advanceUntilIdle()
        assertEquals(CollectionUiState.Success(SampleCollectionData.stations), viewModel.uiState.value)
        assertEquals(1, repository.callCount)
    }

    @Test
    fun cancelledScopeDoesNotFetchOrShowError() = runTest {
        val repository = FakeCollectionRepository(SampleCollectionData.stations)
        val viewModel = CollectionViewModel(repository)
        viewModel.viewModelScope.cancel()
        advanceUntilIdle()
        assertEquals(0, repository.callCount)
        assertEquals(CollectionUiState.Loading, viewModel.uiState.value)
    }

    private class FakeCollectionRepository(
        private val stations: List<StationMasterResponse>
    ) : CollectionRepository {
        var callCount = 0
            private set
        var fail = false

        override fun getStations(): List<StationMasterResponse> {
            callCount += 1
            if (fail) throw IllegalStateException("テスト用の失敗")
            return stations
        }
    }
}
