package net.valorhcf.trojan.check.checks.misc;

import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class BlockPlaceA extends Check {

    public BlockPlaceA(Profile profile) {
        super("Block Place A", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInBlockPlace) {
            PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) message;

            float x = packet.d();
            float y = packet.e();
            float z = packet.f();

            // Some cheats do this, don't ask me why
            if (x > 1 || y > 1 || z > 1) {
                flag("X: %.2f Y: %.2f Z: %.2f", x, y, z);
                ban();
            }
        }
    }
}
