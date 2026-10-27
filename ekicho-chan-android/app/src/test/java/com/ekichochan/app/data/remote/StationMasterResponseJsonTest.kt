package com.ekichochan.app.data.remote

import com.ekichochan.app.data.model.StationMasterResponse
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class StationMasterResponseJsonTest {
    // 実サーバーから取得したJSONではなく、Java DTOとServiceを基に作ったテスト用データ。
    private val responseJson = """
        {
            "id": 1,
            "stationCd": "00000001",
            "stationName": "学習用の駅",
            "stationEnglishName": "Sample Station",
            "latitude": null,
            "longitude": null,
            "prefectureCd": 0,
            "locationName": "学習用の地域",
            "appearanceRateCd": "1",
            "stationInformationText": "JSON変換テスト用です。",
            "imageData": "c2FtcGxl",
            "imageType": null,
            "isObtained": false
        }
    """.trimIndent()

    @Test
    fun decodesResponseIncludingNullsAndBooleanField() {
        val response = apiJson.decodeFromString<StationMasterResponse>(responseJson)

        assertEquals(1, response.id)
        assertEquals("00000001", response.stationCd)
        assertEquals("学習用の駅", response.stationName)
        assertEquals("Sample Station", response.stationEnglishName)
        assertNull(response.latitude)
        assertNull(response.longitude)
        assertEquals(0, response.prefectureCd)
        assertEquals("学習用の地域", response.locationName)
        assertEquals("1", response.appearanceRateCd)
        assertEquals("JSON変換テスト用です。", response.stationInformationText)
        assertEquals("c2FtcGxl", response.imageData)
        assertNull(response.imageType)
        assertEquals(false, response.isObtained)
    }

    @Test
    fun ignoresAdditionalServerFields() {
        val extendedJson = responseJson.replace(
            "\"id\": 1,",
            "\"id\": 1, \"futureField\": \"unused\","
        )

        val response = apiJson.decodeFromString<StationMasterResponse>(extendedJson)

        assertEquals("学習用の駅", response.stationName)
    }

    @Test
    fun rejectsMissingRequiredId() {
        val incompleteJson = responseJson.replace("\"id\": 1,", "")

        assertThrows(SerializationException::class.java) {
            apiJson.decodeFromString<StationMasterResponse>(incompleteJson)
        }
    }

    @Test
    fun createsApiDefinitionWithoutSendingRequest() {
        // 予約済みの無効ドメイン。クライアント生成だけで、APIメソッドは呼び出さない。
        val api = createStationApi(baseUrl = "https://example.invalid/")

        assertNotNull(api)
    }
}
