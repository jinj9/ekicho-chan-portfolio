package com.ekichochan.app.ui.collection

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ekichochan.app.R
import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.sample.SampleCollectionData
import com.ekichochan.app.ui.theme.EkichoChanTheme
import com.ekichochan.app.ui.components.SampleCharacter
import com.ekichochan.app.ui.components.StationBase64Image

@Composable
fun StationDetailScreen(
    station: StationMasterResponse?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.station_detail_title), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.station_detail_sample_notice), style = MaterialTheme.typography.bodyLarge)
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.back_to_collection))
        }
        if (station == null) {
            Text(stringResource(R.string.station_detail_not_found), style = MaterialTheme.typography.bodyLarge)
        } else {
            // nullでない分岐では、Kotlinがstationを非nullとして扱う。
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        station.stationName ?: stringResource(R.string.station_name_unavailable),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    SampleCharacter()
                    DetailField(
                        label = stringResource(R.string.station_location_label),
                        value = station.locationName ?: stringResource(R.string.station_location_unavailable)
                    )
                    DetailField(
                        label = stringResource(R.string.gacha_station_information_label),
                        value = station.stationInformationText ?: stringResource(R.string.station_information_unavailable)
                    )
                    Text(stringResource(R.string.web_detail_unavailable), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun LiveStationDetailScreen(state: LiveStationDetailState, onBack: () -> Unit,
    modifier: Modifier = Modifier) {
    when (state) {
        LiveStationDetailState.Loading -> Column(modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            androidx.compose.material3.CircularProgressIndicator()
            Text("獲得情報を読み込み中…")
        }
        LiveStationDetailState.Error -> Column(modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(stringResource(R.string.station_detail_not_found))
            OutlinedButton(onClick = onBack) { Text(stringResource(R.string.back_to_collection)) }
        }
        is LiveStationDetailState.Success -> {
            val station = state.station
            Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(stringResource(R.string.station_detail_title), style = MaterialTheme.typography.headlineMedium)
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.back_to_collection))
                }
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(station.stationName ?: stringResource(R.string.station_name_unavailable),
                            style = MaterialTheme.typography.headlineSmall)
                        StationBase64Image(station.imageData, station.stationName.orEmpty())
                        DetailField(stringResource(R.string.station_location_label),
                            station.locationName ?: stringResource(R.string.station_location_unavailable))
                        DetailField(stringResource(R.string.gacha_station_information_label),
                            station.stationInformationText ?: stringResource(R.string.station_information_unavailable))
                        DetailField("初回取得日時", formatObtainedAt(station.firstObtainedAt))
                        DetailField("初回取得方法", obtainMethodLabel(station.firstObtainedMethod))
                        DetailField("最終取得日時", formatObtainedAt(station.lastObtainedAt))
                        DetailField("最終取得方法", obtainMethodLabel(station.lastObtainedMethod))
                        DetailField("取得回数", "${station.obtainedCount ?: 1}回")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.titleSmall)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(name = "Light", showBackground = true, widthDp = 360, heightDp = 800)
@Preview(name = "Dark", showBackground = true, widthDp = 360, heightDp = 800, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large text", showBackground = true, widthDp = 320, heightDp = 640, fontScale = 1.5f)
@Composable
private fun StationDetailPreview() {
    EkichoChanTheme(dynamicColor = false) {
        Surface { StationDetailScreen(station = SampleCollectionData.findStation(102), onBack = {}) }
    }
}

@Preview(name = "Not found", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun StationNotFoundPreview() {
    EkichoChanTheme(dynamicColor = false) {
        Surface { StationDetailScreen(station = null, onBack = {}) }
    }
}
