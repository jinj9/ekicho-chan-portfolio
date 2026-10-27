package com.ekichochan.app.ui.location

import com.ekichochan.app.R

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ekichochan.app.data.repository.StationImageRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive

private sealed interface ImageState {
    data object Loading : ImageState
    data object Empty : ImageState
    data object Failed : ImageState
    data class Ready(val bitmap: Bitmap) : ImageState
}

@Composable
fun StationImageDialog(stationId: Int, stationName: String, onDismiss: () -> Unit) {
    val repository = remember { StationImageRepository() }
    var attempt by remember(stationId) { mutableIntStateOf(0) }
    // A new station/retry gets a fresh state immediately. Closing cancels this producer.
    key(stationId, attempt) {
        val state by produceState<ImageState>(ImageState.Loading) {
            try {
                val bitmap = repository.load(stationId)
                coroutineContext.ensureActive()
                value = if (bitmap == null) ImageState.Empty else ImageState.Ready(bitmap)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                value = ImageState.Failed
            }
        }
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.large) {
                Column(
                    Modifier.heightIn(max = 620.dp).verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(stringResource(R.string.station_image_title), style = MaterialTheme.typography.titleLarge)
                    Text(stationName, style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.location_live_notice), style = MaterialTheme.typography.bodySmall)
                    when (val current = state) {
                        ImageState.Loading -> {
                            CircularProgressIndicator()
                            Text(stringResource(R.string.station_image_loading))
                        }
                        ImageState.Empty -> Text(stringResource(R.string.station_image_empty))
                        ImageState.Failed -> Text(stringResource(R.string.station_image_error))
                        is ImageState.Ready -> Image(
                            bitmap = current.bitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.station_image_description, stationName),
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                            contentScale = ContentScale.Fit
                        )
                    }
                    if (state == ImageState.Failed || state == ImageState.Empty) {
                        OutlinedButton(onClick = { attempt++ }) { Text(stringResource(R.string.location_retry)) }
                    }
                    StationInfoSection(stationId)
                    Text(stringResource(R.string.web_location_result))
                    Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.web_close))
                    }
                }
            }
        }
    }
}
