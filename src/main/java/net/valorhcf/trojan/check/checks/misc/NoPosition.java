package net.valorhcf.trojan.check.checks.misc;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class NoPosition extends Check {

    private int streak = 0;

    public NoPosition(Profile profile) {
        super("No Position", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInFlying) {
            PacketPlayInFlying packet = (PacketPlayInFlying) message;

            if (packet.g()) {
                streak = 0;
            } else if (++streak > 20) {
                flag("S: %s", streak);

                if (incrementViolations() == 5) {
                    ban();
                }
            }
        } else if (message instanceof PacketPlayInSteerVehicle) {
            // TODO: Change this to rely on the server, cheats can send vehicle steer packets to bypass this check
            //  but most don't and it still detects a lot of cheats!
            streak = 0;
        }
    }
}
