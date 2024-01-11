package net.valorhcf.trojan.check.checks.misc;

import net.minecraft.server.v1_8_R3.*;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class PostFlyingPacket extends Check {

    private final Set<Class<?>> packets = new HashSet<>();

    public PostFlyingPacket(Profile profile) {
        super("Post Flying Packet", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInFlying) {
            packets.clear();
        } else if (profile.connectionTracker.hasRespondedToTransaction()
                && !profile.movementTracker.teleporting
                && (message instanceof PacketPlayInArmAnimation
                || message instanceof PacketPlayInUseEntity
                || message instanceof PacketPlayInBlockPlace
                || message instanceof PacketPlayInBlockDig
                || message instanceof PacketPlayInEntityAction
                // Fuck you BlazingPack
//                || message instanceof PacketPlayInWindowClick
                || message instanceof PacketPlayInAbilities)) {
            packets.add(message.getClass());
        } else if (message instanceof PacketPlayInTransaction) {
            if (!packets.isEmpty()
                    && profile.connectionTracker.hasRespondedToTransaction()
                    && !profile.movementTracker.teleporting) {
                StringJoiner sj = new StringJoiner(", ");
                packets.forEach(p -> sj.add(p.getSimpleName().substring(12)));
                flag("P: %s", sj.toString());
                packets.clear();

                if (incrementViolations(200) >= 200 * 5) {
                    ban();
                }
            } else {
                decrementViolations();
            }
        }
    }
}
