package com.ekichochan.app.ui.collection

import com.ekichochan.app.R

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ekichochan.app.data.sample.SampleCollectionData
import com.ekichochan.app.ui.components.*
import com.ekichochan.app.ui.theme.EkichoChanTheme
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun CollectionScreen(
    uiState: CollectionUiState,
    onBack: () -> Unit,
    onStationClick: (Int) -> Unit,
    onNextSampleState: () -> Unit,
    onRestoreSampleStations: () -> Unit,
    liveMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    var appliedQuery by rememberSaveable { mutableStateOf("") }
    var showResults by rememberSaveable { mutableStateOf(true) }
    var showDemoControls by rememberSaveable { mutableStateOf(false) }
    // 見出し・検索欄は全列を使用。カードは画面幅に応じて列数を変える。
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp), modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.web_collection_title), style = MaterialTheme.typography.headlineSmall)
                if (liveMode) Text("取得履歴は最終取得日時が新しい順に表示されます。",
                    style = MaterialTheme.typography.bodySmall)
                if (!liveMode) DemoNotice()
                OutlinedTextField(
                    value = query, onValueChange = { query = it },
                    label = { Text(stringResource(R.string.web_search_hint)) },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { appliedQuery = query; showResults = true; onRestoreSampleStations() },
                        enabled = uiState != CollectionUiState.Loading, modifier = Modifier.weight(1f)
                    ) { Text(stringResource(R.string.web_search)) }
                    OutlinedButton(onClick = { query = ""; appliedQuery = ""; showResults = false }, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.web_reset))
                    }
                }
                if (!liveMode) TextButton(onClick = { showDemoControls = !showDemoControls }) {
                    Text(stringResource(if (showDemoControls) R.string.web_hide_demo else R.string.web_show_demo))
                }
                if (showDemoControls) {
                    Text(stringResource(R.string.collection_state_demo_notice), style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(
                        onClick = { showResults = true; onNextSampleState() },
                        enabled = uiState != CollectionUiState.Loading
                    ) { Text(stringResource(R.string.collection_next_sample_state)) }
                }
            }
        }
        if (!showResults) {
            item(span = { GridItemSpan(maxLineSpan) }) { Text(stringResource(R.string.web_search_prompt)) }
        } else when (uiState) {
            CollectionUiState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator()
                    Text(if (liveMode) "コレクションを読み込み中…"
                        else stringResource(R.string.collection_loading_sample))
                }
            }
            CollectionUiState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(if (liveMode) "コレクションを取得できませんでした。サーバーとの接続を確認してください。"
                            else stringResource(R.string.collection_error_sample), color = MaterialTheme.colorScheme.error)
                        OutlinedButton(onClick = onRestoreSampleStations) { Text(stringResource(R.string.collection_restore_sample)) }
                    }
                }
            }
            is CollectionUiState.Success -> {
                val stations = filterCollection(uiState.stations, appliedQuery)
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(if (liveMode) "獲得駅：${stations.size}件"
                        else stringResource(R.string.collection_count, stations.size), style = MaterialTheme.typography.titleMedium)
                }
                if (stations.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(if (liveMode && appliedQuery.isBlank()) "まだ駅を獲得していません。ガチャで駅をGETしてみましょう。"
                            else stringResource(R.string.web_no_matches))
                    }
                }
                items(stations, key = { it.id }) { station ->
                    Card(onClick = { onStationClick(station.id) }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            StationBase64Image(station.imageData, station.stationName.orEmpty())
                            Text(station.stationName ?: stringResource(R.string.station_name_unavailable), style = MaterialTheme.typography.titleMedium)
                            if (liveMode) Text("取得回数：${station.obtainedCount ?: 1}回",
                                style = MaterialTheme.typography.bodySmall)
                            if (liveMode) Text("最終取得：${formatObtainedAt(station.lastObtainedAt)}",
                                style = MaterialTheme.typography.bodySmall)
                            if (liveMode) Text("取得方法：${obtainMethodLabel(station.lastObtainedMethod)}",
                                style = MaterialTheme.typography.bodySmall)
                            Text(stringResource(R.string.open_station_detail), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            TextButton(onClick = onBack) { Text(stringResource(R.string.back_to_home)) }
        }
    }
}

internal fun obtainMethodLabel(method: String?): String = when (method) {
    "GACHA" -> "ガチャ"
    "LOCATION" -> "位置情報GET"
    else -> "不明"
}

internal fun formatObtainedAt(value: String?): String {
    if (value == null) return "-"
    return runCatching {
        val normalized = when {
            Regex("\\.\\d{4,}Z$").containsMatchIn(value) ->
                value.replace(Regex("(\\.\\d{3})\\d*Z$"), "$1Z")
            value.endsWith("Z") && !value.contains('.') -> value.removeSuffix("Z") + ".000Z"
            else -> value
        }
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.JAPAN).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN).apply {
            timeZone = TimeZone.getTimeZone("Asia/Tokyo")
        }
        formatter.format(requireNotNull(parser.parse(normalized)))
    }.getOrElse { value }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun CollectionPreview() {
    EkichoChanTheme(dynamicColor = false) {
        SkyBackground {
            CollectionScreen(CollectionUiState.Success(SampleCollectionData.stations), {}, {}, {}, {})
        }
    }
}
