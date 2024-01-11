package net.valorhcf.trojan.check.checks.movement;

import net.minecraft.server.v1_8_R3.BlockIce;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import net.valorhcf.trojan.util.TrojanLocation;
import net.valorhcf.trojan.util.Velocity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Comparator;
import java.util.logging.Level;

public class Speed extends Check {

    private int fuckedTicks = 0;
    private int moreFuckedTicks = 0;
    private int reallyFuckedTicks = 0;

    private int speedAmplifier = 0;
    private int speedTicks = 0;

    private Long lastIcePacket = null;

    public Speed(Profile profile) {
        super("Speed", profile);
        super.setMinViolations(-40 * 5);
        super.setViolations(-40 * 5);
    }

    // This check sucks ASS (but it works for now (mostly))


    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {

        if (message instanceof PacketPlayInBlockPlace) {

            ItemStack stack = ((PacketPlayInBlockPlace) message).getItemStack();

            if (stack != null) {

                if (stack.getName().equals("minecraft.ice") || stack.getName().equals("minecraft.packed_ice")) {
                    this.lastIcePacket = ((PacketPlayInBlockPlace) message).timestamp;
                }

            }

        }

    }

    @Override
    public void onMove(TrojanLocation from, TrojanLocation to, boolean moved, boolean rotated) {
        if (moved && !profile.movementTracker.teleporting) {

            if (this.lastIcePacket != null && (System.currentTimeMillis() - this.lastIcePacket) < 300L) {
                return;
            }

            Velocity velocityMax = profile.movementTracker.velocityList.stream()
                    .max(Comparator.comparing(Velocity::getXz))
                    .orElse(null);

            Trojan.getInstance().getQueue().add(() -> {
                double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());
                double deltaY = to.getY() - from.getY();

                PotionEffect speedEffect = profile.player.getActivePotionEffects()
                        .stream()
                        .filter(p -> p.getType().equals(PotionEffectType.SPEED))
                        .findFirst()
                        .orElse(null);

                int currentSpeedAmplifier = speedEffect == null ? 0 : speedEffect.getAmplifier() + 1;

                // TRASH
                if (currentSpeedAmplifier >= speedAmplifier) {
                    speedAmplifier = currentSpeedAmplifier;
                    speedTicks = 200;
                } else if (speedTicks > 0 && --speedTicks == 0) {
                    speedAmplifier = currentSpeedAmplifier;
                }

                // MORE TRASH
                if (profile.player.getAllowFlight()
                        || profile.player.getWalkSpeed() > 0.2f
                        || profile.player.isInsideVehicle()) {
                    reallyFuckedTicks = 200;
                } else if (reallyFuckedTicks > 0) {
                    --reallyFuckedTicks;
                }

                World world = profile.player.getWorld();

                // EVEN MORE TRASH
                if (isUnderBlock(world, to) || isFucked(world, to)) {
                    fuckedTicks = 20;
                } else if (fuckedTicks > 0) {
                    --fuckedTicks;
                }

                // AND MORE
                if (isOnIce(world, to)) {
                    moreFuckedTicks = 60;
                } else if (moreFuckedTicks > 0) {
                    --moreFuckedTicks;
                }

                if (reallyFuckedTicks == 0) {
                    // Base speed
                    double maxDeltaXZ = 0.36;

                    if (deltaY >= 0.42 - 1E-4) {
                        // Jump speed
                        maxDeltaXZ = 0.62;
                    } else if (deltaY == 0) {
                        maxDeltaXZ = 0.42;
                    }

                    if (speedAmplifier > 0) maxDeltaXZ += speedAmplifier * 0.06;
                    if (velocityMax != null) maxDeltaXZ += velocityMax.getXz();
                    if (fuckedTicks > 0) maxDeltaXZ *= 2;
                    if (moreFuckedTicks > 0) maxDeltaXZ *= 2;

                    // Don't question this
                    if (profile.movementTracker.ticksSinceFuckingPiston <= 200) {
                        maxDeltaXZ = Math.max(1, maxDeltaXZ);
                    }

                    if (deltaXZ > maxDeltaXZ && profile.connectionTracker.hasRespondedToTransaction()) {
                        if (incrementViolations(deltaXZ > maxDeltaXZ * 2 ? 80 : 40) >= 0) {
                            flag("%.3f > %.3f VL: %.1f/20", deltaXZ, maxDeltaXZ, getViolations() / 40d);

                            if (getViolations() >= 40 * 20) {
                                ban();
                            }
                        }
                    } else {
                        decrementViolations();
                    }
                }
            });

            lastIcePacket = null;
        }
    }

    public Boolean isUnderBlock(World world, TrojanLocation location) {
        if (Math.abs(location.getY()) > 500) return false;

        int minX = (int) Math.floor(location.getX() - 0.4);
        int minY = (int) Math.floor(location.getY() + 1.8);
        int minZ = (int) Math.floor(location.getZ() - 0.4);

        int maxX = (int) Math.floor(location.getX() + 0.4);
        int maxY = (int) Math.floor(location.getY() + 2 + 1E-4);
        int maxZ = (int) Math.floor(location.getZ() + 0.4);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(world, x, y, z);

                    if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                        return true;
                    }

                    if (loc.getBlock().getType() != Material.AIR) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Boolean isOnIce(World world, TrojanLocation location) {
        if (Math.abs(location.getY()) > 500) return false;

        int minX = (int) Math.floor(location.getX() - 0.4);
        int minY = (int) Math.floor(location.getY() - 1 - 1E-4);
        int minZ = (int) Math.floor(location.getZ() - 0.4);

        int maxX = (int) Math.floor(location.getX() + 0.4);
        int maxY = (int) Math.floor(location.getY());
        int maxZ = (int) Math.floor(location.getZ() + 0.4);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(world, x, y, z);

                    if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                        return true;
                    }

                    if (loc.getBlock().getType().name().contains("ICE")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Boolean isFucked(World world, TrojanLocation location) {
        if (Math.abs(location.getY()) > 500) return false;

        int minX = (int) Math.floor(location.getX() - 0.4);
        int minY = (int) Math.floor(location.getY() - 1);
        int minZ = (int) Math.floor(location.getZ() - 0.4);

        int maxX = (int) Math.floor(location.getX() + 0.4);
        int maxY = (int) Math.floor(location.getY() + 2 + 1E-4);
        int maxZ = (int) Math.floor(location.getZ() + 0.4);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(world, x, y, z);

                    if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                        return true;
                    }

                    Block block = loc.getBlock();
                    Material material = block.getType();
                    String name = material.name();

                    if (name.contains("STAIR") || name.contains("STEP")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
