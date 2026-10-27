package com.ekichochan.app

import com.ekichochan.app.data.repository.*
import com.ekichochan.app.ui.location.LocationViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class LocationViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun cleanup() { Dispatchers.resetMain() }

    @Test fun selectionLoadsRealPrefectureCode() = runTest(dispatcher) {
        var code = -1
        val vm = LocationViewModel(LocationStationRepository {
            code = it
            listOf(LocationStation(1, "博多"))
        })
        assertEquals(-1, vm.state.value.prefectureIndex)
        vm.selectPrefecture(0)
        assertTrue(vm.state.value.loading)
        advanceUntilIdle()
        assertEquals(40, code)
        assertEquals("博多", vm.state.value.stations.single().name)
        assertFalse(vm.state.value.loading)
    }

    @Test fun failureCanRetryAndReturnEmptyList() = runTest(dispatcher) {
        var fail = true
        val vm = LocationViewModel(LocationStationRepository {
            if (fail) error("offline")
            emptyList()
        })
        vm.selectPrefecture(6)
        advanceUntilIdle()
        assertTrue(vm.state.value.failed)
        fail = false
        vm.retry()
        advanceUntilIdle()
        assertFalse(vm.state.value.failed)
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.stations.isEmpty())
    }

    @Test fun newerSelectionWinsEvenIfOldRequestIgnoresCancellation() = runTest(dispatcher) {
        val vm = LocationViewModel(LocationStationRepository { code ->
            if (code == 40) withContext(NonCancellable) { delay(1000) }
            listOf(LocationStation(code, "station-$code"))
        })
        vm.selectPrefecture(0)
        runCurrent()
        vm.selectPrefecture(1)
        assertTrue(vm.state.value.stations.isEmpty())
        advanceUntilIdle()
        assertEquals(1, vm.state.value.prefectureIndex)
        assertEquals(41, vm.state.value.stations.single().id)
    }
}
