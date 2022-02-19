package com.github.devngho.nplug.impl.block

import com.github.devngho.nplug.api.block.FallingBlock
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.world.entity.item.FallingBlockEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer
import org.bukkit.entity.Shulker
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
            shulkerEntity = position.world.spawn(position, org.bukkit.entity.Shulker::class.java)
            shulkerEntity!!.setGravity(false)
            shulkerEntity!!.isInvulnerable = true
            shulkerEntity!!.setAI(false)
            shulkerEntity!!.isInvisible = true
        }
        for (player in Bukkit.getOnlinePlayers()) {
            (player as CraftPlayer).handle.connection.send(entity.addEntityPacket)
        }
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,{
            val removePacket = ClientboundRemoveEntitiesPacket(IntList.of(entity.id))
            for (player in Bukkit.getOnlinePlayers()) {
                (player as CraftPlayer).handle.connection.send(removePacket)
            }
            if (collidable) shulkerEntity!!.remove()
            entity = FallingBlockEntity(
                (position.world as CraftWorld).handle, position.x, position.y, position.z, (material.createBlockData() as CraftBlockData).state
            )
            if (collidable) {
                shulkerEntity = position.world.spawn(position, org.bukkit.entity.Shulker::class.java)
            }
            entity.isNoGravity = true
            entity.isInvulnerable = true
            entity.dropItem = false
            if (collidable) {
                shulkerEntity!!.setGravity(false)
                shulkerEntity!!.isInvulnerable = true
                shulkerEntity!!.setAI(false)
                shulkerEntity!!.isInvisible = true
            }
            for (player in Bukkit.getOnlinePlayers()) {
                (player as CraftPlayer).handle.connection.send(entity.addEntityPacket)
            }
        }, 0, 1)
    }

    override fun remove() {
        Bukkit.getScheduler().cancelTask(taskID)
        val removePacket = ClientboundRemoveEntitiesPacket(IntList.of(entity.id))
        for (player in Bukkit.getOnlinePlayers()) {
            (player as CraftPlayer).handle.connection.send(removePacket)
        }
        if (collidable) shulkerEntity!!.remove()
    }
}