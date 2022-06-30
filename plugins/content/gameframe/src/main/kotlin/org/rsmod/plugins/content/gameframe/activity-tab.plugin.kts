package org.rsmod.plugins.content.gameframe

import org.rsmod.game.event.impl.StatLevelUp
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.mob.player.getVarbit
import org.rsmod.plugins.api.model.mob.player.runClientScript
import org.rsmod.plugins.api.model.mob.player.setVarbit
import org.rsmod.plugins.api.model.stat.combatLevel
import org.rsmod.plugins.api.model.ui.InterfaceEvent
import org.rsmod.plugins.api.model.ui.closeOverlay
import org.rsmod.plugins.api.model.ui.openOverlay
import org.rsmod.plugins.api.model.ui.setComponentEvents
import org.rsmod.plugins.api.onButton
import org.rsmod.plugins.api.onOpenOverlay
import org.rsmod.plugins.api.onOpenTopLevel

onOpenTopLevel(inter("gameframe_fixed")) {
    player.openActivityTab(player.getVarbit(varbit("quest_tab")))
    player.updateCombatLevelSummary()
}

onOpenOverlay(inter("character_summary")) {
    player.setComponentEvents(component("character_summary_inner"), 3 .. 7, InterfaceEvent.BUTTON1, InterfaceEvent.BUTTON2, InterfaceEvent.BUTTON3, InterfaceEvent.BUTTON4)
}

onOpenOverlay(inter("quests")) {
    player.runClientScript(828, 1) // sets player as a member
    player.setComponentEvents(component("quests_inner"), 0 .. 180, InterfaceEvent.BUTTON1, InterfaceEvent.BUTTON2, InterfaceEvent.BUTTON3, InterfaceEvent.BUTTON4, InterfaceEvent.BUTTON5)
}

onOpenOverlay(inter("achievement_diaries")) {
    player.setComponentEvents(component("achievement_diaries_inner"), 0 .. 11, InterfaceEvent.BUTTON1, InterfaceEvent.BUTTON2)
}

onEvent<StatLevelUp>().then { player.updateCombatLevelSummary() }

fun Player.updateCombatLevelSummary() {
    runClientScript(3954, 46661634, 46661635, stats.combatLevel()) //
}

val buttonNames = listOf("character_summary_button", "quests_button", "achievement_diary_button", "kourend_favour_button")

buttonNames.forEachIndexed { index, name ->
    onButton(component(name)) {
        player.closeActivityTab(player.getVarbit(varbit("quest_tab")))
        player.setVarbit(varbit("quest_tab"), index)
        player.openActivityTab(index)
    }
}

val interNames = listOf("character_summary", "quests", "achievement_diaries", "kourend_favour")

fun Player.openActivityTab(index: Int) {
    openOverlay(inter(interNames[index]), component("activity_tab_inner"))
}

fun Player.closeActivityTab(index: Int) {
    closeOverlay(inter(interNames[index]))
}
