package com.github.devngho.nplug.nms

import org.reflections.Reflections

object CraftClass {
    fun getClass(version: String, name: String): Class<out Any>{
        val ref = Reflections("org.bukkit.craftbukkit.$version.$name")
        return ref.getSubTypesOf(Any::class.java).first()
    }
}