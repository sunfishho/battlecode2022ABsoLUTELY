
package first_bot;

import battlecode.common.*;
import java.util.ArrayList;


public class Archon extends RobotCommon{

    // static RobotController rc;
    static MapLocation home;
    static int[] knownMap = new int[62 * 60];
    static int numArchons, numScoutsSent, numForagersSent, numSoldiersSent, teamLeadAmount, targetArchon;
    static ArrayList<Integer> vortexRndNums;
    int numArchonsAtStart;
    static int vortexCnt = 0;
    static int oppLeadCount = 200;
    static int changeOppLeadCount = 0;
    static MapLocation archonLocs;
    static int numMinersAlive, numSoldiersAlive;
    static RobotInfo[] nearbyTeammatesWithinHealingRange;
    static int numBuilders;

    /*
        Values of important locations are stored on the map, negative values correspond to opponent:
            1-4: archons of corresponding rank
            5: miner sent to location
    */

    public Archon(RobotController rc, int r, MapLocation loc) throws GameActionException{
        super(rc, r, loc);
        numArchonsAtStart = rc.getArchonCount();
        numForagersSent = 100;
        numScoutsSent = 0;
        //initialize the Symmetry bit
        rc.writeSharedArray(Util.getSymmetryMemoryBlock(), 3);
        rc.writeSharedArray(17, 65535);
        rc.writeSharedArray(18, 65535);
        rc.writeSharedArray(19, 100);
        rc.writeSharedArray(20, 0);
        rc.writeSharedArray(23, 0);
        
        // for Util to know the width/height of the map
        Util.WIDTH = rc.getMapWidth();
        Util.HEIGHT = rc.getMapHeight();
        numBuilders = 0;
    }

    public void takeTurn() throws GameActionException {
        nearbyTeammatesWithinHealingRange = rc.senseNearbyRobots(20, myTeam);
        // update variables
        round = rc.getRoundNum();
        numArchons = rc.getArchonCount();
        teamLeadAmount = rc.getTeamLeadAmount(rc.getTeam());
        targetArchon = rc.readSharedArray(20);

        // update ranks of archons if changed

        int rankInfo = rc.readSharedArray(23);
        int newRankInfo = rankInfo + 2000;
        if (rankInfo % 2000 != round) {
            newRankInfo = round + 2000;
        }
        rc.writeSharedArray(23, newRankInfo);
        rank = newRankInfo / 2000;
        int loc = Util.getIntFromLocation(me);
        rc.writeSharedArray(rank-1, loc);
        numMinersAlive = rc.readSharedArray(28);
        numSoldiersAlive = rc.readSharedArray(29);
        // System.out.println(round + " " + numMinersAlive + " " + numSoldiersAlive);
        if (rank == numArchons){
            rc.writeSharedArray(28, 0);
            rc.writeSharedArray(29, 0);
            rc.writeSharedArray(30, 0);
        }
        // establishRank and relocCheck on turn 1, writeArchonLocations on turn 2

        if (changeOppLeadCount > 51 && round > 10){
            rc.writeSharedArray(17, 65534);
        }

        if(round == 1) {
            relocCheck();
            if(rc.readSharedArray(16) == 0){//array initialized to 0, but we should initialize to 7
                rc.writeSharedArray(16, 7);
            }
            observeSymmetry();
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
            for (int archonIndex = 0; archonIndex < numArchonsAtStart; archonIndex++){
                archonLocationsInitial[archonIndex] = Util.getLocationFromInt(rc.readSharedArray(archonIndex));
            }
        }


        if (rc.readSharedArray(22) != 0 && round >= 2){
            /*
            MapLocation archonSpotted = Util.getLocationFromInt(rc.readSharedArray(22));
            for (int archonIndex = 0; archonIndex < numArchonsAtStart; archonIndex++){
                if (archonSpotted.equals(Util.horizontalRefl(archonLocationsInitial[archonIndex]))){
                    //then symmetry = 1
                    rc.writeSharedArray(16, 1);
                }
                else if (archonSpotted.equals(Util.verticalRefl(archonLocationsInitial[archonIndex]))){
                    rc.writeSharedArray(16, 2);
                }
                else if (archonSpotted.equals(Util.centralRefl(archonLocationsInitial[archonIndex]))){
                    rc.writeSharedArray(16, 4);
                }
                //if none of these are true for any archonIndex we apparently have an archon walker on our hands
            }
             */
        }
        // System.out.println("Symmetry: " + rc.readSharedArray(16));
        int alarm = rc.readSharedArray(18);
        int prevIncome = rc.readSharedArray(30);

        if(vortexCnt < vortexRndNums.size() && round == vortexRndNums.get(vortexCnt) + 1 && alarm == 65535){//vortex --> we might have been moved onto lots of rubble
            relocCheck();
            vortexCnt++;
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
        // rc.setIndicatorString(rank + " " + newRankInfo);

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


        // System.out.println("ALARM: " + alarm);
        // System.out.println("LOCATION: " + rc.readSharedArray(17));
        boolean enemiesNear = observe();
        if (alarm == 65535 || alarm == 65534) {
            if (teamLeadAmount <= numArchons * 50 && (targetArchon % numArchons) != (rank % numArchons)) {
                heal();
                return;
            }
        }
        else { // figure out where the alarm is coming from and send troops
            if (teamLeadAmount < 50 || (targetArchon % numArchons) != (rank % numArchons)) {
                if (heal()) {
                    return;
                }
            }
        }
        if (rc.readSharedArray(31) == 1) {
            heal();
            rc.setIndicatorString("Halting production for labs");
            return;
        }
        if (rc.canBuildRobot(RobotType.SAGE, dir)) {
            rc.buildRobot(RobotType.SAGE, dir);
            rc.writeSharedArray(20, targetArchon + 1);
        }
        if (round > 100 && numBuilders == 0 && rank == 1) {
            if (rc.canBuildRobot(RobotType.BUILDER, dir)) {
                numBuilders = 1;
                rc.buildRobot(RobotType.BUILDER, dir);
                rc.writeSharedArray(20, targetArchon + 1);
            } 
        }
        if (rc.canBuildRobot(RobotType.MINER, dir) 
            && (((numMinersAlive < Math.max(6, Util.WIDTH * Util.HEIGHT / 120)) && (alarm == 65535 || round % 13 == 0))) && prevIncome < 10) {

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
            
            int targetLoc = findLocalLocation();
            if(targetLoc != -1 && rng.nextInt(2) == 1) {
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank), targetLoc);
                dir = pf.findBestDirection(Util.getLocationFromInt(targetLoc), 60);
            }
            else {
                int minerReport = findMinerReport();
                    rc.writeSharedArray(Util.getArchonMemoryBlock(rank), minerReport);
                dir = pf.findBestDirection(Util.getLocationFromInt(minerReport), 60);
            }
            rc.buildRobot(RobotType.MINER, dir);
            rc.writeSharedArray(20, targetArchon + 1);
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
        heal();
    }

