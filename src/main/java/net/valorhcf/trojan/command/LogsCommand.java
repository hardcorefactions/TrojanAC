package net.valorhcf.trojan.command;

import cc.fyre.core.api.ApiResponse;
import cc.fyre.core.api.response.EmptyApiResponse;
import cc.fyre.core.prefix.Prefix;
import cc.fyre.core.profile.Profile;
import cc.fyre.core.profile.ProfileHandler;
import cc.fyre.core.uuid.UUIDHandler;
import cc.fyre.shard.command.data.command.Command;
import cc.fyre.shard.command.data.parameter.Parameter;
import cc.fyre.shard.constants.ApiConstants;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import mkremins.fanciful.FancyMessage;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.log.LogBody;
import net.valorhcf.trojan.log.LogEntry;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

public class LogsCommand {

    @Command(names = {"trojan logs", "tj logs","logs"}, permission = "trojan.command.logs", async = true)
    public static void logs(CommandSender sender, @Parameter(name = "player") UUID target) {

        List<Document> documentList = new ArrayList<>();

        Trojan.getInstance().getLogsCollection()
                .find(Filters.eq("uuid", target.toString()))
                .sort(Indexes.descending("time"))
                .limit(10000) // Should be enough...?
                .into(documentList);

        if (documentList.isEmpty()) {
            sender.sendMessage(ChatColor.RED + UUIDHandler.INSTANCE.getUsernameById(target,true) + " does not have any logs.");
            return;
        }

        final String username = UUIDHandler.INSTANCE.getUsernameById(target,true);

        StringJoiner sj = new StringJoiner("\n");
        sj.add("Found " + documentList.size() + " logs for " + username  + " (" + target + "):");

        Profile profile = ProfileHandler.INSTANCE.getProfileById(target);

        if (profile == null) {

            try {
                final Response<ApiResponse<Profile>> request = ProfileHandler.INSTANCE.getService().findById(target).execute();

                if (request.isSuccessful()) {
                    profile = request.body().getValue();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        String prefixText = null;
        String prefixHex = null;
        String usernameHex = "#FFFFFF";

        if (profile != null) {

            final Prefix prefix = profile.getPrefix();

            if (prefix != null && prefix.getText() != null) {

                final String stripped = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',prefix.getText()));
                String color = prefix.getText().replace(stripped,"");

                if (color.length() >= 2) {

                    if (color.length() > 2) {
                        color = color.substring(0,2);
                    }

                    final String hex = COLOR_PALETTE.getOrDefault(color, "#FFFFFF");

                    prefixText = stripped;
                    prefixHex = hex;
                }

            }

            String color = profile.getRank().getColor();

            if (color.length() >= 2) {

                if (color.length() > 2) {
                    color = color.substring(0, 2);
                }

                usernameHex = COLOR_PALETTE.getOrDefault(color,"#FFFFFF");
            }

        }

        final List<LogEntry> entries = new ArrayList<>();

        for (Document document : documentList) {
            entries.add(new LogEntry(
                    document.getLong("time"),
                    document.getLong("ping"),
                    document.getString("server"),
                    document.getString("check"),
                    document.getString("metadata"),
                    document.containsKey("banned") ? document.getString("banned") : null
            ));
        }

        try {

            final LogBody body = new LogBody(
                    target,
                    System.currentTimeMillis(),
                    username,
                    usernameHex,
                    prefixText,
                    prefixHex,
                    entries
            );

            final Response<EmptyApiResponse> request = Trojan.getInstance().getLogManager().getService().create(body).execute();

            if (!request.isSuccessful()) {
                sender.sendMessage(ApiConstants.API_REQUEST_FAILED);
                return;
            }

            final String link = Trojan.getInstance().getRetrofit().baseUrl() + "log/?key=" + request.body().getDataAsJsonObject().getString("key");

            new FancyMessage(link)
                    .color(ChatColor.GREEN)
                    .link(link)
                    .send(sender);

        } catch (IOException ex) {
           ex.printStackTrace();
        }

    }

    private static final Map<String,String> COLOR_PALETTE = new HashMap<>();

    static {
        COLOR_PALETTE.put("&0","#000000");
        COLOR_PALETTE.put("&1","#0000AA");
        COLOR_PALETTE.put("&2","#00AA00");
        COLOR_PALETTE.put("&3","#00AAAA");
        COLOR_PALETTE.put("&4","#AA0000");
        COLOR_PALETTE.put("&5","#AA00AA");
        COLOR_PALETTE.put("&6","#FFAA00");
        COLOR_PALETTE.put("&7","#AAAAAA");
        COLOR_PALETTE.put("&8","#555555");
        COLOR_PALETTE.put("&9","#5555FF");

        COLOR_PALETTE.put("&a","#55FF55");
        COLOR_PALETTE.put("&b","#55FFFF");
        COLOR_PALETTE.put("&c","#FF5555");
        COLOR_PALETTE.put("&d","#FF55FF");
        COLOR_PALETTE.put("&e","#FFFF55");
        COLOR_PALETTE.put("&f","#FFFFFF");
    }

}
