package net.valorhcf.trojan.profile.tracker;

import me.lucko.helper.Schedulers;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import net.minecraft.server.v1_8_R3.PacketPlayInTransaction;
import net.minecraft.server.v1_8_R3.PacketPlayOutKeepAlive;
import net.minecraft.server.v1_8_R3.PacketPlayOutTransaction;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.profile.Profile;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConnectionTracker {

    public static final Object LOCK = new Object();

    private final Profile profile;

    public int ticks;
    private short transactionId = Short.MIN_VALUE;
    private final Map<Short, Long> transactionMap = new HashMap<>();
    public long transactionPing = -1;
    public long lastTransaction = System.currentTimeMillis();

    private int keepAliveId = 1;
    private final Map<Integer, Long> keepAliveMap = new HashMap<>();
    public long keepAlivePing = -1;

    public ConnectionTracker(Profile profile) {
        this.profile = profile;
    }

    public void handleTransaction(PacketPlayInTransaction packet, long millis) {
        synchronized (LOCK) {
            if (packet.a() == 0) {
                Long time = transactionMap.remove(packet.b());

                if (time != null) {
                    transactionPing = millis - time;
                    lastTransaction = System.currentTimeMillis();
                }
            }
        }
    }

    public void handleKeepAlive(PacketPlayInKeepAlive packet, long millis) {
        boolean trojan = false;

        synchronized (LOCK) {
            Long time = keepAliveMap.remove(packet.a());

            if (time != null) {
                keepAlivePing = millis - time;
                trojan = true;
            }
        }

        if (trojan) {
            profile.onTrojanKeepAlive();
        }
    }

    public void onAsyncTick() {
        synchronized (LOCK) {
            if (++ticks % 5 == 0) {
                keepAliveMap.put(keepAliveId, System.currentTimeMillis());

                profile.entityPlayer.playerConnection.sendPacket(
                        new PacketPlayOutKeepAlive(keepAliveId)
                );

                ++keepAliveId;
            }

            // Only check every 20 ticks for performance reasons
            if (profile.protocolVersion >= 47 && ticks % 20 == 0) {
                transactionMap.values().stream()
                        .filter(time -> System.currentTimeMillis() - time >= 20000)
                        .findFirst()
                        .ifPresent(time -> Schedulers.sync().runLater(() -> {
                            if (!profile.kicked) {
                                profile.kicked = true;
                                profile.entityPlayer.playerConnection.disconnect("Disconnected");

                                Trojan.getInstance().getLogger().log(Level.WARNING, profile.player.getName()
                                        + " has been disconnected for not responding to a transaction within " + (System.currentTimeMillis() - time) + " ms");
                            }
                        }, 1));

            }
        }
    }

    public short sendTransaction() {
        synchronized (LOCK) {
            short currentTransactionId = transactionId++;

            if (transactionId == 0) {
                transactionId = Short.MIN_VALUE;
            }

            transactionMap.put(currentTransactionId, System.currentTimeMillis());

            profile.entityPlayer.playerConnection.sendPacket(
                    new PacketPlayOutTransaction(0, currentTransactionId, false)
            );
            return currentTransactionId;
        }
    }

    public boolean hasRespondedToTransaction() {
        return transactionPing != -1;
    }
}
