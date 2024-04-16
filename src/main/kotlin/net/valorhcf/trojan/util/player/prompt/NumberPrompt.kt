package cc.fyre.shard.util.player.prompt

import net.valorhcf.trojan.util.player.prompt.EzPrompt
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.text.NumberFormat

class NumberPrompt(lambda: (Number) -> Unit) : EzPrompt<Number>(lambda) {

    init {
        this.text = "${ChatColor.GREEN}Please input a number."
    }

    override fun parse(player: Player, input: String): Number? {

        if (!NUMBER_REGEX.matches(input)) {
            player.sendRawMessage("${ChatColor.RED}Your input doesn't match the expected regex: ${NUMBER_REGEX.pattern}")
            return null
        }

        return NumberFormat.getInstance().parse(input
            .replace(",", "")
            .replace("_", "")
        )
    }

    companion object {

        val NUMBER_REGEX = "(-)?(^\\d{1,3}(,\\d{3})*)*(\\.?[0-9]*)?".toRegex()

    }

}