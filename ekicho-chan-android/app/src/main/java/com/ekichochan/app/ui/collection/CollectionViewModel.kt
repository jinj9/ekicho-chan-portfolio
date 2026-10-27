package com.ekichochan.app.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.repository.CollectionRepository
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** 通信を伴わないUI練習専用。API接続時に実際の取得処理へ置き換える。 */
class CollectionViewModel(private val repository: CollectionRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<CollectionUiState>(CollectionUiState.Loading)
    val uiState: StateFlow<CollectionUiState> = _uiState.asStateFlow()
    private var loadingJob: Job? = null

    init {
        restoreSampleStations()
    }

    fun showNextSampleState() {
        when (val current = _uiState.value) {
            CollectionUiState.Loading -> return
            CollectionUiState.Error -> loadSample { emptyList() }
            is CollectionUiState.Success -> {
                if (current.stations.isEmpty()) {
                    restoreSampleStations()
                } else {
                    loadSample { throw IOException("学習用の取得失敗") }
                }
            }
        }
    }

    fun restoreSampleStations() {
        loadSample { repository.getStations() }
    }

    private fun loadSample(readStations: () -> List<StationMasterResponse>) {
        loadingJob?.cancel()
        _uiState.value = CollectionUiState.Loading
        loadingJob = viewModelScope.launch {
            try {
                // 表示確認用の待ち時間。スレッドは止めない。API導入時には除去する。
                delay(600L)
                // このRepositoryはメモリ内の軽い同期処理のみ。通信は追加しない。
                _uiState.value = CollectionUiState.Success(readStations().toList())
            } catch (cancelled: CancellationException) {
                // 画面終了や再読み込みによるキャンセルをエラー表示にしない。
                throw cancelled
            } catch (_: Exception) {
                _uiState.value = CollectionUiState.Error
            }
        }
    }
}
