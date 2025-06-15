package org.emu.dev

import java.io.File


//fun toHexString(vv : Array<Byte>) : String {
//    return vv.joinToString("") {
//        java.lang.String.format("%02x", it)
//    }
//}


@OptIn(ExperimentalUnsignedTypes::class)
class Memory {
    val ram = UByteArray(4096) { 0.toUByte() }
    var programCounter : UShort = 0u
    var indexRegister : UShort = 0u
    val stack = ArrayDeque<UShort>()
    lateinit var romPath : String

    fun loadRom() {
        val rom = File(romPath).readBytes().toUByteArray()
        rom.indices.forEach { index -> ram[0x200 + index] = rom[index] }
        programCounter = 0x200u
    }

    fun cleanRam() {
        ram.fill(0u)
    }

    fun logRam() {
        ram.forEach { it -> print(" ${it.toInt()} ") }
    }

    fun skipInstruction() {
        programCounter = (programCounter + 2u).toUShort()
    }

    fun blockingInstruction() {
        programCounter = (programCounter - 2u).toUShort()
    }

    fun addressOfCharacter(character : Int) : UShort {
        return (0x50 + 5 * character).toUShort()
    }

    fun loadFont(){
        val font = arrayOf(0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x40, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80)  // F)

        var indexing = 0
        for (i in 0x050..0x09F) {
            ram[i] = font[indexing].toUByte()
            indexing++
        }
    }

    fun onStart() {
        Display.clearScreen()
        cleanRam()
        loadFont()
        loadRom()
    }

    init {
        loadFont()
    }
}

