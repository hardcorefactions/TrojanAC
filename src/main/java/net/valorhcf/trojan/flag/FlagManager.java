package net.valorhcf.trojan.flag;

import cc.fyre.core.profile.ProfileHandler;
import cc.fyre.core.profile.setting.SettingRegistry;
import cc.fyre.core.profile.settings.SettingButton;
import cc.fyre.core.profile.settings.SettingMenu;
import cc.fyre.core.server.ServerModule;
import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.modsuite.staff.setting.StaffAlertsSettingType;
import cc.fyre.modsuite.staff.setting.ViewStaffAlertsSetting;
import cc.fyre.shard.util.item.ItemBuilder;
import mkremins.fanciful.FancyMessage;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import org.apache.commons.math3.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class FlagManager {

    public static FlagAlertSetting SETTING = new FlagAlertSetting();

    public FlagManager() {
        SettingRegistry.INSTANCE.register(SETTING);
        SettingMenu.Companion.addCustomizableSetting(new SettingButton<>(SETTING,ItemBuilder.Companion.of(Material.GOLD_BARDING).build()));
        new FlagQueue().runTaskTimerAsynchronously(Trojan.getInstance(),3 * 20L,3 * 20L);

        Bukkit.getServer().getPluginManager().registerEvents(new FlagListener(),Trojan.getInstance());
    }

    public void alert(Profile profile,Check check,String metadata) {
        this.alert(profile,check.getName(),metadata);
    }

    public void alert(Profile profile,String check, String metadata) {

        final String displayName = ProfileHandler.INSTANCE.getDisplayNameById(profile.player.getUniqueId());
        final Pair<FancyMessage,FancyMessage> pair = this.toAlert(
                ServerModule.INSTANCE.getServerId(),
                profile.player.getName(),
                displayName,
                profile.connectionTracker.keepAlivePing,
                check,
                metadata
        );
        
        Bukkit.getOnlinePlayers()
                .stream()
                .filter((Predicate<Player>) player -> PREDICATE.test(player,FlagAlertType.SERVER))
                .forEach(p -> {
                    
                    if (pair.getSecond() != null && ProfileHandler.INSTANCE.isSuperuser(p.getUniqueId())) {
                        pair.getSecond().send(p);
                    } else {
                        pair.getFirst().send(p);
                    }
                    
                });

        FlagQueue.addToQueue(profile.player.getUniqueId(),profile.player.getName(),displayName,profile.connectionTracker.keepAlivePing,check,metadata);
    }

    public Pair<FancyMessage,FancyMessage> toAlert(
            String server,
            String username,
            String displayName,
            long ping,
            String check,
            String metadata
    ) {

        final FancyMessage normal = new FancyMessage("[");

        normal.color(ChatColor.DARK_GRAY);
        normal.then("T");
        normal.color(ChatColor.DARK_RED);
        normal.style(ChatColor.BOLD);
        normal.then("]");
        normal.color(ChatColor.DARK_GRAY);
        normal.then("[");
        normal.color(ChatColor.DARK_GRAY);
        normal.then(server);
        normal.color(ChatColor.RED);
        normal.style(ChatColor.BOLD);

        if (!server.equals(ServerModule.INSTANCE.getServerId())) {
            normal.tooltip(ChatColor.GREEN + "Click to join " + ChatColor.WHITE + server + ChatColor.GREEN + "!");
            normal.command("/server " + server);
        }

        normal.then("]");
        normal.color(ChatColor.DARK_GRAY);
        normal.then(" ");
        normal.then(displayName);
        normal.tooltip(ChatColor.GREEN + "Click to teleport to " + displayName + ChatColor.GREEN + "!");
        normal.command("/bungeetp " + username);
        normal.then(" ");
        normal.then("failed");
        normal.color(ChatColor.GRAY);
        normal.then(" ");
        normal.then(check);
        normal.color(ChatColor.RED);
        normal.then(" ");
        normal.then("[" + ping + "ms]");
        normal.color(ChatColor.DARK_AQUA);

        FancyMessage advanced = null;

        if (metadata != null) {

            try {
                advanced = normal.clone();
                advanced.then(" ");
                advanced.then("[" + metadata + "]");
                advanced.color(ChatColor.GOLD);
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

        }
        return new Pair<>(normal,advanced);
    }

    public final static BiPredicate<Player,FlagAlertType> PREDICATE = (player, flagAlertType) -> {

        if (!player.hasPermission(Trojan.STAFF_PERMISSION)) {
            return false;
        }

        final cc.fyre.core.profile.Profile profile1 = ProfileHandler.INSTANCE.getProfileById(player.getUniqueId());

        if (profile1 == null) {
            return false;
        }

        final FlagAlertType flagAlertTypeSetting = profile1.getSetting(FlagManager.SETTING).getValue();

        if (flagAlertTypeSetting == FlagAlertType.NONE) {
            return false;
        }

        if (flagAlertType == FlagAlertType.GLOBAL && flagAlertTypeSetting == FlagAlertType.SERVER) {
            return false;
        }

        final StaffAlertsSettingType value = profile1.getSetting(ViewStaffAlertsSetting.INSTANCE).getValue();

        if (value == StaffAlertsSettingType.MOD_MODE) {
            return ModHandler.INSTANCE.isInModMode(player.getUniqueId());
        }

        return value != StaffAlertsSettingType.NEVER;
    };

}
