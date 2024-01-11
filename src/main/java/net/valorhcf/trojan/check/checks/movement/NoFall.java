package net.valorhcf.trojan.check.checks.movement;

import net.valorhcf.trojan.Trojan;
import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import net.valorhcf.trojan.util.TrojanLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class NoFall extends Check {

    public NoFall(Profile profile) {
        super("No Fall", profile);
    }

    @Override
    public void onMove(TrojanLocation from, TrojanLocation to, boolean moved, boolean rotated) {
        double fromRemainder = from.getY() % (1 / 64d);
        double toRemainder = to.getY() % (1 / 64d);

        if (moved
                && fromRemainder >= 1E-4 && from.isOnGround()
                && toRemainder >= 1E-4 && to.isOnGround()
                && from.getY() != to.getY()
        ) {
            Trojan.getInstance().getQueue().add(() -> {
                if (!isNearBoat(profile.player)) {
                    incrementViolations(6000);
                    flag("VL: %.1f/20 FY: %.6f FM: %.6f TY: %.6f TM: %.6f", getViolations() / 6000d,
                            from.getY(), fromRemainder,
                            to.getY(), toRemainder
                    );

                    if (getViolations() >= 6000 * 20) {
                        ban();
                    }
                } else {
                    decrementViolations();
                }
            });
        } else {
            decrementViolations();
        }
    }

    private boolean isNearBoat(Player player) {
        List<Entity> entityList = player.getNearbyEntities(1, 1, 1);

        for (Entity entity : entityList) {
            if (entity.getType() == EntityType.BOAT) {
                return true;
            }
        }

        return false;
    }
}
