package net.valorhcf.trojan.check.checks.misc;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class FrozenMinecraft extends Check {

    private double lastY = Double.NaN;
    private long lastFlying = System.currentTimeMillis();
    private int streak;

    public FrozenMinecraft(Profile profile) {
        super("Frozen Minecraft", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInFlying) {
            PacketPlayInFlying packet = (PacketPlayInFlying) message;

            if (packet.g()) {

                if (!Double.isNaN(lastY)) {
                    double deltaY = packet.b() - lastY;
                    long elapsed = System.currentTimeMillis() - lastFlying;

                    if (deltaY <= -1
                            && elapsed > 500
                            && streak >= 2
                            && profile.movementTracker.ticksSinceTeleport != 0) {
                        flag("Y: %.2f T: %s S: %s TP: %s",
                                deltaY, elapsed, streak, profile.movementTracker.ticksSinceTeleport);
                    }
                }

                lastY = packet.b();
            }

            lastFlying = System.currentTimeMillis();
            streak = 0;
        }
    }

    @Override
    public void onTrojanKeepAlive() {
        ++streak;
    }
}
