package com.github.devngho.nplug.api.entity

import com.github.devngho.nplug.impl.entity.ArmorstandImpl
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
interface Armorstand {
    companion object {
        fun createArmorstand(position: Location, plugin: JavaPlugin, sendPlayers: MutableList<Player>): Armorstand {
            return ArmorstandImpl.createArmorstand(position, plugin, sendPlayers)
        }
    }

    /**
     * Armorstand 의 [Location]을 결정합니다.
     */
    var position: Location

    /**
     * Scheduler 설정을 위해 플러그인을 받습니다.
     */
    val plugin: JavaPlugin

    /**
     * 전송할 플레이어들을 설정합니다.
     * 플레이어를 변경한 이후에는 [refreshPlayers] 함수를 호출해야 합니다.
     */
    val sendPlayers: MutableList<Player>
    fun remove()

    fun refreshPlayers()
    fun refreshPlayersForce()
}