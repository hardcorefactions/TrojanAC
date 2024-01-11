package net.valorhcf.trojan.check.checks.combat;

import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class MultiAura extends Check {

    private int lastAttackedId = -1;

    public MultiAura(Profile profile) {
        super("Multi Aura", profile);
    }

    @SneakyThrows
    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) message;

            if (packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK
                    && lastAttackedId != -1
                    && packet.a != lastAttackedId) {
                flag();

                if (incrementViolations(200) >= 200 * 5) {
                    ban();
                }
            } else {
                decrementViolations();
            }

            lastAttackedId = packet.a;
        } else if (message instanceof PacketPlayInFlying) {
            lastAttackedId = -1;
        }
    }
}
