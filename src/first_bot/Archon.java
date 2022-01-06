
package first_bot;

import battlecode.common.*;


public class Archon extends RobotCommon{
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
                int loc = Util.getIntFromLocation(me);
                rc.writeSharedArray(i, loc);
                System.out.println(i + " " + me);
                rank = i + 1;
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 1, loc);
                break;
            }
        }
    }

    public void takeTurn() throws GameActionException {
        // -1 = establish rank, 0 = find other Archons
        if(rank == -1) establishRank();
        if(!wroteArchons) {
            int loc = Util.getIntFromLocation(me);
            for(int i = 0; i < 4; i++) {
                knownMap[rc.readSharedArray(i)] = i + 1;
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
                break;
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
                // System.out.println(rank + " " + leadLocations[i]);
                break;
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