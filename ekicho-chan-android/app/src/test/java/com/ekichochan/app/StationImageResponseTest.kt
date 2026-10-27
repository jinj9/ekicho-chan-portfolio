package com.ekichochan.app

import com.ekichochan.app.data.model.StationImageResponse
import com.ekichochan.app.data.remote.apiJson
import org.junit.Assert.*
import org.junit.Test

class StationImageResponseTest {
    @Test fun normalImageIsReadAndSpecialImageIgnored() {
        val value = apiJson.decodeFromString<StationImageResponse>(
            """{"id":1,"stationName":"博多","imageData":"YWJj","imageType":"png","imageDataSP":"unused"}"""
        )
        assertEquals(1, value.id)
        assertEquals("YWJj", value.imageData)
    }
    @Test fun absentOrNullImageCanBeHandled() {
        assertNull(apiJson.decodeFromString<StationImageResponse>("""{"id":1}""").imageData)
        assertNull(apiJson.decodeFromString<StationImageResponse>("""{"id":1,"imageData":null}""").imageData)
    }
}
