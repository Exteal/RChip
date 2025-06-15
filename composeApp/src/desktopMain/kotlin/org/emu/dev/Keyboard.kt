package org.emu.dev

import androidx.compose.runtime.mutableStateListOf

class Keyboard {
    val keys = mutableStateListOf<Boolean>()

    init {
        keys.addAll(Array(16) {false})
    }

    fun press(key: Int) {
        if (key in 0..0xF) keys[key] = true
    }

    fun release(key: Int) {
        if (key in 0..0xF) keys[key] = false
    }

    fun isPressed(key: Int): Boolean {
        return key in 0..0xF && keys[key]
    }

    fun reset() {
        keys.fill(false)
    }
}