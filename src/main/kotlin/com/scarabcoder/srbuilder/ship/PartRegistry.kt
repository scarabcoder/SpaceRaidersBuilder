package com.scarabcoder.srbuilder.ship

import com.boydti.fawe.`object`.clipboard.ReadOnlyClipboard
import com.boydti.fawe.`object`.schematic.Schematic
import com.boydti.fawe.util.EditSessionBuilder
import com.scarabcoder.srbuilder.DataFolders
import com.scarabcoder.srbuilder.data.Connections
import com.scarabcoder.srbuilder.fawe
import com.scarabcoder.srbuilder.faweVector
import com.scarabcoder.srbuilder.player.BuildingHangar
import com.scarabcoder.srbuilder.setFaweVector
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.regions.CuboidRegion
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

object PartRegistry {

    private val _parts = HashMap<Int, ShipPart>()

    private var _lastID: Int = 0


    val parts: Map<Int, ShipPart>
        get() = _parts

    fun nextID(): Int = ++_lastID


    fun loadIn() {
        Connections.grabConnection().use { c ->
            val rs = c.prepareStatement("SELECT * FROM parts").executeQuery()
            while(rs.next()){
                val sql = YamlConfiguration.loadConfiguration(File(DataFolders.PLAYER_PARTS.folder, "${rs.getInt("id")}.yml"))
                val size = ShipSize.valueOf(sql.getString("size"))
                val id = rs.getInt("id")
                val name = rs.getString("name")
                val owner = UUID.fromString(rs.getString("owner"))
                val schematic = ClipboardFormat.SCHEMATIC.load(File(DataFolders.PLAYER_PARTS.folder, "$id.schematic"))
                if(sql.getString("part") == "hull"){
                    val turrets = sql.getConfigurationSection("turrets")?.getKeys(false)?.map { sql.faweVector("turrets.$it")!! } ?: ArrayList()
                    _parts.put(id, Hull(
                            id = id,
                            size = size,
                            name = name,
                            ownerID = owner,
                            schematic = schematic,
                            turrets = turrets.toMutableList(),
                            engineOneLink = sql.faweVector("engineOneLink")!!,
                            engineTwoLink = sql.faweVector("engineTwoLink")!!
                    ))
                }else if(sql.getString("part") == "engine"){
                    _parts.put(id, Engine(
                            id = id,
                            size = size,
                            name = name,
                            ownerID = owner,
                            schematic = schematic,
                            hullLink = sql.faweVector("hullLink")!!
                    ))
                }
            }
        }
        _lastID = parts.values.sortedBy { it.id }.lastOrNull()?.id ?: 0
    }

    fun getPart(name: String): ShipPart? = parts.values.firstOrNull { it.name.equals(name, true) }

    fun getPartsByOwner(owner: UUID): List<ShipPart> = parts.values.filter { it.ownerID == owner }

    fun getPart(id: Int): ShipPart? = _parts[id]

    fun registerNew(part: ShipPart) {
        _parts.put(part.id, part)
    }

    fun savePartData(part: ShipPart, hangar: BuildingHangar) {

        val cp = hangar.currentPart ?: return

        val region = CuboidRegion(hangar.world.fawe(), hangar.area.first.toVector().fawe(), hangar.area.second.toVector().fawe())
        val dif = hangar.area.second.toVector().fawe().subtract(hangar.area.first.toVector().fawe())

        //Convert a region to a clipboard
        val session = EditSessionBuilder(region.world!!).allowedRegionsEverywhere().autoQueue(false).build()
        val clip = BlockArrayClipboard(region, ReadOnlyClipboard.of(session, region))

        cp.schematic = Schematic(clip)
        cp.schematic.save(File(DataFolders.PLAYER_PARTS.folder, "${cp.id}.schematic"), ClipboardFormat.SCHEMATIC)

        Connections.grabConnection().use { c->
            val ps = c.prepareStatement("INSERT OR REPLACE INTO parts(id, owner, name) VALUES (?, ?, ?)")
            ps.setInt(1, part.id)
            ps.setString(2, part.ownerID.toString())
            ps.setString(3, part.name)
            ps.executeUpdate()
        }

        part.schematic.save(File(DataFolders.PLAYER_PARTS.folder, "${part.id}.schematic"), ClipboardFormat.SCHEMATIC)
        val f = File(DataFolders.PLAYER_PARTS.folder, "${part.id}.yml")
        if(f.exists()) f.delete()
        f.createNewFile()
        val yml = YamlConfiguration.loadConfiguration(f)
        yml.set("part", if(part is Hull) "hull" else "engine")
        yml.set("size", part.size.toString())
        if(part is Engine){
            yml.setFaweVector("hullLink", part.hullLink)
        }else if(part is Hull) {
            part.turrets.forEachIndexed({  i, v -> yml.setFaweVector("turrets.$i", v)  })
            yml.setFaweVector("engineOneLink", part.engineOneLink)
            yml.setFaweVector("engineTwoLink", part.engineTwoLink)
        }
        yml.save(f)
    }

    fun deletePart(part: ShipPart) {

    }

}