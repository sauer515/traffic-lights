package app.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {
    @Test void computes_turns() {
        assertEquals(Turn.STRAIGHT, new Vehicle("a", Direction.NORTH, Direction.SOUTH).getTurn());
        assertEquals(Turn.RIGHT,     new Vehicle("b", Direction.NORTH, Direction.EAST).getTurn());
        assertEquals(Turn.LEFT,    new Vehicle("c", Direction.NORTH, Direction.WEST).getTurn());
    }
}
