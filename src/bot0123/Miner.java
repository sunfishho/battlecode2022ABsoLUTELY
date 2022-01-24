package bot0123;

import java.util.Map;
import java.util.Random;
import battlecode.common.*;


public class Miner extends Unit{
    public static boolean reachedTarget;
    public static RobotInfo[] robotLocations;
    static RobotInfo[] nearbyBotsSeen;
    static int numEnemies;
    static MapLocation enemySoldierCentroid = new MapLocation(0, 0);
    static int maxBytecodeUsed = 0;
    static int income;
    static boolean needsHeal;
    static int loopingIncrement = 1;//experiment w/ this maybe idk
    static int loopingPenalty;
    static boolean isDefended; // If the miner is close to a soldier our team or does not need defense
    static int friendlyMinerCount = 1;
    static int enemyMinerCount = 0;
    static MapLocation prevTarget = null;

    public Miner(RobotController rc, int r, MapLocation loc) throws GameActionException {
        super(rc, r, loc);
        isRetreating = false;
        needsHeal = false;
        target = chooseRandomInitialDestination();
    }

    public Miner(RobotController rc, int r, MapLocation loc, MapLocation t) throws GameActionException {
        super(rc, r, loc);
        isRetreating = false;
        needsHeal = false;
        target = t;
    }
    
