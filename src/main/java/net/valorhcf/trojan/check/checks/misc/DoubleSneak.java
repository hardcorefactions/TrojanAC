package net.valorhcf.trojan.check.checks.misc;

import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class DoubleSneak extends Check {

    private boolean sent = false;
    private boolean flagged = false;

    public DoubleSneak(Profile profile) {
        super("Double Sneak", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {

        if (profile.entityPlayer.C() != null && profile.entityPlayer.C() != profile.entityPlayer) {
            return;
        }

        if (message instanceof PacketPlayInEntityAction) {
            PacketPlayInEntityAction packet = (PacketPlayInEntityAction) message;

            final PacketPlayInEntityAction.EnumPlayerAction action = packet.b();

            if (action == PacketPlayInEntityAction.EnumPlayerAction.STOP_SNEAKING) {
                if (!sent) {
                    sent = true;
                } else {
                    flag();
                    flagged = true;

                    if (incrementViolations(200) >= 200 * 5) {
                        ban();
                    }
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
