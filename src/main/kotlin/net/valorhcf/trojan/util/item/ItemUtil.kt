package net.valorhcf.trojan.util.item

import cc.fyre.shard.util.NumberUtil
import cc.fyre.shard.util.item.Item
import net.valorhcf.trojan.Trojan
import org.apache.commons.io.IOUtils
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @project carnage
 *
 * @date 28/02/2021
 * @author xanderume@gmail.com
 */
object ItemUtil {

    private val itemsByName = hashMapOf<String, Item>()
    private val customItems = hashMapOf<String,ItemStack>()

    fun init() {
        IOUtils.readLines(Trojan::class.java.classLoader.getResourceAsStream("items.csv")).forEach{

            if (it[0] == '#') {
                return @forEach
            }

            Shard.AI.getNmsAI().loadItem(it).also{pair ->
                itemsByName[pair.first] = pair.second
            }
        }
    }

    fun register(item: ItemStack,name: String) {
        customItems[name.lowercase()] = item
    }

    fun getByName(name: String):ItemStack? {

        val lowercase = name.lowercase()

        if (customItems.containsKey(lowercase)) {
            return customItems[lowercase]
        }

        val input = name.replace(" ","")

        if (NumberUtil.isInteger(input)) {
            return ItemStack(Material.getMaterial(input.toInt()))
        }

        if (input.contains(":")) {

            val split = input.split(":")

            if (itemsByName.containsKey(split[0])) {
                return ItemStack(itemsByName[split[0].lowercase()]!!.material,1,split[1].toShort())
            }

            if (NumberUtil.isInteger(split[0])) {
                return ItemStack(Material.getMaterial(input.split(":")[0].toInt()),1,input.split(":")[1].toShort())
            }

            return null
        }

        return itemsByName[input]?.toItemStack()
    }

    @JvmStatic
    fun getName(item: ItemStack):String {
        return Shard.AI.getNmsAI().getName(item)
    }

    @JvmStatic
    fun isSword(item: ItemStack):Boolean {
        return item.type == Material.WOOD_SWORD
                || item.type == Material.STONE_SWORD
                || item.type == Material.IRON_SWORD
                || item.type == Material.GOLD_SWORD
                || item.type == Material.DIAMOND_SWORD
    }

    @JvmStatic
    fun setSkullTexture(itemStack: ItemStack,texture: String):ItemStack {
        return Shard.AI.getNmsAI().setSkullTexture(itemStack,texture)
    }

    @JvmStatic
    fun setMetadata(item: ItemStack,vararg pair: Pair<String,Any>):ItemStack {
        return Shard.AI.getNmsAI().setItemMetadata(item,*pair)
    }

    @JvmStatic
    fun <T> getMetadata(item: ItemStack,key: String,clazz: Class<T>):T? {
        return Shard.AI.getNmsAI().getItemMetadata(item,key,clazz)
    }

}