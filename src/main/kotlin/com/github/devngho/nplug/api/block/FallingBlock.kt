package com.github.devngho.nplug.api.block

import com.github.devngho.nplug.impl.block.FallingBlockImpl
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
interface FallingBlock {
    companion object {
        fun createFallingBlock(material: Material, position: Location, plugin: JavaPlugin, collidable: Boolean): FallingBlock {
            return FallingBlockImpl.createFallingBlock(material, position, plugin, collidable)
        }
    }
    var material: Material
    var position: Location
    val plugin: JavaPlugin
    val collidable: Boolean
    fun remove()
}