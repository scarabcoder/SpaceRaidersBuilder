package com.scarabcoder.srbuilder

import com.scarabcoder.srbuilder.command.registerCommands
import com.scarabcoder.srbuilder.data.Connections
import com.scarabcoder.srbuilder.player.BuildingHangar
import com.scarabcoder.srbuilder.ship.PartRegistry
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class SRBuilderPlugin: JavaPlugin() {



    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(Listeners, this)
        config.options().copyDefaults(true)
        saveDefaultConfig()

        registerCommands()

        timer(20) {
            Bukkit.getWorlds().forEach {
                it.time = 18000
            }
        }
        Connections.grabConnection().use { c ->
            c.prepareStatement("CREATE TABLE IF NOT EXISTS parts (id INTEGER PRIMARY KEY, owner TEXT NOT NULL, name TEXT NOT NULL)").executeUpdate()
            c.prepareStatement("CREATE TABLE IF NOT EXISTS hangars (owner TEXT PRIMARY KEY, username TEXT NOT NULL, part INTEGER DEFAULT NULL, last_login INTEGER NOT NULL)").executeUpdate()
            PartRegistry.loadIn()
        }

    }

    companion object {
        val plugin: Plugin get() = Bukkit.getPluginManager().getPlugin("SpaceRaidersBuilder")
    }

}