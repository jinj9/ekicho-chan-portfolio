package com.ekichochan.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StationImageResponse(
    val id: Int,
    val imageData: String? = null,
    val imageType: String? = null
)
