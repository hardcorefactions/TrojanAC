package net.valorhcf.trojan.check.checks.combat;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.listener.BukkitListeners;
import net.valorhcf.trojan.profile.Profile;
import net.valorhcf.trojan.profile.tracker.LocationHistoryTracker;
import net.valorhcf.trojan.util.MathUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Reach extends Check {

    private int attackedId = -1;

    private final LocationHistoryTracker.SparkLocation location = new LocationHistoryTracker.SparkLocation(0, 0, 0);
    private boolean moved = false;

    public Reach(Profile profile) {
        super("Reach", profile);
    }

    @Override
    public void handleInboundPacket(Object message, long millis, long nanos) {
        if (message instanceof PacketPlayInFlying) {
            PacketPlayInFlying packet = (PacketPlayInFlying) message;

            if (packet.h()) {
                location.setYaw(packet.d());
                location.setPitch(packet.e());
            }

            List<LocationHistoryTracker.SparkLocation> locations
                    = profile.locationHistoryTracker.locationHistoryMap.get(attackedId);

            Player player = BukkitListeners.ENTITY_ID_TO_PLAYER_MAP.get(attackedId);
            Profile targetProfile;

            if (player != null
                    && (targetProfile = Trojan.getInstance().getProfileManager().getProfile(player)) != null
                    && targetProfile.movementTracker.ticksSinceVehicle > 200 // LAZY FIX
                    && profile.movementTracker.ticksSinceVehicle > 200 // LAZY FIX
                    && attackedId != -1
                    && profile.player.getGameMode() == GameMode.SURVIVAL
                    && !profile.movementTracker.teleporting
                    && locations != null) {
                List<LocationHistoryTracker.SparkLocation> checking = new ArrayList<>();
                int size = locations.size();
                boolean fucked = false;

                for (int i = 0; i < size; i++) {
                    LocationHistoryTracker.SparkLocation l = locations.get(i);

                    if (i == size - 1) {
                        fucked = true;
                        break;
                    }

                    checking.add(l);

                    if (l.getTicks() > 3) {
                        break;
                    }
                }

                if (!fucked) {
                    double dist = checking.stream()
                            .mapToDouble(t -> MathUtils.getMagicDistanceToHitbox(location, t))
                            .min().orElse(0);

                    int ticks = checking.stream().mapToInt(LocationHistoryTracker.SparkLocation::getTicks)
                            .max().orElse(0);

                    if (dist > 3.03) {
                        incrementViolations(ticks >= 10 && checking.size() == 1 ? 12000 : 6000);

                        if (getViolations() > 6000) {
                            flag("%.2f blocks",dist);
                        } else {
                            flag("D: %.3f T: %s S: %s M: %s VL: %.1f/8",dist, ticks, checking.size(), moved, getViolations() / 6000d);
                        }

                        if (getViolations() >= 6000 * 8) {
                            ban();
                        }
                    }
                }
            }

            attackedId = -1;

            if (packet.g()) {
                location.setX(packet.a());
                location.setY(packet.b());
                location.setZ(packet.c());
                moved = packet.g();
            }

            decrementViolations();
        } else if (message instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) message;

            if (packet.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK
                    && BukkitListeners.ENTITY_ID_TO_PLAYER_MAP.containsKey(packet.a)
                    && !profile.movementTracker.teleporting) { // MAYBE???
                attackedId = packet.a;
            }
        }
    }
}
