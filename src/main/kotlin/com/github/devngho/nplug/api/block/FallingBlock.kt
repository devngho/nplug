package com.github.devngho.nplug.api.block

import com.github.devngho.nplug.impl.block.FallingBlockImpl
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

interface FallingBlock {
    companion object {
        fun createFallingBlock(material: Material, position: Location, plugin: JavaPlugin): FallingBlock {
            return FallingBlockImpl.createFallingBlock(material, position, plugin)
        }
    }
    val material: Material
    var position: Location
    val plugin: JavaPlugin
}