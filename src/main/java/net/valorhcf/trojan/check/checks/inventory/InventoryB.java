package net.valorhcf.trojan.check.checks.inventory;

import net.minecraft.server.v1_8_R3.PacketPlayInCloseWindow;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class InventoryB extends Check {

    private boolean check = false;
    private boolean closed = false;
    private int button;
    private int slot;

    public InventoryB(Profile profile) {
        super("Inventory B", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInWindowClick) {
            check = true;
            button = ((PacketPlayInWindowClick) message).c();
            slot = ((PacketPlayInWindowClick) message).b();
        } else if (message instanceof PacketPlayInCloseWindow) {
            closed = true;
        } else if (message instanceof PacketPlayInFlying) {
            if (check
                    && !closed
                    && profile.movementTracker.sprinting
                    && profile.movementTracker.deltaXZ != 0
            ) {
                flag("XZ: %.2f B: %s S: %s", profile.movementTracker.deltaXZ, button, slot);
            }

            check = false;
            closed = false;
        }
    }
}
