package com.github.devngho.nplug.api.block

import com.github.devngho.nplug.impl.block.FallingBlocksImpl
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

@Suppress("unused")
interface FallingBlocks {
    companion object {
        fun createFallingBlocks(fallingBlock: MutableList<Pair<Vector, FallingBlock>>, position: Location, javaPlugin: JavaPlugin): FallingBlocks {
            return FallingBlocksImpl.createFallingBlocks(fallingBlock, position, javaPlugin)
        }
    }
    val fallingBlocks: MutableList<Pair<Vector, FallingBlock>>
    var position: Location
    val javaPlugin: JavaPlugin
    fun remove()
}