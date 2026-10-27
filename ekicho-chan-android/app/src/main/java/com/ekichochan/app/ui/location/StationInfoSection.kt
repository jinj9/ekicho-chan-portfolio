package com.ekichochan.app.ui.location

import com.ekichochan.app.R

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ekichochan.app.data.repository.StationInfoRepository
import com.ekichochan.app.data.repository.StationInfoState
import kotlinx.coroutines.ensureActive

@Composable
fun StationInfoSection(stationId: Int) {
    val repository = remember { StationInfoRepository() }
    var attempt by remember(stationId) { mutableIntStateOf(0) }
    key(stationId, attempt) {
        val state by produceState<StationInfoState>(StationInfoState.Loading) {
            val result = repository.load(stationId)
            coroutineContext.ensureActive()
            value = result
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.station_info_title), style = MaterialTheme.typography.titleMedium)
            when (val current = state) {
                StationInfoState.Loading -> {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Text(stringResource(R.string.station_info_loading))
                }
                StationInfoState.Failed -> {
                    Text(stringResource(R.string.station_info_error))
                    OutlinedButton(onClick = { attempt++ }) { Text(stringResource(R.string.station_info_retry)) }
                }
                is StationInfoState.Ready -> {
                    val missing = stringResource(R.string.station_info_missing)
                    Text(stringResource(R.string.station_info_prefecture, current.info.prefecture ?: missing))
                    Text(stringResource(R.string.station_info_code, current.info.code ?: missing))
                    Text(current.info.description ?: stringResource(R.string.station_info_no_description))
                }
            }
        }
    }
}
