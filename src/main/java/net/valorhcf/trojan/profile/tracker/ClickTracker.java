package net.valorhcf.trojan.profile.tracker;

import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.valorhcf.trojan.profile.Profile;

public class ClickTracker {

    private final Profile profile;

    private boolean digging;
    private boolean placing;

    private int animations = 0;
    private int lastClickTicks = 10;

    public ClickTracker(Profile profile) {
        this.profile = profile;
    }

    public void handleArmAnimation() {
        ++animations;
    }

    public void handleBlockDig(PacketPlayInBlockDig packet) {

        if (packet.c() != PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
            return;
        }

        digging = true;
    }

    public void handleUseEntity(PacketPlayInUseEntity packet) {

        if (packet.a() != PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
            return;
        }

        digging = false;
    }

    public void handleBlockPlace() {
        placing = true;
    }

    public void handleFlying() {

        if (!digging && !placing) {
            for (int i = 0; i < animations; i++) {
                profile.onMouseLeftClick(lastClickTicks);
                lastClickTicks = 0;
            }
        }

        placing = false;
        animations = 0;
        ++lastClickTicks;
    }
}
