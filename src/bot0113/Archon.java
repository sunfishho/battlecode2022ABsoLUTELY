
package bot0113;

import battlecode.common.*;
import java.util.ArrayList;


public class Archon extends RobotCommon{

    // static RobotController rc;
    static MapLocation home;
    static int[] knownMap = new int[62 * 60];
    static int numArchons, numScoutsSent, numForagersSent, numSoldiersSent, teamLeadAmount, targetArchon;
    static ArrayList<Integer> vortexRndNums;
    static int vortexCnt = 0;
    static int oppLeadCount = 200;
    static int changeOppLeadCount = 0;
    Pathfinding pf = new Pathfinding(this);

    /*
        Values of important locations are stored on the map, negative values correspond to opponent:
            1-4: archons of corresponding rank
            5: miner sent to location
    */

    public Archon(RobotController rc, int r, MapLocation loc) throws GameActionException{
        super(rc, r, loc);

        numForagersSent = 100;
        numScoutsSent = 0;
        //initialize the Symmetry bit
        rc.writeSharedArray(Util.getSymmetryMemoryBlock(), 3);
        rc.writeSharedArray(17, 65535);
        rc.writeSharedArray(18, 65535);
        rc.writeSharedArray(19, 100);
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

        if (changeOppLeadCount > 51 && round > 10){
            rc.writeSharedArray(17, 65534);
        }

        if(round == 1) {
            establishRank();
            relocCheck();
            vortexRndNums = new ArrayList<Integer>();
            AnomalyScheduleEntry[] sched = rc.getAnomalySchedule();
            for (AnomalyScheduleEntry a : sched){
                if(a.anomalyType == AnomalyType.VORTEX){
                    vortexRndNums.add(a.roundNumber);
                }
            }
        }
        if(round == 2){
            writeArchonLocations();
        }

        int alarm = rc.readSharedArray(18);

        if(vortexCnt < vortexRndNums.size() && round == vortexRndNums.get(vortexCnt) + 1 && alarm == 65535){//vortex --> we might have been moved onto lots of rubble
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
        /*
        if(me != home && rc.getMode() == RobotMode.TURRET){//we should try moving to home
            if(rc.canTransform()) {
                rc.transform();
                return;
            }
        }
        else if(me != home){//already on the move, keep going to home
            //Pathfinding pf = new Pathfinding(this);
            System.out.println("ON THE MOVE: " + me.x + " " + me.y + " ---> " + home.x + " " + home.y + rc.getRoundNum());
            Direction dir = me.directionTo(home);
            if (rc.canMove(dir)) {
                rc.move(dir);
                me = rc.getLocation();
            }
            return;
        }
        else if(me == home && rc.getMode() == RobotMode.PORTABLE){//we're home, settle down
            if(rc.canTransform()){
                rc.transform();
                return;
            }
        }
      
        Archon Relocation is way too slow for now oops
         */
        rc.setIndicatorString(rank + "");

        int numBuilders = 0;
        Team us = rc.getTeam();
        for (RobotInfo robot : rc.senseNearbyRobots()) {
            if (robot.getTeam() == us && robot.getType() == RobotType.BUILDER) {
                numBuilders++;
            }
        }

        // Try to pick a direction to build in based on nearby rubble counts
        int minRubbleCount = 101;
        Direction dir = Direction.CENTER;
        for (int i = 0; i < Util.directions.length; i++) {
            Direction temp = Util.directions[i];
            if (rc.canBuildRobot(RobotType.BUILDER, temp)) {
                int rubble = rc.senseRubble(rc.getLocation().add(temp));
                if (rubble < minRubbleCount) {
                    minRubbleCount = rubble;
                    dir = temp;
                }
            }
        }
        if (rng.nextInt(4) == 1) {
            dir = Util.directions[rng.nextInt(Util.directions.length)];
            for (int i = 0; i < 15; i++) {
                if (rc.canBuildRobot(RobotType.BUILDER, dir)) break;
                dir = Util.directions[rng.nextInt(Util.directions.length)];
            }
        }

        if (alarm < round - 3) {
            rc.writeSharedArray(18, 65535);
            rc.writeSharedArray(17, 65535);
        }

        //going to cap it at round 180 before normal soldier production begins to happen
        if (round % 40 == 39 && round <= 170 && alarm == 65535 && rank == 1){
            rc.writeSharedArray(21, Util.getIntFromLocation(chooseRandomInitialDestination()) * 3 + 1);
        }
        int current_soldier_building_status = rc.readSharedArray(21);
        switch(current_soldier_building_status % 3){
            case 1: 
                if (rc.getTeamLeadAmount(rc.getTeam()) >= 75 && rank == rankOfNearestArchon(new MapLocation(Util.WIDTH/2, Util.HEIGHT/2))){
                    dir = pf.findBestDirection(Util.getLocationFromInt(rc.readSharedArray(21) / 3), 20);
                    rc.buildRobot(RobotType.SOLDIER, dir);
                    rc.writeSharedArray(21, current_soldier_building_status + 1);
                }
                return;
            case 2: 
                if (rc.getTeamLeadAmount(rc.getTeam()) >= 75 && rank == rankOfNearestArchon(new MapLocation(Util.WIDTH/2, Util.HEIGHT/2))){
                    dir = pf.findBestDirection(Util.getLocationFromInt(rc.readSharedArray(21) / 3 - 1), 20);
                    rc.buildRobot(RobotType.SOLDIER, dir);
                    rc.writeSharedArray(21, current_soldier_building_status + 1);
                }
                return;
            case 0:
                break;
        }

        // System.out.println("ALARM: " + alarm);
        // System.out.println("LOCATION: " + rc.readSharedArray(17));
        boolean enemiesNear = observe();
        if (alarm == 65535 || alarm == 65534) {
            if (teamLeadAmount <= numArchons * 50 && (targetArchon % numArchons) != (rank % numArchons)) {
                return;
            }
        }
        else { // figure out where the alarm is coming from and send troops
            if (teamLeadAmount <= numArchons * 50 && !enemiesNear && rank != rc.readSharedArray(17) / 10000) {
                return;
            }
        }

        if (rc.canBuildRobot(RobotType.MINER, dir) 
            && (((teamLeadAmount < 400 || round < 5) && alarm == 65535))) {

            //SCOUT CODE
            // want to send two scouts, one in the two orthogonal directions to try to find the symmetry of the map
            // if(rank == 1 && numScoutsSent < 2) { // subtype 1: SCOUT
            //     MapLocation target = new MapLocation(0, 0);
            //     //First scout should be sent to the horizontal reflection of the current archon.
            //     if (numScoutsSent == 0){
            //         target = Util.horizontalRefl(me);
            //         System.out.println(me.x + " " + me.y);
            //         System.out.println("MINER TARGET IS: " + target);
            //     }
            //     //Second scout should be sent to the vertical reflection of the current archon.
            //     else{
            //         target = Util.verticalRefl(me);
            //         System.out.println(me.x + " " + me.y);
            //         System.out.println("MINER TARGET IS: " + target);
            //     }
            //     numScoutsSent++;
            //     rc.writeSharedArray(Util.getArchonMemoryBlock(rank), Util.getIntFromLocation(target) + Util.MAX_LOC);
            //     rc.buildRobot(RobotType.MINER, dir);
            //     rc.writeSharedArray(20, targetArchon + 1);
            // }

            //FORAGER CODE
            //Foragers head to the closest guesses of where an enemy archon is.
            if(numForagersSent < numArchons) { // subtype 2: FORAGERS
                System.out.println("FORAGER: " + numForagersSent + " " + numArchons);
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank), 
                    Util.getIntFromLocation(computeMirrorGuess(Util.getLocationFromInt(rc.readSharedArray(numForagersSent)))) 
                        + 2 * Util.MAX_LOC);
                numForagersSent++;
                dir = pf.findBestDirection(computeMirrorGuess(Util.getLocationFromInt(rc.readSharedArray(numForagersSent))), 60);
                rc.writeSharedArray(19, rc.readSharedArray(19) + 1); 
                rc.buildRobot(RobotType.MINER, dir);
                rc.writeSharedArray(20, targetArchon + 1);
            }
            else if(rc.readSharedArray(19) >= numArchons * numArchons) { // want all of the foragers to be sent first
                int target = findLocalLocation();
                if(target != -1 && rng.nextInt(2) == 1) {
                    rc.writeSharedArray(Util.getArchonMemoryBlock(rank), target);
                    dir = pf.findBestDirection(Util.getLocationFromInt(target), 60);
                }
                else {
                    int minerReport = findMinerReport();
                    rc.writeSharedArray(Util.getArchonMemoryBlock(rank), minerReport);

                    dir = pf.findBestDirection(Util.getLocationFromInt(minerReport), 60);
                }
                rc.buildRobot(RobotType.MINER, dir);
                rc.writeSharedArray(20, targetArchon + 1);
            }
        }
        else if (rc.getTeamLeadAmount(rc.getTeam()) >= 400 && rc.canBuildRobot(RobotType.BUILDER, dir)) {
            rc.buildRobot(RobotType.BUILDER, dir);
            rc.writeSharedArray(20, targetArchon + 1);
        } 
        else if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            // System.out.println("SOLDIER on round " + round);
            rc.buildRobot(RobotType.SOLDIER, dir);
            rc.writeSharedArray(20, targetArchon + 1);
        }
    }

    //returns the change in lead count since the last turn and updates oppLeadCount
    public int computeDifferenceOppLeadCount(){
        int currentOppLeadCount = rc.getTeamLeadAmount(rc.getTeam().opponent());
        changeOppLeadCount = currentOppLeadCount - oppLeadCount;
        oppLeadCount = currentOppLeadCount;
        return changeOppLeadCount;
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
            if (robot.getTeam() != rc.getTeam()) {
                int rankClosest = rankOfNearestArchon(robot.location);
                rc.writeSharedArray(17, Util.getIntFromLocation( robot.location) + 10000 * rankClosest);
                rc.writeSharedArray(18, round);
                return true;
            }
        }
        return false;
    }

    // Guesses mirror to Archon at loc by min distance metric (note we could predict different symmetries for different archons)
    public MapLocation computeMirrorGuess(MapLocation loc) throws GameActionException {
        MapLocation[] guesses = {Util.horizontalRefl(loc), Util.verticalRefl(loc), Util.centralRefl(loc)};
            
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
        System.out.println("CHECKING RELOCATION AT ROUND " + rc.getRoundNum());
        int lx = me.x;
        int ly = me.y;
        int bestd = 0;
        int bestr = rc.senseRubble(me);
        System.out.println(lx + " " + ly + " RUBBLE COUNT: " + bestr);
        MapLocation newhome = me;
        for(int dx = 1; dx >= -1; dx--){
            for(int dy = 1; dy >= -1; dy--){
                int d = Util.max(Util.abs(dx), Util.abs(dy));
                if(d == 0){
                    continue;
                }
                MapLocation mc = new MapLocation(dx + lx, dy + ly);
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
            if (rc.senseLead(leadLocations[i]) > 10) {
                return Util.getIntFromLocation(leadLocations[i]);
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