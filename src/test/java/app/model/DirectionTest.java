package app.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {
    @Test void fromString_accepts_variants() {
        assertEquals(Direction.NORTH, Direction.fromString("north"));
        assertEquals(Direction.SOUTH, Direction.fromString("S"));
        assertEquals(Direction.EAST,  Direction.fromString("EAST"));
        assertEquals(Direction.WEST,  Direction.fromString("w"));
    }
    @Test void fromString_rejects_invalid() {
        assertThrows(IllegalArgumentException.class, () -> Direction.fromString("nope"));
        assertThrows(IllegalArgumentException.class, () -> Direction.fromString(""));
    }
}
