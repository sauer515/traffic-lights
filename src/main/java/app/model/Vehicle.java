package app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static app.model.Direction.*;
import static app.model.Turn.*;

public class Vehicle {
    private final String id;
    private final Direction start;
    private final Direction end;
    private final Turn turn;
    private final long enqueueTime;

    @JsonCreator
    public Vehicle(
            @JsonProperty("vehicleId") String id,
            @JsonProperty("startRoad") String start,
            @JsonProperty("endRoad") String end
    ) {
        this(id, Direction.fromString(start), Direction.fromString(end));
    }

    public Vehicle(String id, Direction start, Direction end) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.turn = computeTurn(start, end);
        this.enqueueTime = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public Direction getStart() { return start; }
    public Direction getEnd() { return end; }
    public Turn getTurn() { return turn; }
    public long getEnqueueTime() { return enqueueTime; }

    private static Turn computeTurn(Direction start, Direction end) {
        if (start == end.opposite()) return STRAIGHT;

        return switch (start) {
            case NORTH -> (end == WEST) ? LEFT : RIGHT;
            case SOUTH -> (end == EAST) ? LEFT : RIGHT;
            case EAST -> (end == NORTH) ? LEFT : RIGHT;
            case WEST -> (end == SOUTH) ? LEFT : RIGHT;
            default -> {
                throw new IllegalArgumentException("Invalid direction: " + start + " to " + end);
            }
        };
    }
}
