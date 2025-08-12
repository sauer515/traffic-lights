package app.sched;

import app.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WeightedSchedulerTest {
    @Test void chooses_axis_by_count() {
        var x = new Intersection();
        x.east.add(new Vehicle("e1", Direction.EAST, Direction.WEST));
        x.east.add(new Vehicle("e2", Direction.EAST, Direction.WEST));
        var sched = new WeightedScheduler(x, 10_000, 0);
        assertEquals(Phase.EW, sched.decideNextPhase());
    }

    @Test void hold_steps_keep_phase_temporarily() {
        var x = new Intersection();
        x.north.add(new Vehicle("n1", Direction.NORTH, Direction.SOUTH));
        x.east.add(new Vehicle("e1", Direction.EAST, Direction.WEST));
        var sched = new WeightedScheduler(x, 10_000, 2);
        var first = sched.decideNextPhase();
        var second = sched.decideNextPhase();
        assertEquals(first, second);
    }
}
