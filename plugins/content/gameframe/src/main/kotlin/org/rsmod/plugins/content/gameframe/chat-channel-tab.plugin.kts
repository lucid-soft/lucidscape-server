package org.rsmod.plugins.content.gameframe


import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.mob.player.getVarbit
import org.rsmod.plugins.api.model.mob.player.runClientScript
import org.rsmod.plugins.api.model.mob.player.setVarbit
import org.rsmod.plugins.api.model.ui.InterfaceEvent
import org.rsmod.plugins.api.model.ui.closeOverlay
import org.rsmod.plugins.api.model.ui.openOverlay
import org.rsmod.plugins.api.model.ui.setComponentEvents
import org.rsmod.plugins.api.onButton
import org.rsmod.plugins.api.onOpenOverlay
import org.rsmod.plugins.api.onOpenTopLevel

onOpenTopLevel(inter("gameframe_fixed")) {
    player.openChatTab(player.getVarbit(varbit("chat_tab")))
}

onOpenOverlay(inter("grouping")) {
    player.runClientScript(828, 1) // sets player as a member
    player.setComponentEvents(component("grouping_inner"), 0 .. 24, InterfaceEvent.BUTTON1)
}

val buttonNames = listOf("friend_chat_button", "clan_chat_button", "guest_clan_chat_button", "grouping_button")

buttonNames.forEachIndexed { index, name ->
    onButton(component(name)) {
        player.closeChatTab(player.getVarbit(varbit("chat_tab")))
        player.setVarbit(varbit("chat_tab"), index)
        player.openChatTab(index)
    }
}

val interNames = listOf("friend_chat", "clan_chat", "guest_clan_chat", "grouping")

fun Player.openChatTab(index: Int) {
    openOverlay(inter(interNames[index]), component("chat_channel_tab_inner"))
}

fun Player.closeChatTab(index: Int) {
    closeOverlay(inter(interNames[index]))
}
