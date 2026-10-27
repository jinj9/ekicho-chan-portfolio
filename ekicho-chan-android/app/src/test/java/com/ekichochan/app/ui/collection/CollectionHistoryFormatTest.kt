package com.ekichochan.app.ui.collection

import org.junit.Assert.assertEquals
import org.junit.Test

class CollectionHistoryFormatTest {
    @Test
    fun obtainMethodIsDisplayedInJapanese() {
        assertEquals("ガチャ", obtainMethodLabel("GACHA"))
        assertEquals("位置情報GET", obtainMethodLabel("LOCATION"))
        assertEquals("不明", obtainMethodLabel("UNKNOWN"))
    }

    @Test
    fun utcTimestampIsDisplayedInJapanTime() {
        assertEquals("2026/09/12 10:02:03", formatObtainedAt("2026-09-12T01:02:03Z"))
        assertEquals("2026/09/12 10:02:03", formatObtainedAt("2026-09-12T01:02:03.123456Z"))
    }
}
