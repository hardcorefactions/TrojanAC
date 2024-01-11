package net.valorhcf.trojan.command;

import cc.fyre.shard.command.data.command.Command;
import cc.fyre.shard.command.data.parameter.Parameter;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.profile.Profile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;

public class InfoCommand {

    @Command(names = {"trojan info", "tj info"}, permission = "trojan.command.info")
    public static void info(CommandSender sender, @Parameter(name = "player") Player target) {
        if (!Trojan.getInstance().isEnabled()) return;

        Profile targetProfile = Trojan.getInstance().getProfileManager().getProfile(target);

        if (targetProfile == null) {
            sender.sendMessage(RED + "Unable to fetch information of " + target.getName() + ".");
            return;
        }

        sender.sendMessage(new String[]{
                RED + "Name: " + GRAY + target.getName(),
                RED + "Ping: " + GRAY + targetProfile.connectionTracker.keepAlivePing + " ms",
                RED + "Transaction Ping: " + GRAY + targetProfile.connectionTracker.transactionPing + " ms",
                RED + "Client: " + GRAY + targetProfile.payloadTracker.getClientInformation(),
                RED + "Version: " + GRAY + targetProfile.clientVersion.getProtocolName(),
        });
    }
}
