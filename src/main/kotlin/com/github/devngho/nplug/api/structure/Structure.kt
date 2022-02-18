package com.github.devngho.nplug.api.structure

import com.github.devngho.nplug.impl.structure.StructureImpl
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector

interface Structure {
    companion object{
        fun createFrom(world: World, pos1: Location, pos2: Location, center: Location): Structure {
            return StructureImpl.createFrom(world, pos1, pos2, center)
        }
        fun createFrom(blocks: MutableMap<Int, MutableMap<Int, MutableMap<Int, Pair<Vector, Material>>>>): Structure {
            return StructureImpl.createFrom(blocks)
        }
    }
    val blocks: MutableMap<Int, MutableMap<Int, MutableMap<Int, Pair<Vector, Material>>>>
}