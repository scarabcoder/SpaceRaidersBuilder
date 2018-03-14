package com.scarabcoder.srbuilder

import com.boydti.fawe.`object`.schematic.Schematic
import com.scarabcoder.srbuilder.player.BuildingHangar
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.bukkit.BukkitWorld
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

fun org.bukkit.util.Vector.fawe(): com.sk89q.worldedit.Vector {
    return com.sk89q.worldedit.Vector(this.x, this.y, this.z)
}

fun com.sk89q.worldedit.Vector.iterate(max: com.sk89q.worldedit.Vector): Iterator<com.sk89q.worldedit.Vector> {
    val items = ArrayList<com.sk89q.worldedit.Vector>()
    for(x in this.x.toInt()..max.x.toInt()){
        for(y in this.y.toInt()..max.y.toInt()){
            for(z in this.z.toInt()..max.z.toInt()){
                items.add(com.sk89q.worldedit.Vector(x, y, z))
            }
        }
    }
    return items.iterator()
}

fun timer(timer: Long, runnable: () -> Unit) {
    object: BukkitRunnable() {
        override fun run() {
            runnable()
        }

    }.runTaskTimer(SRBuilderPlugin.plugin, timer, timer)
}

fun Vector.toLocation(world: World): Location = Location(world, this.x, this.y, this.z)

fun Schematic.paste(location: Location) {
    this.paste(BukkitWorld(location.world), location.toVector().fawe())
}

fun ConfigurationSection.faweVector(key: String): com.sk89q.worldedit.Vector? {
    val sect: ConfigurationSection = this.getConfigurationSection(key) ?: return null
    return com.sk89q.worldedit.Vector(sect.getDouble("x"), sect.getDouble("y"), sect.getDouble("z"))
}

val OfflinePlayer.hangar: BuildingHangar
        get() = BuildingHangar.getHangar(this.uniqueId)!!


fun drawTo(c1: Location, c2: Location, p: Player){
    val c1 = c1.clone()
    val c2 = c2.clone()
    c1.add(1.0, 1.0, 1.0)
    c2.add(1.0, 1.0, 1.0)

    for(x in c1.blockX.toDouble().step(c2.blockX.toDouble(), 0.2)){
        p.spawnParticle(Particle.REDSTONE, Location(p.world, x, c1.blockY.toDouble(), c1.blockZ.toDouble()), 0, 1.0, 1.0, 1.0)
        p.spawnParticle(Particle.REDSTONE, Location(p.world, x, c2.blockY.toDouble(), c1.blockZ.toDouble()), 0, 1.0, 1.0, 1.0)
        p.spawnParticle(Particle.REDSTONE, Location(p.world, x, c1.blockY.toDouble(), c2.blockZ.toDouble()), 0, 1.0, 1.0, 1.0)
        p.spawnParticle(Particle.REDSTONE, Location(p.world, x, c2.blockY.toDouble(), c2.blockZ.toDouble()), 0, 1.0, 1.0, 1.0)
    }

    for(y in c1.blockY.toDouble().step(c2.blockY.toDouble(), 0.2)){
        p.spawnParticle(Particle.REDSTONE, Location(p.world, c1.blockX.toDouble(), y, c1.blockZ.toDouble()), 0, 1.0, 1.0, 1.0)
        p.spawnParticle(Particle.REDSTONE, Location(p.world, c2.blockX.toDouble(), y, c1.blockZ.toDouble()), 0, 1.0, 1.0, 1.0)
        p.spawnParticle(Particle.REDSTONE, Location(p.world, c1.blockX.toDouble(), y, c2.blockZ.toDouble()), 0, 1.0, 1.0, 1.0)
        p.spawnParticle(Particle.REDSTONE, Location(p.world, c2.blockX.toDouble(), y, c2.blockZ.toDouble()), 0, 1.0, 1.0, 1.0)
    }

    for(z in c1.blockZ.toDouble().step(c2.blockZ.toDouble(), 0.2)){
        p.spawnParticle(Particle.REDSTONE, Location(p.world, c1.blockX.toDouble(), c1.blockY.toDouble(), z), 0, 1.0, 1.0, 1.0)
        p.spawnParticle(Particle.REDSTONE, Location(p.world, c1.blockX.toDouble(), c2.blockY.toDouble(), z), 0, 1.0, 1.0, 1.0)
        p.spawnParticle(Particle.REDSTONE, Location(p.world, c2.blockX.toDouble(), c1.blockY.toDouble(), z), 0, 1.0, 1.0, 1.0)
        p.spawnParticle(Particle.REDSTONE, Location(p.world, c2.blockX.toDouble(), c2.blockY.toDouble(), z), 0, 1.0, 1.0, 1.0)
    }

}

fun World.fawe(): com.sk89q.worldedit.world.World = BukkitWorld(this)

fun FileConfiguration.setFaweVector(key: String, value: Vector) {
    val s = this.createSection(key)
    s.set("x", value.x)
    s.set("y", value.y)
    s.set("z", value.z)
}

fun Pair<Location, Location>.isInArea(location: Location): Boolean {
    val l1 = first
    val l2 = second
    return (location.x in l1.x..l2.x &&
            location.y in l1.y..l2.y &&
            location.z in l1.z..l2.z)
}

fun Double.step(to: Double, step: Double): Iterator<Double> {
    val it = ArrayList<Double>()
    var x = this
    while(x <= to){
        x += step
        it.add(x)
    }
    if(x != to){
        it.add(to)
    }
    return it.iterator()
}