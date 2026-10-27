package com.ekichochan.app

import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.remote.apiJson
import com.ekichochan.app.data.repository.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class StationInfoRepositoryTest {
    private val response = apiJson.decodeFromString<StationMasterResponse>(
        """{"id":1,"stationCd":"1190101","stationName":"博多","stationEnglishName":"HAKATA","latitude":33.589933,"longitude":130.4207381,"prefectureCd":40,"locationName":"福岡県","appearanceRateCd":"1","stationInformationText":"九州・福岡を代表するターミナル駅だよ。大きな駅ビルがあるよ。","imageData":null,"imageType":null,"isObtained":null}"""
    )

    @Test fun mapsServerResponse() = runTest {
        var requested = -1
        val repo = StationInfoRepository { requested = it; response }
        val result = repo.load(1) as StationInfoState.Ready
        assertEquals(1, requested)
        assertEquals("1190101", result.info.code)
        assertEquals("福岡県", result.info.prefecture)
        assertEquals(response.stationInformationText, result.info.description)
    }

    @Test fun nullAndBlankFieldsBecomeMissing() = runTest {
        val result = StationInfoRepository {
            response.copy(stationCd = null, locationName = " ", stationInformationText = "")
        }.load(1) as StationInfoState.Ready
        assertNull(result.info.code)
        assertNull(result.info.prefecture)
        assertNull(result.info.description)
    }

    @Test fun failedRequestCanBeRetried() = runTest {
        var fail = true
        val repo = StationInfoRepository { if (fail) error("offline") else response }
        assertEquals(StationInfoState.Failed, repo.load(1))
        fail = false
        assertTrue(repo.load(1) is StationInfoState.Ready)
    }

    @Test fun wrongStationIsRejected() = runTest {
        assertEquals(StationInfoState.Failed, StationInfoRepository { response }.load(2))
    }

    @Test fun cancellationIsNotAnErrorState() = runTest {
        val repo = StationInfoRepository { throw CancellationException("closed") }
        try {
            repo.load(1)
            fail("Cancellation must propagate")
        } catch (_: CancellationException) { }
    }
}
