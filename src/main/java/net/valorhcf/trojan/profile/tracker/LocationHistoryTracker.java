package net.valorhcf.trojan.profile.tracker;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import net.valorhcf.trojan.profile.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationHistoryTracker {

    private final Profile profile;

    private short transactionId = -1;

    public LocationHistoryTracker(Profile profile) {
        this.profile = profile;
    }

    public final Map<Integer, List<SparkLocation>> locationHistoryMap = new HashMap<>();
    private final Map<Integer, SparkLocation> lastLocationMap = new HashMap<>();

    public void preEntityTracker() {
        transactionId = profile.connectionTracker.sendTransaction();
    }

    public void handleFlyingPost() {
        for (Map.Entry<Integer, List<SparkLocation>> entry : locationHistoryMap.entrySet()) {
            for (SparkLocation location : entry.getValue()) {
                if (location.isReceived()) {
                    location.incrementTicks();
                }
            }
        }
    }

    public void handleTransaction(PacketPlayInTransaction packet) {
        for (Map.Entry<Integer, List<SparkLocation>> entry : locationHistoryMap.entrySet()) {
            for (SparkLocation location : entry.getValue()) {
                if (location.getTransactionId() == packet.b()) {
                    location.setReceived(true);
                }
            }
        }
    }

    public void handleSpawnEntity(PacketPlayOutSpawnEntity packet) {
        int entityId = packet.a;
        double x = packet.b / 32d;
        double y = packet.c / 32d;
        double z = packet.d / 32d;

        SparkLocation location = new SparkLocation(x, y, z);
        location.transactionId = transactionId;

        List<SparkLocation> locations = locationHistoryMap.computeIfAbsent(entityId, e -> new ArrayList<>());

        if (locations.size() == 200) {
            locations.remove(200 - 1);
        }

        locations.add(0, location);
        lastLocationMap.put(entityId, location);
    }

    public void handleEntity(PacketPlayOutEntity packet) {
        if (packet instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMove || packet instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook) {
            int entityId = packet.a;
            double x = packet.b / 32d;
            double y = packet.c / 32d;
            double z = packet.d / 32d;

            SparkLocation lastLocation = lastLocationMap.get(entityId);

            if (lastLocation != null) {
                SparkLocation location = new SparkLocation(
                        lastLocation.x + x,
                        lastLocation.y + y,
                        lastLocation.z + z
                );
                location.transactionId = transactionId;

                List<SparkLocation> locations = locationHistoryMap.computeIfAbsent(entityId, e -> new ArrayList<>());

                if (locations.size() == 200) {
                    locations.remove(200 - 1);
                }

                locations.add(0, location);
                lastLocationMap.put(entityId, location);
            }
        }
    }

    public void handleSpawnEntityTeleport(PacketPlayOutEntityTeleport packet) {
        int entityId = packet.a;
        double x = packet.b / 32d;
        double y = packet.c / 32d;
        double z = packet.d / 32d;

        SparkLocation location = new SparkLocation(x, y, z);
        location.transactionId = transactionId;

        List<SparkLocation> locations = locationHistoryMap.computeIfAbsent(entityId, e -> new ArrayList<>());

        if (locations.size() == 200) {
            locations.remove(200 - 1);
        }

        locations.add(0, location);
        lastLocationMap.put(entityId, location);
    }

    public void handleEntityDestroy(PacketPlayOutEntityDestroy packet) {
        for (int entityId : packet.a) {
            locationHistoryMap.remove(entityId);
            lastLocationMap.remove(entityId);
        }
    }

    @Getter
    @Setter
    public static class SparkLocation {
        private short transactionId;
        private boolean received;
        private double x;
        private double y;
        private double z;
        private float yaw;
        private float pitch;
        private int ticks;

        public SparkLocation(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void incrementTicks() {
            ++ticks;
        }
    }
}
