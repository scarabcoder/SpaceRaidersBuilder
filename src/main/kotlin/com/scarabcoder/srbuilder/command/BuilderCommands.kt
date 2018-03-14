package com.scarabcoder.srbuilder.command

import com.scarabcoder.commandapi2.Command
import com.scarabcoder.commandapi2.CommandRegistry
import com.scarabcoder.commandapi2.CommandSection
import com.scarabcoder.srbuilder.hangar
import com.scarabcoder.srbuilder.ship.Engine
import com.scarabcoder.srbuilder.ship.Hull
import com.scarabcoder.srbuilder.ship.ShipSize
import org.bukkit.entity.Player


class NewCommand internal constructor(): CommandSection("new") {

    @Command(description = "Create a new hull")
    fun hull(sender: Player, size: ShipSize, hullName: String = "Untitled Hull") {
        Hull.new(sender, sender.hangar, size, hullName)
    }

    @Command(description = "Create a new engine")
    fun engine(sender: Player, size: ShipSize, engineName: String = "Untitled Engine") {
        Engine.new(sender, sender.hangar, size, engineName)

    }

}

class Commands internal constructor() {


    @Command(description = "Open a GUI with all of your saved parts, or load a part by the given ID.")
    fun load(sender: Player, id: Int? = null) {
    }

    @Command(description = "Submit the current part or a part with the given ID for review to be published in the game.")
    fun submit(sender: Player, idToSubmit: Int? = sender.hangar.currentPart?.id) {


    }

    @Command(description = "Get help for commands.")
    fun help(sender: Player) {
        sender.sendMessage("Test")
    }

}

fun registerCommands(){
    CommandRegistry.registerCommand(NewCommand())
    CommandRegistry.registerMultiCommands(Commands())
}