package com.github.devngho.nplug.impl.nms

import com.github.devngho.nplug.nms.CraftClass
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.state.BlockState
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import kotlin.reflect.full.memberFunctions

object NMSVersion {
    val nmsVersion by lazy {
        Bukkit.getServer()::class.java.packageName.split(".")[3]
    }
    val craftWorld by lazy {
        CraftClass.getClass(nmsVersion, "CraftWorld")
    }
    val craftBlockData by lazy {
        CraftClass.getClass(nmsVersion, "CraftBlockData")
    }
    val craftPlayer by lazy {
        CraftClass.getClass(nmsVersion, "CraftPlayer")
    }
    val World.toServerLevel: ServerLevel
    get() {
        return (craftWorld.cast(this))::class.memberFunctions.find { it.name == "getHandle" }?.call() as ServerLevel
    }
    val BlockData.toBlockState: BlockState
        get() {
            return (craftBlockData.cast(this))::class.memberFunctions.find { it.name == "getState" }?.call() as BlockState
        }
    val Player.toServerPlayer: ServerPlayer
        get() {
            return (craftPlayer.cast(this))::class.memberFunctions.find { it.name == "getHandle" }?.call() as ServerPlayer
        }
    val Player.handle: ServerPlayer
        get() {
            return this.toServerPlayer
        }
}