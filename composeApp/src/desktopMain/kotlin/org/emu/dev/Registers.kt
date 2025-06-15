package org.emu.dev

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


data class Register(var registerValue : MutableState<UByte>) {
    var value by registerValue
}

class Registers() {
    val registersMap = mutableStateMapOf<String, Register>()

    fun clearRegisters() {
        registersMap.values.forEach {it.value = 0u}
    }

    init {

        for (i in 0..9) {
            registersMap.put("v$i", Register(mutableStateOf(0.toUByte())))
        }

        for (j in 'a'..'f') {
            registersMap.put("v$j", Register(mutableStateOf(0.toUByte())))
        }
    }
}