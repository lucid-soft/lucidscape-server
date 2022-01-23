package org.rsmod.plugins.api.protocol.packet.client

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import org.rsmod.game.cache.GameCache
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.cache.binary.Huffman
import org.rsmod.plugins.api.util.HuffmanUtil
import kotlin.experimental.and

val logger = InlineLogger()

class PublicChat(
    val effect: Int,
    val color: Int,
    val length: Int,
    val data: ByteArray,
    val type: Int
) : ClientPacket

class PublicChatHandler @Inject constructor(
    val cache: GameCache
) : ClientPacketHandler<PublicChat> {

    override fun handle(client: Client, player: Player, packet: PublicChat) {
        //val messageData = byteArrayOf()[256]


        //HuffmanUtil.load(Huffman.load(cache.archive(10).readGroup("huffman").files[0]!!.data))

        //val message = HuffmanUtil.decompress(packet.data, packet.length)

        //val huffman = cache.archive(10).readGroup("huffman").files[0]!!.data

        //val stringData = ByteArray(256)
        //val codec = HuffmanCodec(cache.archive(10).readGroup("huffman").files[0]?.data?.array()!!)
        //codec.decode(packet.data, stringData, 0, 0, packet.length)
        //val message = String(stringData, 0, packet.length)

        //logger.debug { "Message: $message" }
    }
}


class HuffmanCodec(sizes: ByteArray) {
    private val masks: IntArray
    private val sizes: ByteArray
    private var keys: IntArray

    fun decode(input: ByteArray, output: ByteArray, i: Int, i_1_: Int, len: Int): Int {
        var i = i
        var len = len
        return if (len == 0) {
            0
        } else {
            var idx = 0
            len += i
            var i_6_ = i_1_
            while (true) {
                val i_7_ = input[i_6_]
                if (i_7_ < 0) {
                    idx = keys[idx]
                } else {
                    ++idx
                }
                var i_8_: Int
                if (keys[idx].also { i_8_ = it }.inv() > -1) {
                    output[i++] = i_8_.inv().toByte()
                    if (len <= i) {
                        break
                    }
                    idx = 0
                }
                if ((64 and i_7_.toInt()).inv() != -1) {
                    idx = keys[idx]
                } else {
                    ++idx
                }
                if (0 > keys[idx].also { i_8_ = it }) {
                    output[i++] = i_8_.inv().toByte()
                    if (len <= i) {
                        break
                    }
                    idx = 0
                }
                if (0 == (i_7_ and 32).toInt()) {
                    ++idx
                } else {
                    idx = keys[idx]
                }
                if (0 > keys[idx].also { i_8_ = it }) {
                    output[i++] = i_8_.inv().toByte()
                    if (len.inv() >= i.inv()) {
                        break
                    }
                    idx = 0
                }
                if (0 != (i_7_ and 16).toInt()) {
                    idx = keys[idx]
                } else {
                    ++idx
                }
                if (-1 < keys[idx].also { i_8_ = it }.inv()) {
                    output[i++] = i_8_.inv().toByte()
                    if (len.inv() >= i.inv()) {
                        break
                    }
                    idx = 0
                }
                if (0 == (i_7_ and 8).toInt()) {
                    ++idx
                } else {
                    idx = keys[idx]
                }
                if (0 > keys[idx].also { i_8_ = it }) {
                    output[i++] = i_8_.inv().toByte()
                    if (len <= i) {
                        break
                    }
                    idx = 0
                }
                if ((i_7_ and 4).toInt().inv() == -1) {
                    ++idx
                } else {
                    idx = keys[idx]
                }
                if (keys[idx].also { i_8_ = it } < 0) {
                    output[i++] = i_8_.inv().toByte()
                    if (i.inv() <= len.inv()) {
                        break
                    }
                    idx = 0
                }
                if ((i_7_ and 2).toInt().inv() == -1) {
                    ++idx
                } else {
                    idx = keys[idx]
                }
                if (keys[idx].also { i_8_ = it }.inv() > -1) {
                    output[i++] = i_8_.inv().toByte()
                    if (len <= i) {
                        break
                    }
                    idx = 0
                }
                if (0 != (i_7_ and 1.toByte()).toInt()) {
                    idx = keys[idx]
                } else {
                    ++idx
                }
                if (0 > keys[idx].also { i_8_ = it }) {
                    output[i++] = i_8_.inv().toByte()
                    if (len.inv() >= i.inv()) {
                        break
                    }
                    idx = 0
                }
                ++i_6_
            }
            1 + i_6_ - i_1_
        }
    }

