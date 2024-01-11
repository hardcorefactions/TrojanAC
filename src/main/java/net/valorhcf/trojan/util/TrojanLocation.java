package net.valorhcf.trojan.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrojanLocation {

    private final long time = System.currentTimeMillis();

    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    private boolean onGround;

    public TrojanLocation(double x, double y, double z) {
        this(x, y, z, 0, 0, false);
    }

    public TrojanLocation(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public String toString() {
        return String.format("x=%.5f y=%.5f z=%.5f", x, y, z);
    }
}
