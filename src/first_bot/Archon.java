
package first_bot;

import battlecode.common.*;
import java.util.*;


public class Archon extends RobotCommon{

    // static RobotController rc;
    static MapLocation home;
    static int teamLeadAmount, teamGoldAmount, targetArchon, nextWriteValue, labValue, nextTypeValue, writeLocation;
    static boolean builtMinerLast, localMiner;
    static ArrayList<Integer> vortexRndNums;
    static ArrayList<Integer> chargeRndNums;
    int numArchonsAtStart;
    static int vortexCnt = 0;
    static int oppLeadCount = 200;
    static int changeOppLeadCount = 0;
    static MapLocation archonLocs;
    static int numArchons, numMinersAlive, numSoldiersAlive, numFarmersAlive, numSacrifices, numDefenders;
    static RobotInfo[] nearbyTeammatesWithinHealingRange;
    static int numBuilders;
    static int incomeSum;
    static Queue<Integer> incomeQueue;
    static Queue<Integer> aggregateHealthQueue;
    static boolean shouldFarm = false;
    static int lastAggregateHealth = 0;
    static int winningCounter = 0;
    MapLocation archonTarget;
    static int farthestArchonFromCenterIdx;

    /*
        Values of important locations are stored on the map, negative values correspond to opponent:
            1-4: archons of corresponding rank
            5: miner sent to location
    */

    public Archon(RobotController rc, int r, MapLocation loc) throws GameActionException{
        super(rc, r, loc);
        numArchonsAtStart = rc.getArchonCount();
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
        aggregateHealthQueue = new LinkedList<Integer>();
    }

    public void takeTurn() throws GameActionException {
        initializeEachTurn();

        if (rank == numArchons){
            rc.writeSharedArray(40, 0);
        }
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
        if (incomeQueue.size() > 20) {
            incomeSum -= incomeQueue.poll();
        }
        
        if (rank == rc.getArchonCount()) {
            rc.writeSharedArray(62, 0);
        }

        checkIfArchonShouldRelocate();

        moveIfArchonHasTarget();
        
        Direction dir = findDirectionToBuildIn();
        boolean alarmRecent = (alarmRound > round - 3 && alarmRound != 65535) || (rc.readSharedArray(38) > 0);
        
        if (!alarmRecent && round != 1) {
            if (teamLeadAmount < 250 && labValue >= 10000 && (labValue % 10000) % 101 != 0) {
                // we want to build labs and we've sent out a builder
                heal();
                rc.setIndicatorString(rank + " " + labValue + " halting production for labs");
                return;
            }
            if (teamLeadAmount < (numArchons - rank + 1) * 50 && (targetArchon % numArchons) != (rank % numArchons)) {
                // If we don't have enough lead for 50 * remaining archons, don't spawn if you're not target
                rc.setIndicatorString(rank + " " + alarmRound + " " + round + " healing");
                heal();
                return;
            }
        }
        else {
            if (teamGoldAmount < 40 && teamLeadAmount < 120 && alarmLocation != 65535 && (alarmLocation / 10000) != rank) {
                // Only the closest archon should be spawning with limited lead if there is a valid alarm
                rc.setIndicatorString(rank + " " + alarmLocation + " " + round + " healing");
                heal();
                return;
            }
        }
        tryToBuildStuff(dir, alarmRecent, prevIncome);
        rc.setIndicatorString(rank + " " + nextWriteValue  + " " + nextTypeValue + " " + " attempting to build");
        heal();
    }

