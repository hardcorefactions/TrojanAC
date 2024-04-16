package cc.fyre.shard.util.entity

import cc.fyre.shard.Shard
import cc.fyre.shard.util.bukkit.NMSUtil
import net.minecraft.server.v1_8_R3.EntityTypes
import org.apache.commons.lang.WordUtils
import org.bukkit.Chunk
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity

/**
 * @project carnage
 *
 * @date 15/02/2021
 * @author xanderume@gmail.com
 */
object EntityUtil {

    private val names = hashMapOf<EntityType,String>()
    private var count = Integer.MAX_VALUE
    /*private val ENTITY_COUNT_FIELD = NMSUtil.getNMSClass("Entity").getDeclaredField("entityCount")

    init {
        ENTITY_COUNT_FIELD.isAccessible = true
    }*/

    init {
        names[EntityType.ARROW] = "Arrow"
        names[EntityType.BAT] = "Bat"
        names[EntityType.BLAZE] = "Blaze"
        names[EntityType.BOAT] = "Boat"
        names[EntityType.CAVE_SPIDER] = "Cave Spider"
        names[EntityType.CHICKEN] = "Chicken"
        names[EntityType.COW] = "Cow"
        names[EntityType.CREEPER] = "Creeper"
        names[EntityType.DROPPED_ITEM] = "Item"
        names[EntityType.EGG] = "Egg"
        names[EntityType.ENDER_CRYSTAL] = "Ender Crystal"
        names[EntityType.ENDER_DRAGON] = "Ender Dragon"
        names[EntityType.ENDER_PEARL] = "Ender Pearl"
        names[EntityType.ENDER_SIGNAL] = "Ender Signal"
        names[EntityType.ENDERMAN] = "Enderman"
        names[EntityType.EXPERIENCE_ORB] = "Experience Orb"
        names[EntityType.FALLING_BLOCK] = "Falling Block"
        names[EntityType.FIREBALL] = "Fireball"
        names[EntityType.FIREWORK] = "Firework"
        names[EntityType.FISHING_HOOK] = "Fishing Rod Hook"
        names[EntityType.GHAST] = "Ghast"
        names[EntityType.GIANT] = "Giant"
        names[EntityType.HORSE] = "Horse"
        names[EntityType.IRON_GOLEM] = "Iron Golem"
        names[EntityType.ITEM_FRAME] = "Item Frame"
        names[EntityType.LEASH_HITCH] = "Lead Hitch"
        names[EntityType.LIGHTNING] = "Lightning"
        names[EntityType.MAGMA_CUBE] = "Magma Cube"
        names[EntityType.MINECART] = "Minecart"
        names[EntityType.MINECART_CHEST] = "Chest Minecart"
        names[EntityType.MINECART_FURNACE] = "Furnace Minecart"
        names[EntityType.MINECART_HOPPER] = "Hopper Minecart"
        names[EntityType.MINECART_MOB_SPAWNER] = "Spawner Minecart"
        names[EntityType.MINECART_TNT] = "TNT Minecart"
        names[EntityType.OCELOT] = "Ocelot"
        names[EntityType.PAINTING] = "Painting"
        names[EntityType.PIG] = "Pig"
        names[EntityType.PIG_ZOMBIE] = "Zombie Pigman"
        names[EntityType.PLAYER] = "Player"
        names[EntityType.PRIMED_TNT] = "TNT"
        names[EntityType.SHEEP] = "Sheep"
        names[EntityType.SILVERFISH] = "Silverfish"
        names[EntityType.SKELETON] = "Skeleton"
        names[EntityType.SLIME] = "Slime"
        names[EntityType.SMALL_FIREBALL] = "Fireball"
        names[EntityType.SNOWBALL] = "Snowball"
        names[EntityType.SNOWMAN] = "Snowman"
        names[EntityType.SPIDER] = "Spider"
        names[EntityType.SPLASH_POTION] = "Potion"
        names[EntityType.SQUID] = "Squid"
        names[EntityType.THROWN_EXP_BOTTLE] = "Experience Bottle"
        names[EntityType.UNKNOWN] = "Custom"
        names[EntityType.VILLAGER] = "Villager"
        names[EntityType.WITCH] = "Witch"
        names[EntityType.WITHER] = "Wither"
        names[EntityType.WITHER_SKULL] = "Wither Skull"
        names[EntityType.WOLF] = "Wolf"
        names[EntityType.ZOMBIE] = "Zombie"
    }

    @JvmStatic
    fun getName(type: EntityType):String {
        return this.names.getOrPut(type) { WordUtils.capitalizeFully(type.name.lowercase().replace(" ", "")) }
    }

    //TODO optimize, faster lookups
    @JvmStatic
    fun getByName(input: String): EntityType? {
        return this.names.entries.firstOrNull{it.value.replace(" ","").equals(input,true)}?.key ?: EntityType.values().firstOrNull{it.name.equals(input,true)}
    }

    @JvmStatic
    fun getEntityCount():Int {
        //return ENTITY_COUNT_FIELD.getInt(null)
        return this.count
    }

    @JvmStatic
    fun getNewEntityId():Int {
        return this.count--
    }

    @JvmStatic
    fun registerEntity(clazz: Class<*>,name: String,id: Int) {
        this.setField("f",clazz,id)
        this.setField("d",clazz,name)
    }

    private fun <K,V> setField(field: String,key: K, value: V) {

        try {
            NMSUtil.getNMSClass("EntityTypes").getDeclaredField(field).also{
                it.isAccessible = true

                val map = it.get(null) as HashMap<K,V>

                map[key] = value
            }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        } catch (ex: IllegalAccessException) {
            ex.printStackTrace()
        } catch (ex: NoSuchFieldException) {
            ex.printStackTrace()
        }

    }

    fun isActive(entity: Entity): Boolean {
        return Shard.AI.getNmsAI().isActive(entity)
    }

    fun isMobSpawner(entity: Entity): Boolean {
        return Shard.AI.getNmsAI().isMobSpawner(entity)
    }

    fun setMobSpawner(entity: Entity, spawner: Boolean) {
        Shard.AI.getNmsAI().setMobSpawner(entity,spawner)
    }

    fun getEntityCountByChunk(chunk: Chunk): Int {
        return chunk.entities.count{it is LivingEntity}
    }

}