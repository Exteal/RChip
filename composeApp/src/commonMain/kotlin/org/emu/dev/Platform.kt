package org.emu.dev

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform