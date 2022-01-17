package first_bot;

import java.util.Map;

import battlecode.common.*;


public class Miner extends Unit{
    public static boolean reachedTarget;
    public static RobotInfo[] robotLocations;
    static RobotInfo[] nearbyBotsSeen;
    static int numEnemies;
    static MapLocation enemySoldierCentroid = new MapLocation(0, 0);
    static int maxBytecodeUsed = 0;
    static int income;

    public Miner(RobotController rc, int r, MapLocation loc, MapLocation t) {
        super(rc, r, loc);
        isRetreating = false;
        target = t;
    }
    
    public void takeTurn() throws GameActionException {
        income = 0;
        targetCountdown++;
        if (targetCountdown == 150){
            target = chooseRandomInitialDestination();
            targetCountdown = 0;
        }
        isRetreating = false;
        takeAttendance();
        me = rc.getLocation();
        round = rc.getRoundNum();
        archonLocation = nearestArchon(me);
        rc.setIndicatorString("MINER: " + me + " " + archonLocation + " " + target + " " + reachedTarget);
        robotLocations = rc.senseNearbyRobots(20);
        if (me.isAdjacentTo(target) && rc.senseRubble(target) > 30){
            target = target.translate(rng.nextInt(Util.WIDTH) - target.x, rng.nextInt(Util.HEIGHT) - target.y);
            targetCountdown = 0;
        }
        if (observe()){
            int enemyCentroidx = 0;
            int enemyCentroidy = 0;
            int numEnemies = 0;
            MapLocation enemyLoc = me;
            for (RobotInfo bot : robotLocations){
                if (bot.getTeam() == myTeam){
                    continue;
                }
                switch(bot.getType()){
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
                enemyCentroidx /= numEnemies;
                enemyCentroidy /= numEnemies;
                retreat(new MapLocation(enemyCentroidx, enemyCentroidy));
            }
        }
        observeSymmetry();
        tryToMine(1);
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
            // explore();
            // System.out.println(rc.getID() + ": finding new location");
            tryToWriteTarget(true);
        }
        
        if(!reachedTarget && me.equals(target)) {
            // somehow stay still if lots of lead
            reachedTarget = true;
            tryToWriteTarget(true);
        }
        tryToMove();
        // Check if we can still mine stuff, stay still if so
        // for (int dx = -1; dx <= 1; dx++) {
        //     for (int dy = -1; dx <= 1; dy++) {
        //         MapLocation newLoc = new MapLocation(me.x + dx, me.y + dy);
        //         if (rc.senseLead(newLoc) > 1 || rc.senseGold(newLoc) > 0) {

        //         }
        //     }
        // }
        tryToWriteTarget(false);
        tryToMine(1);
        rc.writeSharedArray(30, rc.readSharedArray(30) + income);
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
            // int initialBytecode = 0;
            // if (rc.getID() == 10080){
            //     initialBytecode = Clock.getBytecodeNum();
            // }
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
                target = bestLoc;
                targetCountdown = 0;
                if (target.equals(me) == false) {
                    reachedTarget = false;
                }
                return;
            }
        }
        if (rc.readSharedArray(17) == 65535 && !change){
            MapLocation minerLoc = me;
            int minerCentroidx = 0;
            int minerCentroidy = 0;
            int numMiners = 0;
            for (int idx = robotLocations.length - 1; idx >= 0; idx--){
                if (robotLocations[idx].getTeam() == myTeam && robotLocations[idx].getType() == RobotType.MINER){
                    minerLoc = robotLocations[idx].getLocation();
                    numMiners++;
                    minerCentroidx += minerLoc.x;
                    minerCentroidy += minerLoc.y;
                }
            }
            if (numMiners != 0){
                minerCentroidx /= numMiners;
                minerCentroidy /= numMiners;
                Direction dirRetreat = retreat(new MapLocation(minerCentroidx, minerCentroidy));
                isRetreating = false;
                if (rc.canMove(dirRetreat)){
                    rc.move(dirRetreat);
                    return;
                }
            }

        }
        // Choose random location
        if (resetLoc || reachedTarget) {
            MapLocation bestLoc = new MapLocation(rng.nextInt(Util.WIDTH), rng.nextInt(Util.HEIGHT));
            while (bestLoc.distanceSquaredTo(archonLocation) <= 10) {
                bestLoc = new MapLocation(rng.nextInt(Util.WIDTH), rng.nextInt(Util.HEIGHT));
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

    // Tries to mine in 3x3 square around Miner, and leaves leaveLead amount at location
    public void tryToMine(int leaveLead) throws GameActionException {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
                    rc.mineLead(mineLocation);
                    income++;
                }
            }
        }
    }

    // Moves toward target through pathfinding
    public void tryToMove() throws GameActionException {
        int curRubble = rc.senseRubble(me);
        Direction dir = Direction.CENTER;
        if (!isRetreating) {
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
            dir = pf.findBestDirection(target, 80);
        } else {
            dir = pf.findBestDirection(target, 10);
        }
        
        if (rc.canMove(dir)) {
            rc.setIndicatorLine(me, me.translate(dir.dx, dir.dy), 0, 100, 0);
            rc.move(dir);
            
            me = rc.getLocation();
        }
    }
}