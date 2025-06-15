package org.emu.dev

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextUBytes
import androidx.compose.ui.input.key.Key

class Chip8 {
    var running : Boolean = false
    val registers = Registers()
    val memory = Memory()
    private val delayTimer = Timer()
    private val soundTimer = Timer()
    val keyboard : Keyboard = Keyboard()
    val keyMap: MutableMap<Key, Int> = mutableMapOf()


    init {
        modifyKeyMap(keyMapAZERTY)
    }



    fun modifyKeyMap(map: Map<Key, Int>) {
        keyMap.putAll(map)
    }


    fun start() {
        if (running ) return
        running = true

        CoroutineScope(Dispatchers.Default).launch {
            while (running) {
                emulateCycle(
                    registers = registers,
                    memory = memory
                )
                delay(1 )
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            while (running) {
                delay(1)
                delayTimer.tick()
                soundTimer.tick()
            }
        }
    }

    fun stop() {
        running = false
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun emulateCycle(registers: Registers, memory: Memory) {
        val instruction = fetch(memory)
        memory.skipInstruction()
        decode(instruction, registers = registers, memory = memory)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun fetch(memory: Memory) : Int {
        val pc = memory.programCounter.toInt()
        val first = memory.ram[pc].toInt() shl 8
        val second = memory.ram[pc + 1].toInt()

        return first or second
    }

    @OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)
    fun decode(instruction : Int, registers: Registers, memory: Memory) {

        val head = (instruction and 0xF000) shr 12
        val midhead = (instruction and 0x0F00) shr 8
        val midtail = (instruction and 0x00F0) shr 4
        val tail = (instruction and 0x000F)

        val masks = listOf<UByte>(1u, 2u, 4u, 8u, 16u, 32u, 64u, 128u).reversed()

        when(head) {
            0x0 -> {
                if (midtail == 0xE) {
                    when(tail) {
                        0x0 -> {
                            Display.clearScreen()
                        }
                        0xE -> {
                            memory.programCounter = memory.stack.removeLast()
                        }
                    }
                }

            }
            0x1 -> {
                memory.programCounter = (instruction and 0x0FFF).toUShort()
            }
            0x2 -> {
                memory.stack.addLast(memory.programCounter)
                memory.programCounter = (instruction and 0x0FFF).toUShort()
            }
            0x3 -> {
                val value = (instruction and 0x00FF).toUByte()

                if (value == registers.registersMap["v${midhead.hexToChar()}"]?.value) {
                    memory.skipInstruction()
                }
            }
            0x4 -> {
                val value = (instruction and 0x00FF).toUByte()
                if (value != registers.registersMap["v${midhead.hexToChar()}"]?.value) {
                    memory.skipInstruction()
                }
            }
            0x5 -> {
                when(tail) {
                    0x0 ->{
                        if (registers.registersMap["v${midhead.hexToChar()}"]?.value == registers.registersMap["v${midtail.hexToChar()}"]?.value) {
                            memory.skipInstruction()
                        }
                    }
                }

            }
            0x6 -> {
                registers.registersMap["v${midhead.hexToChar()}"]?.value = (instruction and 0x00FF).toUByte()
            }

            0x7 -> {
                val old = registers.registersMap["v${midhead.hexToChar()}"]?.value
                if (old != null) {
                    registers.registersMap["v${midhead.hexToChar()}"]?.value = (old + ((instruction and 0x00FF).toUInt())).toUByte()
                }

            }
            0x8 -> {
                val first = registers.registersMap["v${midhead.hexToChar()}"]?.value?.toInt()
                val other = registers.registersMap["v${midtail.hexToChar()}"]?.value?.toInt()

                if (first == null || other == null) {
                    return
                }

                when(tail) {
                    0x0 -> {
                        registers.registersMap["v${midhead.hexToChar()}"]?.value = other.toUByte()
                    }

                    0x1 -> {
                        registers.registersMap["v${midhead.hexToChar()}"]?.value = (first or other).toUByte()
                    }

                    0x2 -> {
                        registers.registersMap["v${midhead.hexToChar()}"]?.value = (first and other).toUByte()
                    }

                    0x3 -> {
                        registers.registersMap["v${midhead.hexToChar()}"]?.value = (first xor other).toUByte()
                    }

                    0x4 -> {
                        registers.registersMap["v${midhead.hexToChar()}"]?.value = (first + other).toUByte()
                        if (first + other > 255) {
                            registers.registersMap["vf"]?.value = 1u
                        }
                    }

                    0x5 -> {
                        registers.registersMap["v${midhead.hexToChar()}"]?.value = (first - other).toUByte()
                        registers.registersMap["vf"]?.value = if (first >= other)  1.toUByte() else 0.toUByte()
                    }

                    0x6 -> {
                        registers.registersMap["vf"]?.value = if (first and 0b00000001 != 0) 1u else 0u
                        registers.registersMap["v${midhead.hexToChar()}"]?.value = (first shr 1).toUByte()
                    }

                    0x7 -> {
                        registers.registersMap["v${midhead.hexToChar()}"]?.value = (other - first).toUByte()
                        registers.registersMap["vf"]?.value = if (other > first)  1u else 0u
                    }

                    0xE -> {
                        registers.registersMap["vf"]?.value = if ((first and 0b10000000) != 0) 1u else 0u
                        registers.registersMap["v${midhead.hexToChar()}"]?.value = (first shl 1).toUByte()
                    }
                }
            }
            0x9 -> {
                if (registers.registersMap["v${midhead.hexToChar()}"]?.value != registers.registersMap["v${midtail.hexToChar()}"]?.value) {
                    memory.skipInstruction()
                }
            }
            0xA -> {
                memory.indexRegister = (instruction and 0xFFF).toUShort()
            }
            0xB -> {
                val value = registers.registersMap["v0"]?.value
                if (value != null) {
                    memory.programCounter = ((instruction and 0x0FFF).toUShort() + value).toUShort()
                }

            }
            0xC -> {
                val random = Random.nextUBytes(1)
                val value = (instruction and 0x00FF)

                registers.registersMap["v${midhead.hexToChar()}"]?.value = (random[0].toInt() and value).toUByte()

            }
            0xD -> {
                val x = registers.registersMap["v${midhead.hexToChar()}"]?.value?.rem(64u)
                val y = registers.registersMap["v${midtail.hexToChar()}"]?.value?.rem(32u)

                if (x != null && y != null) {

                    val size = tail

                    for (n in 0u..(size - 1).toUInt()) {
                        val sp = memory.ram[(memory.indexRegister + n).toInt()]
                        val ny = (y + n).toInt()

                        if (ny >= 32) {
                            break
                        }

                        masks.forEachIndexed { pxIndex, mask ->
                            val switchPixel = (sp and mask).toInt() != 0
                            val nx = (x+pxIndex.toUInt()).toInt()

                            if (nx >= 64) {
                                return@forEachIndexed
                            }

                            if (switchPixel) {
                                if (Display.pixels[ny][nx].state) {
                                    registers.registersMap["vf"]?.value = 1.toUByte()
                                    Display.pixels[ny][nx].state = false
                                }
                                else {
                                    Display.pixels[ny][nx].state = true
                                }
                            }
                        }
                    }
                }

            }
            0xE -> {
                val registered = registers.registersMap["v${midhead.hexToChar()}"]?.value
                if (registered != null) {
                    when(instruction and 0x00FF) {
                        0x9E -> {
                            if (keyboard.isPressed(registered.toInt())) {
                                memory.skipInstruction()
                            }
                        }

                        0xA1 -> {
                            if (!keyboard.isPressed(registered.toInt())) {
                                memory.skipInstruction()
                            }
                        }
                    }
                }

            }
            0xF -> {
                val registered = registers.registersMap["v${midhead.hexToChar()}"]?.value
                if (registered != null) {
                    when(instruction and 0x00FF) {
                        0x07 -> {
                            registers.registersMap["v${midhead.hexToChar()}"]?.value = delayTimer.value.toUByte()
                        }

                        0x0A -> {
                            var anyPress = false
                            for (key in keyMap.values) {
                                if (keyboard.isPressed(key)) {
                                    anyPress = true
                                    registers.registersMap["v${midhead.hexToChar()}"]?.value = key.toUByte()
                                    break
                                }
                            }

                            if(!anyPress) {
                                memory.blockingInstruction()
                            }
                        }

                        0x15 -> {
                            delayTimer.value = registered.toInt()
                        }

                        0x18 -> {
                            soundTimer.value = registered.toInt()
                        }

                        0x1E -> {
                            memory.indexRegister = (memory.indexRegister + registered).toUShort()
                        }

                        0x29 -> {
                            memory.indexRegister = memory.addressOfCharacter(registered.toInt())
                        }

                        0x33 -> {
                            var idx = memory.indexRegister.toInt()
                            var number = registered.toInt()
                            while(true) {
                                var divisor = 10
                                while (number / divisor > 10) {
                                    divisor *= 10
                                }

                                if(number < divisor) {
                                    memory.ram[idx] = number.toUByte()
                                    break
                                }

                                else {
                                    memory.ram[idx] = (number / divisor).toUByte()
                                    number = number % divisor
                                    idx++
                                }
                            }

                        }

                        0x55 -> {
                            var indexValue = memory.indexRegister.toInt()
                            for (i in 0x0..0xF) {
                                val reg = registers.registersMap["v${i.hexToChar()}"]?.value
                                if (reg != null) {
                                    memory.ram[indexValue] = reg
                                    indexValue += 1
                                    if (i.hexToChar() == midhead.hexToChar()) {
                                        break
                                    }
                                }
                            }
                        }

                        0x65 -> {
                            var indexValue = memory.indexRegister.toInt()
                            for (i in 0x0..0xF) {
                                registers.registersMap["v${i.hexToChar()}"]?.value = memory.ram[indexValue]
                                indexValue += 1
                                if (i.hexToChar() == midhead.hexToChar()) {
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}