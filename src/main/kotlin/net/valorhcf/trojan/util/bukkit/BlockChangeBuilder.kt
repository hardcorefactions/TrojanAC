package net.valorhcf.trojan.util.bukkit

import net.valorhcf.trojan.util.player.PlayerUtil
import com.google.common.collect.LinkedListMultimap
import net.minecraft.server.v1_8_R3.PacketPlayOutMultiBlockChange
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash
import org.bukkit.entity.Player

class BlockChangeBuilder(private val world: World) {

    private val changes = LinkedListMultimap.create<Long, Entry>()

    fun addChange(x: Int,y: Int,z: Int,type: Int,data: Int): BlockChangeBuilder {

        val chunk = LongHash.toLong(x shr 4, z shr 4)

        val xOffset = x and 15
        val zOffset = z and 15

        val location = (xOffset shl 12 or (zOffset shl 8) or y).toShort()

        this.changes[chunk].add(Entry(location,type,data))

        return this
    }

    fun sendChanges(player: Player) {
        this.changes.keySet().forEach{chunkKey ->

            val chunkX = LongHash.msw(chunkKey)
            val chunkZ = LongHash.lsw(chunkKey)

            val packet = PacketPlayOutMultiBlockChange()
            val changes = this.changes[chunkKey]
            val info = arrayOfNulls<PacketPlayOutMultiBlockChange.MultiBlockChangeInfo>(changes.size)

            for ((counter,entry) in changes) {

            }
        }
    }

    fun resetChanges(player: Player) {
        this.changes.keySet().forEach{chunkKey ->

            val chunkX = LongHash.msw(chunkKey)
            val chunkZ = LongHash.lsw(chunkKey)

            val chunk = (this.world as CraftWorld).handle.getChunkAt(chunkX,chunkZ)
            var counter = 0

            val changes = ShortArray(this.changes[chunkKey].size)

            for (entry in this.changes[chunkKey]) {
                changes[counter++] = entry.key
            }

            PlayerUtil.sendPacket(player,Shard.AI.getPacketAI().PacketPlayOutMultiBlockChange(changes.size,changes,chunk.bukkitChunk))
        }
    }

    data class Entry(
        val key: Short, // location
        val type: Int, // block
        val data: Int, // block data
    )

}