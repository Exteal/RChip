package org.emu.dev

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DisplayKeyboard(chip8: Chip8) {
    val textMeasurer = rememberTextMeasurer()
    val keySize = 30.sp

    Canvas(modifier = Modifier.background(color = Color.White).size(300.dp)) {

        for (t in 0x0..0xF) {
            drawText(
                text = t.hexToChar().toString(),
                textMeasurer = textMeasurer,
                topLeft = Offset(keySize.toPx() * (t % 0x4), keySize.toPx() * (t / 0x4)),
                style = TextStyle(fontSize = keySize, color = Color.Black,
                                background = if (chip8.keyboard.isPressed(t)) Color.Cyan.copy(alpha = 0.7f) else Color.LightGray.copy(alpha = 0.7f)),
                size = Size((1.3 * keySize.toPx()).toFloat(), (1.3 * keySize.toPx()).toFloat())
            )
        }
    }
}