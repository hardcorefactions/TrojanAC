package net.valorhcf.trojan.check.checks.combat;

import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class AutoClickerS extends Check {

    private final double[] delays = new double[100];
    private int index = 0;

    public AutoClickerS(Profile profile) {
        super("Auto Clicker S", profile);
        this.autoBan = false;
    }

    @Override
    public void onMouseLeftClick(int ticks) {
        if (ticks <= 8) {
            delays[index++] = ticks;

            if (index == delays.length) {
                index = 0;

                double cps = 20 / new Mean().evaluate(delays);
                double sd = new StandardDeviation().evaluate(delays);

                if (sd <= 0.5 && cps >= 12) {
                    flag("SD: %.3f CPS: %.1f", sd, cps);
                }
            }
        }
    }
}
