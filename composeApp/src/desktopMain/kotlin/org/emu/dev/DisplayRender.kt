package org.emu.dev

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.emu.dev.Display.dpSize
import org.emu.dev.Display.offColor

@Composable
fun DisplayPixels() {
    val pxSize = with(LocalDensity.current) { dpSize.toPx() }

    Canvas(modifier = Modifier.background(offColor).padding(5.dp)
        .size(width = dpSize * Display.COLS, height = dpSize * Display.ROWS)) {

        for (rowIdx in Display.pixels.indices) {
            for (pixelIdx in Display.pixels[rowIdx].indices) {
                if (Display.pixels[rowIdx][pixelIdx].state) {
                    drawRect(
                        color = Display.onColor,
                        topLeft = Offset(pixelIdx * pxSize, rowIdx * pxSize),
                        size = Size(pxSize, pxSize),

                    )
                }

            }

        }
    }
}