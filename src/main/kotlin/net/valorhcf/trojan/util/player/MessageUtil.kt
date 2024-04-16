package cc.fyre.shard.util.player

import cc.fyre.shard.util.player.font.DefaultFont
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MessageUtil {

    @JvmStatic
    val CENTER_PX = 154

    @JvmStatic
    fun sendCenteredMessage(player: Player,message: String) {

        val toSend = ChatColor.translateAlternateColorCodes('&',message)

        var messagePixels = 0

        var previousCode = false
        var bold = false

        for(char in toSend.toCharArray()) {

            if (char == '§') {
                previousCode = true
                continue
            } else if (previousCode) {
                previousCode = false

                if (char == '1' || char == 'L') {
                    bold = true
                    continue
                } else {
                    bold = false
                }

            } else {
                val font = DefaultFont.getDefaultFontInfo(char)

                messagePixels += if (bold) font.getBoldLength() else font.length
                messagePixels++
            }

        }

        val halvedMessageSize = messagePixels / 2
        val toCompensate = CENTER_PX - halvedMessageSize

        val spaceLength = DefaultFont.SPACE.length + 1

        val stringBuilder = StringBuilder()

        for (i in 0..toCompensate step spaceLength) {
            stringBuilder.append(" ")
        }

        player.sendMessage("$stringBuilder$toSend")
    }

    fun center(message: String,px: Int = CENTER_PX):String {

        val toSend = ChatColor.translateAlternateColorCodes('&',message)

        var messagePixels = 0

        var previousCode = false
        var bold = false

        for(char in toSend.toCharArray()) {

            if (char == '§') {
                previousCode = true
                continue
            } else if (previousCode) {
                previousCode = false

                if (char.equals('L',true)) {
                    bold = true
                    continue
                } else {
                    bold = false
                }

            } else {
                val font = DefaultFont.getDefaultFontInfo(char)

                messagePixels += if (bold) font.getBoldLength() else font.length
                messagePixels++
            }

        }

        val halvedMessageSize = messagePixels / 2
        val toCompensate = px - halvedMessageSize

        val spaceLength = DefaultFont.SPACE.length + 1

        val stringBuilder = StringBuilder()

        for (i in 0..toCompensate step spaceLength) {
            stringBuilder.append(" ")
        }

        return "$stringBuilder$toSend"
    }
}