    public void takeTurn() throws GameActionException {
        // Set general variables
        takeAttendance();
        me = rc.getLocation();
        round = rc.getRoundNum();
        archonLocation = nearestArchon(me);
        isDefended = true;
        income = 0;
        preventLooping();
        // We don't want miner to go back once the enemy leaves because they're probably still somewhere nearby
        if (isRetreating) {
            tryToWriteTarget(true);
        }
        isRetreating = false;
        
        // If previously not on offense and low health set target to nearest archon
        if (rc.getHealth() < 10 && round < 200) {
            needsHeal = true;
            target = archonLocation;
            prevTarget = null;
            tryToMine();
            if (me.distanceSquaredTo(archonLocation) > 13) {
                tryToMove(30);
                moveLowerRubble(true);
            }
        }
        if (needsHeal) {
            if (rc.getHealth() > 35 || round >= 200) {
                needsHeal = false;
                tryToWriteTarget(true);
            } else {
                return;
            }
        }
        rc.setIndicatorString("MINER: " + archonLocation + " " + target + " " + loopingPenalty);
        robotLocations = rc.senseNearbyRobots(20);
        // Sometimes we don't want to step on the target
        if (rc.canSenseLocation(target) && rc.senseRubble(target) > 30){
            tryToWriteTarget(true);
            targetCountdown = 0;
        }
        // If we observe enemies, compute centroid and try to run away
        boolean observeResult = observe();
        if (observeResult){
            int enemyCentroidx = 0;
            int enemyCentroidy = 0;
            int numEnemies = 0;
            isDefended = false;
            MapLocation enemyLoc = me;
            for (RobotInfo bot : robotLocations){
                if (bot.getTeam() == myTeam){
                    if (bot.getType() == RobotType.MINER){
                        friendlyMinerCount++;
                    }
                    continue;
                }
                switch(bot.getType()){
                    case MINER:
                        enemyMinerCount++;
                    case SOLDIER:
                        if (bot.getTeam() == myTeam && bot.getLocation().distanceSquaredTo(me) <= 8){
                            isDefended = true;
                            break;
                        }
                        enemyLoc = bot.getLocation();
                        numEnemies++;
                        enemyCentroidx += enemyLoc.x;
                        enemyCentroidy += enemyLoc.y;
                        break;
                    case ARCHON:
                        if (bot.getTeam() == myTeam){
                            break;
                        }
                        enemyLoc = bot.getLocation();
                        numEnemies++;
                        enemyCentroidx += enemyLoc.x;
                        enemyCentroidy += enemyLoc.y;
                        break;
                    default:
                }
            }
            if (numEnemies != 0){
                enemyCentroidx = (int) ((enemyCentroidx / (numEnemies + 0.0)) + 0.5);
                enemyCentroidy = (int) ((enemyCentroidy / (numEnemies + 0.0)) + 0.5);
                MapLocation enemyCentroid = new MapLocation(enemyCentroidx, enemyCentroidy);
                // If too close to enemy centroid then just go back to archon
                if (enemyCentroid.distanceSquaredTo(me) > 2) {
                    Direction dir = retreat(enemyCentroid);
                    if (rc.canMove(dir)) {  
                        rc.move(dir);
                    }
                } else {
                    prevTarget = target;
                    target = archonLocation;
                    tryToMove(20);
                }
                //only do scorched earth if we're far enough from our Archon
                if (Util.distanceMetric(me, nearestArchon(me)) > Math.max(Util.WIDTH, Util.HEIGHT) / 5) tryToMine(0);
                else{
                    tryToMine(1);
                }
                rc.writeSharedArray(62, rc.readSharedArray(62) + income);
                return;
            }
        }
        observeSymmetry();
        tryToMine();
        /*
        // If there are mineable neighboring deposits, don't keep moving
        */
        // System.out.println(round + ": " + rc.getID() + ", " + Clock.getBytecodeNum());
        if (rc.canSenseLocation(target)) {
            RobotInfo[] robotAtTarget = rc.senseNearbyRobots(target, 2, rc.getTeam());
            for (RobotInfo robot : robotAtTarget) {
                if (robot.getType().equals(RobotType.MINER) && robot.getID() < rc.getID()) {
                    tryToWriteTarget(true);
                    break;
                }
            }
        }

        // Case when Archon could not assign a Location to the Miner
        if(target.equals(archonLocation) && isRetreating == false) {
            // System.out.println(rc.getID() + ": finding new location");
            tryToWriteTarget(true);
        }
        // If we've reached the target try to find a new target
        if(!reachedTarget && me.equals(target)) {
            if (Util.getIntFromLocation(target) == rc.readSharedArray(57)){
                RobotInfo[] nearbyRobots = rc.senseNearbyRobots(8);
                int teammateMinerCount = 0;
                for (RobotInfo nearbyBot : nearbyRobots){
                    if (nearbyBot.getType() == RobotType.MINER){
                        teammateMinerCount++;
                    }
                }
                if (teammateMinerCount > 3){
                    rc.writeSharedArray(57, 0);
                }
            }
            // somehow stay still if lots of lead
            reachedTarget = true;
            tryToWriteTarget(true);
        }
        tryToMove(30 + loopingPenalty);
        tryToWriteTarget(false);
        tryToMine();
        rc.writeSharedArray(62, rc.readSharedArray(62) + income);
        // rc.setIndicatorString(bytecodeBeforeMoving0 + " " + bytecodeBeforeMoving1 + " " + bytecodeBeforeMoving2 + " " + bytecodeBeforeMoving3);
    }



    // When exploring, the Miner should write the furthest gold/lead location it can see to shared array.
    // Returns if target was written
    // resetLoc is true if we want a new location even if we haven't reached the old one yet
    public void tryToWriteTarget(boolean resetLoc) throws GameActionException {
        
        MapLocation[] goldLocations = rc.senseNearbyLocationsWithGold(getVisionRadiusSquared());
        int numGoldLocations = goldLocations.length;
        // Whether we want to change target
        boolean change = false;
        robotLocations = rc.senseNearbyRobots();
        if(numGoldLocations > 0) {
            MapLocation bestLoc = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(rank) + 1));
            int bestDist = bestLoc.distanceSquaredTo(archonLocation);

            for(int i = 0; i < numGoldLocations; i++) {
                MapLocation newLoc = goldLocations[i];
                boolean occupied = false;
                RobotInfo[] robotAtTarget = rc.senseNearbyRobots(target, 2, rc.getTeam());
                for (RobotInfo robot : robotAtTarget) {
                    if (robot.getTeam().equals(rc.getTeam()) && robot.getType().equals(RobotType.MINER) && robot.getID() < rc.getID() && robot.getLocation().distanceSquaredTo(target) <= 2) {
                        occupied = true;
                        break;
                    }
                }
                if (occupied) {
                    continue;
                }
                int newDist = archonLocation.distanceSquaredTo(newLoc);
                if(newDist > bestDist) {
                    bestDist = newDist;
                    bestLoc = newLoc;
                    change = true;
                }
            }

