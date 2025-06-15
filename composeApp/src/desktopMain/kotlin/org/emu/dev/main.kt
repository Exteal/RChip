package org.emu.dev

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material3.SuggestionChip
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File


fun Int.hexToChar(): Char {
    return when(this) {
        0x0 -> '0'
        0x1 -> '1'
        0x2 -> '2'
        0x3 -> '3'
        0x4 -> '4'
        0x5 -> '5'
        0x6 -> '6'
        0x7 -> '7'
        0x8 -> '8'
        0x9 -> '9'
        0xA -> 'a'
        0xB -> 'b'
        0xC -> 'c'
        0xD -> 'd'
        0xE -> 'e'
        0xF -> 'f'
        else -> throw IllegalArgumentException("Illegal hexadecimal character : $this")
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.toHexString() = joinToString("") { it.toString(16).padStart(2, '0') }

@OptIn(ExperimentalUnsignedTypes::class)
fun main() = application {
    val chip8 : Chip8 = remember { Chip8() }
    val ttr = rememberWindowState()
    var keyboardMode by remember { mutableStateOf(true) }
    val romList = File("roms/").walkTopDown().filter { it.isFile }.map { it.path }.toList()
    val (selectedOption, onOptionSelected) = remember { mutableStateOf("") }

    ttr.size= DpSize(1200.dp, 800.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "C8_Chip",
        state = ttr,
        onKeyEvent = {
            val keyboardKey = it.key
            val chip8Key = chip8.keyMap[keyboardKey]

            if (chip8Key != null) {
                if (it.type == KeyEventType.KeyDown) {
                    chip8.keyboard.press(chip8Key)
                    //DisplayPixels()
                    true
                }

                if (it.type == KeyEventType.KeyUp) {
                    chip8.keyboard.release(chip8Key)
                    true
                }

                false
            }

            else {
                false
            }
        }
    ) {

        LaunchedEffect(Unit) {
            chip8.start()
        }

        MaterialTheme {
            Row {
                Column {
                    DisplayPixels()
                    Row {
                        DisplayKeyboard(chip8)
                        DisplayRegisters(chip8)
                    }

                }
                
                Column {

                    Button(
                        onClick = { if (chip8.running) chip8.stop() else chip8.start() },
                        content = { Text("Pause") }
                    )

                    Row {

                        Column {

                            SuggestionChip(
                                label = { Text(if (keyboardMode) "AZERTY mode" else "QWERTY mode") },
                                onClick = {
                                    keyboardMode = !keyboardMode
                                    val newMap = if (keyboardMode) keyMapAZERTY else keyMapQWERTY
                                    chip8.modifyKeyMap(newMap)
                                }
                            )

                            Button(
                                onClick = {
                                    chip8.memory.onStart()
                                    chip8.registers.clearRegisters()
                                },
                                content = { Text("Start ROM") }
                            )
                        }

                        Column(Modifier.selectableGroup()) {
                            romList.forEach { text ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .selectable(
                                            selected = (text == selectedOption),
                                            onClick = {
                                                onOptionSelected(text)
                                                chip8.memory.romPath = text
                                            },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (text == selectedOption),
                                        onClick = null
                                    )
                                    Text(
                                        "$text"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