    public void initializeEachTurn() throws GameActionException{
        rc.setIndicatorString("" + rank);
        nearbyTeammatesWithinHealingRange = rc.senseNearbyRobots(20, myTeam);
        // update variables
        round = rc.getRoundNum();
        numArchons = rc.getArchonCount();
        teamLeadAmount = rc.getTeamLeadAmount(rc.getTeam());
        teamGoldAmount = rc.getTeamGoldAmount(rc.getTeam());
        targetArchon = rc.readSharedArray(52);
        numSacrifices = rc.readSharedArray(48);
        numFarmersAlive = rc.readSharedArray(41);
        numMinersAlive = rc.readSharedArray(60);
        numSoldiersAlive = rc.readSharedArray(61);

        // update ranks of archons if change

        int rankInfo = rc.readSharedArray(55);
        int newRankInfo = rankInfo + 2000;
        if (rankInfo % 2000 != round) {
            newRankInfo = round + 2000;
        }
        rc.writeSharedArray(55, newRankInfo);
        rank = newRankInfo / 2000;
        int loc = Util.getIntFromLocation(me);
        if (loc != (rc.readSharedArray(rank-1))) rc.writeSharedArray(rank-1, loc);

        if (rank == numArchons){
            rc.writeSharedArray(41, 0);
            rc.writeSharedArray(60, 0);
            rc.writeSharedArray(61, 0);
            rc.writeSharedArray(62, 0);
        }

        // write the next write value
        writeLocation = Util.getArchonMemoryBlock(rank);
        rc.writeSharedArray(writeLocation, nextWriteValue);
        rc.writeSharedArray(writeLocation + 2, nextTypeValue);
        nextWriteValue = 0;
        nextTypeValue = 0;
        labValue = rc.readSharedArray(63);
        labValue = computeLabValue();
        rc.writeSharedArray(63, labValue);

        if (changeOppLeadCount > 51 && round > 10){
            rc.writeSharedArray(49, 65534);
        }

        farthestArchonFromCenterIdx = -1;
        int bestDistance = -1;
        for (int idx = 0; idx < numArchons; idx++){
            if (bestDistance < Util.distanceMetric(new MapLocation(Util.WIDTH / 2, Util.HEIGHT / 2), Util.getLocationFromInt(rc.readSharedArray(idx)))){
                bestDistance = Util.distanceMetric(new MapLocation(Util.WIDTH / 2, Util.HEIGHT / 2), Util.getLocationFromInt(rc.readSharedArray(idx)));
                farthestArchonFromCenterIdx = idx + 1;
            }
        }

        if (aggregateHealthQueue.size() == 10){
            if (rc.readSharedArray(40) >= aggregateHealthQueue.peek() && aggregateHealthQueue.peek() > 200 && rank == farthestArchonFromCenterIdx && round >= 60) {
                if ((rc.readSharedArray(42) == 0 || rc.readSharedArray(42) > 55) && (rc.readSharedArray(50) == 0 || rc.readSharedArray(50) > 55 )){
                    shouldFarm = true;
                    rc.writeSharedArray(39, 1);
                }
            }
            else{
                shouldFarm = false;
                rc.writeSharedArray(39, 0);
            }
            aggregateHealthQueue.remove();
        }
        aggregateHealthQueue.add(rc.readSharedArray(40));
    }

    public void doRoundOneDuties() throws GameActionException{
        relocCheck();
        if(rc.readSharedArray(Util.getSymmetryMemoryBlock()) == 0){//array initialized to 0, but we should initialize to 7
            rc.writeSharedArray(Util.getSymmetryMemoryBlock(), 7);
        }
        observeSymmetry();
    }

