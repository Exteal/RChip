package org.emu.dev

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer

@Composable
fun DisplayRegisters(chip8: Chip8) {
    val textMeasurer = rememberTextMeasurer()
    val offset = 100f

    Canvas(modifier = Modifier.background(Color.LightGray)) {

        chip8.registers.registersMap.entries.forEachIndexed {
            idx, (name, register) ->
                drawText(
                    text = "$name : ${register.value}",
                    textMeasurer = textMeasurer,
                    topLeft = Offset(offset  * (idx % 4), offset * (idx / 4)),
                    style = TextStyle(fontSize = 30.toSp(), color = Color.Black, background = Color.Green.copy(alpha = 0.4f)),
                    size = Size(200f, 200f)
                )
        }
    }
}