    fun encode(text: String, output: ByteArray): Int {
        var key = 0
        val input = text.toByteArray()
        var bitpos = 0
        for (pos in 0 until text.length) {
            val data: Int = (input[pos] and 255.toByte()).toInt()
            val size = sizes[data]
            val mask = masks[data]
            if (size.toInt() == 0) {
                throw RuntimeException("No codeword for data value $data")
            }
            var remainder = bitpos and 7
            key = key and (-remainder shr 31)
            var offset = bitpos shr 3
            bitpos += size.toInt()
            val i_41_ = (-1 + (remainder - -size) shr 3) + offset
            remainder += 24
            output[offset] = (mask ushr remainder.let { key = key or it; key }).toByte()
            if (i_41_.inv() < offset.inv()) {
                remainder -= 8
                output[++offset] = (mask ushr remainder.also { key = it }).toByte()
                if (offset.inv() > i_41_.inv()) {
                    remainder -= 8
                    output[++offset] = (mask ushr remainder.also { key = it }).toByte()
                    if (offset.inv() > i_41_.inv()) {
                        remainder -= 8
                        output[++offset] = (mask ushr remainder.also { key = it }).toByte()
                        if (i_41_ > offset) {
                            remainder -= 8
                            output[++offset] = (mask shl -remainder.also { key = it }).toByte()
                        }
                    }
                }
            }
        }
        return 7 + bitpos shr 3
    }

    init {
        val numcodewords = sizes.size
        val is_13_ = IntArray(33)
        masks = IntArray(numcodewords)
        this.sizes = sizes
        var i_14_ = 0
        keys = IntArray(8)
        var pos = 0
        while (numcodewords > pos) {
            val size = sizes[pos]
            if (size.toInt() != 0) {
                val i_17_ = 1 shl 32 - size
                val mask = is_13_[size.toInt()]
                masks[pos] = mask
                var i_19_: Int
                if (i_17_ and mask != 0) {
                    i_19_ = is_13_[size - 1]
                } else {
                    var idx_ = size - 1
                    while (idx_.inv() <= -2) {
                        val idk = is_13_[idx_]
                        if (idk.inv() != mask.inv()) {
                            break
                        }
                        val i_26_ = 1 shl 32 + -idx_
                        if (idk and i_26_ != 0) {
                            is_13_[idx_] = is_13_[-1 + idx_]
                            break
                        }
                        is_13_[idx_] = i_26_ or idk
                        --idx_
                    }
                    i_19_ = mask or i_17_
                }
                is_13_[size.toInt()] = i_19_
                var idx_ = size + 1
                while (32 >= idx_) {
                    if (mask == is_13_[idx_]) {
                        is_13_[idx_] = i_19_
                    }
                    idx_++
                }
                var idx = 0
                for (shift in 0 until size) {
                    val i_26_ = -0x80000000 ushr shift
                    if (i_26_ and mask == 0) {
                        idx++
                    } else {
                        if (keys[idx] == 0) {
                            keys[idx] = i_14_
                        }
                        idx = keys[idx]
                    }
                    if (keys.size <= idx) {
                        val realloc = IntArray(keys.size * 2)
                        System.arraycopy(keys, 0, realloc, 0, keys.size)
                        keys = realloc
                    }
                }
                keys[idx] = pos.inv()
                if (idx >= i_14_) {
                    i_14_ = idx + 1
                }
            }
            pos++
        }
    }
}
