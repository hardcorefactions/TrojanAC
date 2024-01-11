package net.valorhcf.trojan.check.checks.combat;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import net.valorhcf.trojan.util.TrojanLocation;

public class AimAssist extends Check {

    // https://en.wikipedia.org/wiki/IEEE_754
    private static final double MULTIPLIER = Math.pow(2, 24);

    private float lastDeltaPitch = Float.NaN;
    private int lastAttackTicks = 5 + 1;

    private int total;
    private int bad;
    private float totalDeltaYaw;

    public AimAssist(Profile profile) {
        super("Aim Assist", profile);
    }

    @Override
    public void onMove(TrojanLocation from, TrojanLocation to, boolean moved, boolean rotated) {
        if (rotated) {
            float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
            float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

            if (!Float.isNaN(lastDeltaPitch)
                    && deltaPitch != 0
                    && deltaPitch <= 10
                    && Math.abs(from.getPitch()) != 90
                    && Math.abs(to.getPitch()) != 90
                    && lastAttackTicks <= 5
            ) {
                long one = (long) (deltaPitch * MULTIPLIER);
                long two = (long) (lastDeltaPitch * MULTIPLIER);
                long gcd = gcd(one, two);

                if (gcd <= 131072) {
                    ++bad;
                }

                totalDeltaYaw += deltaYaw;

                if (++total == 250) {

                    if (bad >= 75 && totalDeltaYaw >= 180) {
                        flag("B: %s/250 Y: %.2f", bad, totalDeltaYaw);
                    }

                    total = 0;
                    bad = 0;
                    totalDeltaYaw = 0;
                }
            }

            lastDeltaPitch = deltaPitch;
        }
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) message;

            if (packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                lastAttackTicks = 0;
            }
        } else if (message instanceof PacketPlayInFlying) {
            ++lastAttackTicks;
        }
    }

    private long gcd(long one, long two) {
        if (two <= 16384) return one;
        return gcd(two, one % two);
    }
}
