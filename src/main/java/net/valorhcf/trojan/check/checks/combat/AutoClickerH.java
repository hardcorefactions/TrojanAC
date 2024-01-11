package net.valorhcf.trojan.check.checks.combat;

import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

public class AutoClickerH extends Check {

    private final double[] delays = new double[100];
    private int index = 0;

    public AutoClickerH(Profile profile) {
        super("High CPS", profile);
        this.autoBan = false;
    }

    @Override
    public void onMouseLeftClick(int ticks) {
        if (ticks <= 8) {
            delays[index++] = ticks;

            if (index == delays.length) {
                index = 0;

                double cps = 20 / new Mean().evaluate(delays);

                if (cps >= 18) {
                    flag("CPS: %.1f", cps);

                    if (cps >= 24) {
                        if (incrementViolations(4) >= 10) ban();
                    }
                } else {
                    decrementViolations();
                }
            }
        }
    }
}