            if(change) {
                if (prevTarget == null) {
                    prevTarget = target;
                }
                target = bestLoc;
                targetCountdown = 0;
                if (target.equals(me) == false) {
                    reachedTarget = false;
                }
                // rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 1, Util.moveOnLattice(Util.getIntFromLocation(bestLoc)));
                // System.out.println("Miner " + rc.getID() + " to (" + target.x + ", " + target.y + "), gold, turn " + round);
                return;
            }
        }

        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead(getVisionRadiusSquared());
        robotLocations = rc.senseNearbyRobots();
        
        int numLeadLocations = leadLocations.length;
        if(numLeadLocations > 0) {
            MapLocation bestLoc = archonLocation;
            int bestDist = 100000;
            
            for(int idx = numLeadLocations - 1; idx >= 0 ; idx--) {
                if (rc.senseLead(leadLocations[idx]) > 10 && bestDist > archonLocation.distanceSquaredTo(leadLocations[idx])){
                    bestDist = archonLocation.distanceSquaredTo(leadLocations[idx]);
                    bestLoc = leadLocations[idx];
                    change = true;
                }
            }

            if(change) {
                if (prevTarget == null) {
                    prevTarget = target;
                }
                target = bestLoc;
                targetCountdown = 0;
                if (target.equals(me) == false) {
                    reachedTarget = false;
                }
                return;
            }
        }
        // Choose random location
        if (resetLoc || reachedTarget) {
            if (prevTarget != null) {
                target = prevTarget;
                prevTarget = null;
                targetCountdown = 0;
                if (target.equals(me) == false) {
                    reachedTarget = false;
                }
                return;
            }
            MapLocation bestLoc = chooseRandomInitialDestination();
            while (bestLoc.distanceSquaredTo(archonLocation) <= 10) {
                bestLoc = chooseRandomInitialDestination();
            }
            
            target = bestLoc;
            targetCountdown = 0;
            if (target.equals(me) == false) {
                reachedTarget = false;
            }
            // System.out.println("Miner " + rc.getID() + " to (" + target.x + ", " + target.y + "), random, turn " + round);
            // rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 1, Util.moveOnLattice(Util.getIntFromLocation(bestLoc)));
        }
        return;
    }
    // Tries to mine by determining how much lead we want to leave
    public void tryToMine() throws GameActionException {
        if ((isRetreating || friendlyMinerCount < enemyMinerCount) && me.distanceSquaredTo(nearestArchon(me)) > 25) {
            tryToMine(0);
        } else {
            tryToMine(1);
        }
    }

    // Tries to mine in 3x3 square around Miner, and leaves leaveLead amount at location
    public void tryToMine(int leaveLead) throws GameActionException {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
                    rc.mineGold(mineLocation);
                    loopingPenalty = 0;
                }
                while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
                    rc.mineLead(mineLocation);
                    loopingPenalty = 0;
                    income++;
                }
            }
        }
    }

    // Moves toward target through pathfinding
    public void tryToMove(int avgRubble) throws GameActionException {
        int curRubble = rc.senseRubble(me);
        Direction dir = Direction.CENTER;
        if (!isRetreating && !needsHeal) {
            if (rc.canSenseLocation(target) && me.distanceSquaredTo(target) <= 2 && (rc.senseLead(target) > 1 || rc.senseGold(target) > 0)) {
                int bestRubble = curRubble;
                // try to get on a better rubble square
                for (int i = 0; i < Util.directions.length; i++) {
                    MapLocation newLoc = me.add(Util.directions[i]);
                    if (rc.canSenseLocation(newLoc) && newLoc.distanceSquaredTo(target) <= 2 && rc.senseRubble(newLoc) <= bestRubble && rc.canMove(Util.directions[i])) {
                        bestRubble = rc.senseRubble(newLoc);
                        dir = Util.directions[i];
                    }
                }
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    me = rc.getLocation();
                }
                return;
            }
            dir = pf.findBestDirection(target, avgRubble);
        } else {
            if (needsHeal && isDefended) {
                dir = pf.findBestDirection(target, 30);
            } else {
                dir = pf.findBestDirection(target, 10);
            }
        }
        
        if (rc.canMove(dir)) {
            rc.setIndicatorLine(me, me.translate(dir.dx, dir.dy), 0, 100, 0);
            rc.move(dir);
            
            me = rc.getLocation();
        }
    }
    // Check if we're cycling, and if so we want to increase rubble tolerance
    public void preventLooping() throws GameActionException {
        targetCountdown++;
        if(loopingPenalty > 50){//let's just pick a new target at this point
        tryToWriteTarget(true);
            targetCountdown = 0;
            loopingPenalty = 0;
        }
        targetCountdown++;
        if (targetCountdown == 150){
            tryToWriteTarget(true);
            targetCountdown = 0;
            loopingPenalty = 0;
        }
        switch(checkLoop()){
            case 1: //cycling
                loopingPenalty += loopingIncrement;
                break;
            case 2: //not cycling
                loopingPenalty = 0;
                break;
            default: break;
        }
    }
    public static MapLocation chooseRandomInitialDestination(){
        Random rng2 = new Random(rc.getRoundNum() + rc.getID() * 1000);
        int goToMiddleOrCorner = rng2.nextInt(4);
        if (goToMiddleOrCorner == 0){
            int x_coord = rng2.nextInt(rc.getMapWidth() - 2) + 1;
            int y_coord = rng2.nextInt(rc.getMapHeight() - 2) + 1;
            MapLocation randPoint = new MapLocation(x_coord, y_coord);
            if (x_coord != me.x){
                MapLocation candidatePoint1 = new MapLocation(0, (-me.x) * (y_coord - me.y) / (x_coord - me.x) + me.y);
                MapLocation candidatePoint2 = new MapLocation(Util.WIDTH - 1, (Util.WIDTH - 1 - me.x) * (y_coord - me.y) / (x_coord - me.x) + me.y);
                if (Util.isOnMap(candidatePoint1) && Util.distanceMetric(candidatePoint1, me) > Util.distanceMetric(candidatePoint1, randPoint)){
                    return candidatePoint1;
                }
                if (Util.isOnMap(candidatePoint2) && Util.distanceMetric(candidatePoint2, me) > Util.distanceMetric(candidatePoint2, randPoint)){
                    return candidatePoint2;
                }
            }
            if (y_coord != me.y){
                MapLocation candidatePoint3 = new MapLocation((-me.y) * (x_coord - me.x) / (y_coord - me.y) + me.x, 0);
                MapLocation candidatePoint4 = new MapLocation((Util.HEIGHT - 1 - me.y) * (x_coord - me.x) / (y_coord - me.y) + me.x, Util.HEIGHT - 1);
                if (Util.isOnMap(candidatePoint3) && Util.distanceMetric(candidatePoint3, me) > Util.distanceMetric(candidatePoint3, randPoint)){
                    return candidatePoint3;
                }
                if (Util.isOnMap(candidatePoint4) && Util.distanceMetric(candidatePoint4, me) > Util.distanceMetric(candidatePoint4, randPoint)){
                    return candidatePoint4;
                }
            }
            return new MapLocation(x_coord, y_coord);
        }
        else{
            int whichMiddleOrCorner = rng2.nextInt(5);
            switch(whichMiddleOrCorner){
                case 0: return new MapLocation(Util.WIDTH / 2, Util.HEIGHT / 2);
                case 1: return new MapLocation(0, 0);
                case 2: return new MapLocation(0, Util.HEIGHT - 1);
                case 3: return new MapLocation(Util.WIDTH - 1, Util.HEIGHT - 1);
                default: return new MapLocation(Util.WIDTH - 1, 0);
            }
        }
    }
}