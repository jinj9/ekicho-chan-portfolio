package com.ekichochan.app.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.ekichochan.app.data.remote.createStationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StationImageRepository {
    private val api by lazy { createStationApi() }

    suspend fun load(id: Int): Bitmap? = withContext(Dispatchers.IO) {
        val response = api.getStationImage(id)
        check(response.id == id) { "Station ID mismatch" }
        val encoded = response.imageData
        if (encoded.isNullOrBlank()) return@withContext null
        require(encoded.length <= 16 * 1024 * 1024) { "Image too large" }
        val bytes = Base64.decode(encoded, Base64.DEFAULT)
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        require(bounds.outWidth > 0 && bounds.outHeight > 0) { "Invalid image" }
        val options = BitmapFactory.Options().apply { inSampleSize = 1 }
        while (bounds.outWidth / options.inSampleSize > 1600 || bounds.outHeight / options.inSampleSize > 1600) {
            options.inSampleSize *= 2
        }
        requireNotNull(BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)) { "Cannot decode image" }
    }
}
