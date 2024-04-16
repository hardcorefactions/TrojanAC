package cc.fyre.shard.util.player.prompt

import net.valorhcf.trojan.util.player.prompt.EzPrompt
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DatePrompt(lambda: (Date) -> Unit) : EzPrompt<Date>(lambda) {

    init {
        this.text = "${ChatColor.GREEN}Please input a date with this format: $PARSER_FORMAT"
    }

    override fun parse(player: Player, input: String): Date? {
        return try {
            DATE_FORMATTER.parse(input)
        } catch (ex: ParseException) {
            return null
        }
    }

    fun test() {

    }

    companion object {

        val PARSER_FORMAT = "dd-M-yyyy hh:mm:ss"
        val DATE_FORMATTER = SimpleDateFormat(PARSER_FORMAT)

    }

}