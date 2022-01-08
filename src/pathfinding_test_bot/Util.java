package first_bot;
import battlecode.common.*;

public class Util {
    static int TAXICAB_WEIGHT = 30;
    static int ARCHON_MEMORY_SIZE = 3;
    /*
        Shared Array:
            0-3: location of Archons 1-4
            4-15: memory blocks
            16: symmetry indicator (3 if undecided, 1 if symmetric about vertical line, 2 if symmetric about horizontal, 0 if rotationally symmetric)
        Indices within each memory block size correspond to:
            0: Archon writes to value (read by Miners)
            1: Miner writes to value (read by Archons)
        Use Util.getMemoryBlock(rank) to find beginning of memory block
    */
    static int NUM_ITERATIONS_BELLMAN_FORD = 7;
    static int WALL_HEIGHT_DIFF = 30;
    static int LOC_BASE = 61;
    static int HEIGHT, WIDTH;
    static int NUM_ARCHONS;
    //this is for distanceMetric()
    static double DISTANCE_WEIGHT_OTHER_COMPONENT = 0.2;

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

    static final RobotType[] attackOrder = new RobotType[] {
        RobotType.ARCHON,
        RobotType.SAGE,
        RobotType.LABORATORY,
        RobotType.WATCHTOWER,
        RobotType.SOLDIER,
        RobotType.BUILDER,
        RobotType.MINER
    };

    static int getAttackPref(RobotType rt){
        switch (rt){
            case ARCHON: return 0;
            case SAGE: return 1;
            case LABORATORY: return 2;
            case WATCHTOWER: return 3;
            case SOLDIER: return 4;
            case BUILDER: return 5;
            case MINER: return 6;
        }
        return 7;
    }

    public static int getDirectionIndex(Direction d){
        switch (d) {
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

    // returns beginning of entry block
    public static int getArchonMemoryBlock(int rank) {
        return 4 + (rank - 1) * ARCHON_MEMORY_SIZE;
    }

    public static MapLocation getLocationFromInt(int loc) {
        return new MapLocation((loc - 1) / LOC_BASE, (loc - 1) % LOC_BASE);
    }

    public static int getIntFromLocation(MapLocation loc) {
        return loc.x * LOC_BASE + loc.y + 1;
    }
    //return index of symmetry number
    public static int getSymmetryMemoryBlock(){
        return 7 + 3 * ARCHON_MEMORY_SIZE;
    }

    public static int moveOnLattice(int intLoc) {
        MapLocation loc = getLocationFromInt(intLoc);
        if(loc.x % 2 == 1) {
            if(loc.x != WIDTH) loc.translate(1, 0);
            else loc.translate(-1, 0);
        }
        if(loc.y % 2 == 1) {
            if(loc.y != WIDTH) loc.translate(0, 1);
            else loc.translate(0, -1);
        }
        return getIntFromLocation(loc);
    }

    public static boolean onLattice(int intLoc) {
        MapLocation loc = getLocationFromInt(intLoc);
        return (loc.x % 2 == 0 && loc.y % 2 == 0);
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
        return max(abs(x1 - x2), abs(y1 - y2)) + (int)(DISTANCE_WEIGHT_OTHER_COMPONENT * min(abs(x1 - x2), abs(y1 - y2)));
    }

    public static int distanceMetric(MapLocation m1, MapLocation m2){
        return max(abs(m1.x - m2.x), abs(m1.y - m2.y));
    }

    public static void markSymmetry(MapLocation mL){

    }
}