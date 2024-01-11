package net.valorhcf.trojan.check.checks.combat;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.util.Arrays;

public class AutoClickerT extends Check {

    private final double[] delays = new double[400];
    private int index = 0;

    private int ticks = 0;

    public AutoClickerT(Profile profile) {
        super("Auto Clicker T", profile);
        this.autoBan = false;
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) message;

            if (packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {

                if (ticks <= 4) {
                    delays[index++] = ticks;

                    if (index == delays.length) {
                        index = 0;

                        double cps = 20 / new Mean().evaluate(delays);
                        long zeros = Arrays.stream(delays).filter(d -> d == 0).count();

                        if (zeros <= 10 && cps >= 14) {
                            flag("Z: %s/%s CPS: %.2f", zeros, delays.length, cps);
                        }
                    }
                }

                ticks = 0;
            }
        } else if (message instanceof PacketPlayInFlying) {
            ++ticks;
        }
    }
}
