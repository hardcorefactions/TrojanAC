package net.valorhcf.trojan.command;

import cc.fyre.shard.command.data.command.Command;
import cc.fyre.shard.command.data.parameter.Parameter;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.RED;

public class BanCommand {

    @Command(names = {"trojan ban", "tj ban"},hidden = true,permission = "trojan.command.ban")
    public static void ban(CommandSender sender,@Parameter(name = "player") Player target) {

        Profile targetProfile = Trojan.getInstance().getProfileManager().getProfile(target);

        if (targetProfile == null) {
            sender.sendMessage(RED + "Unable to forcefully ban " + target.getName() + ".");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Forcefully banning " + target.getName() + "...");
        Trojan.getInstance().getBanManager().ban(
                targetProfile,
                sender
        );
    }
}
