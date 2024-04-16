package cc.fyre.shard.util.bukkit

import org.apache.commons.lang.WordUtils
import org.bukkit.GameMode

object GameModeUtil {

    private val displayNames = mutableMapOf<GameMode,String>()

    init {
        GameMode.values().forEach{
            this.displayNames[it] = WordUtils.capitalizeFully(it.name.lowercase().replace("_"," "))
        }
    }

    fun GameMode.getDisplayName():String {
        return displayNames[this] ?: this.name
    }

}