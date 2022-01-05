package first_bot;
import battlecode.common.*;

public class Util {
    static int TAXICAB_WEIGHT = 30;
    static int ARCHON_MEMORY_SIZE = 1;
    static int NUM_ITERATIONS_BELLMAN_FORD = 7;

    static final int[] dxDiff = new int[] {-1, -1, -1, 0, 0, 1, 1, 1};
    static final int[] dyDiff = new int[] {-1, 0, 1, -1, 1, -1, 0, 1};

    static final Direction[] directions = new Direction[] {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    public static int getArchonMemoryBlock(int rank) {
        return 4 + rank * ARCHON_MEMORY_SIZE;
    }

    public static MapLocation getLocationFromInt(int loc) {
        return new MapLocation(loc / 64, loc % 64);
    }

    public static int getIntFromLocation(MapLocation loc) {
        return loc.x * 64 + loc.y;
    }

    public static int abs(int a){
        if (a >= 0){
            return a;
        }
        return -a;
    }

    // static int WALL_DEFINITION_CUTOFF = 50;
}