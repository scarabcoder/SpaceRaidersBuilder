package com.scarabcoder.srbuilder.ship

import com.boydti.fawe.`object`.schematic.Schematic
import com.scarabcoder.srbuilder.DataFolders
import com.scarabcoder.srbuilder.fawe
import com.scarabcoder.srbuilder.faweVector
import com.scarabcoder.srbuilder.player.BuildingHangar
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.*

class Engine(
        override val id: Int,
        override val size: ShipSize,
        override var name: String,
        override val ownerID: UUID,
        override var schematic: Schematic,
        var hullLink: Vector) : ShipPart {


    companion object {


        fun new(player: Player, hangar: BuildingHangar, size: ShipSize, name: String): Engine {

            val e = Engine(
                    id = PartRegistry.nextID(),
                    size = size,
                    name = name,
                    ownerID = player.uniqueId,
                    schematic = defaultSchem(size),
                    hullLink = defaultLink(size)
            )
            hangar.currentPart = e
            PartRegistry.registerNew(e)
            return e
        }

        fun defaultSchem(size: ShipSize): Schematic {
            return ClipboardFormat.SCHEMATIC.load(File(DataFolders.DEFAULT_ENGINES.folder, size.name.toLowerCase() + ".schematic"))
        }

        fun defaultLink(size: ShipSize): Vector {
            val cfg = YamlConfiguration.loadConfiguration(File(DataFolders.DEFAULT_ENGINES.folder   , size.name.toLowerCase() + ".yml"))
            return cfg.faweVector("hullLink")!!
        }

    }


}