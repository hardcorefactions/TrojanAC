package net.valorhcf.trojan.check.checks.combat;

import net.valorhcf.trojan.check.Check;
import net.valorhcf.trojan.profile.Profile;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.util.Arrays;

public class AutoClickerO extends Check {

    private final double[] delays = new double[500];
    private int index = 0;

    public AutoClickerO(Profile profile) {
        super("Auto Clicker O", profile);
        this.autoBan = false;
    }

    @Override
    public void onMouseLeftClick(int ticks) {
        if (ticks <= 8) {
            delays[index++] = ticks;

            if (index == delays.length) {
                index = 0;

                double cps = 20 / new Mean().evaluate(delays);
                long outliers = Arrays.stream(delays).filter(d -> d >= 4).count();

                if (outliers <= 5 && cps >= 6) {
                    flag("O: %s/%s CPS: %.2f", outliers, delays.length, cps);
                }
            }
        }
    }
}
