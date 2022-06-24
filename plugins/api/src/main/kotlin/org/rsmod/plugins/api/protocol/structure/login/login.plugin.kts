package org.rsmod.plugins.api.protocol.structure.login

import io.guthix.buffer.readIntIME
import io.guthix.buffer.readIntME
import org.rsmod.game.cache.GameCache
import org.rsmod.plugins.api.protocol.packet.login.AuthCode
import org.rsmod.plugins.api.protocol.packet.login.CacheChecksum
import org.rsmod.plugins.api.protocol.packet.login.LoginPacketMap

val packets: LoginPacketMap by inject()
val cache: GameCache by inject()

packets.register {
    val code = when (readByte().toInt()) {
        2 -> {
            skipBytes(Int.SIZE_BYTES)
            -1
        }
        1, 3 -> {
            val auth = readUnsignedMedium()
            skipBytes(Byte.SIZE_BYTES)
            auth
        }
        else -> readInt()
    }
    AuthCode(code)
}

packets.register {
    val crcs = IntArray(cache.archiveCount)
    crcs[9] = readIntIME()
    crcs[6] = readIntLE()
    crcs[10] = readIntLE()
    crcs[5] = readInt()
    crcs[18] = readIntLE()
    crcs[1] = readIntLE() // could be 1 or 3
    crcs[7] = readIntIME()
    crcs[2] = readIntME()
    crcs[19] = readIntIME()
    crcs[8] = readIntIME()
    crcs[11] = readIntLE()
    crcs[16] = readInt()
    crcs[14] = readIntLE()
    crcs[3] = readIntME() // coult be 1 or 3
    crcs[4] = readIntME()
    crcs[15] = readIntIME()
    crcs[13] = readInt()
    crcs[20] = readIntLE()
    crcs[17] = readIntLE()
    crcs[12] = readIntME()
    CacheChecksum(crcs)
}
