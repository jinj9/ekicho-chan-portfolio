package com.ekichochan.app.ui.location

import com.ekichochan.app.R

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.ui.components.GoldGetButton
import com.ekichochan.app.ui.components.StationBase64Image
import kotlinx.coroutines.launch

private val prefectures = listOf("福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県")
private enum class PermissionGuidance { RETRY, SETTINGS }

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

@Composable
fun LocationScreen(
    state: LocationStationState,
    onPrefectureSelected: (Int) -> Unit,
    onRetry: () -> Unit,
    onLocating: () -> Unit,
    onLocationReady: (Int, Double, Double) -> Unit,
    onLocationUnavailable: (String) -> Unit,
    onResultDismissed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val prefecture = state.prefectureIndex
    var stationId by rememberSaveable { mutableIntStateOf(-1) }
    var permissionGuidance by rememberSaveable { mutableStateOf<PermissionGuidance?>(null) }
    var waitingForSettings by rememberSaveable { mutableStateOf(false) }
    val stations = state.stations
    val station = stations.find { it.id == stationId }

    fun locate() {
        if (station == null) {
            onLocationUnavailable("駅を選択してください。")
            return
        }
        onLocating()
        scope.launch {
            val location = getCurrentLocation(context)
            if (location == null) onLocationUnavailable("現在地を取得できません。端末の位置情報を有効にしてください。")
            else if (!isLocationAccuracyAcceptable(location.hasAccuracy(), location.accuracy)) {
                val accuracyText = if (location.hasAccuracy()) "約${location.accuracy.toInt()}m" else "不明"
                onLocationUnavailable(
                    "位置情報の精度が不足しています（誤差：$accuracyText）。" +
                        "屋外など測位しやすい場所で、誤差50m以内になってからもう一度お試しください。"
                )
            } else onLocationReady(station.id, location.latitude, location.longitude)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            permissionGuidance = null
            locate()
        } else {
            val activity = context.findActivity()
            val canAskAgain = activity != null && listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).any { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
            permissionGuidance = if (canAskAgain) PermissionGuidance.RETRY else PermissionGuidance.SETTINGS
        }
    }

    DisposableEffect(lifecycleOwner, waitingForSettings) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && waitingForSettings) {
                if (context.hasLocationPermission()) permissionGuidance = null
                waitingForSettings = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    fun requestLocation() {
        if (station == null) {
            onLocationUnavailable("駅を選択してください。")
            return
        }
        if (context.hasLocationPermission()) locate() else permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.web_location_title), style = MaterialTheme.typography.headlineSmall)
        Text("現在地を使って駅をGETします。位置情報は駅との距離判定にのみ使用し、測位誤差50m以内の場合に送信します。",
            style = MaterialTheme.typography.bodySmall)
        LocationSelect(
            label = stringResource(R.string.web_prefecture),
            value = prefectures.getOrNull(prefecture) ?: stringResource(R.string.web_select),
            options = prefectures,
            onSelect = { stationId = -1; onResultDismissed(); onPrefectureSelected(it) }
        )
        LocationSelect(
            label = stringResource(R.string.web_station),
            value = station?.name ?: stringResource(R.string.web_select),
            options = stations.map { it.name },
            onSelect = { stationId = stations[it].id; onResultDismissed() }
        )
        when {
            state.loading -> { CircularProgressIndicator(); Text(stringResource(R.string.location_loading)) }
            state.failed -> {
                Text(stringResource(R.string.location_load_error))
                OutlinedButton(onClick = { stationId = -1; onResultDismissed(); onRetry() }) { Text(stringResource(R.string.location_retry)) }
            }
            prefecture >= 0 && stations.isEmpty() -> Text(stringResource(R.string.location_empty))
        }
        val getInProgress = state.obtainState == LocationObtainState.Locating ||
            state.obtainState == LocationObtainState.Loading
        GoldGetButton(onClick = ::requestLocation, enabled = !getInProgress)
        if (state.obtainState == LocationObtainState.Locating || state.obtainState == LocationObtainState.Loading) {
            CircularProgressIndicator()
            Text(if (state.obtainState == LocationObtainState.Locating) "現在地を取得しています…" else "駅との距離を確認しています…")
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.web_location_help_title), style = MaterialTheme.typography.titleMedium)
                Text("都道府県と駅を選択し、駅から100m以内で「GETする」を押してください。初回取得日時と最終取得日時がコレクションに保存されます。")
            }
        }
    }

    when (val obtain = state.obtainState) {
        is LocationObtainState.Success -> LocationResultDialog(obtain.station, onResultDismissed)
        is LocationObtainState.Error -> AlertDialog(
            onDismissRequest = onResultDismissed,
            title = { Text("位置情報GET") }, text = { Text(obtain.message) },
            confirmButton = { TextButton(onClick = onResultDismissed) { Text(stringResource(R.string.web_close)) } }
        )
        else -> Unit
    }

    permissionGuidance?.let { guidance ->
        val settingsRequired = guidance == PermissionGuidance.SETTINGS
        AlertDialog(
            onDismissRequest = { permissionGuidance = null },
            title = { Text("位置情報の権限") },
            text = {
                Text(if (settingsRequired)
                    "位置情報GETを利用するには、Androidのアプリ設定で位置情報を許可してください。他の機能は許可しなくても利用できます。"
                else "選択した駅との距離を確認するために位置情報を使用します。位置情報は距離判定にのみ使用します。")
            },
            confirmButton = {
                TextButton(onClick = {
                    if (settingsRequired) {
                        waitingForSettings = true
                        context.startActivity(Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        ))
                    } else permissionLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                }) { Text(if (settingsRequired) "アプリ設定を開く" else "もう一度許可する") }
            },
            dismissButton = {
                TextButton(onClick = { permissionGuidance = null }) { Text("今は許可しない") }
            }
        )
    }
}

@Composable
private fun LocationResultDialog(station: StationMasterResponse, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${station.stationName.orEmpty()}をGETしました！") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                StationBase64Image(station.imageData, station.stationName.orEmpty(), Modifier.fillMaxWidth())
                Text(if (station.alreadyObtained == true) "取得回数：${station.obtainedCount ?: 1}回" else "コレクションに追加しました。")
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.web_close)) } }
    )
}

@Composable
private fun LocationSelect(label: String, value: String, options: List<String>, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Box {
            OutlinedButton(onClick = { expanded = true }, enabled = options.isNotEmpty(), modifier = Modifier.fillMaxWidth()) { Text(value) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEachIndexed { index, text -> DropdownMenuItem(
                    text = { Text(text) }, onClick = { onSelect(index); expanded = false }
                ) }
            }
        }
    }
}
