package net.valorhcf.trojan.profile.tracker;

import net.minecraft.server.v1_8_R3.*;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.profile.Profile;
import net.valorhcf.trojan.util.TrojanLocation;
import net.valorhcf.trojan.util.TrojanTeleport;
import net.valorhcf.trojan.util.Velocity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class MovementTracker {

    private final Profile profile;

    public TrojanLocation lastLocation = new TrojanLocation(
            Double.NaN, Double.NaN, Double.NaN,
            Float.NaN, Float.NaN,
            false
    );

    public boolean sprinting = false;

    private final Map<Short, TrojanTeleport> teleportMap = new ConcurrentHashMap<>();
    public boolean teleporting = false;

    public final List<Velocity> velocityList = new ArrayList<>();

    public int ticksSinceVehicle = 1000;
    public int ticksSinceTeleport = 1000;
    public int ticksSinceFuckingPiston = 1000;
    public int ticksSinceLogin = 0;

    public double deltaXZ;

    public MovementTracker(Profile profile) {
        this.profile = profile;
    }

    public void handleFlying(PacketPlayInFlying packet) {
        teleporting = false;

        ++ticksSinceVehicle;
        ++ticksSinceTeleport;
        ++ticksSinceFuckingPiston;
        ++ticksSinceLogin;

        if (profile.player.isInsideVehicle()) {
            ticksSinceVehicle = 0;
        }

        for (Velocity v : velocityList) {
            if (!v.isReceived()) continue;
            v.setTicks(v.getTicks() - 1);
        }

        velocityList.removeIf(v -> v.getTicks() == 0);

        boolean onGround = packet.f();
        boolean hasPos = packet.g();
        boolean hasLook = packet.h();

        double x = packet.a();
        double y = packet.b();
        double z = packet.c();
        float yaw = packet.d();
        float pitch = packet.e();

        TrojanLocation location = new TrojanLocation(x, y, z, yaw, pitch, onGround);

        if (!hasPos) {
            location.setX(lastLocation.getX());
            location.setY(lastLocation.getY());
            location.setZ(lastLocation.getZ());
        }

        if (!hasLook) {
            location.setYaw(lastLocation.getYaw());
            location.setPitch(lastLocation.getPitch());
        }

        TrojanTeleport teleport = null;

        for (TrojanTeleport t : teleportMap.values()) {
            if (!t.isRespondedToTransaction()) continue;
            if (Math.abs(t.getX() - location.getX()) > 0.005) continue;
            if (Math.abs(t.getY() - location.getY()) > 0.005) continue;
            if (Math.abs(t.getZ() - location.getZ()) > 0.005) continue;
            teleport = t;
            break;
        }

        boolean teleported = teleport != null;

        if (teleported) {
            teleportMap.remove(teleport.getTransactionId());
            profile.onTeleport();
            this.teleporting = true;
            ticksSinceTeleport = 0;

//            Optional.ofNullable(Bukkit.getPlayerExact("Rowin"))
//                    .ifPresent(p -> p.sendMessage("[T] " + ChatColor.GREEN + profile.player.getName() + " teleported"));
        }

        boolean moved = !Double.isNaN(lastLocation.getX()) && !Double.isNaN(location.getX())
                && (lastLocation.getX() != location.getX()
                || lastLocation.getY() != location.getY()
                || lastLocation.getZ() != location.getZ());

        boolean rotated = !Float.isNaN(lastLocation.getYaw()) && !Float.isNaN(location.getPitch())
                && (lastLocation.getYaw() != location.getYaw()
                || lastLocation.getPitch() != location.getPitch());

        deltaXZ = Math.hypot(lastLocation.getX() - location.getX(), lastLocation.getZ() - location.getZ());

        if (moved && !teleported && deltaXZ > 10) {
            lastLocation = location;
            Trojan.getInstance().getLogger().log(Level.WARNING,
                    profile.player.getName() + " moved " + deltaXZ + " blocks");
            return;
        }

        if (moved || rotated) {
            profile.onMove(lastLocation, location, moved, rotated);
        }

        lastLocation = location;
    }

    public void handlePositionBeforeWrite(PacketPlayOutPosition packet) {
        short id = profile.connectionTracker.sendTransaction();

        TrojanTeleport teleport = new TrojanTeleport(
                profile.player.getWorld(),
                packet.a,
                packet.b,// - (profile.protocolVersion >= 16 ? 0:1.62),
                packet.c,
                packet.d,
                packet.e,
                id
        );

        teleportMap.put(id, teleport);

//        Optional.ofNullable(Bukkit.getPlayerExact("Rowin"))
//                .ifPresent(p -> p.sendMessage("[T] " + ChatColor.GREEN + profile.player.getName() + " is teleporting"));
    }

    public void handleTransaction(PacketPlayInTransaction packet) {
        short id = packet.b();

        TrojanTeleport teleport = teleportMap.get(id);

        if (teleport != null) {
            teleport.setRespondedToTransaction(true);
        }

        velocityList.stream()
                .filter(v -> v.getTransactionId() == id)
                .forEach(v -> v.setReceived(true));
    }

    public void handleEntityVelocity(PacketPlayOutEntityVelocity packet) {
        if (packet.a == profile.player.getEntityId()) {
            double x = packet.b / 8000d;
            double y = packet.c / 8000d;
            double z = packet.d / 8000d;

            double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            int ticks = (int) (length * 20 + 20);
            short id = profile.connectionTracker.sendTransaction();

            velocityList.add(new Velocity(x, y, z, ticks, id));
        }
    }

    public void handleExplosion(PacketPlayOutExplosion packet) {
        double x = packet.f;
        double y = packet.g;
        double z = packet.h;

        double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        int ticks = (int) (length * 20 + 20);
        short id = profile.connectionTracker.sendTransaction();

        velocityList.add(new Velocity(x, y, z, ticks, id));
    }

    public void handleEntityAction(PacketPlayInEntityAction packet) {
        if (packet.b() == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING) {
            sprinting = true;
        } else if (packet.b() == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING) {
            sprinting = false;
        }
    }
}
