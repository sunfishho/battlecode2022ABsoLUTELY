package first_bot;
import battlecode.common.*;

public class Util {
    static int TAXICAB_WEIGHT = 30;
    static int ARCHON_MEMORY_SIZE = 1;
    static int NUM_ITERATIONS_BELLMAN_FORD = 3;

    static final int[] dxDiff = new int[] {-1, -1, -1, 0, 0, 1, 1, 1};
    static final int[] dyDiff = new int[] {-1, 0, 1, -1, 1, -1, 0, 1};

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


    public static Direction getDirectionFromIndex(int idx) throws GameActionException{
        switch (idx){
            case 0: return Direction.SOUTHWEST;
            case 1: return Direction.WEST;
            case 2: return Direction.NORTHWEST;
            case 3: return Direction.SOUTH;
            case 4: return Direction.NORTH;
            case 5: return Direction.SOUTHEAST;
            case 6: return Direction.EAST;
            case 7: return Direction.NORTHEAST;
        }
        //kinda a hack
        throw new GameActionException(GameActionExceptionType.CANT_DO_THAT, "INVALID INDEX INTO DIRECTION ARRAY");
    }




    // static int WALL_DEFINITION_CUTOFF = 50;
}