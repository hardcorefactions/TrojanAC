package net.valorhcf.trojan.check.checks.misc;

import net.minecraft.server.v1_8_R3.PacketPlayInHeldItemSlot;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class SameSlotSwitch extends Check {

    private int lastSlot = Integer.MIN_VALUE;

    public SameSlotSwitch(Profile profile) {
        super("Same Slot Switch", profile);
    }

    @Override
    public void handleOutboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInHeldItemSlot) {
            PacketPlayInHeldItemSlot packetPlayInHeldItemSlot = (PacketPlayInHeldItemSlot) message;

            if (lastSlot != Integer.MIN_VALUE && lastSlot == packetPlayInHeldItemSlot.a()) {
                flag("S: %s", lastSlot);
                ban();
            }

            lastSlot = packetPlayInHeldItemSlot.a();
        }
    }
}
