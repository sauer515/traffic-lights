package app.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Intersection {
    public final RoadState north = new RoadState();
    public final RoadState south = new RoadState();
    public final RoadState east  = new RoadState();
    public final RoadState west  = new RoadState();

    public RoadState by(Direction d) {
        return switch (d) {
            case NORTH -> north; case SOUTH -> south; case EAST -> east; case WEST -> west;
        };
    }

    /** Removes at most one vehicle from each direction on the given axis, chosen by earliest enqueue time. */
    public List<Vehicle> passThroughAxisWithVehicles(Phase phase) {
        List<Vehicle> candidates = new ArrayList<>();

        if (phase == Phase.NS) {
            if (!south.isEmpty()) candidates.add(south.getQueue().peekFirst());
            if (!north.isEmpty()) candidates.add(north.getQueue().peekFirst());
        } else {
            if (!east.isEmpty()) candidates.add(east.getQueue().peekFirst());
            if (!west.isEmpty()) candidates.add(west.getQueue().peekFirst());
        }

        candidates.sort(Comparator.comparingLong(Vehicle::getEnqueueTime));

        List<Vehicle> left = new ArrayList<>();
        for (Vehicle v : candidates) {
            left.add(v);
            by(v.getStart()).poll();
        }
        return left;
    }

    /** Backward-compatible variant used by CLI: returns only ids. */
    public List<String> passThroughAxis(Phase phase) {
        List<Vehicle> vs = passThroughAxisWithVehicles(phase);
        List<String> ids = new ArrayList<>(vs.size());
        for (Vehicle v : vs) ids.add(v.getId());
        return ids;
    }
}
