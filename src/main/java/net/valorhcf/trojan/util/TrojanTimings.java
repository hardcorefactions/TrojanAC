package net.valorhcf.trojan.util;

import com.google.common.collect.EvictingQueue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lucko.helper.Schedulers;

public class TrojanTimings {

    private int events;
    private long time;

    private final EvictingQueue<Timings> lastTwentyTicks = EvictingQueue.create(20);

    public TrojanTimings() {
        Schedulers.sync().runRepeating(() -> {
            synchronized (this) {
                lastTwentyTicks.add(new Timings(events, time));
                events = 0;
                time = 0;
            }
        }, 1, 1);
    }

    public synchronized void addTiming(long timing) {
        ++events;
        time += timing;
    }

    public synchronized Timings getLastTwentyTicks() {
        int events = 0;
        long time = 0;

        for (Timings tick : lastTwentyTicks) {
            events += tick.getEvents();
            time += tick.getTime();
        }

        return new Timings(events, time);
    }

    @Getter
    @AllArgsConstructor
    public static class Timings {
        private final int events;
        private final long time;
    }
}
