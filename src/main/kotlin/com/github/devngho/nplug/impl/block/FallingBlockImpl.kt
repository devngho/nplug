package com.github.devngho.nplug.impl.block

import com.github.devngho.nplug.api.block.FallingBlock
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.FallingBlockEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_18_R1.util.CraftMagicNumbers
import org.bukkit.plugin.java.JavaPlugin

class FallingBlockImpl internal constructor(
    override var material: Material,
    override var position: Location,
    override val plugin: JavaPlugin
) : FallingBlock {
    private var entity: FallingBlockEntity =
        FallingBlockEntity((position.world as CraftWorld).handle, position.x, position.y, position.z, CraftMagicNumbers.getBlock(material).defaultBlockState())

    companion object{
        fun createFallingBlock(material: Material, position: Location, plugin: JavaPlugin): FallingBlock {
            return FallingBlockImpl(material, position, plugin)
        }
    }
    init {
        entity.isNoGravity = true
        entity.isInvulnerable = true
        entity.dropItem = false
        entity.time = 1
        val spawnPacket = ClientboundAddEntityPacket(entity)
        for (player in Bukkit.getOnlinePlayers()) {
            (player as CraftPlayer).handle.connection.send(spawnPacket)
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,{
            entity.time = 1
            entity.setPos(position.x, position.y, position.z)
            if (material != entity.blockState.bukkitMaterial){
                entity.remove(Entity.RemovalReason.KILLED)
                entity = FallingBlockEntity(
                    (position.world as CraftWorld).handle, position.x, position.y, position.z, CraftMagicNumbers.getBlock(material).defaultBlockState()
                )
                val packet = ClientboundAddEntityPacket(entity)
                for (player in Bukkit.getOnlinePlayers()) {
                    (player as CraftPlayer).handle.connection.send(packet)
                }
            }else {
                val packet = ClientboundSetEntityDataPacket(entity.id, entity.entityData, true)
                for (player in Bukkit.getOnlinePlayers()) {
                    (player as CraftPlayer).handle.connection.send(packet)
                }
            }
        }, 0, 1)
    }
}