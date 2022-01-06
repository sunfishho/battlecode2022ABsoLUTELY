
package first_bot;

import battlecode.common.*;


public class Archon extends RobotCommon{

    // static RobotController rc;

    static boolean checkedNearby = false;
    static MapLocation home;
    static int rank = -1; // 1-based
    static int[] knownMap = new int[65 * 60];
    static boolean wroteArchons;
    /*
        Values of important locations are stored on the map, negative values correspond to opponent:
            1-4: archons of corresponding rank
            5: miner sent to location
    */
    static boolean builtMinersLast;

    public Archon(RobotController rc){
        super(rc);
    }

    // Establish an order between the Archons by writing to the shared array.
    public void establishRank() throws GameActionException {
        for(int i = 0; i < 4; i++) {
            if(rc.readSharedArray(i) == 0) {
                rc.writeSharedArray(i, Util.getIntFromLocation(me));
                System.out.println(i + " " + me);
                rank = i + 1;
                break;
            }
        }
    }

    //see if any nearby squares have significantly less rubble
    public void relocCheck() throws GameActionException {
        MapLocation loc = rc.getLocation();
        int lx = loc.x;
        int ly = loc.y;
        int bestd = 0;
        int bestr = rc.senseRubble(loc);
        MapLocation newhome = loc;
        for(int dx = 2; dx >= -2; dx--){
            for(int dy = 2; dy >= -2; dy--){
                int d = Util.max(Util.abs(dx), Util.abs(dy));
                if(d == 0){
                    continue;
                }
                MapLocation mc = new MapLocation(dx - 2 + lx, dy - 2 + ly);
                try{//see if mc is a viable place to move to
                    int rub = rc.senseRubble(mc);
                    if((10 + rub) * (100 - 10 * bestd) < (10 + bestr) * (100 - 10 * d)){
                        bestd = d;
                        bestr = rub;
                        newhome = mc;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        home = newhome;
    }

    public void takeTurn() throws GameActionException {
        // -1 = establish rank, 0 = find other Archons
        if(rank == -1) establishRank();

        if(!checkedNearby){
            relocCheck();
            checkedNearby = true;
        }

        if(me != home){//we should try moving to a nearby place with less rubble
            Direction dir = me.directionTo(home);
            if(rc.canMove(dir)){
                rc.move(dir);
                me = rc.getLocation();
            }
        }

        if(!wroteArchons) {
            int loc = Util.getIntFromLocation(me);
            for(int i = 0; i < 4; i++) {
                int cur = rc.readSharedArray(i);
                if(cur == loc) {
                    System.out.println(rank);
                }
                knownMap[cur] = i + 1;
            }
            wroteArchons = true;
        }
        rc.setIndicatorString(Integer.toString(rank));

        // Pick a direction to build in.
        Direction dir = Util.directions[rng.nextInt(Util.directions.length)];
        if (rc.canBuildRobot(RobotType.MINER, dir) && !builtMinersLast) {
            rc.buildRobot(RobotType.MINER, dir);

            writeMinerLocation();

            builtMinersLast = true;
        }
        else {
            builtMinersLast = false;
        }
    }

    public void writeMinerLocation() throws GameActionException {
        boolean sentTarget = false;

        MapLocation[] goldLocations = rc.senseNearbyLocationsWithGold(Util.ARCHON_VISION_RADIUS);
        for(int i = 0; i < goldLocations.length; i++) {
            int loc = Util.getIntFromLocation(goldLocations[i]);
            if(knownMap[loc] == 0) {
                knownMap[loc] = 5;
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank), loc);
                sentTarget = true;
            }
        }

        if(sentTarget) return;

        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead(Util.ARCHON_VISION_RADIUS);
        for(int i = 0; i < leadLocations.length; i++) {
            int loc = Util.getIntFromLocation(leadLocations[i]);
            if(knownMap[loc] == 0) {
                knownMap[loc] = 5;
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank), loc);
                sentTarget = true;
            }
        }

        if(sentTarget) return;

        int locFromMiner = rc.readSharedArray(Util.getArchonMemoryBlock(rank) + 1);
        if(knownMap[locFromMiner] == 0) {
            knownMap[locFromMiner] = 5;
            rc.writeSharedArray(Util.getArchonMemoryBlock(rank), locFromMiner);
        }
        else {
            rc.writeSharedArray(Util.getArchonMemoryBlock(rank), Util.getIntFromLocation(me));
        }
    }
}