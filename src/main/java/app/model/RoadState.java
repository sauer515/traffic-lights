package app.model;

import java.util.ArrayDeque;
import java.util.Deque;

public class RoadState {
    private final Deque<Vehicle> queue = new ArrayDeque<>();
    private long oldestEnqueueMillis = 0L;

    public void add(Vehicle v) {
        queue.addLast(v);
        oldestEnqueueMillis = oldestEnqueueMillis == 0 ? v.getEnqueueTime() : Math.min(oldestEnqueueMillis, v.getEnqueueTime());
    }

    public Vehicle poll() {
        Vehicle v = queue.pollFirst();
        if (queue.isEmpty()) oldestEnqueueMillis = 0L;
        return v;
    }

    public int size() { return queue.size(); }
    public boolean isEmpty() { return queue.isEmpty(); }
    public long getOldestEnqueueMillis() { return oldestEnqueueMillis; }

    public Deque<Vehicle> getQueue() { return queue; }
}
