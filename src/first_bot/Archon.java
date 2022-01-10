
package first_bot;

import battlecode.common.*;
import java.util.ArrayList;


public class Archon extends RobotCommon{

    // static RobotController rc;
    static MapLocation home;
    static int[] knownMap = new int[62 * 60];
    static int numArchons, numScoutsSent, numForagersSent, teamLeadAmount, targetArchon;
    static ArrayList<Integer> vortexRndNums;
    static int vortexCnt = 0;
    
    /*
        Values of important locations are stored on the map, negative values correspond to opponent:
            1-4: archons of corresponding rank
            5: miner sent to location
    */

    public Archon(RobotController rc, int r, MapLocation loc) throws GameActionException{
        super(rc, r, loc);

        numForagersSent = 0;

        //initialize the Symmetry bit
        rc.writeSharedArray(Util.getSymmetryMemoryBlock(), 3);
        rc.writeSharedArray(17, 65535);
        rc.writeSharedArray(18, 65535);
        rc.writeSharedArray(20, 0);
        
        // for Util to know the width/height of the map
        Util.WIDTH = rc.getMapWidth();
        Util.HEIGHT = rc.getMapHeight();
    }

    public void takeTurn() throws GameActionException {
        // update variables
        round = rc.getRoundNum();
        teamLeadAmount = rc.getTeamLeadAmount(rc.getTeam());
        targetArchon = rc.readSharedArray(20);
        // establishRank and relocCheck on turn 1, writeArchonLocations on turn 2
        if(round == 1) {
            establishRank();
            relocCheck();
            AnomalyScheduleEntry[] sched = rc.getAnomalySchedule();
            for (AnomalyScheduleEntry a : sched){
                if(a.anomalyType == AnomalyType.VORTEX){
                    vortexRndNums.add(a.roundNumber);
                }
            }
        }
        if(round == 2) {
            writeArchonLocations();
        }

        int alarm = rc.readSharedArray(18);

        if(round == vortexRndNums.get(vortexCnt) && alarm == 65535){//vortex --> we might have been moved onto lots of rubble
            relocCheck();
            vortexCnt++;
        }
        // If number of archons has decreased, shift all the archons down
        int newNumArchons = rc.getArchonCount();
        if (newNumArchons != numArchons) {
            numArchons = newNumArchons;
            for (int i = rank-1; i < 4; i++) {
                rc.writeSharedArray(i, 0);
            }
            establishRank();
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

        if (alarm < round - 6) {
            rc.writeSharedArray(18, 65535);
            rc.writeSharedArray(17, 65535);
        }

        // System.out.println("ALARM: " + alarm);
        // System.out.println("LOCATION: " + rc.readSharedArray(17));
        if (alarm == 65535) {
            if (teamLeadAmount <= numArchons * 50 && (targetArchon % numArchons) != (rank % numArchons)) {
                return;
            }
        } else { // figure out where the alarm is coming from and send troops
            if (teamLeadAmount <= numArchons * 50 && !observe() && rank != rc.readSharedArray(17) / 10000) {
                return;
            }
        }
        if (rc.canBuildRobot(RobotType.MINER, dir) 
            && (((teamLeadAmount < 400 || round < 5) && alarm == 65535) || round % 7 == 0)) {

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
                rc.writeSharedArray(20, targetArchon + 1);
            }
            else if(numForagersSent < numArchons) { // subtype 2
                System.out.println("FORAGER: " + numForagersSent + " " + numArchons);
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank), 
                    Util.getIntFromLocation(computeMirrorGuess(Util.getLocationFromInt(rc.readSharedArray(numForagersSent)))) 
                        + 2 * Util.MAX_LOC);
                numForagersSent++;
                rc.writeSharedArray(19, rc.readSharedArray(19) + 1); 
                rc.buildRobot(RobotType.MINER, dir);
                rc.writeSharedArray(20, targetArchon + 1);
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
                rc.writeSharedArray(20, targetArchon + 1);
            }
        }
        else if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            rc.writeSharedArray(20, targetArchon + 1);
        }
        else if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
            rc.buildRobot(RobotType.BUILDER, dir);
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

    // Check if any aggressive enemy archons nearby
    public boolean observe() throws GameActionException {
        for (RobotInfo robot : rc.senseNearbyRobots()) {
            if (robot.getTeam() != rc.getTeam() && robot.getType() != RobotType.MINER) {
                rc.writeSharedArray(17, Util.getIntFromLocation( robot.location) + 10000 * rank);
                rc.writeSharedArray(18, round);
                return true;
            }
        }
        return false;
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