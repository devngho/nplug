package com.github.devngho.nplug.impl.block

import com.github.devngho.nplug.api.block.FallingBlock
import com.github.devngho.nplug.impl.Setting
import com.github.devngho.nplug.impl.nms.NMSVersion.handle
import com.github.devngho.nplug.impl.nms.NMSVersion.toBlockState
import com.github.devngho.nplug.impl.nms.NMSVersion.toServerLevel
import com.github.devngho.nplug.impl.nms.NMSVersion.toServerPlayer
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.entity.monster.Shulker
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
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
    private var standEntity: ArmorStand = ArmorStand(position.world.toServerLevel, position.x, position.y, position.z)
    private var entity: FallingBlockEntity =
        FallingBlockEntity(position.world.toServerLevel, position.x, position.y, position.z, material.createBlockData().toBlockState)
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
            shulkerEntity = Shulker(EntityType.SHULKER, position.world.toServerLevel)
            shulkerEntity!!.isInvisible = true
            shulkerEntity!!.isInvulnerable = true
            shulkerEntity!!.isNoAi = true
            shulkerEntity!!.startRiding(standEntity, true)
        }
        for (player in sendPlayers) {
            player.handle.connection.send(standEntity.addEntityPacket)
            player.handle.connection.send(entity.addEntityPacket)
            if (collidable) player.handle.connection.send(shulkerEntity!!.addEntityPacket)
            player.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            player.handle.connection.send(ClientboundSetPassengersPacket(
                standEntity
            ))
            if (collidable) player.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            player.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            player.handle.connection.send(ClientboundSetPassengersPacket(
                standEntity
            ))
            if (collidable) player.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            player.handle.connection.send(ClientboundTeleportEntityPacket(standEntity))
        }
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            standEntity.setPos(position.x, position.y, position.z)
            for (player in sendPlayers) {
                player.toServerPlayer.connection.send(
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
                player.handle.connection.send(
                    ClientboundSetPassengersPacket(
                        standEntity
                    )
                )
                player.handle.connection.send(
                    ClientboundSetPassengersPacket(
                        standEntity
                    )
                )
                if (collidable) player.handle.connection.send(
                    ClientboundSetPassengersPacket(
                        standEntity
                    )
                )
                player.handle.connection.send(ClientboundTeleportEntityPacket(standEntity))
            }
        }, 0, Setting.FallingRefreshTicks.toLong())
    }

    override fun remove() {
        Bukkit.getScheduler().cancelTask(taskID)
        var list = IntList.of(entity.id, standEntity.id)
        if (collidable) list = IntList.of(entity.id, standEntity.id, shulkerEntity!!.id)
        val removePacket = ClientboundRemoveEntitiesPacket(list)
        for (player in sendPlayers) {
            player.handle.connection.send(removePacket)
        }
    }

    override fun refreshPlayers() {
        sendPlayers.subtract(sentPlayers.toSet()).forEach {
            it.handle.connection.send(standEntity.addEntityPacket)
            it.handle.connection.send(entity.addEntityPacket)
            if (collidable) it.handle.connection.send(shulkerEntity!!.addEntityPacket)
            it.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            it.handle.connection.send(ClientboundSetPassengersPacket(
                standEntity
            ))
            if (collidable) it.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            it.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            it.handle.connection.send(ClientboundSetPassengersPacket(
                standEntity
            ))
            if (collidable) it.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            it.handle.connection.send(ClientboundTeleportEntityPacket(standEntity))
        }
        var list = IntList.of(entity.id, standEntity.id)
        if (collidable) list = IntList.of(entity.id, standEntity.id, shulkerEntity!!.id)
        val removePacket = ClientboundRemoveEntitiesPacket(list)
        sentPlayers.subtract(sendPlayers.toSet()).forEach {
            it.toServerPlayer.connection.send(removePacket)
        }
        sentPlayers.clear()
        sendPlayers.forEach { sentPlayers.add(it) }
    }

    override fun refreshPlayersForce() {
        sendPlayers.forEach {
            it.handle.connection.send(standEntity.addEntityPacket)
            it.handle.connection.send(entity.addEntityPacket)
            if (collidable) it.handle.connection.send(shulkerEntity!!.addEntityPacket)
            it.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            it.handle.connection.send(ClientboundSetPassengersPacket(
                standEntity
            ))
            if (collidable) it.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            it.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            it.handle.connection.send(ClientboundSetPassengersPacket(
                standEntity
            ))
            if (collidable) it.handle.connection.send(
                ClientboundSetPassengersPacket(
                    standEntity
                )
            )
            it.handle.connection.send(ClientboundTeleportEntityPacket(standEntity))
        }
        var list = IntList.of(entity.id, standEntity.id)
        if (collidable) list = IntList.of(entity.id, standEntity.id, shulkerEntity!!.id)
        val removePacket = ClientboundRemoveEntitiesPacket(list)
        sendPlayers.forEach {
            it.toServerPlayer.connection.send(removePacket)
        }
        sentPlayers.clear()
        sendPlayers.forEach { sentPlayers.add(it) }
    }
}