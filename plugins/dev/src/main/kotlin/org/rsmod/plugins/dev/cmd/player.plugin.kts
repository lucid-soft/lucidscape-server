package org.rsmod.plugins.dev.cmd

import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.onCommand
import org.rsmod.plugins.api.privilege.Privileges

onCommand("empty") {
    description = "Empty inventory"
    execute {
        player.inventory.clear()
        player.sendMessage("Your inventory items have been cleared")
    }
}

onCommand("admin") {
    description = "Makes you an admin"
    execute {
        player.privileges.add(Privileges.Mod)
        player.privileges.add(Privileges.Admin)
        player.sendMessage("You are now an admin")
    }
}
