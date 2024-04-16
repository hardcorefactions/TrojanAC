package cc.fyre.shard.util.item

import net.valorhcf.trojan.util.item.ItemUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @project carnage
 *
 * @date 28/02/2021
 * @author xanderume@gmail.com
 */
data class Item(val material: Material,val data: Short) {

    fun getName():String {
        return ItemUtil.getName(this.toItemStack())
    }

    fun matches(item: ItemStack):Boolean {
        return this.material == item.type && this.data == item.durability
    }

    fun toItemStack():ItemStack {
        return ItemStack(this.material,1,this.data)
    }

}