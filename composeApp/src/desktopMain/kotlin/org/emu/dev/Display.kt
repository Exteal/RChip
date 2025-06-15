package org.emu.dev

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


data class Pixel(var isOn : Boolean = false) {
    var state by mutableStateOf(isOn)
}

object Display {
    const val ROWS = 32
    const val COLS = 64

    val pixels : Array<Array<Pixel>> = Array(ROWS) {
        Array(COLS) {
            Pixel()
        }
    }

    fun clearScreen() {
        pixels.forEach { row -> row.forEach { px -> px.state = false }}
    }

    fun togglePixel(x: Int, y: Int): Boolean {
        val pixel = pixels[y][x]
        val previous = pixel.state
        pixel.state = !previous
        return previous // true if collision
    }

    val onColor = Color.Red
    val offColor = Color.Black
    val dpSize = 12.dp

}

