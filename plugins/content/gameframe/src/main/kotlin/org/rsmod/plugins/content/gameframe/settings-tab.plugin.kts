package org.rsmod.plugins.content.gameframe

import com.github.michaelbull.logging.InlineLogger
import org.rsmod.plugins.api.model.ui.InterfaceEvent
import org.rsmod.plugins.api.model.ui.setComponentEvents
import org.rsmod.plugins.api.onLogin
import org.rsmod.plugins.api.protocol.packet.server.IfSetEvents

val logger = InlineLogger()

onLogin {
    logger.debug { "On Login!!!" }

    for (i in 0 until 100) {
        player.write(IfSetEvents(component = i, events = 1 shl 1, dynamic = 0..2))
    }

    player.setComponentEvents(
        component = component("display_mode_quick").toComponent(),
        range = 0..3,
        events = arrayOf(
            InterfaceEvent.BUTTON1
        )
    )
}
