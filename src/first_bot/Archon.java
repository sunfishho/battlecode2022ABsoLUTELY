
package first_bot;

import battlecode.common.*;


public class Archon extends RobotCommon{

    // static RobotController rc;
    static MapLocation home;
    static int[] knownMap = new int[62 * 60];
    static int numArchons, numScoutsSent, numForagersSent;
    /*
        Values of important locations are stored on the map, negative values correspond to opponent:
            1-4: archons of corresponding rank
            5: miner sent to location
    */

    public Archon(RobotController rc, int r, MapLocation loc) throws GameActionException{
        super(rc, r, loc);
        
        //initialize the Symmetry bit
        rc.writeSharedArray(Util.getSymmetryMemoryBlock(), 3);
        rc.writeSharedArray(17, 65535);
        rc.writeSharedArray(18, 65535);
        
        // for Util to know the width/height of the map
        Util.WIDTH = rc.getMapWidth();
        Util.HEIGHT = rc.getMapHeight();
    }

    public void takeTurn() throws GameActionException {
        round = rc.getRoundNum();
        // establishRank and relocCheck on turn 1, writeArchonLocations on turn 2
        if(round == 1) {
            establishRank();
            relocCheck();
        }
        if(round == 2) {
            writeArchonLocations();
        }
        /*if(round == 500) {
            rc.disintegrate();
        }*/

        // we should try moving to a nearby place with less rubble
        if(me != home){
            Pathfinding pf = new Pathfinding(this);
            Direction dir = pf.findBestDirection(home);
            if (rc.canMove(dir)) {
                rc.move(dir);
                me = rc.getLocation();
            }
        }

        rc.setIndicatorString(Integer.toString(rank));

        // Try randomly to pick a direction to build in
        Direction dir = Util.directions[rng.nextInt(Util.directions.length)];
        for (int i = 0; i < 8; i++) {
            if (rc.canBuildRobot(RobotType.MINER, dir)) break;
            dir = Util.directions[rng.nextInt(Util.directions.length)];
        }
        
        int alarm = rc.readSharedArray(18);

        if (alarm < round - 10) {
            rc.writeSharedArray(18, 65535);
            rc.writeSharedArray(17, 65535);
        }

        // System.out.println("ALARM: " + alarm);
        // System.out.println("LOCATION: " + rc.readSharedArray(17));
        if (rc.canBuildRobot(RobotType.MINER, dir) 
            && (((rc.getTeamLeadAmount(rc.getTeam()) < 200 || round < 5) && alarm == 65535) || round % 10 == 0)) {

            // want to send two scouts, one in the two orthogonal directions to try to find the symmetry of the map
            if(rank == 1 && numScoutsSent < 2) { // subtype 1
                MapLocation target = new MapLocation(0, 0);
                if (numScoutsSent == 0){
                    target = new MapLocation(Util.WIDTH - me.x - 1, me.y);
                    System.out.println(me.x + " " + me.y);
                    System.out.println("MINER TARGET IS: " + target);
                }
                else{
                    target = new MapLocation(me.x, Util.HEIGHT - me.y - 1);
                    System.out.println(me.x + " " + me.y);
                    System.out.println("MINER TARGET IS: " + target);
                }
                numScoutsSent++;
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank), Util.getIntFromLocation(target) + Util.MAX_LOC);
                rc.buildRobot(RobotType.MINER, dir);
            }
            else if(numForagersSent < numArchons) { // subtype 2
                System.out.println(numForagersSent + " " + numArchons);
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank), 
                    Util.getIntFromLocation(computeMirrorGuess(Util.getLocationFromInt(rc.readSharedArray(numForagersSent)))) 
                        + 2 * Util.MAX_LOC);
                numForagersSent++;
                rc.writeSharedArray(19, rc.readSharedArray(19) + 1); 
                rc.buildRobot(RobotType.MINER, dir);
            }
            else if(rc.readSharedArray(19) == numArchons * numArchons) { // want all of the foragers to be sent first
                int target = findLocalLocation();
                if(target != -1) {
                    rc.writeSharedArray(Util.getArchonMemoryBlock(rank), target);
                }
                else {
                    rc.writeSharedArray(Util.getArchonMemoryBlock(rank), findMinerReport());
                }
                rc.buildRobot(RobotType.MINER, dir);
            }
        }
        else {
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
        }
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

    // Writes Archon locations to knownMap
    public void writeArchonLocations() throws GameActionException {
        int loc = Util.getIntFromLocation(me);
        for(int i = 0; i < 4; i++) {
            if(rc.readSharedArray(i) != 0) {
                knownMap[rc.readSharedArray(i)] = i + 1;
                numArchons++;
            }
        }
    }

    // Guesses mirror to Archon at loc by min distance metric (note we could predict different symmetries for different archons)
    public MapLocation computeMirrorGuess(MapLocation loc) throws GameActionException {
        MapLocation[] guesses = {new MapLocation(Util.WIDTH - loc.x - 1, loc.y), new MapLocation(loc.x, Util.HEIGHT - loc.y - 1),
            new MapLocation(Util.WIDTH - loc.x - 1, Util.HEIGHT - loc.y - 1)};
            
        int bestDist = 0;
        MapLocation bestGuess = new MapLocation(0, 0);
        for(int i = 0; i < 3; i++) {
            int minDist = 100000;
            for(int j = 0; j < numArchons; j++) {
                minDist = Util.min(minDist, guesses[i].distanceSquaredTo(Util.getLocationFromInt(rc.readSharedArray(j))));
            }
            if(minDist > bestDist) {
                bestDist = minDist;
                bestGuess = guesses[i];
            }
        }
        return bestGuess;
    }

    //see if any nearby squares have significantly less rubble
    public void relocCheck() throws GameActionException {
        int lx = me.x;
        int ly = me.y;
        int bestd = 0;
        int bestr = rc.senseRubble(me);
        MapLocation newhome = me;
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

    // Returns nearby new locations with lead/gold (probably can remove in the future)
    public int findLocalLocation() throws GameActionException {
        // iterate through gold locations that have not been targets before
        MapLocation[] goldLocations = rc.senseNearbyLocationsWithGold(getVisionRadiusSquared());
        for(int i = 0; i < goldLocations.length; i++) {
            int loc = Util.moveOnLattice(Util.getIntFromLocation(goldLocations[i]));
            if(knownMap[loc] == 0) {
                knownMap[loc] = 5;
                return loc;
            }
        }

        // iterate through lead locations that have not been targets before
        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead(getVisionRadiusSquared());
        for(int i = 0; i < leadLocations.length; i++) {
            int loc = Util.moveOnLattice(Util.getIntFromLocation(leadLocations[i]));
            if(knownMap[loc] == 0) {
                knownMap[loc] = 5;
                return loc;
            }
        }
        return -1;
    }

    // Returns new location reported by miner, if not new, report default (me)
    public int findMinerReport() throws GameActionException {
        int locFromMiner = rc.readSharedArray(Util.getArchonMemoryBlock(rank) + 1);
        int ret = Util.getIntFromLocation(me);
        if(knownMap[locFromMiner] == 0) {
            knownMap[locFromMiner] = 5;
            ret = locFromMiner;
        }
        rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 1, Util.getIntFromLocation(me));
        return ret;
    }
}