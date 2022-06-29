package org.rsmod.plugins.api.protocol.packet.client

import javax.inject.Inject
import org.rsmod.game.action.ActionBus
import org.rsmod.game.message.ClientPacket
import org.rsmod.game.message.ClientPacketHandler
import org.rsmod.game.model.client.Client
import org.rsmod.game.model.mob.Player

class EventMouseMove : ClientPacket
class EventMouseClick : ClientPacket
class EventMouseIdle : ClientPacket
class EventAppletFocus(val hasFocus: Boolean) : ClientPacket
class EventKeyboard : ClientPacket
class EventCameraPosition : ClientPacket
class WindowStatus : ClientPacket

class AppletFocusHandler @Inject constructor(
    private val actions: ActionBus
) : ClientPacketHandler<EventAppletFocus> {

    override fun handle(client: Client, player: Player, packet: EventAppletFocus) {
        val hasFocus = packet

    }
}
