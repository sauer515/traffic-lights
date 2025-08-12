package app.sched;

import app.model.*;

public class WeightedScheduler {
    private final Intersection x;
    private final long agingQuantumMs;
    private final int holdSteps;

    private Phase current = Phase.NS;
    private int held = 0;

    public WeightedScheduler(Intersection x, long agingQuantumMs, int holdSteps) {
        this.x = x; this.agingQuantumMs = agingQuantumMs; this.holdSteps = holdSteps;
    }

    public Phase decideNextPhase() {
        if (held > 0) { held--; return current; }

        long now = System.currentTimeMillis();
        long nsOldest = oldest(x.north.getOldestEnqueueMillis(), x.south.getOldestEnqueueMillis());
        long ewOldest = oldest(x.east.getOldestEnqueueMillis(),  x.west.getOldestEnqueueMillis());

        int nsWeight = x.north.size() + x.south.size() + aging(now, nsOldest);
        int ewWeight = x.east.size()  + x.west.size()  + aging(now, ewOldest);

        Phase chosen = (ewWeight > nsWeight) ? Phase.EW : Phase.NS; // NS wins ties
        if (chosen != current) { current = chosen; held = Math.max(0, holdSteps - 1); }
        return current;
    }

    private int aging(long now, long oldest) {
        if (oldest == 0L) return 0;
        long waited = Math.max(0, now - oldest);
        return (int)(waited / Math.max(1, agingQuantumMs));
    }

    private long oldest(long a, long b) {
        if (a == 0) return b; if (b == 0) return a; return Math.min(a, b);
    }

    public Phase getCurrent() { return current; }
}
