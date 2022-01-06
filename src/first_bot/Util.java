package first_bot;
import battlecode.common.*;

public class Util {
    static int TAXICAB_WEIGHT = 30;
    static int ARCHON_MEMORY_SIZE = 1;
    static int NUM_ITERATIONS_BELLMAN_FORD = 7;
    static int WALL_HEIGHT_DIFF = 30;

    static final int[] dxDiff = new int[] {0, 1, 1, 1, 0, -1, -1, -1};
    static final int[] dyDiff = new int[] {1, 1, 0, -1, -1, -1, 0, 1};

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

    public static int getDirectionIndex(Direction d){
        switch (d){
            case NORTH: return 0;
            case NORTHEAST: return 1;
            case EAST: return 2;
            case SOUTHEAST: return 3;
            case SOUTH: return 4;
            case SOUTHWEST: return 5;
            case WEST: return 6;
            case NORTHWEST: return 7;
            default: return 8;
        }
    }

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

    public static int max(int a, int b){
        if (a >= b){
            return a;
        }
        return b;
    }

    public static int min(int a, int b){
        if (a >= b){
            return b;
        }
        return a;
    }

    public static int distanceMetric(int x1, int x2, int y1, int y2){
        return max(abs(x1 - x2), abs(y1 - y2));
    }
}