package org.emu.dev

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*

class Timer {
    var value by mutableStateOf(0)

    fun set(newVal: Int) {
        value = newVal.coerceIn(0..255)
    }

    fun tick() {
        if (value > 0) value--
    }
}