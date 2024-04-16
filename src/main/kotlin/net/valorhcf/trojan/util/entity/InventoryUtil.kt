package cc.fyre.shard.util.entity

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionType

/**
 * @project carnage
 *
 * @date 28/02/2021
 * @author xanderume@gmail.com
 */
object InventoryUtil {

    @JvmStatic
    fun getAvailableSlots(inventory: Inventory):Int {
        return inventory.contents.filter{it == null || it.type == Material.AIR}.count()
    }

    fun addItem(player: Player, item: ItemStack) {
        this.addItem(player,arrayOf(item))
    }

    fun addItem(player: Player, array: Array<ItemStack>) {

        if (this.getAvailableSlots(player.inventory) > array.size) {
            player.inventory.addItem(*array)
            return
        }

        var j = 0

        for (i in 0 until player.inventory.size) {

            if (j >= array.size) {
                break
            }

            val slot = player.inventory.getItem(i)

            if (slot == null || slot.type != Material.POTION) {
                continue
            }

            val potion = Potion.fromItemStack(slot)

            if (potion.type != PotionType.INSTANT_HEAL) {
                continue
            }

            player.inventory.setItem(i,array[j++])
        }

    }

}