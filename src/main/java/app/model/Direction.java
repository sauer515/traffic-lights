package app.model;

public enum Direction {
    NORTH, SOUTH, EAST, WEST;

    public static Direction fromString(String s) {
        if (s == null) throw new IllegalArgumentException("Direction null");
        return switch (s.trim().toLowerCase()) {
            case "n", "north" -> NORTH;
            case "s", "south" -> SOUTH;
            case "e", "east" -> EAST;
            case "w", "west" -> WEST;
            default -> throw new IllegalArgumentException("Unknown direction: " + s);
        };
    }

    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST  -> WEST;
            case WEST  -> EAST;
        };
    }
}
