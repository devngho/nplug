package com.github.devngho.nplug.impl.block

import com.github.devngho.nplug.api.block.FallingBlock
import com.github.devngho.nplug.api.block.FallingBlocks
import com.github.devngho.nplug.api.structure.Structure
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

class FallingBlocksImpl internal constructor(
    override var fallingBlocks: MutableList<Pair<Vector, FallingBlock>>,
    override var position: Location,
    override val javaPlugin: JavaPlugin
) : FallingBlocks {
    companion object {
        fun createFallingBlocks(fallingBlock: MutableList<Pair<Vector, FallingBlock>>, position: Location, javaPlugin: JavaPlugin): FallingBlocks {
            return FallingBlocksImpl(fallingBlock, position, javaPlugin)
        }
        fun createFallingBlocks(structure: Structure, position: Location, javaPlugin: JavaPlugin): FallingBlocks {
            val fallingBlocks = structure.blocks.map {
                val returnValue = mutableListOf<Pair<Vector, FallingBlock>>()
                it.value.forEach { t ->
                    t.value.forEach  { v ->
                        returnValue.add(Pair(
                            v.value.first,
                            FallingBlock.createFallingBlock(v.value.second, position, javaPlugin)
                        ))
                    }
                }
                returnValue
            }.flatten().toMutableList()
            return createFallingBlocks(fallingBlocks, position, javaPlugin)
        }
    }
    init {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, {
            fallingBlocks = fallingBlocks.map {
                it.second.position = it.second.position.apply {
                    x = position.x + it.first.x
                    y = position.y + it.first.y
                    z = position.z + it.first.z
                }
                Pair(it.first, it.second)
            }.toMutableList()
        }, 0, 1)
    }
}