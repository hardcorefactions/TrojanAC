package net.valorhcf.trojan.check.checks.combat;

import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;

public class AutoClickerD extends Check {

    private boolean chilling = false;
    private int streak = 0;

    public AutoClickerD(Profile profile) {
        super("Auto Clicker D", profile);
        this.autoBan = false;
    }

    @Override
    public void onMouseLeftClick(int ticks) {
        if (!chilling) {
            if (ticks == 0) {
                chilling = true;

                if (++streak >= 10 && streak % 2 == 0) {
                    flag("S: %s", streak);

                    if (streak == 40) {
                        ban();
                    }
                }
            } else {
                streak = 0;
            }
        } else {
            chilling = false;
        }
    }
}
