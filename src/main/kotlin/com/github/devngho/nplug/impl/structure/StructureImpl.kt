package com.github.devngho.nplug.impl.structure

import com.github.devngho.nplug.api.structure.Structure
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector
import kotlin.math.max
import kotlin.math.min

class StructureImpl internal constructor(override val blocks: MutableList<Pair<Vector, Material>>) :
    Structure {
    companion object {
        fun createFrom(world: World, pos1: Location, pos2: Location, center: Location) : Structure {
            val start = Location(pos1.world, min(pos1.x, pos2.x), min(pos1.y, pos2.y), min(pos1.z, pos2.z))
            val end = Location(pos2.world, max(pos1.x, pos2.x), max(pos1.y, pos2.y), max(pos1.z, pos2.z))
            val blocks = mutableListOf<Pair<Vector, Material>> ()
            for (x in start.blockX..end.blockX){
                for (y in start.blockY..end.blockY){
                    for (z in start.blockZ..end.blockZ){
                        val xC = x - center.blockX
                        val yC = y - center.blockY
                        val zC = z - center.blockZ
                        val mat = world.getBlockAt(x, y, z).type
                        val posPair = Pair(Vector(xC, yC, zC), mat)
                        blocks.add(posPair)
                    }
                }
            }
            return StructureImpl(blocks)
        }
        fun createFrom(blocks: MutableList<Pair<Vector, Material>>): StructureImpl {
            return StructureImpl(blocks)
        }
    }
}