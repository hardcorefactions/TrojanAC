package net.valorhcf.trojan.check.checks.combat;

import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

public class AutoClickerK extends Check {

    private final double[] delays = new double[500];
    private int index = 0;

    public AutoClickerK(Profile profile) {
        super("Auto Clicker K", profile);
        this.autoBan = false;
    }

    @Override
    public void onMouseLeftClick(int ticks) {
        if (ticks <= 8) {
            delays[index++] = ticks;

            if (index == delays.length) {
                index = 0;

                double cps = 20 / new Mean().evaluate(delays);
                double kurtosis = new Kurtosis().evaluate(delays);

                if (kurtosis <= 0 && cps >= 8) {
                    flag("K: %.2f CPS: %.2f", kurtosis, cps);
                }
            }
        }
    }
}
