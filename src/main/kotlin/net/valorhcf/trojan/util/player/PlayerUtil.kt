package net.valorhcf.trojan.util.player

import net.valorhcf.trojan.util.bukkit.NMSUtil
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.conversations.ConversationFactory
import org.bukkit.conversations.Prompt
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.util.BlockIterator


object PlayerUtil {

    @JvmStatic
    fun sendPacket(player: Player,vararg packets: Any) {

        for (packet in packets) {
            Shard.AI.getPacketAI().sendPacket(player,packet)
        }

    }

    @JvmStatic
    fun startPrompt(player: Player,prompt: Prompt,timeout: Int = 30,plugin: Plugin = Shard.instance) {
        player.beginConversation(ConversationFactory(plugin)
            .withFirstPrompt(prompt)
            .withModality(false)
            .withLocalEcho(false)
            .withTimeout(timeout)
            .buildConversation(player)
        )
    }

    @JvmStatic
    fun createCloneProfile(player: Player):GameProfile {
        return createCloneProfile(NMSUtil.getGameProfile(player))
    }

    @JvmStatic
    fun createCloneProfile(profile: GameProfile):GameProfile {

        val toReturn = GameProfile(profile.id,profile.name)
        var textures = profile.properties["textures"].firstOrNull()

        if (textures != null && textures.value != null && textures.hasSignature()) {
            textures = Property(textures.name,textures.value,textures.signature)
            toReturn.properties.put("textures",textures)
        }

        return toReturn
    }

    @JvmStatic
    fun getCardinalDirection(player: Player):BlockFace {

        var rot = (player.location.yaw - 90) % 360.toDouble()

        if (rot < 0) {
            rot += 360.0
        }

        return getDirection(rot)
    }

    @JvmStatic
    fun getDirection(rot: Double):BlockFace {

        return if (0 <= rot && rot < 22.5) {
            BlockFace.WEST
        } else if (22.5 <= rot && rot < 67.5) {
            BlockFace.NORTH_WEST
        } else if (67.5 <= rot && rot < 112.5) {
            BlockFace.NORTH
        } else if (112.5 <= rot && rot < 157.5) {
            BlockFace.NORTH_EAST
        } else if (157.5 <= rot && rot < 202.5) {
            BlockFace.EAST
        } else if (202.5 <= rot && rot < 247.5) {
            BlockFace.SOUTH_EAST
        } else if (247.5 <= rot && rot < 292.5) {
            BlockFace.SOUTH
        } else if (292.5 <= rot && rot < 337.5) {
            BlockFace.SOUTH_WEST
        } else if (337.5 <= rot && rot < 360.0) {
            BlockFace.WEST
        } else {
            // should never happen
            throw IllegalStateException("???")
        }
    }

    @JvmStatic
    fun getTargetBlock(player: Player,range: Int): Block? {

        var block: Block? = null
        val iterator = BlockIterator(player,range)

        for (next in iterator) {

            if (next == null || next.type == Material.AIR) {
                continue
            }

            block = next
            break
        }

        return block
    }
}