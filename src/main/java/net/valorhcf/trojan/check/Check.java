package net.valorhcf.trojan.check;

import lombok.Getter;
import lombok.Setter;
import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.ban.BanManager;
import net.valorhcf.trojan.flag.FlagManager;
import net.valorhcf.trojan.log.LogManager;
import net.valorhcf.trojan.profile.Profile;
import net.valorhcf.trojan.util.TrojanLocation;

@Getter
public abstract class Check {

    private final Trojan main = Trojan.getInstance();
    private final FlagManager flagManager = main.getFlagManager();
    private final LogManager logManager = main.getLogManager();
    private final BanManager banManager = main.getBanManager();

    private final String name;

    public final Profile profile;

    @Setter
    private int minViolations;
    @Getter
    @Setter
    private int violations;

    @Getter protected boolean autoBan = true;

    public Check(String name, Profile profile) {
        this.name = name;
        this.profile = profile;
    }

    public int incrementViolations(int amount) {
        return (violations += amount);
    }

    public int incrementViolations() {
        return incrementViolations(1);
    }

    public void decrementViolations() {
        violations = Math.max(minViolations, violations - 1);
    }

    public void flag(String metadata) {
        if (!profile.kicked) {
            flagManager.alert(profile, this, metadata);
            logManager.log(profile, this, metadata);
        }
    }

    public void flag(String metadata, Object... args) {
        flag(String.format(metadata, args));
    }

    public void flag() {
        flag(null);
    }

    public void ban() {
        banManager.ban(profile, this);
    }

    public void handleInboundPacket(Object message, long millis, long nanos) {}
    public void handleOutboundPacket(Object message, long millis, long nanos) {}
    public void onMove(TrojanLocation from, TrojanLocation to, boolean moved, boolean rotated) {}

    public void onMouseLeftClick(int ticks) {}
    public void onTeleport() {}
    public void onTrojanKeepAlive() {}
}
