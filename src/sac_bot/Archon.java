
package sac_bot;

import battlecode.common.*;
import java.util.*;


public class Archon extends RobotCommon{

    // static RobotController rc;
    static MapLocation home;
    static int numArchons, numScoutsSent, numForagersSent, numSoldiersSent, teamLeadAmount, targetArchon, numSacrifices, numFarmers, numDefenders;
    static boolean localMiner;
    static ArrayList<Integer> vortexRndNums;
    int numArchonsAtStart;
    static int vortexCnt = 0;
    static int oppLeadCount = 200;
    static int changeOppLeadCount = 0;
    static MapLocation archonLocs;
    static int numMinersAlive, numSoldiersAlive;
    static RobotInfo[] nearbyTeammatesWithinHealingRange;
    static int numBuilders;
    static int incomeSum;
    static Queue<Integer> incomeQueue;
    int firstRoundMiner;
    MapLocation archonTarget;

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
        rc.writeSharedArray(49, 65535);
        rc.writeSharedArray(50, 65535);
        rc.writeSharedArray(51, 100);
        rc.writeSharedArray(52, 0);
        rc.writeSharedArray(55, 0);
        
        // for Util to know the width/height of the map
        Util.WIDTH = rc.getMapWidth();
        Util.HEIGHT = rc.getMapHeight();
        numBuilders = 0;
        incomeSum = 0;
        incomeQueue = new LinkedList<Integer>();
    }

    public void takeTurn() throws GameActionException {
        initializeEachTurn();

        if(round == 1) {
            doRoundOneDuties();
        }
        if(round == 2){
            for (int archonIndex = 0; archonIndex < numArchonsAtStart; archonIndex++){
                archonLocationsInitial[archonIndex] = Util.getLocationFromInt(rc.readSharedArray(archonIndex));
                rc.writeSharedArray(56 + archonIndex, Util.getIntFromLocation(archonLocationsInitial[archonIndex]));
            }
        }
        int alarmRound = rc.readSharedArray(50);
        int alarmLocation = rc.readSharedArray(49);
        int prevIncome = rc.readSharedArray(62);

        incomeQueue.add(prevIncome);
        incomeSum += prevIncome;
        if (incomeQueue.size() > 50) {
            incomeSum -= incomeQueue.poll();
        }
        
        if (rank == rc.getArchonCount()) {
            rc.writeSharedArray(62, 0);
        }

        if (alarmRound < round - 3) {
            rc.writeSharedArray(50, 65535);
            rc.writeSharedArray(49, 65535);
        }

        //archon relocation code
        checkIfArchonShouldRelocate();

        moveIfArchonHasTarget();
        // rc.setIndicatorString(rank + " " + newRankInfo);

        
        Direction dir = findDirectionToBuildIn();
        
        if (alarmRound == 65535 || alarmRound == 65534) {
            if (teamLeadAmount <= numArchons * 50 && (targetArchon % numArchons) != (rank % numArchons)) {
                heal();
                return;
            }
            if (rc.readSharedArray(63) % 102 != 0) {
                heal();
                rc.setIndicatorString("Halting production for labs");
                return;
            }
        }
        else { // figure out where the alarm is coming from and send troops
            if (teamLeadAmount < 120 && ((alarmLocation / 10000) % numArchons) != (rank % numArchons)) {
                heal();
                return;
            }
        }
        tryToBuildStuff(dir, alarmRound, prevIncome);
        heal();
    }

    public void tryToBuildStuff(Direction dir, int alarm, int prevIncome) throws GameActionException { 
        int writeLocation = Util.getArchonMemoryBlock(rank);
        boolean built = false;
        
        // Build sages always if you can
        if (rc.canBuildRobot(RobotType.SAGE, dir)) {
            rc.buildRobot(RobotType.SAGE, dir);
            built = true;
        }
        // Build miners up to limit before round 100
        if(!built && round < 100 && rc.canBuildRobot(RobotType.MINER, dir) 
            && numMinersAlive < Math.max(6, Util.WIDTH * Util.HEIGHT / 120) && (alarm == 65535 || round % 13 == 0)) {
            int targetLoc = findLocalLocation();
            if(targetLoc != -1 && !localMiner) { // we have local lead locations
                rc.writeSharedArray(writeLocation, targetLoc);
                dir = pf.findBestDirection(Util.getLocationFromInt(targetLoc), 60);
                localMiner = true;
            }
            else {
                rc.writeSharedArray(writeLocation, 0);
            }
            rc.buildRobot(RobotType.MINER, dir);
            built = true;
            if (rank > 1){
                rc.disintegrate();
            }
        }
        // Build one laboratory builder with sufficient income
        if (!built && numBuilders == 0 && incomeSum > 150 && rc.canBuildRobot(RobotType.BUILDER, dir)) {
            numBuilders++;
            rc.writeSharedArray(63, rc.readSharedArray(63) + 100);
            rc.buildRobot(RobotType.BUILDER, dir);
            built = true;
        }
        // Build soldiers when there is a specific alarm
        if(!built && alarm != 65535 && alarm != 65534 && rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            built = true;
        }
        // Build one farmer at least
        if(!built && numFarmers < 1 && rc.canBuildRobot(RobotType.MINER, dir)) {
            rc.writeSharedArray(writeLocation, Util.MAX_LOC); // subtype 1
            rc.buildRobot(RobotType.MINER, dir);
            built = true;
        }
        if(alarm == 65535 && (firstRoundMiner >= 60 || firstRoundMiner == 0)) {
            // Build builders when there is an abundance of lead
            if (!built && numBuilders >= 1 && rc.getTeamLeadAmount(rc.getTeam()) >= 300 * numBuilders && rc.canBuildRobot(RobotType.BUILDER, dir)) {
                rc.buildRobot(RobotType.BUILDER, dir);
                built = true;
                numBuilders++;
            }
            // Build miner-farmers if you've sacrificed at least ten builders or your income is simply low
            if(!built && (numFarmers == 0 || numSacrifices / numFarmers > 20 || prevIncome < 15) && rc.canBuildRobot(RobotType.MINER, dir)) {
                rc.writeSharedArray(writeLocation, Util.MAX_LOC); // subtype 1
                rc.buildRobot(RobotType.MINER, dir);
                built = true;
            }
            // Build one defender if you've sacrificed at least five builders
            if(!built && numSacrifices > 5 && numDefenders == 0 && rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
                numDefenders++;
                built = true;
            }
            // Build builder-sacrifices 
            if(!built && rc.canBuildRobot(RobotType.BUILDER, dir) && numFarmers > 0) {
                int minerReport = rc.readSharedArray(writeLocation + 1);
                rc.writeSharedArray(writeLocation, Util.MAX_LOC + minerReport); // subtype 1
                rc.writeSharedArray(writeLocation + 1, 0);
                rc.buildRobot(RobotType.BUILDER, dir);
                built = true;
            }
        }
        // Default
        if(!built && numSacrifices > 5 && numDefenders == 0 && rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            numDefenders++;
            built = true;
        }
        // Update the target archon value
        if(built) {
            rc.writeSharedArray(52, targetArchon + 1);
        }
    }

    public void moveIfArchonHasTarget() throws GameActionException{
        if (archonTarget != null && !archonTarget.equals(me)){
            if (rc.getMode() == RobotMode.TURRET && rc.canTransform()){
                rc.transform();
            }
            else if (rc.getMode() == RobotMode.TURRET){
                return;
            }
            tryToMove(70);
        }
        else if (archonTarget != null && archonTarget.equals(me)){
            if (rc.getMode() == RobotMode.PORTABLE){
                if (rc.canTransform()){
                    rc.transform();
                }
            }
        }
    }

    public Direction findDirectionToBuildIn() throws GameActionException{
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
        return dir;
    }

    public void checkIfArchonShouldRelocate() throws GameActionException{
        if (round % 10 == 0 && round >= 20 && rc.senseRubble(me) > 20 && (archonTarget == null || (!me.equals(archonTarget)))){
            MapLocation bestLocation = me;
            int rubbleHere = rc.senseRubble(me);
            int bestRubble = rubbleHere;
            int lowestDistance = 10000;
            for (MapLocation mL : rc.getAllLocationsWithinRadiusSquared(me, 34)){
                if (rc.senseRubble(mL)/10 < bestRubble/10 && rubbleHere / 10 - rc.senseRubble(mL) / 10 >= 2){
                    bestLocation = mL;
                    bestRubble = rc.senseRubble(mL);
                    lowestDistance = Util.distanceMetric(me, mL);
                }
                else if (rc.senseRubble(mL) / 10 == bestRubble/10 && rubbleHere / 10 - rc.senseRubble(mL) / 10 >= 2 && lowestDistance > Util.distanceMetric(me, mL))    {
                    bestLocation = mL;
                    bestRubble = rc.senseRubble(mL);
                    lowestDistance = Util.distanceMetric(me, mL);
                }
            }
            archonTarget = bestLocation;
            if (rc.canTransform() && rc.getMode() == RobotMode.TURRET){
                rc.transform();
            }
        }
    }

    public void initializeEachTurn() throws GameActionException{
        rc.setIndicatorString("" + rank);
        nearbyTeammatesWithinHealingRange = rc.senseNearbyRobots(20, myTeam);
        // update variables
        round = rc.getRoundNum();
        numArchons = rc.getArchonCount();
        teamLeadAmount = rc.getTeamLeadAmount(rc.getTeam());
        targetArchon = rc.readSharedArray(52);
        numSacrifices = rc.readSharedArray(48);
        numFarmers = rc.readSharedArray(47);
        firstRoundMiner = rc.readSharedArray(46);

        // update ranks of archons if changed



        int rankInfo = rc.readSharedArray(55);
        int newRankInfo = rankInfo + 2000;
        if (rankInfo % 2000 != round) {
            newRankInfo = round + 2000;
        }
        rc.writeSharedArray(55, newRankInfo);
        rank = newRankInfo / 2000;
        int loc = Util.getIntFromLocation(me);
        if (loc != (rc.readSharedArray(rank-1))) rc.writeSharedArray(rank-1, loc);
        numMinersAlive = rc.readSharedArray(60);
        numSoldiersAlive = rc.readSharedArray(61);

        if (rank == numArchons){
            rc.writeSharedArray(47, 0);
            rc.writeSharedArray(60, 0);
            rc.writeSharedArray(61, 0);
            rc.writeSharedArray(62, 0);
        }

        if (changeOppLeadCount > 51 && round > 10){
            rc.writeSharedArray(49, 65534);
        }
    }

    public void doRoundOneDuties() throws GameActionException{
        relocCheck();
        if(rc.readSharedArray(Util.getSymmetryMemoryBlock()) == 0){//array initialized to 0, but we should initialize to 7
            rc.writeSharedArray(Util.getSymmetryMemoryBlock(), 7);
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
                    case SAGE:
                        // check if the fraction of health is lower for this robot
                        if (mostNeedy.getType() != RobotType.SAGE || robot.health * mostNeedy.getType().health >= mostNeedy.health * robot.getType().health){
                            mostNeedy = robot;
                        }
                        break;
                    case SOLDIER:
                        if (mostNeedy.getType() == RobotType.SAGE) {
                            break;
                        }
                        if (mostNeedy.getType() != RobotType.SOLDIER || robot.health * mostNeedy.getType().health >= mostNeedy.health * robot.getType().health){
                            mostNeedy = robot;
                        }
                        break;
                    default:
                        if (mostNeedy.getType() != RobotType.SAGE && mostNeedy.getType() != RobotType.SOLDIER && robot.health * mostNeedy.getType().health >= mostNeedy.health * robot.getType().health){
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

    // Check if any aggressive enemy archons nearby
    public boolean observe() throws GameActionException {
        for (RobotInfo robot : rc.senseNearbyRobots(34, enemyTeam)) {
            switch (robot.getType()){
                    case MINER: continue;
                    case ARCHON: 
                        rc.writeSharedArray(54, Util.getIntFromLocation(robot.location));
                        rc.writeSharedArray(49, Util.getIntFromLocation(robot.location) + 10000 * rankOfNearestArchon(robot.getLocation()));
                        rc.writeSharedArray(50, round);
                        return true;
                    default:
                        rc.writeSharedArray(49, Util.getIntFromLocation(robot.location) + 10000 * rankOfNearestArchon(robot.getLocation()));
                        rc.writeSharedArray(50, round);
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
        if (goldLocations.length > 0) {
            int loc = Util.moveOnLattice(Util.getIntFromLocation(goldLocations[0]));
            return loc;
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

    public void tryToMove(int avgRubble) throws GameActionException {
        MapLocation target = archonTarget;
        Direction dir = Direction.CENTER;
        dir = pf.findBestDirection(target, avgRubble);
        
        if (rc.canMove(dir)) {
            rc.setIndicatorLine(me, me.translate(dir.dx, dir.dy), 0, 100, 0);
            rc.move(dir);
            
            me = rc.getLocation();
        }
    }
}
