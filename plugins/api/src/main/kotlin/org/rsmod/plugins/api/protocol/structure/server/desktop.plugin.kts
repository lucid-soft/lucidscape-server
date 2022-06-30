package org.rsmod.plugins.api.protocol.structure.server

import com.github.michaelbull.logging.InlineLogger
import io.guthix.buffer.*
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.rsmod.game.message.PacketLength
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.game.model.item.Item
import org.rsmod.game.model.map.MapSquare
import org.rsmod.plugins.api.protocol.Device
import org.rsmod.plugins.api.protocol.packet.server.*
import org.rsmod.plugins.api.protocol.structure.DevicePacketStructureMap
import org.rsmod.util.security.Xtea
import kotlin.math.min

val structures: DevicePacketStructureMap by inject()
val packets = structures.server(Device.Desktop)
val logger = InlineLogger()

packets.register<RunClientScript> {
    opcode = 0
    length = PacketLength.Short
    write {
        val types = CharArray(args.size) { i -> if (args[i] is String) 's' else 'i' }
        it.writeStringCP1252(String(types))
        args.reversed().forEach { arg ->
            if (arg is String) it.writeStringCP1252(arg)
            else it.writeInt(arg.toString().toInt())
        }
        it.writeInt(id)

        logger.debug { "Writing RunClientScript (id=$id, types=${String(types)}, args=$args)" }
    }
}

packets.register<IfSetEvents> {
    opcode = 1
    write {
        it.writeIntME(events)
        it.writeShortLE(dynamic.last)
        it.writeIntIME(component)
        it.writeShortAdd(dynamic.first)
    }
}

packets.register<NpcInfoSmallViewport> {
    opcode = 22
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}

packets.register<MinimapFlagSet> {
    opcode = 25
    write {
        it.writeByte(x)
        it.writeByte(y)
    }
}

packets.register<ResetAnims> {
    opcode = 28
    write {}
}

packets.register<UpdateInvPartial> {
    opcode = 37
    length = PacketLength.Short
    write {
        it.writeInt(-1)
        it.writeShort(key)
        it.writePartialItemContainer(updated)
    }
}

packets.register<MessageGame> {
    opcode = 38
    length = PacketLength.Byte
    write {
        it.writeSmallSmart(type)
        it.writeBoolean(username != null)
        if (username != null) {
            it.writeStringCP1252(username)
        }
        it.writeStringCP1252(text)
    }
}

packets.register<ResetClientVarCache> {
    opcode = 43
    write {}
}

packets.register<RebuildNormal> {
    opcode = 46
    length = PacketLength.Short
    write {
        val xteas = xteasBuffer(viewport, xteas)
        val buf = gpi?.write(it) ?: it
        buf.writeShort(playerZone.y)
        buf.writeShort(playerZone.x)
        buf.writeBytes(xteas)
    }
}

packets.register<IfOpenTop> {
    opcode = 48
    write {
        it.writeShortAddLE(interfaceId)
    }
}

packets.register<IfOpenSub> {
    opcode = 49
    write {
        it.writeInt(targetComponent)
        it.writeByte(clickMode)
        it.writeShort(interfaceId)
    }
}

packets.register<IfSetText> {
    opcode = 52
    length = PacketLength.Short
    write {
        it.writeStringCP1252(text)
        it.writeIntIME(component)
    }
}

packets.register<PlayerInfo> {
    opcode = 54
    length = PacketLength.Short
    write {
        it.writeBytes(buffer)
    }
}

packets.register<UpdateStat> {
    opcode = 55
    write {
        it.writeByteSub(skill)
        it.writeInt(xp)
        it.writeByteAdd(currLevel)
    }
}

packets.register<UpdateInvFull> {
    opcode = 67
    length = PacketLength.Short
    write {
        it.writeInt(-1)
        it.writeShort(key)
        it.writeShort(items.size)
        it.writeFullItemContainer(items)
    }
}

packets.register<VarpLarge> {
    opcode = 72
    write {
        it.writeShortAddLE(id)
        it.writeIntLE(value)
    }
}

packets.register<UpdateRunEnergy> {
    opcode = 75
    write {
        it.writeByte(energy)
    }
}

packets.register<VarpSmall> {
    opcode = 96
    write {
        it.writeShortAddLE(id)
        it.writeByteSub(value)
    }
}

fun xteasBuffer(viewport: List<MapSquare>, xteasRepository: XteaRepository): ByteBuf {
    val buf = Unpooled.buffer(Short.SIZE_BYTES + (Int.SIZE_BYTES * 4 * 4))
    buf.writeShort(viewport.size)
    viewport.forEach { mapSquare ->
        val xteas = xteasRepository[mapSquare.id] ?: Xtea.EMPTY_KEY_SET
        xteas.forEach { buf.writeInt(it) }
    }
    return buf
}

fun ByteBuf.writeFullItemContainer(items: List<Item?>) {
    items.forEach { item ->
        val id = (item?.id ?: -1) + 1
        val amount = (item?.amount ?: 0)
        writeByteNeg(min(255, amount))
        if (amount >= 255) {
            writeInt(item?.amount ?: 0)
        }
        writeShort(id)
    }
}

fun ByteBuf.writePartialItemContainer(items: Map<Int, Item?>) {
    items.forEach { (slot, item) ->
        val id = (item?.id ?: -1) + 1
        val amount = (item?.amount ?: 0)
        writeSmallSmart(slot)
        writeShort(id)
        if (id != 0) {
            writeByte(min(255, amount))
            if (amount >= 255) {
                writeInt(item?.amount ?: 0)
            }
        }
    }
}
