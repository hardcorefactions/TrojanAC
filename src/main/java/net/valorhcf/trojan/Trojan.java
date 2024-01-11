package net.valorhcf.trojan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import io.netty.channel.Channel;
import lombok.AccessLevel;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.valorhcf.trojan.ban.BanManager;
import net.valorhcf.trojan.flag.FlagManager;
import net.valorhcf.trojan.listener.BukkitListeners;
import net.valorhcf.trojan.log.LogManager;
import net.valorhcf.trojan.profile.Profile;
import net.valorhcf.trojan.profile.ProfileManager;
import net.valorhcf.trojan.util.TrojanTimings;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class Trojan extends ExtendedJavaPlugin {

    public static final String STAFF_PERMISSION = "trojan.staff";

    @Getter
    private static Trojan instance;

    private TrojanTimings checkTimings;
    private ProfileManager profileManager;
    private FlagManager flagManager;
    private LogManager logManager;
    private BanManager banManager;

    @Getter(AccessLevel.NONE)
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    @Getter private JedisPool jedisPool;

    private MongoCollection<Document> logsCollection;

    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private final Map<UUID, Double> range = new HashMap<>();

    @Getter
    private boolean HCFEnabled;
    private String teleportCommand;

    public static final Gson GSON = new GsonBuilder().create();

    @Override
    public void enable() {
        long time = System.currentTimeMillis();

        instance = this;

        initMongo();
        initRedis();

        checkTimings = new TrojanTimings();
        profileManager = new ProfileManager();
        flagManager = new FlagManager();
        logManager = new LogManager();
        banManager = new BanManager();

        Bukkit.getPluginManager().registerEvents(new BukkitListeners(), this);

        Schedulers.async().runRepeating(() -> {
            profileManager.getProfileMap().values().forEach(Profile::onAsyncTick);
        }, 1, 1);

        // Aids
        Bukkit.getScheduler().runTaskTimer(this, new BukkitRunnable() {
            @Override
            public void run() {
                profileManager.getProfileMap().values().forEach(p -> p.locationHistoryTracker.preEntityTracker());
            }
        }, 0L, 20L);

        FileConfiguration configuration = getConfig();

        configuration.addDefault("teleportCommand", "/tp {player}");
        teleportCommand = configuration.getString("teleportCommand", "/tp {player}");

//        Schedulers.sync().runLater(() -> {
//
//            final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("HCF");
//
//            if (plugin == null || !plugin.isEnabled()) {
//                return;
//            }
//
//            HCFEnabled = true;
//        },60L);

        Schedulers.sync().runRepeating(() -> {
            Runnable runnable;
            while ((runnable = queue.poll()) != null) {
                runnable.run();
            }
        }, 0, 1);

//        // Because java.lang.ClassNotFoundException: net.valorhcf.trojan.util.WebUtils
//        // when running /tj logs after uploading a Trojan update
//        try {
//            Class.forName("net.valorhcf.trojan.util.WebUtils");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        // Use a new command handler --> CommandHandler.INSTANCE.registerAll(this);

        getLogger().info("Trojan launched in " + (System.currentTimeMillis() - time) + " ms");
    }

    @Override
    public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;

            channel.eventLoop().execute(() -> channel.pipeline().remove("trojan_packet_handler"));
        }

        profileManager.getProfileMap().clear();
    }

    private void initMongo() {
        FileConfiguration configuration = getConfig();

        configuration.addDefault("mongo.host", "127.0.0.1");
        configuration.addDefault("mongo.port", 27017);
        configuration.addDefault("mongo.authentication.enabled", false);
        configuration.addDefault("mongo.authentication.username", "admin");
        configuration.addDefault("mongo.authentication.password", "");

        configuration.options().copyDefaults(true);
        saveConfig();

        ServerAddress address = new ServerAddress(
                configuration.getString("mongo.host"),
                configuration.getInt("mongo.port")
        );

        if (configuration.getBoolean("mongo.authentication.enabled")) {
            MongoCredential credential = MongoCredential.createPlainCredential(
                    configuration.getString("mongo.authentication.username"),
                    "admin",
                    configuration.getString("mongo.authentication.password").toCharArray()
            );

            mongoClient = new MongoClient(address, credential, MongoClientOptions.builder().build());
        } else {
            mongoClient = new MongoClient(address);
        }

        mongoDatabase = mongoClient.getDatabase("trojan");

        logsCollection = mongoDatabase.getCollection("logs");
        logsCollection.createIndex(Indexes.ascending("uuid"));

    }

    private void initRedis() {
        jedisPool = new JedisPool(getConfig().getString("mongo.host"),6379);
    }

    public String getServerId() {
        return "KitMap";
    }
}
