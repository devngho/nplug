package com.github.devngho.nplug.impl.entity

import com.github.devngho.nplug.api.entity.FallingBlock
import com.github.devngho.nplug.api.entity.FallingBlocks
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
    override fun remove() {
        fallingBlocks.forEach {
            it.second.remove()
        }
    }
}