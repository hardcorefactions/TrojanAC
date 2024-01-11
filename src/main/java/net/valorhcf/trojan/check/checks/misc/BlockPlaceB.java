package net.valorhcf.trojan.check.checks.misc;

import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class BlockPlaceB extends Check {

    public BlockPlaceB(Profile profile) {
        super("Block Place B", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInBlockPlace) {
            PacketPlayInBlockPlace packet = (PacketPlayInBlockPlace) message;

            int face = packet.getFace();

            float x = packet.d();
            float y = packet.e();
            float z = packet.f();

            // No clue if any cheat actually fucks this up but why not check for it
            if (face == 255 && (x != 0 || y != 0 || z != 0)) {
                flag("X: %.2f Y: %.2f Z: %.2f", x, y, z);
            }
        }
    }
}