    public void tryToBuildStuff(Direction dir, boolean alarmRecent, int prevIncome) throws GameActionException { 
        boolean built = false;
        
        // Build sages always if you can
        if (rc.canBuildRobot(RobotType.SAGE, dir)) {
            rc.buildRobot(RobotType.SAGE, dir);
            nextTypeValue = 3;
            built = true;
        }
        // Build one laboratory builder when we want to
        if (!built && numBuilders == 0 && (labValue % 10000) % 101 != 0 && rc.canBuildRobot(RobotType.BUILDER, dir) && rank == farthestArchonFromCenterIdx) {
            rc.setIndicatorString(rank + " builder built");
            numBuilders++;
            rc.buildRobot(RobotType.BUILDER, dir);
            nextTypeValue = 1;
            labValue += 10000;
            rc.writeSharedArray(63, labValue);
            built = true;
        }
        // Build miners up to limit before round 100
        if(!built && (round < 100 || round % 13 == 0) && rc.canBuildRobot(RobotType.MINER, dir) 
            && numMinersAlive < Math.max(6, Util.WIDTH * Util.HEIGHT / 120) && (!alarmRecent || round % 13 == 0)) {
            int targetLoc = findLocalLocation();
            if(targetLoc != -1 && numFarmersAlive < 1 && !localMiner) { // we have local lead locations, make a farmer
                rc.setIndicatorString(rank + " making targeted farmer");
                nextWriteValue = targetLoc;
                dir = pf.findBestDirection(Util.getLocationFromInt(targetLoc), 60);
                localMiner = true;
            }
            else { // make a forager
                rc.setIndicatorString(rank + " making forager");
                nextWriteValue = Util.MAX_LOC; //subtype 1
            }
            rc.buildRobot(RobotType.MINER, dir);
            nextTypeValue = 0;
            built = true;
        }
        if(!built && localMiner && numDefenders == 0 && rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            nextTypeValue = 2;
            numDefenders++;
            rc.writeSharedArray(45, Util.getIntFromLocation(me.add(dir)));
            built = true;
        }
        // Build soldiers when there is a specific alarm and no laboratories
        if(!built && alarmRecent && rc.canBuildRobot(RobotType.SOLDIER, dir) && (labValue % 100) < 2) {
            rc.buildRobot(RobotType.SOLDIER, dir);
            nextTypeValue = 2;
            built = true;
        }
        if((!alarmRecent || shouldFarm) && rank == farthestArchonFromCenterIdx && numDefenders > 0) { //incorporate foundMiner at some point
            // Build builders when there is an abundance of lead
            if (!built && numBuilders >= 1 && rc.getTeamLeadAmount(rc.getTeam()) >= 300 * numBuilders && rc.canBuildRobot(RobotType.BUILDER, dir)) {
                rc.buildRobot(RobotType.BUILDER, dir);
                nextTypeValue = 1;
                built = true;
                numBuilders++;
            }
            // Build miner-farmers if you've sacrificed at least ten builders
            if(!built && (numFarmersAlive == 0 || numSacrifices / numFarmersAlive > 20) && rc.canBuildRobot(RobotType.MINER, dir)) {
                rc.buildRobot(RobotType.MINER, dir);
                nextWriteValue = 0;
                nextTypeValue = 0;
                built = true;
            }
            // Build one defender if you've sacrificed at least five builders
            if(!built && numSacrifices >= 1 && numDefenders == 0 && rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
                nextTypeValue = 2;
                numDefenders++;
                rc.writeSharedArray(45, Util.getIntFromLocation(me.add(dir)));
                built = true;
            }
            // Build builder-sacrifices as default case
            if(!built && rc.canBuildRobot(RobotType.BUILDER, dir) && numFarmersAlive > 0) {
                int minerReport = rc.readSharedArray(Util.getArchonMemoryBlock(rank) + 1);
                nextWriteValue = Util.MAX_LOC + minerReport; // subtype 1
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 1, 0);
                rc.buildRobot(RobotType.BUILDER, dir);
                nextTypeValue = 1;
                numSacrifices++;
                built = true;
            }
        }
        if(!built && labValue % 100 > 0 && !alarmRecent) {
            shouldFarm = true;
            if((numFarmersAlive == 0 || numSacrifices / numFarmersAlive > 20) && rc.canBuildRobot(RobotType.MINER, dir)) {
                rc.buildRobot(RobotType.MINER, dir);
                nextWriteValue = 0;
                nextTypeValue = 0;
                built = true;
            }
            if(rc.canBuildRobot(RobotType.BUILDER, dir) && numFarmersAlive > 0) {
                int minerReport = rc.readSharedArray(Util.getArchonMemoryBlock(rank) + 1);
                nextWriteValue = Util.MAX_LOC + minerReport; // subtype 1
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 1, 0);
                rc.buildRobot(RobotType.BUILDER, dir);
                nextTypeValue = 1;
                numSacrifices++;
                built = true;
            }
        }

        // Update the target archon value
        if(built) {
            rc.writeSharedArray(52, targetArchon + 1);
        }
    }

    public void moveIfArchonHasTarget() throws GameActionException{
        int clear = rc.readSharedArray(46);
        if (archonTarget != null && !archonTarget.equals(me)){
            if (rc.getMode() == RobotMode.TURRET && rc.canTransform() && clear != 1){
                rc.transform();
                rc.writeSharedArray(46, 1);
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
                    rc.writeSharedArray(46, 0);//done moving
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
        int clear = rc.readSharedArray(46);
        if(clear == 1){//another archon is on the move, so wait
            return;
        }
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
                rc.writeSharedArray(46, 1);
            }
        }
    }

    public boolean heal() throws GameActionException {
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
                    rc.writeSharedArray(38, 1);
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
        int lx = me.x;
        int ly = me.y;
        int bestd = 0;
        int bestr = rc.senseRubble(me);
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

    //determines how many labs the Archon thinks we should build at this stage
    public int computeLabValue() throws GameActionException {
        int curLabValue = labValue % 10000;
        int curExpectation = curLabValue / 100;
        int curNumLabs = curLabValue % 100;
        if (curExpectation > curNumLabs){
            // we haven't made enough labs to meet expectation
            return labValue;
        }
        if(incomeQueue.size() == 0) return labValue;
        switch (curNumLabs) {
            case 0:
                if(round > 20 && incomeSum / incomeQueue.size() >= 5 && teamLeadAmount > 50) return labValue + 100;
                return labValue;
            case 1:
                if(incomeSum / incomeQueue.size() >= 8) return labValue + 100;
                return labValue;
            default:
                if(incomeSum / incomeQueue.size() >= 8 + 4 * curNumLabs) return labValue + 100;
                return labValue;
        }
    }
}
