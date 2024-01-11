package net.valorhcf.trojan.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;

@Getter
@Setter
public class TrojanTeleport {

    private World world;

    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    private short transactionId;

    private boolean respondedToTransaction;

    public TrojanTeleport(World world, double x, double y, double z, float yaw, float pitch, short transactionId) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return String.format("x=%.5f y=%.5f z=%.5f", x, y, z);
    }
}
