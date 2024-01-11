package net.valorhcf.trojan.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Velocity {

    private short transactionId;
    private boolean received;

    private int ticks;

    private final double x;
    private final double y;
    private final double z;

    private final double xz;

    public Velocity(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.xz = Math.hypot(x, z);
    }

    public Velocity(double x, double y, double z, int ticks, short transactionId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ticks = ticks;
        this.transactionId = transactionId;

        this.xz = Math.hypot(x, z);
    }
}
