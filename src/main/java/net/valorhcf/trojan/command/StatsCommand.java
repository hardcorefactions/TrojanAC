package net.valorhcf.trojan.command;

import cc.fyre.core.profile.ProfileHandler;
import cc.fyre.shard.command.CommandHandler;
import cc.fyre.shard.command.data.command.Command;
import com.mongodb.client.MongoDatabase;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.util.TrojanTimings;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

import static org.bukkit.ChatColor.*;

public class StatsCommand {

    private static final DecimalFormat FORMAT_1 = new DecimalFormat("#.#");
    private static final DecimalFormat FORMAT_4 = new DecimalFormat("#.####");

    @Command(names = {"trojan stats", "tj stats"}, permission = "trojan.command.stats", async = true)
    public static void statistics(CommandSender sender) {

        if (sender instanceof Player && !ProfileHandler.INSTANCE.isSuperuser(((Player)sender).getUniqueId())) {
            sender.sendMessage(CommandHandler.INSTANCE.getNO_PERMISSION());
            return;
        }

        TrojanTimings.Timings timings = Trojan.getInstance().getCheckTimings().getLastTwentyTicks();
        long online = Bukkit.getOnlinePlayers().size();
        long injected = Trojan.getInstance().getProfileManager().getProfileMap().size();

        String avgTickTime = FORMAT_4.format(timings.getTime() / 1000000d / 20);
        String avgTickTimePerPlayer = FORMAT_4.format(timings.getTime() / 1000000d / 20 / injected);
        String avgEventsPerSecondPerPlayer = FORMAT_1.format(timings.getEvents() / injected);
        String threads = System.getProperty("net.minecraft.util.io.netty.eventLoopThreads");

        MongoDatabase database = Trojan.getInstance().getMongoDatabase();
        Document stats = database.runCommand(new Document("collStats", "logs"));

        int count = stats.getInteger("count");
        int storageSize = stats.getInteger("storageSize");
        String storageSizeDisplay = FORMAT_1.format(storageSize / 1024d / 1024d) + " MB";

        sender.sendMessage(new String[]{
                DARK_RED.toString() + BOLD + "Trojan Timings" + RED + " - Last 20 Ticks",
                RED + "Avg. Tick Time: " + GRAY + avgTickTime,
                RED + "Avg. Tick Time per Player: " + GRAY + avgTickTimePerPlayer,
                RED + "Avg. Events per Second per Player: " + GRAY + avgEventsPerSecondPerPlayer,
                "",
                DARK_RED.toString() + BOLD + "Other",
                RED + "Online Players: " + GRAY + online,
                RED + "Injected Players: " + GRAY + injected,
                RED + "Netty Threads: " + GRAY + threads,
                RED + "Logs: " + GRAY + count,
                RED + "Storage: " + GRAY + storageSizeDisplay
        });
    }
}