    public boolean heal() throws GameActionException {
        // System.out.println(round + ", " + rank + ", " + nearbyTeammatesWithinHealingRange.length);
        if (nearbyTeammatesWithinHealingRange.length != 0){
            RobotInfo mostNeedy = null;

            for (RobotInfo robot : nearbyTeammatesWithinHealingRange){
                if (robot.health + 2 > robot.getType().health || !rc.canRepair(robot.getLocation())){
                    continue;
                }
                if (mostNeedy == null){
                    mostNeedy = robot;
                    continue;
                }
                switch (robot.getType()){
                    case ARCHON:
                        continue;
                    case SOLDIER:
                        // check if the fraction of health is lower for this robot
                        if (mostNeedy.getType() != RobotType.SOLDIER || robot.health * mostNeedy.getType().health >= mostNeedy.health * robot.getType().health){
                            mostNeedy = robot;
                        }
                        break;
                    default:
                        if (mostNeedy.getType() != RobotType.SOLDIER && robot.health * mostNeedy.getType().health >= mostNeedy.health * robot.getType().health){
                            mostNeedy = robot;
                        }
                        break;
                }
            }
            // System.out.println("HEALABLE: " + healable);
            if (mostNeedy != null && rc.canRepair(mostNeedy.getLocation())){
                MapLocation targetBot = mostNeedy.getLocation();
                rc.repair(targetBot);
                rc.setIndicatorString(targetBot.x + ", " + targetBot.y);
                return true;
            }
        }
        return false;
    }

    //returns the change in lead count since the last turn and updates oppLeadCount
    public int computeDifferenceOppLeadCount(){
        int currentOppLeadCount = rc.getTeamLeadAmount(rc.getTeam().opponent());
        changeOppLeadCount = currentOppLeadCount - oppLeadCount;
        oppLeadCount = currentOppLeadCount;
        return changeOppLeadCount;
    }

    // Writes Archon locations to knownMap
    public void writeArchonLocations() throws GameActionException {
        int loc = Util.getIntFromLocation(me);
        for(int i = 0; i < 4; i++) {
            if(rc.readSharedArray(i) != 0) {
                knownMap[rc.readSharedArray(i)] = i + 1;
                archonLocationsInitial[i] = Util.getLocationFromInt(rc.readSharedArray(i));
            }
        }
    }

    // Check if any aggressive enemy archons nearby
    public boolean observe() throws GameActionException {
        for (RobotInfo robot : rc.senseNearbyRobots(34, enemyTeam)) {
            switch (robot.getType()){
                    case MINER: continue;
                    case ARCHON: 
                        rc.writeSharedArray(22, Util.getIntFromLocation(robot.location));
                        rc.writeSharedArray(17, Util.getIntFromLocation(robot.location) + 10000 * rankOfNearestArchon(robot.getLocation()));
                        rc.writeSharedArray(18, round);
                        return true;
                    default:
                        rc.writeSharedArray(17, Util.getIntFromLocation(robot.location) + 10000 * rankOfNearestArchon(robot.getLocation()));
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
