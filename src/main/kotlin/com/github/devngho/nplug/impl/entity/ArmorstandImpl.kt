package com.github.devngho.nplug.impl.entity

import com.github.devngho.nplug.api.entity.Armorstand
import com.github.devngho.nplug.impl.Setting
import com.github.devngho.nplug.impl.nms.NMSVersion.handle
import com.github.devngho.nplug.impl.nms.NMSVersion.toServerLevel
import com.github.devngho.nplug.impl.nms.NMSVersion.toServerPlayer
import com.mojang.datafixers.util.Pair
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class ArmorstandImpl internal constructor(
    override var position: Location,
    override val plugin: JavaPlugin,
    override val sendPlayers: MutableList<Player>,
    private val sentPlayers: MutableList<Player>
) : Armorstand {
    var entity: ArmorStand = ArmorStand(position.world.toServerLevel, position.x, position.y, position.z)
    val bukkitEntity: org.bukkit.entity.ArmorStand
    get() {
        return this.entity.bukkitEntity as org.bukkit.entity.ArmorStand
    }
    private var taskID: Int

    companion object{
        fun createArmorstand(position: Location, plugin: JavaPlugin, sendPlayers: MutableList<Player>): Armorstand {
            return ArmorstandImpl(position, plugin, sendPlayers, sendPlayers)
        }
    }
    init {
        entity.isMarker = true
        entity.isInvulnerable = true
        entity.isInvisible = true
        entity.isSmall = true
        entity.isNoBasePlate = true
        entity.setPos(position.x, position.y, position.z)
        for (player in sendPlayers) {
            player.handle.connection.send(entity.addEntityPacket)
            player.handle.connection.send(
                ClientboundSetPassengersPacket(
                    entity
                )
            )
            player.handle.connection.send(ClientboundTeleportEntityPacket(entity))
            player.handle.connection.send(ClientboundSetEquipmentPacket(entity.id, EquipmentSlot.values().map { Pair(it, entity.getItemBySlot(it)) }))
        }
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            entity.setPos(position.x, position.y, position.z)
            for (player in sendPlayers) {
                player.toServerPlayer.connection.send(
                    ClientboundSetEntityDataPacket(
                        entity.id,
                        entity.entityData,
                        true
                    )
                )
                player.handle.connection.send(ClientboundSetEntityDataPacket(entity.id, entity.entityData, true))
                player.handle.connection.send(ClientboundSetEquipmentPacket(entity.id, EquipmentSlot.values().map { Pair(it, entity.getItemBySlot(it)) }))
                player.handle.connection.send(ClientboundTeleportEntityPacket(entity))
            }
        }, 0, Setting.FallingRefreshTicks.toLong())
    }

    override fun remove() {
        Bukkit.getScheduler().cancelTask(taskID)
        val list = IntList.of(entity.id)
        val removePacket = ClientboundRemoveEntitiesPacket(list)
        for (player in sendPlayers) {
            player.handle.connection.send(removePacket)
        }
    }

    override fun refreshPlayers() {
        sendPlayers.subtract(sentPlayers.toSet()).forEach {
            it.handle.connection.send(entity.addEntityPacket)
            it.handle.connection.send(ClientboundTeleportEntityPacket(entity))
            it.handle.connection.send(ClientboundSetEquipmentPacket(entity.id, EquipmentSlot.values().map { Pair(it, entity.getItemBySlot(it)) }))
        }
        val list = IntList.of(entity.id)
        val removePacket = ClientboundRemoveEntitiesPacket(list)
        sentPlayers.subtract(sendPlayers.toSet()).forEach {
            it.toServerPlayer.connection.send(removePacket)
        }
        sentPlayers.clear()
        sendPlayers.forEach { sentPlayers.add(it) }
    }

    override fun refreshPlayersForce() {
        sendPlayers.forEach {
            it.handle.connection.send(entity.addEntityPacket)
            it.handle.connection.send(ClientboundTeleportEntityPacket(entity))
            it.handle.connection.send(ClientboundSetEquipmentPacket(entity.id, EquipmentSlot.values().map { Pair(it, entity.getItemBySlot(it)) }))
        }
        val list = IntList.of(entity.id)
        val removePacket = ClientboundRemoveEntitiesPacket(list)
        sendPlayers.forEach {
            it.toServerPlayer.connection.send(removePacket)
        }
        sentPlayers.clear()
        sendPlayers.forEach { sentPlayers.add(it) }
    }
}