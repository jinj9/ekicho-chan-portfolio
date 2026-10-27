package com.ekichochan.app.ui.gacha

import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.repository.SampleStationRepository
import com.ekichochan.app.data.repository.StationRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotSame
import org.junit.Test

class GachaViewModelTest {
    @Test
    fun initialStationContainsLearningData() {
        // 単体テストではデータを確認するため直接生成する。画面側ではviewModel()を使う。
        val viewModel = GachaViewModel(repository = SampleStationRepository())
        val station = viewModel.station.value

        assertEquals("学習用の駅", station.stationName)
        assertEquals(
            "この駅はViewModelの役割を学ぶための架空のデータです。",
            station.stationInformationText
        )
        assertNull(station.imageData)
        assertNull(station.latitude)
        assertNull(station.longitude)
    }

    @Test
    fun toggleUpdatesStateWithoutChangingPreviousData() {
        val viewModel = GachaViewModel(repository = SampleStationRepository())
        val original = viewModel.station.value

        viewModel.toggleSampleStationName()

        val updated = viewModel.station.value
        assertEquals("学習用の駅", original.stationName)
        assertEquals("変更後の学習用の駅", updated.stationName)
        assertNotSame(original, updated)
        assertEquals(original.copy(stationName = "変更後の学習用の駅"), updated)
    }

    @Test
    fun togglingTwiceRestoresInitialValues() {
        val viewModel = GachaViewModel(repository = SampleStationRepository())
        val initial = viewModel.station.value

        viewModel.toggleSampleStationName()
        viewModel.toggleSampleStationName()

        assertEquals(initial, viewModel.station.value)
    }

    @Test
    fun newViewModelStartsWithInitialData() {
        val previous = GachaViewModel(repository = SampleStationRepository())
        previous.toggleSampleStationName()

        val fresh = GachaViewModel(repository = SampleStationRepository())

        assertEquals("変更後の学習用の駅", previous.station.value.stationName)
        assertEquals("学習用の駅", fresh.station.value.stationName)
    }

    @Test
    fun viewModelUsesDataFromInjectedRepository() {
        val expected = SampleStationRepository().getStation().copy(
            id = 99,
            stationName = "差し替えテスト用の駅",
            stationInformationText = null
        )
        val repository = FixedStationRepository(expected)
        val viewModel = GachaViewModel(repository = repository)

        assertEquals(expected, viewModel.station.value)

        viewModel.toggleSampleStationName()
        viewModel.toggleSampleStationName()

        // 固定の駅名ではなく、受け取った初期データへ戻れることも確認する。
        assertEquals(expected, viewModel.station.value)
    }

    private class FixedStationRepository(
        private val response: StationMasterResponse
    ) : StationRepository {
        override fun getStation(): StationMasterResponse {
            return response
        }
    }
}
