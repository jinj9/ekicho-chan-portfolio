package com.ekichochan.app.ui.gacha

import com.ekichochan.app.R

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ekichochan.app.ui.components.*
import com.ekichochan.app.ui.theme.EkichoChanTheme
import com.ekichochan.app.data.model.StationMasterResponse

@Composable
fun GachaScreen(
    stationName: String?,
    stationInformationText: String?,
    onToggleStationName: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var resultOpen by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.gacha_title), style = MaterialTheme.typography.headlineSmall)
        DemoNotice()
        Spacer(Modifier.height(32.dp))
        GoldGetButton(onClick = { onToggleStationName(); resultOpen = true })
        Text(stringResource(R.string.web_gacha_help), style = MaterialTheme.typography.bodyLarge)
        OutlinedButton(onClick = onBack) { Text(stringResource(R.string.back_to_home)) }
    }
    if (resultOpen) {
        SampleResultDialog(
            stationName = stationName ?: stringResource(R.string.station_name_unavailable),
            description = stationInformationText ?: stringResource(R.string.station_information_unavailable),
            onDismiss = { resultOpen = false },
            onAgain = onToggleStationName
        )
    }
}

@Composable
fun LiveGachaScreen(state: LiveGachaState, onDraw: () -> Unit, onBack: () -> Unit,
    modifier: Modifier = Modifier) {
    var resultOpen by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(state) { if (state is LiveGachaState.Success) resultOpen = true }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.gacha_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(32.dp))
        GoldGetButton(onClick = onDraw, enabled = state != LiveGachaState.Loading)
        when (state) {
            LiveGachaState.Loading -> CircularProgressIndicator()
            LiveGachaState.Error -> {
                Text("ガチャを実行できませんでした。サーバーとの接続を確認してください。",
                    color = MaterialTheme.colorScheme.error)
                OutlinedButton(onClick = onDraw, enabled = state != LiveGachaState.Loading) { Text("再試行") }
            }
            else -> Text("GETすると駅がコレクションへ保存されます。")
        }
        OutlinedButton(onClick = onBack) { Text(stringResource(R.string.back_to_home)) }
    }
    val station = (state as? LiveGachaState.Success)?.station
    if (resultOpen && station != null) LiveGachaResultDialog(station,
        onDismiss = { resultOpen = false }, onAgain = onDraw)
}

@Composable
private fun LiveGachaResultDialog(station: StationMasterResponse, onDismiss: () -> Unit,
    onAgain: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) {
            Column(Modifier.heightIn(max = 620.dp).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("GET結果", style = MaterialTheme.typography.titleLarge)
                Text(if (station.alreadyObtained == true) "再取得しました" else "新しい駅を獲得しました",
                    color = MaterialTheme.colorScheme.primary)
                StationBase64Image(station.imageData, station.stationName.orEmpty())
                Text(station.stationName ?: stringResource(R.string.station_name_unavailable),
                    style = MaterialTheme.typography.titleLarge)
                Text(station.stationInformationText ?: stringResource(R.string.station_information_unavailable))
                Text("取得回数：${station.obtainedCount ?: 1}回")
                OutlinedButton(onClick = onAgain, modifier = Modifier.fillMaxWidth()) { Text("もう一度GETする") }
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.web_close)) }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun GachaPreview() {
    EkichoChanTheme(dynamicColor = false) {
        SkyBackground { GachaScreen("学習用の駅", "表示例", {}, {}) }
    }
}
