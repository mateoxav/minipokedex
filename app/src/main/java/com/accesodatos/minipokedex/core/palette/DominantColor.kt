package com.accesodatos.minipokedex.core.palette

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun rememberDominantColor(imageUrl: String?, fallback: Color = Color.Unspecified): Color {
    val context = LocalContext.current

    return produceState(initialValue = fallback, key1 = imageUrl) {
        if (imageUrl.isNullOrBlank()) return@produceState

        runCatching {
            val loader = ImageLoader(context)
            val req = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false) // Necesario para Palette
                .build()

            val result = loader.execute(req)
            if (result is SuccessResult) {
                // En Coil 2.x usamos result.drawable
                val bmp = (result.drawable as? BitmapDrawable)?.bitmap
                    ?: result.drawable.toBitmap()

                val color = withContext(Dispatchers.Default) {
                    paletteDominantColor(bmp)
                }
                value = color ?: fallback
            }
        }
    }.value
}

private fun paletteDominantColor(bitmap: Bitmap): Color? {
    val palette = Palette.from(bitmap).generate()
    val argb = palette.getDominantColor(0)
    return if (argb == 0) null else Color(argb)
}