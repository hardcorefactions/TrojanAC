package net.valorhcf.trojan.util.bukkit

import cc.fyre.shard.util.entity.EntityUtil
import net.valorhcf.trojan.util.item.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

/**
 * @project carnage
 *
 * @date 03/14/21
 * @author xanderume@gmail.com
 */
object SpawnerUtil {

    fun isSpawner(item: ItemStack):Boolean {
        return item.type == Material.MOB_SPAWNER
                && item.hasItemMeta()
                && item.itemMeta.hasDisplayName()
                && item.itemMeta.lore.size == 2
    }

    fun create(type: EntityType,delay: Int = 100):ItemStack {
        return ItemBuilder.of(Material.MOB_SPAWNER)
            .name("${ChatColor.WHITE}${EntityUtil.getName(type)} Spawner")
            .lore(
                "${ChatColor.DARK_GRAY}${type.name}",
                "${ChatColor.DARK_GRAY}$delay",
            )
            .build()
    }

}