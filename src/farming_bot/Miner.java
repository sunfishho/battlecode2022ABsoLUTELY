package farming_bot;

import java.util.Map;
import java.util.Random;
import battlecode.common.*;


public class Miner extends MinerFarmer{
    public static boolean reachedTarget;
    public static RobotInfo[] robotLocations;
    static RobotInfo[] nearbyBotsSeen;
    static int numEnemies = 0;
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
    public Miner(RobotController rc, int r, MapLocation loc, MapLocation t) throws GameActionException {
        super(rc, r, loc);
        isRetreating = false;
        needsHeal = false;
        target = t;
    }
    
    public void takeTurn() throws GameActionException {
        initializeEverything();
        rc.setIndicatorString("MINER: " + me + " " + archonLocation + " " + target + " " + reachedTarget + " " + loopingPenalty);
        robotLocations = rc.senseNearbyRobots(20);
        // Sometimes we don't want to step on the target
        if (rc.canSenseLocation(target) && rc.senseRubble(target) > 30){
            tryToWriteTarget(true);
            targetCountdown = 0;
        }
        runAwayFromEnemies();
        observeSymmetry();
        tryToMine();
        MapLocation[] leadLocationsNearby = rc.senseNearbyLocationsWithLead(20);
        //if there aren't too many friendly miners nearby and there's sufficient lead, just stop and farm lol
        if (leadLocationsNearby.length > 5 && friendlyMinerCount - 1 < leadLocationsNearby.length / 4){
            mine(1);
            move();
            mine(1);
        }
        // If there are mineable neighboring deposits, don't keep moving
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
    }

    public void runAwayFromEnemies() throws GameActionException{
        // If we observe enemies, compute centroid and try to run away
        boolean observeResult = observe();
        if (observeResult){
            int enemyCentroidx = 0;
            int enemyCentroidy = 0;
            isDefended = false;
            MapLocation enemyLoc = me;
            for (RobotInfo bot : robotLocations){
                if (bot.getTeam() == myTeam){
                    if (bot.getType() == RobotType.MINER){
                        friendlyMinerCount++;
                    }
                    else if (bot.getType() == RobotType.ARCHON){
                        break;
                    }
                    continue;
                }
                switch(bot.getType()){
                    case MINER:
                        enemyMinerCount++;
                    case SOLDIER:
                        enemyLoc = bot.getLocation();
                        numEnemies++;
                        enemyCentroidx += enemyLoc.x;
                        enemyCentroidy += enemyLoc.y;
                        break;
                    case ARCHON:
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
                if (me.distanceSquaredTo(nearestArchon(me)) > Math.max(Util.HEIGHT / 2, Util.WIDTH / 2)) tryToMine(0);
                else{
                    tryToMine(1);
                }
                rc.writeSharedArray(62, rc.readSharedArray(62) + income);
            }
        }
    }

    public void doHealingThings() throws GameActionException{
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
    }

    public void initializeEverything() throws GameActionException{
        takeAttendance();
        numEnemies = 0;
        friendlyMinerCount = 1;
        enemyMinerCount = 0;
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
                if (rc.senseLead(leadLocations[idx]) > 1 && bestDist > archonLocation.distanceSquaredTo(leadLocations[idx])){
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
        if ((isRetreating || friendlyMinerCount < enemyMinerCount || numEnemies > 1) && me.distanceSquaredTo(nearestArchon(me)) > 25) {
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
        if (Clock.getBytecodesLeft() < 50){
            Clock.yield();
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
            return new MapLocation(x_coord, y_coord);
        }
        else{
            int whichMiddleOrCorner = rng2.nextInt(10);
            switch(whichMiddleOrCorner){
                case 0: return new MapLocation(Util.WIDTH / 2, Util.HEIGHT / 2);
                case 1: return new MapLocation(0, 0);
                case 2: return new MapLocation(0, Util.HEIGHT - 1);
                case 3: return new MapLocation(Util.WIDTH - 1, Util.HEIGHT - 1);
                case 4: return new MapLocation(Util.WIDTH - 1, 0);
                default:
                    return new MapLocation(Util.WIDTH / 2, Util.HEIGHT / 2);
            }
        }
    }
    // Stores information about potential locations to move to in radius 2 around
    class LocationInfo {
        final int LOOK_RADIUS = 9;
        final int A = -100, B = -10, C = 1, D = 1;
        int visibleMiners; // # of miners visible in radius LOOK_RADIUS
        int rubble; // rubble level at the square
        int distArchon; // distance to archon
        int leadAround; // number of squares in radius 2 around with lead > 1
        Direction dir;

        public LocationInfo(MapLocation loc) throws GameActionException {
            RobotInfo[] nearby = rc.senseNearbyRobots(loc, LOOK_RADIUS, rc.getTeam());
            for(RobotInfo r : nearby) {
                if(r.getType() == RobotType.MINER) visibleMiners++;
            }
            rubble = rc.senseRubble(loc);
            distArchon = loc.distanceSquaredTo(archonLocation);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    MapLocation l = new MapLocation(loc.x + dx, loc.y + dy);
                    if(rc.onTheMap(l) && rc.senseLead(l) > 0) leadAround += rc.senseLead(l);
                }
            }
            dir = me.directionTo(loc);
        }

        public int compareTo(LocationInfo other) throws GameActionException {
            return getRating() - other.getRating();
        }

        public int getRating() throws GameActionException {
            return A * visibleMiners + B * rubble + C * leadAround + D * distArchon;
        }
    }
}