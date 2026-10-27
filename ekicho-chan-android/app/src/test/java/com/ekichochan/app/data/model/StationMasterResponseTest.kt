package com.ekichochan.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Test

class StationMasterResponseTest {
    @Test
    fun sameValuesAreEqual() {
        val first = createSampleResponse()
        val second = createSampleResponse()

        // 別のインスタンスでも、data classはプロパティの値で比較できる。
        assertNotSame(first, second)
        assertEquals(first, second)
        assertEquals("学習用の駅", first.stationName)
    }

    @Test
    fun copyChangesOnlyTheNewInstance() {
        val original = createSampleResponse()
        val renamed = original.copy(stationName = "変更後の駅")

        assertNotSame(original, renamed)
        assertEquals("学習用の駅", original.stationName)
        assertEquals("変更後の駅", renamed.stationName)
        assertEquals(original.id, renamed.id)
        assertEquals(original.stationCd, renamed.stationCd)
    }

    @Test
    fun referenceFieldsCanRepresentNull() {
        val response = StationMasterResponse(
            id = 1,
            stationCd = null,
            stationName = null,
            stationEnglishName = null,
            latitude = null,
            longitude = null,
            prefectureCd = 0,
            locationName = null,
            appearanceRateCd = null,
            stationInformationText = null,
            imageData = null,
            imageType = null,
            isObtained = null
        )

        assertNull(response.stationName)
        assertNull(response.latitude)
        assertNull(response.longitude)
        assertNull(response.imageType)
        assertNull(response.isObtained)
    }

    // 実際のAPIから取得した値ではなく、単体テストだけで使う架空のデータ。
    private fun createSampleResponse(): StationMasterResponse {
        return StationMasterResponse(
            id = 1,
            stationCd = "00000001",
            stationName = "学習用の駅",
            stationEnglishName = "Sample Station",
            latitude = null,
            longitude = null,
            prefectureCd = 0,
            locationName = "学習用の地域",
            appearanceRateCd = "1",
            stationInformationText = "単体テスト用データです。",
            imageData = null,
            imageType = null,
            isObtained = false
        )
    }
}
