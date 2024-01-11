package net.valorhcf.trojan.check.checks.combat;

import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.listener.BukkitListeners;
import net.valorhcf.trojan.profile.Profile;
import net.valorhcf.trojan.util.ClientUtils;
import net.valorhcf.trojan.util.TrojanLocation;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class KillAura extends Check {

    private static final double[] SPEED_POTION_VALUES = new double[]{
            0.2806,
            0.3367,
            0.3929,
            0.4490,
            0.5051
    };

    private boolean attacked = false;
    private double lastDeltaXZ = Double.NaN;

    public KillAura(Profile profile) {
        super("Kill Aura", profile);
        super.setMinViolations(-10 * 5);
        super.setViolations(-10 * 5);
    }

    @SneakyThrows
    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) message;

            Trojan.getInstance().getQueue().add(() -> {
                if (packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                    Player player = BukkitListeners.ENTITY_ID_TO_PLAYER_MAP.get(packet.a);

                    if (player != null) {
                        attacked = true;
                    }
                }
            });
        }
    }

    @Override
    public void onMove(TrojanLocation from, TrojanLocation to, boolean moved, boolean rotated) {
        Trojan.getInstance().getQueue().add(() -> {
            if (moved) {
                double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());
                int speedPotionAmplifier = ClientUtils.getPotionAmplifier(profile.player, PotionEffectType.SPEED) + 1;

                if (attacked
                        && !Double.isNaN(lastDeltaXZ)
                        && profile.movementTracker.sprinting
                        && speedPotionAmplifier < SPEED_POTION_VALUES.length
                        && !profile.player.getAllowFlight()
                ) {
                    double speedMultiplier = deltaXZ / SPEED_POTION_VALUES[speedPotionAmplifier];
                    double deltaMultiplier = deltaXZ / lastDeltaXZ;

                    if (speedMultiplier >= 0.99) {
                        if (deltaMultiplier >= 0.99 && deltaMultiplier <= 1.01) {
                            if (incrementViolations(10) >= 0) {
                                flag("SM: %.4f DM: %.4f S: %s VL: %.1f/10",speedMultiplier, deltaMultiplier, speedPotionAmplifier, getViolations() / 10d);

                                if (getViolations() >= 10 * 10) {
                                    ban();
                                }
                            }
                        } else {
                            decrementViolations();
                        }
                    }
                }

                lastDeltaXZ = deltaXZ;
            }

            attacked = false;
        });
    }
}
