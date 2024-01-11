package net.valorhcf.trojan.check.checks.movement;

import com.google.common.math.DoubleMath;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import net.valorhcf.trojan.util.BlockUtils;
import net.valorhcf.trojan.util.TrojanLocation;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;

public class Fly extends Check {

    private double lastDeltaY;
    private int streak;

    private int reallyFuckedTicks = 0;

    private static final List<Double> KNOWN_LEGIT_VALUES = Arrays.asList(
            -0.03136, // Cobweb
            -0.094,   // Cobweb
            0.1176,   // Ladder up
            -0.015,   // Ladder down
            0.06,     // Flowing water up
            -0.17,    // Flowing water down
            0.1,      // Stationary water up
            -0.1,     // Stationary water down
            0.04,     // Lava up
            -0.04,    // Lava down
            -0.098    // Unloaded chunk
    );

    public Fly(Profile profile) {
        super("Fly", profile);
    }

    @Override
    public void onMove(TrojanLocation from, TrojanLocation to, boolean moved, boolean rotated) {
        if (moved) {
            Trojan.getInstance().getQueue().add(() -> {

                if (profile.player.getAllowFlight()) {
                    reallyFuckedTicks = 200;
                } else if (reallyFuckedTicks > 0) {
                    --reallyFuckedTicks;
                }

                double deltaY = to.getY() - from.getY();

                if (profile.connectionTracker.hasRespondedToTransaction()
                        && deltaY > -3.8
                        && !DoubleMath.fuzzyEquals(deltaY, 0, 1E-4)
                        && DoubleMath.fuzzyEquals(deltaY, lastDeltaY, 1E-4)
                        && reallyFuckedTicks == 0
                        && profile.movementTracker.velocityList.stream()
                        .noneMatch(v -> DoubleMath.fuzzyEquals(deltaY, v.getY(), 1 / 8000d))
                        && isInAir(profile.player.getWorld(), to)) {
                    if (++streak >= 2) {
                        boolean known = Math.abs(deltaY) <= 0.03
                                || KNOWN_LEGIT_VALUES.stream()
                                .anyMatch(value -> DoubleMath.fuzzyEquals(value, deltaY, 1E-4));

                        if (!known || streak % 20 == 0) {
                            flag("Y: %.8f S: %s K: %s", deltaY, streak, known);

                            if (!known && (streak == 20 || deltaY > 0.5)) {
                                ban();
                            }
                        }
                    }
                } else {
                    streak = 0;
                }

                lastDeltaY = deltaY;
            });
        }
    }

    @Override
    public void onTeleport() {
        streak = 0;
    }

    public Boolean isInAir(World world, TrojanLocation location) {
        if (Math.abs(location.getY()) > 500) return false;

        int minX = (int) Math.floor(location.getX() - 0.3);
        int minY = (int) Math.floor(location.getY() - 1);
        int minZ = (int) Math.floor(location.getZ() - 0.3);

        int maxX = (int) Math.floor(location.getX() + 0.3);
        int maxY = (int) Math.floor(location.getY() + 1.8);
        int maxZ = (int) Math.floor(location.getZ() + 0.3);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(world, x, y, z);

                    if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                        return false;
                    }

                    if (!BlockUtils.FUCKING_MATERIALS.contains(loc.getBlock().getType())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
