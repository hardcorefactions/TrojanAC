package cc.fyre.shard.util.player.prompt

import net.valorhcf.trojan.util.player.prompt.EzPrompt
import org.bukkit.entity.Player

class InputPrompt(lambda: (String) -> Unit) : EzPrompt<String>(lambda) {

    override fun parse(player: Player, input: String): String? {
        return input
    }

}