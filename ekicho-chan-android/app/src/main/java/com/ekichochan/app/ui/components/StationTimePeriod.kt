package com.ekichochan.app.ui.components

enum class StationTimePeriod { DAY, EVENING, NIGHT }

// Device-local time; independent of the server and location permission.
fun stationTimePeriod(hour: Int): StationTimePeriod {
    require(hour in 0..23)
    return when (hour) {
        in 6..15 -> StationTimePeriod.DAY
        in 16..18 -> StationTimePeriod.EVENING
        else -> StationTimePeriod.NIGHT
    }
}
