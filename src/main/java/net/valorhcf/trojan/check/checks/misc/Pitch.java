package net.valorhcf.trojan.check.checks.misc;

import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import net.valorhcf.trojan.util.TrojanLocation;

public class Pitch extends Check {

    public Pitch(Profile profile) {
        super("Pitch", profile);
    }

    @Override
    public void onMove(TrojanLocation from, TrojanLocation to, boolean moved, boolean rotated) {
        if (rotated && !profile.movementTracker.teleporting) {
            float pitch = to.getPitch();

            if (Math.abs(pitch) > 90) {
                flag("P: %.6f", pitch);
                ban();
            }
        }
    }
}
