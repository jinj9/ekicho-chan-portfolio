package com.ekichochan.app.ui.components

import com.ekichochan.app.R

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.ekichochan.app.ui.theme.EkichoChanTheme
import java.util.Calendar
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private fun currentPeriod() = stationTimePeriod(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))

@Composable
private fun rememberStationPeriod(): StationTimePeriod {
    val owner = LocalLifecycleOwner.current
    val period by produceState(currentPeriod(), owner) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                value = currentPeriod()
                delay(1_000L)
            }
        }
    }
    return period
}

@Composable
fun StationSceneBackground(
    modifier: Modifier = Modifier,
    period: StationTimePeriod = rememberStationPeriod(),
    content: @Composable () -> Unit
) {
    val image = ImageBitmap.imageResource(R.drawable.station_time_scenes)
    val surface = MaterialTheme.colorScheme.surface
    Box(modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            // Bounds exclude the white gutters in the approved 1626 x 967 concept sheet.
            val left = when (period) {
                StationTimePeriod.DAY -> 10
                StationTimePeriod.EVENING -> 550
                StationTimePeriod.NIGHT -> 1089
            }
            val panelWidth = 526f * image.width / 1626f
            val panelHeight = 947f * image.height / 967f
            val scale = maxOf(size.width / panelWidth, size.height / panelHeight)
            val cropWidth = (size.width / scale).roundToInt().coerceAtLeast(1)
            val cropHeight = (size.height / scale).roundToInt().coerceAtLeast(1)
            // Bottom alignment keeps the station visible on wide screens as well.
            drawImage(
                image = image,
                srcOffset = IntOffset(
                    (left * image.width / 1626f + (panelWidth - cropWidth) / 2).roundToInt(),
                    (10 * image.height / 967f + panelHeight - cropHeight).roundToInt()
                ),
                srcSize = IntSize(cropWidth, cropHeight),
                dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
            )
        }
        // Keep existing text and form colors legible in both system themes.
        Box(Modifier.fillMaxSize().background(surface.copy(alpha = 0.82f)))
        content()
    }
}

@Preview(name = "Day", widthDp = 360, heightDp = 640)
@Composable
private fun DayBackgroundPreview() {
    EkichoChanTheme(darkTheme = false) { StationSceneBackground(period = StationTimePeriod.DAY) {} }
}

@Preview(name = "Evening", widthDp = 360, heightDp = 640)
@Composable
private fun EveningBackgroundPreview() {
    EkichoChanTheme(darkTheme = false) { StationSceneBackground(period = StationTimePeriod.EVENING) {} }
}

@Preview(name = "Night", widthDp = 360, heightDp = 640)
@Composable
private fun NightBackgroundPreview() {
    EkichoChanTheme(darkTheme = true) { StationSceneBackground(period = StationTimePeriod.NIGHT) {} }
}
