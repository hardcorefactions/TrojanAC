package net.valorhcf.trojan.check.checks.inventory;

import net.minecraft.server.v1_8_R3.*;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class InventoryA extends Check {

    private boolean swung = false;
    private String action;

    public InventoryA(Profile profile) {
        super("Inventory A", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInArmAnimation) {
            swung = true;
        } else if (message instanceof PacketPlayInClientCommand) {
            PacketPlayInClientCommand packet = (PacketPlayInClientCommand) message;

            if (packet.a() == PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                action = "Open";
            }
        } else if (message instanceof PacketPlayInWindowClick) {
            action = "Click";
        } else if (message instanceof PacketPlayInCloseWindow) {
            action = "Close";
        } else if (message instanceof PacketPlayInFlying) {
            if (swung && action != null) {
                flag("A: %s", action);

                if (incrementViolations(200) >= 200 * 5) {
                    ban();
                }
            } else {
                decrementViolations();
            }

            swung = false;
            action = null;
        }
    }
}
