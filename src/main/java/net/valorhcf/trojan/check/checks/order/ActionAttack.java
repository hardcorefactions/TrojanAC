package net.valorhcf.trojan.check.checks.order;

import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class ActionAttack extends Check {

    private boolean sent = false;
    private boolean flagged = false;

    public ActionAttack(Profile profile) {
        super("Action Attack", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInEntityAction) {
            sent = true;
        } else if (message instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) message;

            if (packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK
                    && sent
                    && profile.connectionTracker.hasRespondedToTransaction()
                    && !profile.movementTracker.teleporting) {
                flag();
                flagged = true;

                if (incrementViolations(200) >= 200 * 5) {
                    ban();
                }
            } else {
                decrementViolations();
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
