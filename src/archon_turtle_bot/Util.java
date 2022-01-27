package archon_turtle_bot;
import battlecode.common.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;


public class Util {
    static int TAXICAB_WEIGHT = 30;
    static int ARCHON_MEMORY_SIZE = 3;
    static int seed = 69420;//random seed for rng
    /*
        Shared Array:
            0-3: location of Archons 1-4
            4-15: memory blocks
            16: symmetry indicator (7 if undecided, 1 if symmetric about vertical line, 2 if symmetric about horizontal, 4 if rotationally symmetric)
            37: laboratory attendance
            38: archon alarm special
            39: literally 0 if our aggregate health decreased over last 10, 1 if our aggregate health increased over last 10
            40: aggregate health
            41: farmer attendance
            42: round in which we first observe enemy miner
            43: alarm indicator (location) + 10000 * rank #2
            44: alarm indicator (round)
            45: location of spawned defender
            46: 1 if some archon is in portable mode, 0 else
            48: number of useful builder sacrifices
            49: alarm indicator (location) + 10000 * rank
            50: alarm indicator (round)
            51: number of foragers sent in total
            52: targeted archon for robot production mod numArchons
            53: alarm indicator (location) + 10000 * rank (for enemy archons)
            54: alarm indicator (round) (for enemy archons)
            55: round number + 2000 * number of archons processed (used for updating rank numbers when archons die)
            56-59: archon initial locations
            60: miner attendance
            61: soldier attendance
            62: income
            63: laboratory number (10000 * # of builders sent out + 100 * # of labs we should build + 1 * # of labs we have built)
        Indices within each memory block size correspond to:
            0: Archon writes to value
            1: Miner writes to value
            2: Archon writes to value-secondarily
        Use Util.getMemoryBlock(rank) to find beginning of memory block
    */
    static int NUM_ITERATIONS_BELLMAN_FORD = 7;
    static int WALL_HEIGHT_DIFF = 30;
    static int LOC_BASE = 61;
    static int MAX_LOC = LOC_BASE * LOC_BASE;
    static int HEIGHT, WIDTH;
    static int NUM_ARCHONS;
    static int[] charges;
    static int DISTANCE_WEIGHT_RECIPROCAL = 5;

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
        RobotType.WATCHTOWER,
        RobotType.SAGE,
        RobotType.SOLDIER,
        RobotType.LABORATORY,
        RobotType.BUILDER,
        RobotType.ARCHON,
        RobotType.MINER
    };

    static int getAttackPref(RobotType rt){
        switch (rt){
            case WATCHTOWER: return 0;
            case SAGE: return 1;
            case SOLDIER: return 2;
            case LABORATORY: return 3;
            case BUILDER: return 4;
            case ARCHON: return 5;
            case MINER: return 6;
            default: return 7;
        }
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

    public static boolean isOnMap(MapLocation mL){
        return (mL.x >= 0 && mL.x < Util.WIDTH && mL.y >= 0 && mL.y < Util.HEIGHT);
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

    // Moves an intLoc to nearby on lattice location
    public static int moveOnLattice(int intLoc) {
        MapLocation loc = getLocationFromInt(intLoc);
        /*
            move to 1 mod 3, if beyond bounds (right/top edge) then move left
        */ 
        int x = loc.x / 3 * 3 + 1;
        if(x == WIDTH) x--;
        int y = loc.y / 3 * 3 + 1;
        if(y == HEIGHT) y--;
        return x * LOC_BASE + y + 1;
    }

    public static MapLocation moveOnLattice(MapLocation loc) {
        int x = loc.x / 3 * 3 + 1;
        if(x == WIDTH) x--;
        int y = loc.y / 3 * 3 + 1;
        if(y == HEIGHT) y--;
        return new MapLocation(x, y);
    }

    public static boolean onLattice(int intLoc) {
        MapLocation loc = getLocationFromInt(intLoc);
        int x = loc.x / 3 * 3 + 1;
        if(x == WIDTH) x--;
        int y = loc.y / 3 * 3 + 1;
        if(y == HEIGHT) y--;
        return (x == loc.x && y == loc.y);
    }

    public static boolean onLattice(MapLocation loc) {
        int x = loc.x / 3 * 3 + 1;
        if(x == WIDTH) x--;
        int y = loc.y / 3 * 3 + 1;
        if(y == HEIGHT) y--;
        return (x == loc.x && y == loc.y);
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

    public static int distanceMetric(int x1, int y1, int x2, int y2){
        return max(abs(x1 - x2), abs(y1 - y2)) + (min(abs(x1 - x2), abs(y1 - y2)))/DISTANCE_WEIGHT_RECIPROCAL;
    }

    public static int distanceMetric(MapLocation m1, MapLocation m2){
        return max(abs(m1.x - m2.x), abs(m1.y - m2.y));
    }

    public static MapLocation horizontalRefl(MapLocation loc){
        return new MapLocation(WIDTH - 1 - loc.x, loc.y);
    }

    public static MapLocation verticalRefl(MapLocation loc){
        return new MapLocation(loc.x, HEIGHT - 1 - loc.y);
    }

    public static MapLocation centralRefl(MapLocation loc){
        return new MapLocation(WIDTH - 1 - loc.x, HEIGHT - 1 - loc.y);
    }

    //decide later
    public static boolean watchtowerElig(MapLocation loc){
        return (loc.x % 3 == 2 && loc.y % 3 == 2);
    }

    //decide later
    public static boolean labElig(MapLocation loc){
        return true;
    }

    public static MapLocation pickBuilderTarget(MapLocation loc){//pick target of builder spawned from loc
        //picks random location on "opposite walls" of the map
        Random rng = new Random();
        int r = rng.nextInt(WIDTH + HEIGHT - 1);
        if(r < WIDTH){//target on horizontal edge
            if(loc.y * 2 <= HEIGHT){
                return new MapLocation(r, HEIGHT - 1);
            }
            else{
                return new MapLocation(r, 0);
            }
        }
        else{//target on vertical edge
            if(loc.x * 2 <= WIDTH){
                return new MapLocation(WIDTH - 1, r - WIDTH);
            }
            else{
                return new MapLocation(0, r - WIDTH);
            }
        }
    }

    public static MapLocation getCorner(MapLocation loc){
        //returns corner closest to loc
        int x = 0;
        int y = 0;
        if(2 * loc.x >= WIDTH){
            x = WIDTH - 1;
        }
        if(2 * loc.y >= HEIGHT){
            y = HEIGHT - 1;
        }
        return new MapLocation(x, y);
    }

    public static boolean buildingHealthy(RobotType x, int hp, int level){//sees if building is healthy or not
        if(x == RobotType.LABORATORY){
            switch(level){
                case 1: return (hp == 100);
                case 2: return (hp == 180);
                default: return (hp == 324);
            }
        }
        if(x == RobotType.ARCHON){
            switch(level){
                case 1: return (hp == 600);
                case 2: return (hp == 1080);
                default: return (hp == 1944);
            }
        }
        if(x == RobotType.WATCHTOWER){
            switch(level){
                case 1: return (hp == 150);
                case 2: return (hp == 270);
                default: return (hp == 486);
            }
        }
        return false;
    }

    public static boolean inGrid(MapLocation loc){
        return (0 <= loc.x) && (loc.x < WIDTH) && (0 <= loc.y) && (loc.y < HEIGHT);
    }

    public static void getChargeSchedule(ArrayList<Integer> chargeRndNums){//store charge round numbers in Util
        int sz = chargeRndNums.size();
        charges = new int[sz];
        for(int i = 0; i < sz; i++){
            charges[i] = chargeRndNums.get(i);
        }
    }

    public static int nextCharge(int rnd){//get the round number of the next charge
        int idx = 0;
        if(charges == null){
            return 3000;
        }
        //System.out.println(charges + " " + charges.length);
        while(idx < charges.length){
            if(charges[idx] > rnd){
                return charges[idx];
            }
            idx++;
        }
        return 3000;//no more charges
    }

}