package net.valorhcf.trojan.util;

import net.valorhcf.trojan.profile.tracker.LocationHistoryTracker;

import java.util.Random;

public class MathUtils {

    public static final Random RANDOM = new Random();

    public static double getMagicDistanceToHitbox(LocationHistoryTracker.SparkLocation from,
                                                  LocationHistoryTracker.SparkLocation to) {
        double nearestX = clamp(from.getX(), to.getX() - 0.4, to.getX() + 0.4);
        double nearestZ = clamp(from.getZ(), to.getZ() - 0.4, to.getZ() + 0.4);

        double distX = from.getX() - nearestX;
        double distZ = from.getZ() - nearestZ;

        double dist = Math.hypot(distX, distZ);

        if (Math.abs(from.getPitch()) != 90) {
            dist /= Math.cos(Math.toRadians(from.getPitch()));
        }

        return dist;
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}
