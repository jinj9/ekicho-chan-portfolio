package com.ekichochan.app.ui.collection

import com.ekichochan.app.data.model.StationMasterResponse

sealed interface CollectionUiState {
    object Loading : CollectionUiState
    data class Success(val stations: List<StationMasterResponse>) : CollectionUiState
    object Error : CollectionUiState
}
