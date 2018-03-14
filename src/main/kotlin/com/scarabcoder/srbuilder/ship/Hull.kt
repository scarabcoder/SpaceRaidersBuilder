package com.scarabcoder.srbuilder.ship

import com.boydti.fawe.`object`.schematic.Schematic
import com.scarabcoder.srbuilder.DataFolders
import com.scarabcoder.srbuilder.fawe
import com.scarabcoder.srbuilder.faweVector
import com.scarabcoder.srbuilder.player.BuildingHangar
import com.scarabcoder.srbuilder.player.SRPlayer
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.*

class Hull(override val id: Int,
           override val size: ShipSize,
           override var name: String,
           override val ownerID: UUID,
           override var schematic: Schematic,
           val turrets: MutableList<Vector>,
           var engineOneLink: Vector,
           var engineTwoLink: Vector): ShipPart {


    companion object {


        fun new(player: Player, hangar: BuildingHangar, size: ShipSize, name: String): Hull {
            val (link1, link2) = defaultLinks(size)
            val h = Hull(
                    id = PartRegistry.nextID(),
                    size = size,
                    name = name,
                    ownerID = player.uniqueId,
                    schematic = defaultSchem(size),
                    turrets = ArrayList(),
                    engineOneLink = link1,
                    engineTwoLink = link2
            )
            hangar.currentPart = h
            PartRegistry.registerNew(h)
            return h
        }

        fun defaultSchem(size: ShipSize): Schematic {
            return ClipboardFormat.SCHEMATIC.load(File(DataFolders.DEFAULT_HULLS.folder, size.name.toLowerCase() + ".schematic"))
        }

        fun defaultLinks(size: ShipSize): Pair<Vector, Vector> {
            val cfg = YamlConfiguration.loadConfiguration(File(DataFolders.DEFAULT_HULLS.folder, size.name.toLowerCase() + ".yml"))
            return Pair(cfg.faweVector("engineOneLink")!!, cfg.faweVector("engineTwoLink")!!)
        }

    }

}