package net.valorhcf.trojan.command;

import cc.fyre.core.profile.ProfileHandler;
import cc.fyre.core.profile.adapter.UUIDParameterAdapter;
import cc.fyre.shard.command.CommandHandler;
import cc.fyre.shard.command.data.command.Command;
import cc.fyre.shard.command.data.parameter.Parameter;
import com.mongodb.client.model.Filters;
import net.valorhcf.trojan.Trojan;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static org.bukkit.ChatColor.*;

import java.util.UUID;

public class PurgeCommand {

    @Command(names = {"trojan purge", "tj purge"},permission = "trojan.command.purge",async = true)
    public static void purge(CommandSender sender,@Parameter(name = "player") String target) {

        if (sender instanceof Player && !ProfileHandler.INSTANCE.isSuperuser(((Player)sender).getUniqueId())) {
            sender.sendMessage(CommandHandler.INSTANCE.getNO_PERMISSION());
            return;
        }

        if (target.equals("**")) {
            Trojan.getInstance().getLogsCollection().deleteMany(new Document());
            sender.sendMessage(GREEN + "All logs have been purged.");
            return;
        }

        final UUID uuid = UUIDParameterAdapter.INSTANCE.transform(sender,target);

        if (uuid == null) {
            return;
        }

        Trojan.getInstance().getLogsCollection().deleteMany(Filters.eq(uuid.toString()));

        sender.sendMessage(ProfileHandler.INSTANCE.getDisplayNameById(uuid) + GREEN + "'s logs have been purged.");
    }
}
