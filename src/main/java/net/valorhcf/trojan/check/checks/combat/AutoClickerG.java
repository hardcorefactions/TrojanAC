package net.valorhcf.trojan.check.checks.combat;

import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class AutoClickerG extends Check {

    private boolean sent = false;
    private int streak = 0;

    public AutoClickerG(Profile profile) {
        super("Auto Clicker G", profile);
        this.autoBan = false;
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInBlockDig) {
            PacketPlayInBlockDig packet = (PacketPlayInBlockDig) message;

            if (packet.c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                sent = true;
            } else if (packet.c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                if (sent) {
                    if (++streak >= 20 && streak % 5 == 0) {
                        flag("S: %s", streak);
                    }
                } else {
                    streak = 0;
                }
            }
        } else if (message instanceof PacketPlayInFlying) {
            sent = false;
        } else if (message instanceof PacketPlayInBlockPlace) {
            PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) message;

            if (packet.getFace() != 255) {
                streak = 0;
            }
        }
    }
}
