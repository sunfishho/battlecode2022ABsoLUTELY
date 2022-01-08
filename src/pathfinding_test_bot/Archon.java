
package first_bot;

import battlecode.common.*;


public class Archon extends RobotCommon{

    // static RobotController rc;

    static boolean checkedNearby = false;
    static MapLocation home;
    static int rank = -1; // 1-based
    static int[] knownMap = new int[62 * 60];
    static boolean wroteArchons;
    static int numScoutsSent;
    static boolean isScoutingDone = false;
    /*
        Values of important locations are stored on the map, negative values correspond to opponent:
            1-4: archons of corresponding rank
            5: miner sent to location
    */
    static boolean builtMinersLast; // true if miners were built on the previous turn

    public Archon(RobotController rc) throws GameActionException{
        super(rc);
        
        //initialize the Symmetry bit
        rc.writeSharedArray(Util.getSymmetryMemoryBlock(), 3);
        
        // for Util to know the width/height of the map
        Util.WIDTH = rc.getMapWidth();
        Util.HEIGHT = rc.getMapHeight();
    }

    // Establish an order between the Archons by writing to the shared array.
    public void establishRank() throws GameActionException {
        for(int i = 0; i < 4; i++) {
            if(rc.readSharedArray(i) == 0) {
                int loc = Util.getIntFromLocation(me);
                rc.writeSharedArray(i, loc);
                System.out.println(i + " " + me);
                rank = i + 1;
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 1, loc);
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
                    if (rc.canSenseLocation(mc)) {
                        int rub = rc.senseRubble(mc);
                        if((10 + rub) * (100 - 10 * bestd) < (10 + bestr) * (100 - 10 * d)){
                            bestd = d;
                            bestr = rub;
                            newhome = mc;
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        home = newhome;
    }

    public void takeTurn() throws GameActionException {
        me = rc.getLocation();
        if(!checkedNearby){
            relocCheck();
            checkedNearby = true;
        }

        if(me != home){ // we should try moving to a nearby place with less rubble
            GreedyPathfinding gpf = new GreedyPathfinding(this);
            Direction dir = gpf.travelTo(home);
            if (rc.canMove(dir)) {
                rc.move(dir);
                me = rc.getLocation();
            }
        }

        // establishRank on turn 1, write archon locations on turn 2
        if(!wroteArchons && rank == -1) {
            int loc = Util.getIntFromLocation(me);
            for(int i = 0; i < 4; i++) {
                knownMap[rc.readSharedArray(i)] = i + 1;
            }
            wroteArchons = true;
        }
        if(rank == -1) establishRank();
        rc.setIndicatorString(Integer.toString(rank));

        // Try randomly to pick a direction to build in
        Direction dir = Util.directions[rng.nextInt(Util.directions.length)];
        for (int i = 0; i < 8; i++) {
            if (rc.canBuildRobot(RobotType.MINER, dir)) break;
            dir = Util.directions[rng.nextInt(Util.directions.length)];
        }
        if (rc.canBuildRobot(RobotType.MINER, dir) && !builtMinersLast) {
            rc.buildRobot(RobotType.MINER, dir);

            //want to send two scouts, one in the two orthogonal directions to try to find the symmetry of the map
            if (!isScoutingDone && numScoutsSent == 2){
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 2, 0);
                isScoutingDone = true;
            }
            if (rank == 1 && numScoutsSent < 2){
                MapLocation minerTarget = new MapLocation(0, 0);
                if (numScoutsSent == 0){
                    minerTarget = new MapLocation(Util.WIDTH - me.x - 1, me.y);
                    System.out.println(me.x + " " + me.y);
                    System.out.println("MINER TARGET IS: " + minerTarget);
                }
                else{
                    minerTarget = new MapLocation(me.x, Util.HEIGHT - me.y - 1);
                    System.out.println(me.x + " " + me.y);
                    System.out.println("MINER TARGET IS: " + minerTarget);
                }
                numScoutsSent++;
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 2, Util.getIntFromLocation(minerTarget));
            }

            writeMinerLocation();
            builtMinersLast = true;
        }
        else {
            builtMinersLast = false;
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
        }
    }

    // Writes a new Location for the new Miner to go to, should be on-lattice
    public void writeMinerLocation() throws GameActionException {
        // iterate through gold locations that have not been targets before
        MapLocation[] goldLocations = rc.senseNearbyLocationsWithGold(getVisionRadiusSquared());
        for(int i = 0; i < goldLocations.length; i++) {
            int loc = Util.moveOnLattice(Util.getIntFromLocation(goldLocations[i]));
            if(knownMap[loc] == 0) {
                knownMap[loc] = 5;
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank), loc);
                return;
            }
        }

        // iterate through lead locations that have not been targets before
        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead(getVisionRadiusSquared());
        for(int i = 0; i < leadLocations.length; i++) {
            int loc = Util.moveOnLattice(Util.getIntFromLocation(leadLocations[i]));
            if(knownMap[loc] == 0) {
                knownMap[loc] = 5;
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank), loc);
                return;
            }
        }
        
        // otherwise, see if a Miner has sent back a location or send default location (me)
        int locFromMiner = rc.readSharedArray(Util.getArchonMemoryBlock(rank) + 1);
        if(knownMap[locFromMiner] == 0) {
            knownMap[locFromMiner] = 5;
            rc.writeSharedArray(Util.getArchonMemoryBlock(rank), locFromMiner);
        }
        else {
            rc.writeSharedArray(Util.getArchonMemoryBlock(rank), Util.getIntFromLocation(me));
        }
        rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 1, Util.getIntFromLocation(me)); // replace with default location
    }
}