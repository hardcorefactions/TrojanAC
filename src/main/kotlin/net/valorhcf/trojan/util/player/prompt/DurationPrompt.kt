package net.valorhcf.trojan.util.player.prompt

import cc.fyre.shard.util.TimeUtil
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class DurationPrompt(lambda: (Long) -> Unit) : EzPrompt<Long>(lambda) {

    init {
        this.text = "${ChatColor.GREEN}Please input a duration."
    }

    override fun parse(player: Player, input: String): Long? {
        return TimeUtil.parseTime(input)
    }


}