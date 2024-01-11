package net.valorhcf.trojan.command;

import cc.fyre.core.api.response.EmptyApiResponse;
import cc.fyre.core.profile.Profile;
import cc.fyre.core.profile.ProfileHandler;
import cc.fyre.shard.command.data.command.Command;
import cc.fyre.shard.constants.ApiConstants;
import net.valorhcf.trojan.flag.FlagAlertType;
import net.valorhcf.trojan.flag.FlagManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import retrofit2.Response;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AlertsCommand {

    @Command(names = {"alerts","trojan alerts", "tj alerts"}, permission = "trojan.command.alerts", async = true)
    public static void statistics(Player sender) {

        final Profile profile = ProfileHandler.INSTANCE.getProfileById(sender.getUniqueId());

        if (profile == null) {
            sender.sendMessage(ApiConstants.API_REQUEST_FAILED);
            return;
        }

        final FlagAlertType type = profile.getSetting(FlagManager.SETTING).getValue().next();

        final Map<String,Object> map = new LinkedHashMap<>();

        map.put("value",type.name());
        map.put("setting",FlagManager.SETTING.getKey());

        try {
            final Response<EmptyApiResponse> request = ProfileHandler.INSTANCE.getService().setSetting(sender.getUniqueId(),map).execute();

            if (!request.isSuccessful()) {
                sender.sendMessage(ApiConstants.API_REQUEST_FAILED);
                return;
            }

            profile.setSetting(FlagManager.SETTING,type);

            final StringBuilder builder = new StringBuilder();

            builder.append(ChatColor.GOLD + "Alerts: ");

            switch (type) {
                case NONE: {
                    builder.append(ChatColor.RED + "None");
                    break;
                }
                case SERVER: {
                    builder.append(ChatColor.YELLOW + "Server");
                    break;
                }
                case GLOBAL: {
                    builder.append(ChatColor.GREEN + "Global");
                    break;
                }
            }

            sender.sendMessage(builder.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            sender.sendMessage(ApiConstants.API_REQUEST_FAILED);
        }
    }


}
