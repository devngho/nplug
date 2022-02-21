package com.github.devngho.nplug.api.block

import com.github.devngho.nplug.impl.block.FallingBlockImpl
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
interface FallingBlock {
    companion object {
        fun createFallingBlock(material: Material, position: Location, plugin: JavaPlugin, collidable: Boolean, sendPlayers: MutableList<Player>): FallingBlock {
            return FallingBlockImpl.createFallingBlock(material, position, plugin, collidable, sendPlayers)
        }
    }

    /**
     * FallingBlock의 [Material]을 결정합니다.
     */
    val material: Material

    /**
     * FallingBlock의 [Location]을 결정합니다.
     */
    var position: Location

    /**
     * Scheduler 설정을 위해 플러그인을 받습니다.
     */
    val plugin: JavaPlugin

    /**
     * 셜커를 소환해 밟을 수 있도록 할 지 결정합니다.
     */
    val collidable: Boolean

    /**
     * 전송할 플레이어들을 설정합니다.
     * 플레이어를 변경한 이후에는 [refreshPlayers] 함수를 호출해야 합니다.
     */
    val sendPlayers: MutableList<Player>
    fun remove()

    /**
     *
     */
    fun refreshPlayers()
}