package com.github.devngho.nplug.nms

object CraftClass {
    fun getClass(version: String, name: String): Class<out Any>{
        return Class.forName("org.bukkit.craftbukkit.$version.$name")
    }
}