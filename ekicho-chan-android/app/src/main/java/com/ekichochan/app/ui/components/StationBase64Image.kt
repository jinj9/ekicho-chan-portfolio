package com.ekichochan.app.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

@Composable
fun StationBase64Image(data: String?, description: String, modifier: Modifier = Modifier) {
    val bitmap = remember(data) {
        runCatching {
            val bytes = Base64.decode(data, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }.getOrNull()
    }
    if (bitmap != null) Image(bitmap, description,
        modifier.fillMaxWidth().aspectRatio(bitmap.width.toFloat() / bitmap.height.coerceAtLeast(1)),
        contentScale = ContentScale.Fit)
    else SampleCharacter(modifier)
}
