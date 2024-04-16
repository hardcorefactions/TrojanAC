package cc.fyre.shard.util.bukkit

import org.bukkit.Location
import org.bukkit.util.BlockIterator

object PathUtil {

    fun isObstructed(start: Location,finish: Location): Boolean {

        if (start.distanceSquared(finish) <= 4.0) {
            return false
        }

        val direction = finish.toVector().subtract(start.toVector())

        try {

            val iterator = BlockIterator(start.world,start.toVector(),direction,0.0,100)

            for (block in iterator) {

                if (block.location == finish.block.location) {
                    return false
                }

                if (!block.type.isTransparent && !block.isLiquid && !block.isEmpty) {
                    return true
                }

            }

        } catch (ignored: Exception) {}

        return false
    }

}