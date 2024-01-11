package net.valorhcf.trojan.ban;

import me.lucko.helper.Schedulers;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.log.LogManager;
import net.valorhcf.trojan.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BanManager {

    private final Trojan main = Trojan.getInstance();
    private final LogManager logManager = main.getLogManager();

    public void ban(Profile profile, CommandSender executor) {
        ban(profile, null, executor);
    }

    public void ban(Profile profile, Check check) {
        ban(profile, check, null);
    }

    private void ban(Profile profile, Check check, CommandSender executor) {

        if (check != null && !check.isAutoBan()) {
            logManager.logBan(profile,check,"would have been banned");
            return;
        }

        if (profile.kicked) {
            return;
        }

        profile.kicked = true;

        final String client = profile.payloadTracker.getClientInformation();
        final String version = profile.clientVersion.getProtocolName();

        Schedulers.async().runLater(() -> {

            //TODO
//            if (check == null) {
//                logManager.logBan(profile, "Forcefully banned by " + executor.getName()
//                        + " (Client: " + client + " " + version + ")");
//            } else {
//                logManager.logBan(profile, "Automatically banned for " + check.getName()
//                        + " (Client: " + client + " " + version + ")");
//            }

        }, 1);

        if (Trojan.getInstance().getServerId().contains("Staging")) {
            return;
        }

        final String displayName = profile.entityPlayer.getName();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban" + " " + profile.player.getName() + " " + "14d" + " " + "Cheating");
        Bukkit.broadcastMessage(
                ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "T" + ChatColor.DARK_GRAY + "] "
                        + ChatColor.WHITE + displayName
                        + ChatColor.GRAY + " has been caught cheating!"
        );
    }
}
