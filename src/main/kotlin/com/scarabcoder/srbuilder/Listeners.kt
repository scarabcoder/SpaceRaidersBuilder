package com.scarabcoder.srbuilder

import com.scarabcoder.srbuilder.data.Connections
import com.scarabcoder.srbuilder.player.BuildingHangar
import com.scarabcoder.srbuilder.ship.PartRegistry
import com.sk89q.worldedit.Vector
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Listeners: Listener {

    @EventHandler
    fun onEntitySpawn(e: EntitySpawnEvent) {
        if(e.location.world.name.startsWith("hangar-") && e.entity is LivingEntity) e.isCancelled = true
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent){
        if(e.player.hangar.currentPart == null) {
            e.isCancelled = true
            return
        }
        if(!e.player.hangar.area.isInArea(e.block.location)){
            e.isCancelled = true
            e.player.spawnParticle(Particle.BARRIER, e.block.location.add(0.5, 0.5, 0.5), 1)

        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent){
        if(e.player.hangar.currentPart == null) {
            e.isCancelled = true
            return
        }
        if(!e.player.hangar.area.isInArea(e.block.location)){
            e.isCancelled = true
            e.player.spawnParticle(Particle.BARRIER, e.block.location.add(0.5, 0.5, 0.5), 1)

        }
    }

    @EventHandler
    fun onWeatherChange(e: WeatherChangeEvent){
        if(e.world.name.startsWith("hangar-") && e.toWeatherState()) e.isCancelled = true
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        val h = BuildingHangar.generateNew(e.player.uniqueId)
        e.player.teleport(h.spawn)
        e.player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, true, false), true)

        Connections.grabConnection().use { c->
            val ps = c.prepareStatement("SELECT * FROM hangars WHERE owner=?")
            ps.setString(1, e.player.uniqueId.toString())
            val rs = ps.executeQuery()
            if(rs.next() && rs.getObject("part") != null && PartRegistry.getPart(rs.getInt("part")) != null){
                h.currentPart = PartRegistry.getPart(rs.getInt("part"))
            }

        }

    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        if(e.player.hangar.currentPart != null)
            PartRegistry.savePartData(e.player.hangar.currentPart!!, e.player.hangar)
        Connections.grabConnection().use { c->
            val ps = c.prepareStatement("INSERT OR REPLACE INTO hangars(owner, username, part, last_login) VALUES (?, ?, ?, ?)")
            ps.setString(1, e.player.uniqueId.toString())
            ps.setString(2, e.player.name)
            if(e.player.hangar.currentPart == null)
                ps.setObject(3, null)
            else
                ps.setInt(3, e.player.hangar.currentPart!!.id)
            ps.setLong(4, System.currentTimeMillis())
            ps.executeUpdate()
        }
    }



}