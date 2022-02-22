package com.github.devngho.nplug.impl.block

import com.github.devngho.nplug.api.block.FallingBlock
import com.github.devngho.nplug.impl.Setting
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.entity.monster.Shulker
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class FallingBlockImpl internal constructor(
    override val material: Material,
    override var position: Location,
    override val plugin: JavaPlugin,
    override val collidable: Boolean,
    override val sendPlayers: MutableList<Player>,
    private val sentPlayers: MutableList<Player>
) : FallingBlock {
    private var standEntity: ArmorStand = ArmorStand((position.world as CraftWorld).handle, position.x, position.y, position.z)
    private var entity: FallingBlockEntity =
        FallingBlockEntity((position.world as CraftWorld).handle, position.x, position.y, position.z, (material.createBlockData() as CraftBlockData).state)
    private var shulkerEntity: Shulker? = null
    private var taskID: Int

    companion object{
        fun createFallingBlock(material: Material, position: Location, plugin: JavaPlugin, collidable: Boolean, sendPlayers: MutableList<Player>): FallingBlock {
            return FallingBlockImpl(material, position, plugin, collidable, sendPlayers, sendPlayers)
        }
    }
    init {
        entity.isNoGravity = true
        entity.isInvulnerable = true
        entity.dropItem = false
        standEntity.isMarker = true
        standEntity.isInvulnerable = true
        standEntity.isInvisible = true
        standEntity.isSmall = true
        standEntity.isNoBasePlate = true
        standEntity.setPos(position.x, position.y, position.z)
        entity.setPos(position.x, position.y, position.z)
        shulkerEntity?.setPos(position.x, position.y, position.z)
        entity.startRiding(standEntity, true)
        if (collidable){
            shulkerEntity = Shulker(EntityType.SHULKER, (position.world as CraftWorld).handle)
            shulkerEntity!!.isInvisible = true
            shulkerEntity!!.isInvulnerable = true
            shulkerEntity!!.isNoAi = true
            shulkerEntity!!.startRiding(standEntity, true)
        }
        for (player in sendPlayers) {
            (player as CraftPlayer).handle.connection.send(standEntity.addEntityPacket)
            player.handle.connection.send(entity.addEntityPacket)
            if (collidable) player.handle.connection.send(shulkerEntity!!.addEntityPacket)
            player.handle.connection.send(
                ClientboundSetEntityDataPacket(
                    standEntity.id,
                    standEntity.entityData,
                    true
                )
            )
            player.handle.connection.send(ClientboundSetEntityDataPacket(entity.id, entity.entityData, true))
            if (collidable) player.handle.connection.send(
                ClientboundSetEntityDataPacket(
                    shulkerEntity!!.id,
                    shulkerEntity!!.entityData,
                    true
                )
            )
            player.handle.connection.send(ClientboundTeleportEntityPacket(standEntity))
            player.handle.connection.send(ClientboundTeleportEntityPacket(entity))
            if (collidable) player.handle.connection.send(ClientboundTeleportEntityPacket(shulkerEntity!!))
        }
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,{
            if (standEntity.x != position.x || standEntity.y != position.y || standEntity.z != position.z) {
                standEntity.setPos(position.x, position.y, position.z)
                for (player in sendPlayers) {
                    (player as CraftPlayer).handle.connection.send(
                        ClientboundSetEntityDataPacket(
                            standEntity.id,
                            standEntity.entityData,
                            true
                        )
                    )
                    player.handle.connection.send(ClientboundSetEntityDataPacket(entity.id, entity.entityData, true))
                    if (collidable) player.handle.connection.send(
                        ClientboundSetEntityDataPacket(
                            shulkerEntity!!.id,
                            shulkerEntity!!.entityData,
                            true
                        )
                    )
                    player.handle.connection.send(ClientboundTeleportEntityPacket(standEntity))
                    player.handle.connection.send(ClientboundTeleportEntityPacket(entity))
                    if (collidable) player.handle.connection.send(ClientboundTeleportEntityPacket(shulkerEntity!!))
                }
            }
        }, 0, Setting.FallingRefreshTicks.toLong())
    }

    override fun remove() {
        Bukkit.getScheduler().cancelTask(taskID)
        val list = IntList.of(entity.id, standEntity.id)
        if (collidable) list.add(shulkerEntity!!.id)
        val removePacket = ClientboundRemoveEntitiesPacket(list)
        for (player in Bukkit.getOnlinePlayers()) {
            (player as CraftPlayer).handle.connection.send(removePacket)
        }
    }

    override fun refreshPlayers() {
        sendPlayers.subtract(sentPlayers.toSet()).forEach {
            (it as CraftPlayer).handle.connection.send(standEntity.addEntityPacket)
            it.handle.connection.send(entity.addEntityPacket)
            if (collidable) it.handle.connection.send(shulkerEntity!!.addEntityPacket)
        }
        val list = IntList.of(entity.id, standEntity.id)
        if (collidable) list.add(shulkerEntity!!.id)
        val removePacket = ClientboundRemoveEntitiesPacket(list)
        sentPlayers.subtract(sendPlayers.toSet()).forEach {
            (it as CraftPlayer).handle.connection.send(removePacket)
        }
        sentPlayers.clear()
        sendPlayers.forEach { sentPlayers.add(it) }
    }

    override fun refreshPlayersForce() {
        sendPlayers.forEach {
            (it as CraftPlayer).handle.connection.send(standEntity.addEntityPacket)
            it.handle.connection.send(entity.addEntityPacket)
            if (collidable) it.handle.connection.send(shulkerEntity!!.addEntityPacket)
        }
        val list = IntList.of(entity.id, standEntity.id)
        if (collidable) list.add(shulkerEntity!!.id)
        val removePacket = ClientboundRemoveEntitiesPacket(list)
        sendPlayers.forEach {
            (it as CraftPlayer).handle.connection.send(removePacket)
        }
        sentPlayers.clear()
        sendPlayers.forEach { sentPlayers.add(it) }
    }
}