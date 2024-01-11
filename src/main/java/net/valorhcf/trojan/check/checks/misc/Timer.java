package net.valorhcf.trojan.check.checks.misc;

import me.lucko.helper.Schedulers;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

import java.util.logging.Level;

public class Timer extends Check {

    private long lastFlying = System.nanoTime();
    private long balance = 0;
    private int packets = 0;
    private boolean fucked = false;

    public Timer(Profile profile) {
        super("Timer", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInFlying && profile.connectionTracker.hasRespondedToTransaction()) {
            long delay = nanos - lastFlying;
            balance += 50_000_000 - delay;
            ++packets;

            if (balance <= -20_000_000_000L) {
                balance = -1_000_000_000;
                fucked = true;
            } else if (balance >= 100_000_000) {
                if (packets <= 400) { // 1.005
                    if (!fucked) {
                        double multiplier = 50_000_000 / (50_000_000 - balance / (double) packets);
                        flag("M: %.2f P: %s VL: %s/15", multiplier, packets, incrementViolations());

                        if (getViolations() == 15) {
                            ban();
                        }
                    } else if (!profile.kicked) {
                        profile.kicked = true;

                        Schedulers.sync().runLater(() -> {
                            profile.entityPlayer.playerConnection.disconnect("Disconnected");
                            Trojan.getInstance().getLogger().log(Level.WARNING, profile.player.getName()
                                    + " has been disconnected for fucking up Timer A");
                        }, 1);
                    }
                }

                balance = 0;
                packets = 0;
            }

            lastFlying = nanos;
        }
    }

    @Override
    public void handleOutboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayOutPosition) {
            balance -= 50_000_000;
        }
    }
}
