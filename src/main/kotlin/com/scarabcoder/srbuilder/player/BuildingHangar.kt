package com.scarabcoder.srbuilder.player

import com.boydti.fawe.`object`.schematic.Schematic
import com.scarabcoder.srbuilder.*
import com.scarabcoder.srbuilder.ship.PartRegistry
import com.scarabcoder.srbuilder.ship.ShipPart
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.regions.CuboidRegion
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.util.*
import kotlin.math.roundToInt

class BuildingHangar private constructor(val owner: UUID, private val _yCenter: Int) {

    val world: World
        get() = Bukkit.getWorld("hangar-$owner") ?: generateWorld()
    var currentPart: ShipPart? = null
        set(value) {
            val cp = currentPart
            if(cp != null){
                PartRegistry.savePartData(cp, this)
                for(x in area.first.blockX..area.second.blockX){
                    for(y in area.first.blockY..area.second.blockY){
                        for(z in area.first.blockZ..area.second.blockZ)
                            Location(area.first.world, x.toDouble(), y.toDouble(), z.toDouble()).block.type = Material.AIR
                    }
                }

            }
            value?.schematic?.paste(center)
            field = value
        }

    val area: Pair<Location, Location>
        get() {
            val (xMax, zMax) = currentPart!!.size.size
            return Pair(center.clone().subtract((xMax / 2).toDouble(), (xMax / 2).toDouble(), (zMax / 2).toDouble()),
                    center.clone().add((xMax / 2).toDouble(), (xMax / 2).toDouble(), (zMax / 2).toDouble()))
        }

    val center: Location
        get() {
            return Location(world, 0.0, _yCenter.toDouble(), 0.0)
        }
    val player: Player
        get() = Bukkit.getPlayer(owner) ?: throw UnsupportedOperationException("Player with UUID $owner is not online!")
    val offlinePlayer: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(owner)
    val spawn: Location
        get() {
            val v = SRBuilderPlugin.plugin.config.faweVector("hangar-spawn")!!
            val yOffset = SRBuilderPlugin.plugin.config.getInt("hangar-y-offset")
            return Location(world, v.x, v.y + yOffset, v.z)
        }

    private fun generateWorld(): World {
        val wc = WorldCreator("hangar-$owner")
        wc.type(WorldType.FLAT)
        wc.generatorSettings("2;0;1;")
        return wc.createWorld()
    }


    companion object {

        private val hangars = HashMap<UUID, BuildingHangar>()

        fun getHangar(id: UUID): BuildingHangar? {
            return hangars[id]
        }

        fun generateNew(owner: UUID): BuildingHangar {

            val wc = WorldCreator("hangar-$owner")
            wc.type(WorldType.FLAT)
            wc.generatorSettings("2;0;1;")
            val w = wc.createWorld()
            w.time = 18000
            w.setGameRuleValue("doDaylightCycle", "false")
            val schem = ClipboardFormat.SCHEMATIC.load(File(DataFolders.DEFAULTS.folder, "hangar.schematic"))
            val yOffset = SRBuilderPlugin.plugin.config.getInt("hangar-y-offset")

            schem.paste(BukkitWorld(w), Vector(0, yOffset, 0))
            val hangar = BuildingHangar(owner, yOffset + ((schem.clipboard!!.maximumPoint.y - schem.clipboard!!.minimumPoint.y) / 2).roundToInt())
            hangars.put(owner, hangar)
            BorderDrawer(hangar).runTaskTimer(SRBuilderPlugin.plugin, 0, 4)
            return hangar
        }
    }

    private class BorderDrawer(val h: BuildingHangar): BukkitRunnable() {

        override fun run() {
            if(!h.offlinePlayer.isOnline) {
                this.cancel()
                return
            }
            if(h.currentPart == null) return
            val (xMax, zMax) = h.currentPart!!.size.size

            drawTo(
                    h.center.clone().subtract(xMax / 2.0, xMax / 2.0, zMax / 2.0),
                    h.center.clone().add(xMax / 2.0, xMax / 2.0, zMax / 2.0),
                    h.player
            )
        }

    }

}