package cc.fyre.shard.util.bukkit

import org.apache.commons.lang.WordUtils
import org.bukkit.enchantments.Enchantment

/**
 * @project carnage
 *
 * @date 01/03/2021
 * @author xanderume@gmail.com
 */
object EnchantUtil {

    private val aliases = hashMapOf<Enchantment,Array<String>>()
    private val enchantsByName = hashMapOf<String,Enchantment>()

    init {
        this.aliases[Enchantment.LURE] = arrayOf("Lure")
        this.aliases[Enchantment.LUCK] = arrayOf("Luck","luckofthesea")

        this.aliases[Enchantment.THORNS] = arrayOf("Thorns")
        this.aliases[Enchantment.OXYGEN] = arrayOf("Respiration","oxygen")
        this.aliases[Enchantment.WATER_WORKER] = arrayOf("Aqua Affinity","aquaaffinity","waterworker")

        this.aliases[Enchantment.KNOCKBACK] = arrayOf("Knockback")
        this.aliases[Enchantment.DIG_SPEED] = arrayOf("Efficiency","digspeed")
        this.aliases[Enchantment.DURABILITY] = arrayOf("Unbreaking","durability")
        this.aliases[Enchantment.SILK_TOUCH] = arrayOf("Silk Touch","silktouch")
        this.aliases[Enchantment.FIRE_ASPECT] = arrayOf("Fire Aspect","fireaspect")

        this.aliases[Enchantment.ARROW_FIRE] = arrayOf("Flame")
        this.aliases[Enchantment.ARROW_DAMAGE] = arrayOf("Power")
        this.aliases[Enchantment.ARROW_INFINITE] = arrayOf("Infinity")
        this.aliases[Enchantment.ARROW_KNOCKBACK] = arrayOf("Punch")

        this.aliases[Enchantment.LOOT_BONUS_MOBS] = arrayOf("Looting")
        this.aliases[Enchantment.LOOT_BONUS_BLOCKS] = arrayOf("Fortune")

        this.aliases[Enchantment.DAMAGE_ALL] = arrayOf("Sharpness")
        this.aliases[Enchantment.DAMAGE_UNDEAD] = arrayOf("Smite")
        this.aliases[Enchantment.DAMAGE_ARTHROPODS] = arrayOf("Bane of Arthropods","baneofarthropods")

        this.aliases[Enchantment.PROTECTION_FIRE] = arrayOf("Fire Protection","fireprotection")
        this.aliases[Enchantment.PROTECTION_FALL] = arrayOf("Feather Falling","featherfalling")
        this.aliases[Enchantment.PROTECTION_EXPLOSIONS] = arrayOf("Blast Protection","blastprotection")
        this.aliases[Enchantment.PROTECTION_PROJECTILE] = arrayOf("Projectile Protection","projectileprotection")
        this.aliases[Enchantment.PROTECTION_ENVIRONMENTAL] = arrayOf("Protection","protection")
        this.aliases[Enchantment.DEPTH_STRIDER] = arrayOf("Depth Strider","depthstrider")

        this.aliases.entries.forEach{(key,value) ->
            this.enchantsByName[key.name.lowercase()] = key
            value.forEach{alias -> this.enchantsByName[alias.lowercase()] = key}
        }
    }

    @JvmStatic
    fun getName(enchantment: Enchantment):String {

        var aliases = this.aliases[enchantment]

        if (!aliases.isNullOrEmpty()) {
            return aliases[0]
        }

        aliases = arrayOf(WordUtils.capitalizeFully(enchantment.name.lowercase().replace("_"," "))).also{
            this.aliases[enchantment] = it
        }

        return aliases[0]
    }

    @JvmStatic
    fun addEnchant(enchantment: Enchantment,displayName: String,vararg aliases: String = arrayOf()) {

        val field = Enchantment::class.java.getDeclaredField("acceptingNew")

        field.isAccessible = true
        field.set(null,true)

        try {
            Enchantment.registerEnchantment(enchantment)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        field.set(null,false)
        field.isAccessible = false

        this.aliases[enchantment] = arrayOf(displayName).plus(aliases)
        this.aliases[enchantment]!!.forEach{alias -> this.enchantsByName[alias.lowercase()] = enchantment}
        this.enchantsByName[enchantment.name.lowercase()] = enchantment
    }

    @JvmStatic
    fun getByName(name: String):Enchantment? {
        return this.enchantsByName[name.lowercase()]
    }

    @JvmStatic
    fun getAliasesByEnchant(enchantment: Enchantment):Array<String> {
        return this.aliases[enchantment] ?: arrayOf()
    }
}