package net.valorhcf.trojan.check.checks.order;

import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class PlaceRelease extends Check {

    private boolean sent = false;
    private boolean flagged = false;

    public PlaceRelease(Profile profile) {
        super("Place Release", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInBlockPlace) {
            sent = true;
        } else if (message instanceof PacketPlayInBlockDig) {
            PacketPlayInBlockDig packet = (PacketPlayInBlockDig) message;
            PacketPlayInBlockDig.EnumPlayerDigType status = packet.c();

            // 5 = RELEASE_USE_ITEM
            if (status == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM
                    && sent
                    && profile.connectionTracker.hasRespondedToTransaction()
                    && !profile.movementTracker.teleporting) {
                flag();
                flagged = true;

                if (incrementViolations(200) >= 200 * 5) {
                    ban();
                }
            }
        } else if (message instanceof PacketPlayInFlying) {
            sent = false;

            if (!flagged) {
                decrementViolations();
            } else {
                flagged = false;
            }
        }
    }
}
