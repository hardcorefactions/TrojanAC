package net.valorhcf.trojan.flag;

import cc.fyre.core.profile.ProfileHandler;
import mkremins.fanciful.FancyMessage;
import net.valorhcf.trojan.Trojan;
import org.apache.commons.math3.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FlagQueue extends BukkitRunnable {

    private final Thread pubSubThread;

    private final List<Pair<FancyMessage,FancyMessage>> messages;

    public FlagQueue() {
        pubSubThread = new Thread(() -> Trojan.getInstance().getJedisPool().getResource().subscribe(new FlagQueuePubSub(),CHANNEL_NAME));
        pubSubThread.start();

        messages = new ArrayList<>();
        // probs not the most efficient way
        new BukkitRunnable() {

            private int tick = 0;
            private List<Player> players = Bukkit.getServer().getOnlinePlayers()
                    .stream()
                    .filter((Predicate<Player>) player -> FlagManager.PREDICATE.test(player, FlagAlertType.GLOBAL))
                    .collect(Collectors.toList());

            @Override
            public void run() {

                if (this.tick == 20) {
                    this.tick = 0;
                    this.players = Bukkit.getServer().getOnlinePlayers()
                            .stream()
                            .filter((Predicate<Player>) player -> FlagManager.PREDICATE.test(player, FlagAlertType.GLOBAL))
                            .collect(Collectors.toList());
                } else {
                    ++this.tick;
                }

                if (messages.isEmpty()) {
                    return;
                }

                final Pair<FancyMessage, FancyMessage> message = messages.remove(0);

                // probs not the most efficient way
                this.players.stream().filter(Player::isOnline).forEach(it -> {

                    if (message.getSecond() != null && ProfileHandler.INSTANCE.isSuperuser(it.getUniqueId())) {
                        message.getSecond().send(it);
                    } else {
                        message.getFirst().send(it);
                    }

                });
            }
        }.runTaskTimerAsynchronously(Trojan.getInstance(), 1L, 1L);
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        this.pubSubThread.stop();
    }

    @Override
    public void run() {

        if (queue.isEmpty()) {
            return;
        }

        final JedisPool pool = Trojan.getInstance().getJedisPool();
        final Jedis jedis = pool.getResource();

        final String server = Trojan.getInstance().getServerId();
        final Pipeline pipeline = jedis.pipelined();

        FlagBody body;

        while ((body = queue.poll()) != null) {
            pipeline.publish(CHANNEL_NAME,server + MESSAGE_SPLITTER + body.toJson());
        }

        try {
            pipeline.sync();
        } catch (Exception ignored) {
            pool.returnBrokenResource(jedis);
        } finally {
            pool.returnResource(jedis);
        }

        playerToBody.clear();
    }

    private static final Queue<FlagBody> queue = new ConcurrentLinkedQueue<>();
    private static final Map<UUID,FlagBody> playerToBody = new ConcurrentHashMap<>();

    public static void addToQueue(UUID uuid,String username,String displayName,long ping,String check,String metadata) {

        FlagBody body = playerToBody.get(uuid);
        List<Flag> flags;

        if (body == null) {
            flags = new ArrayList<>();
            body = new FlagBody(uuid,username,displayName,flags);
            queue.add(body);
            playerToBody.put(uuid,body);
        } else {
            flags = body.getFlags();
        }

        Flag flag = flags.stream().filter(it -> {

            if (it.getMetadata() == null && metadata != null) {
                return false;
            }

            return it.getCheck().equals(check);
        })
                .findFirst()
                .orElse(null);

        if (flag == null) {
            flag = new Flag(0,ping,check,metadata);
            flags.add(flag);
        }

        flag.increment();
    }

    private static final String CHANNEL_NAME = "TROJAN_ALERTS";
    private static final String MESSAGE_SPLITTER = "//";

    private class FlagQueuePubSub extends JedisPubSub {

        @Override
        public void onMessage(String channel, String message) {

            if (!channel.equals(CHANNEL_NAME)) {
                return;
            }

            if (!message.contains(MESSAGE_SPLITTER)) {
                return;
            }

            final String[] split = message.split(MESSAGE_SPLITTER);
            final String server = split[0];

            //TODO better way?
            if (Trojan.getInstance().getServerId().equals(server)) {
                return;
            }

            final FlagBody body = Trojan.GSON.fromJson(split[1],FlagBody.class);

            for (Flag flag : body.getFlags()) {

                for (int i = 0; i < flag.getCount(); i++) {
                    FlagQueue.this.messages.add(Trojan.getInstance().getFlagManager().toAlert(
                            server,
                            body.getUsername(),
                            body.getDisplayName(),
                            flag.getPing(),
                            flag.getCheck(),
                            flag.getMetadata()
                    ));
                }

            }

        }

    }

}
