package com.github.devngho.nplug.api.block

import com.github.devngho.nplug.api.structure.Structure
import com.github.devngho.nplug.impl.block.FallingBlocksImpl
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

interface FallingBlocks {
    companion object {
        fun createFallingBlocks(fallingBlock: MutableList<Pair<Vector, FallingBlock>>, position: Location, javaPlugin: JavaPlugin): FallingBlocks {
            return FallingBlocksImpl.createFallingBlocks(fallingBlock, position, javaPlugin)
        }
        fun createFallingBlocks(structure: Structure, position: Location, javaPlugin: JavaPlugin): FallingBlocks {
            return FallingBlocksImpl.createFallingBlocks(structure, position, javaPlugin)
        }
    }
    val fallingBlocks: MutableList<Pair<Vector, FallingBlock>>
    var position: Location
    val javaPlugin: JavaPlugin
}