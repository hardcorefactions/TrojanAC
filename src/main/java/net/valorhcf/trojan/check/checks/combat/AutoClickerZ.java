package net.valorhcf.trojan.check.checks.combat;

import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class AutoClickerZ extends Check {

    private boolean checking = false;
    private boolean magic = false;
    private int streak = 0;

    public AutoClickerZ(Profile profile) {
        super("Auto Clicker Z", profile);

        super.setMinViolations(-15);
        super.setViolations(-15);

        this.autoBan = false;
    }

    @Override
    public void onMouseLeftClick(int ticks) {
        if (ticks > 8) {
            checking = true;
            magic = false;
            streak = 0;
        } else if (checking) {
            if (streak == 0 && ticks == 0) {
                magic = true;
            } else if (ticks == 0) {
                checking = false;
            } else if (++streak == 20) {
                if (magic) {
                    if (incrementViolations(10) >= 0) {
                        flag("VL: %.1f/15", getViolations() / 10d);
                        if (getViolations() >= 150) {
                            ban();
                        }
                    }
                } else {
                    decrementViolations();
                }

                checking = false;
            }
        }
    }
}
