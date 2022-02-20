package com.github.devngho.nplug.impl.block

import com.github.devngho.nplug.api.block.FallingBlock
import com.github.devngho.nplug.impl.Setting
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.entity.monster.Shulker
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
    override val plugin: JavaPlugin,
    override val collidable: Boolean
) : FallingBlock {
    private var entity: FallingBlockEntity =
        FallingBlockEntity((position.world as CraftWorld).handle, position.x, position.y, position.z, (material.createBlockData() as CraftBlockData).state)
    private var shulkerEntity: Shulker? = null
    private var taskID: Int

    companion object{
        fun createFallingBlock(material: Material, position: Location, plugin: JavaPlugin, collidable: Boolean): FallingBlock {
            return FallingBlockImpl(material, position, plugin, collidable)
        }
    }
    init {
        entity.isNoGravity = true
        entity.isInvulnerable = true
        entity.dropItem = false
        if (collidable){
            shulkerEntity = Shulker(EntityType.SHULKER, (position.world as CraftWorld).handle)
            shulkerEntity!!.isInvisible = true
            shulkerEntity!!.isInvulnerable = true
            shulkerEntity!!.isNoAi = true
        }
        for (player in Bukkit.getOnlinePlayers()) {
            (player as CraftPlayer).handle.connection.send(entity.addEntityPacket)
            if (collidable) player.handle.connection.send(shulkerEntity!!.addEntityPacket)
        }
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,{
            if (entity.blockState.bukkitMaterial != material){
                val list = IntList.of(entity.id)
                if (collidable) list.add(shulkerEntity!!.id)
                val removePacket = ClientboundRemoveEntitiesPacket(list)
                for (player in Bukkit.getOnlinePlayers()) {
                    (player as CraftPlayer).handle.connection.send(removePacket)
                }
                entity = FallingBlockEntity(
                    (position.world as CraftWorld).handle, position.x, position.y, position.z, (material.createBlockData() as CraftBlockData).state
                )
                entity.isNoGravity = true
                entity.hurtEntities = false
                entity.isInvulnerable = true
                entity.dropItem = false
                if (collidable) {
                    shulkerEntity = Shulker(EntityType.SHULKER, (position.world as CraftWorld).handle)
                    shulkerEntity!!.isInvisible = true
                    shulkerEntity!!.isInvulnerable = true
                    shulkerEntity!!.isNoAi = true
                    shulkerEntity!!.isNoGravity = true
                }
                for (player in Bukkit.getOnlinePlayers()) {
                    (player as CraftPlayer).handle.connection.send(entity.addEntityPacket)
                    if (collidable) player.handle.connection.send(shulkerEntity!!.addEntityPacket)
                }
            }
            entity.setPos(position.x, position.y, position.z)
            shulkerEntity?.setPos(position.x, position.y, position.z)
            for (player in Bukkit.getOnlinePlayers()) {
                (player as CraftPlayer).handle.connection.send(ClientboundSetEntityDataPacket(entity.id, entity.entityData, true))
                if (collidable) player.handle.connection.send(ClientboundSetEntityDataPacket(shulkerEntity!!.id, shulkerEntity!!.entityData, true))
            }
        }, 0, Setting.FallingRefreshTicks.toLong())
    }

    override fun remove() {
        Bukkit.getScheduler().cancelTask(taskID)
        val list = IntList.of(entity.id)
        if (collidable) list.add(shulkerEntity!!.id)
        val removePacket = ClientboundRemoveEntitiesPacket(list)
        for (player in Bukkit.getOnlinePlayers()) {
            (player as CraftPlayer).handle.connection.send(removePacket)
        }
    }
}