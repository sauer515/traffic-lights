package app.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {
    @Test void pass_through_axis_order_by_enqueue_NS() throws Exception {
        var x = new Intersection();
        x.north.add(new Vehicle("n1", Direction.NORTH, Direction.SOUTH));
        Thread.sleep(2);
        x.south.add(new Vehicle("s1", Direction.SOUTH, Direction.NORTH));
        var ids = x.passThroughAxis(Phase.NS);
        assertEquals(List.of("n1","s1"), ids);
    }

    @Test void passThroughAxisWithVehicles_returns_objects() {
        var x = new Intersection();
        x.east.add(new Vehicle("e1", Direction.EAST, Direction.WEST));
        var vs = x.passThroughAxisWithVehicles(Phase.EW);
        assertEquals(1, vs.size());
        assertEquals("e1", vs.get(0).getId());
    }
}
