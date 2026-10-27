package com.ekichochan.app

import com.ekichochan.app.ui.components.StationTimePeriod
import com.ekichochan.app.ui.components.stationTimePeriod
import org.junit.Assert.assertEquals
import org.junit.Test

class StationTimePeriodTest {
    @Test fun allHoursUseExpectedScene() {
        for (hour in 0..23) {
            val expected = when (hour) {
                in 6..15 -> StationTimePeriod.DAY
                in 16..18 -> StationTimePeriod.EVENING
                else -> StationTimePeriod.NIGHT
            }
            assertEquals("hour=$hour", expected, stationTimePeriod(hour))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeHourRejected() { stationTimePeriod(-1) }

    @Test(expected = IllegalArgumentException::class)
    fun hour24Rejected() { stationTimePeriod(24) }
}
