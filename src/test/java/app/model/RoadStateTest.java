package app.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoadStateTest {
    @Test void fifo_behavior_and_size() {
        var r = new RoadState();
        r.add(new Vehicle("v1", Direction.NORTH, Direction.SOUTH));
        r.add(new Vehicle("v2", Direction.NORTH, Direction.SOUTH));
        assertEquals(2, r.size());
        assertEquals("v1", r.poll().getId());
        assertEquals("v2", r.poll().getId());
        assertTrue(r.isEmpty());
    }
}
