package cc.fyre.shard.util.bukkit

import org.apache.commons.lang.WordUtils
import org.bukkit.World

object WorldUtil {

    private val displayNames = mutableMapOf<World.Environment,String>()

    init {
        World.Environment.values().forEach{
            this.displayNames[it] = WordUtils.capitalizeFully(it.name.lowercase().replace("_"," "))
        }
    }

    fun World.getDisplayName():String {
        return displayNames[this.environment] ?: this.environment.name
    }

    fun World.Environment.getDisplayName():String {
        return displayNames[this] ?: this.name
    }

}