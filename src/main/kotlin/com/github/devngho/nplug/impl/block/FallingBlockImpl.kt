package com.github.devngho.nplug.impl.block

import com.github.devngho.nplug.api.block.FallingBlock
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.world.entity.item.FallingBlockEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer
import org.bukkit.plugin.java.JavaPlugin

class FallingBlockImpl internal constructor(
    override var material: Material,
    override var position: Location,
    override val plugin: JavaPlugin
) : FallingBlock {
    private var entity: FallingBlockEntity =
        FallingBlockEntity((position.world as CraftWorld).handle, position.x, position.y, position.z, (material.createBlockData() as CraftBlockData).state)

    companion object{
        fun createFallingBlock(material: Material, position: Location, plugin: JavaPlugin): FallingBlock {
            return FallingBlockImpl(material, position, plugin)
        }
    }
    init {
        val packet = ClientboundAddEntityPacket(entity)
        for (player in Bukkit.getOnlinePlayers()) {
            (player as CraftPlayer).handle.connection.send(packet)
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {

        }, 0, 1)
    }
}