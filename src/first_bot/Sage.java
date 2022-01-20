
package first_bot;

import battlecode.common.*;

import java.util.Random;

public class Sage extends Unit {

    private static boolean isRetreating;
    static RobotInfo[] nearbyBotsSeen, enemyBotsWithinRange;
    static MapLocation enemySoldierCentroid = new MapLocation(0, 0);
    static boolean isHealing;
    static int health;

    public Sage(RobotController rc, int r, MapLocation loc) throws GameActionException {
        super(rc, r, loc);
        isRetreating = false;
        //if no alarm:
        if (rc.readSharedArray(17) < 65534){
            target = Util.getLocationFromInt(rc.readSharedArray(17) % 10000);
        }
        else if (rc.readSharedArray(17) == 65534){
            target = chooseRandomInitialDestination();
        }
        else{
            target = Util.getLocationFromInt(rc.readSharedArray(21)/3 - 1);
        }
        isHealing = false;
        health = RobotType.SAGE.health;
    }

    //TODO
    public void takeTurn() throws GameActionException {
        round = rc.getRoundNum();
        me = rc.getLocation();
        health = rc.getHealth();
        if (me.equals(target)){
            target = chooseRandomInitialDestination();
        }
        if (rc.getActionCooldownTurns() < 2) {
            isRetreating = false;
        }
        observe();
        // If low health, go to archon
        if (rc.getHealth() < 40) {
            if (!isHealing) {
                // reset recentdists
                isHealing = true;
            }
            target = archonLocation;
        }
        // If high health, leave archon
        if (isHealing) {
            target = archonLocation;
            if (me.distanceSquaredTo(archonLocation) > 13) {
                tryToMove(30);
                int crowdCount = 0;
                for (RobotInfo robot : rc.senseNearbyRobots(20, rc.getTeam())) {
                    if (robot.getLocation().distanceSquaredTo(target) <= 20 && robot.getMode() == RobotMode.DROID && robot.getHealth() < robot.getType().health) {
                        crowdCount++;
                    }
                }

                if (!isHealing && crowdCount > 3 && me.distanceSquaredTo(archonLocation) < 25) {
                    // We can't get healed by the archon so try to move to a different archon
                    rank = (rank % rc.getArchonCount()) + 1;
                    archonLocation = Util.getLocationFromInt(rc.readSharedArray(rank - 1));
                }
                moveLowerRubble(true);
            }
            if (rc.getHealth() > 95 ) {
                isRetreating = false;
                
                target = chooseRandomInitialDestination();
            } else {
                return;
            }
        }
        observeSymmetry();
        nearbyBotsSeen = rc.senseNearbyRobots(visionRadius);
        enemyBotsWithinRange = rc.senseNearbyRobots(actionRadius, enemyTeam);
        numEnemies = 0;
        double enemySoldierCentroidx = 0;
        double enemySoldierCentroidy = 0;
        for (RobotInfo bot : nearbyBotsSeen){
            switch(bot.getType()){
                case SOLDIER:
                    if (bot.getTeam() != myTeam) {
                        MapLocation enemyLoc = bot.getLocation();
                        numEnemies++;
                        enemySoldierCentroidy += enemyLoc.y;
                        enemySoldierCentroidx += enemyLoc.x;
                    }
                    break;
                default:
            }
        }
        if (numEnemies == 0){
            tryToMove(40);
            moveLowerRubble(false);
            tryToAttack();
            rc.setIndicatorString("target1: " + target.x + ", " + target.y + ", " + isHealing);
            return;
        }
        enemySoldierCentroidx = (int) ((enemySoldierCentroidx / (numEnemies + 0.0)) + 0.5);
        enemySoldierCentroidy = (int) ((enemySoldierCentroidy / (numEnemies + 0.0)) + 0.5);
        enemySoldierCentroid = enemySoldierCentroid.translate((int) enemySoldierCentroidx - enemySoldierCentroid.x, (int) enemySoldierCentroidy - enemySoldierCentroid.y);
        
        // This whole block only runs if we have an enemy in sight
        tryToAttack();
        if (rc.getActionCooldownTurns() > 2) {
            Direction dir = retreat(enemySoldierCentroid);
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
        } else {
            target = enemySoldierCentroid;
            tryToMove(30);
        }
        // rc.setIndicatorString(teammateSoldiers + " " + enemySoldiers + " " + onOffense + " " + onDefense);
        rc.setIndicatorString("target2: " + target.x + ", " + target.y + ", " + isHealing);

    }
    
    public void tryToAttack() throws GameActionException {
        enemyBotsWithinRange = rc.senseNearbyRobots(actionRadius, enemyTeam);
        int bestType = 10;
        int highestRubble = 0;
        int highestHealth = -1;
        int highestOneshotHealth = -1;
        if (enemyBotsWithinRange.length == 0){
            return;
        }
        RobotInfo bestBot = enemyBotsWithinRange[0];
        // Go through list of enemies and find the one we want to attack the most
        for (RobotInfo bot: enemyBotsWithinRange) {
            if (bot.getTeam() == myTeam){
                continue;
            }
            int enemyType = 8;
            for (int j = 0; j < 7; j++) {
                if (bot.getType().equals(Util.attackOrder[j])) {
                    enemyType = j;
                    break;
                }
            }
            if (enemyType < bestType) {
                bestType = enemyType;
                highestRubble = rc.senseRubble(bot.getLocation());
                highestHealth = bot.getHealth();
                if (bot.getHealth() <= 40) {
                    highestOneshotHealth = bot.getHealth();
                }
                bestBot = bot;
                continue;
            }
            else if (bestType == enemyType){
                if (highestRubble < rc.senseRubble(bot.getLocation())){
                    highestRubble = rc.senseRubble(bot.getLocation());
                    highestHealth = bot.getHealth();
                    if (bot.getHealth() <= 40) {
                        highestOneshotHealth = bot.getHealth();
                    }
                    bestBot = bot;
                }
                else if (highestRubble == rc.senseRubble(bot.getLocation())){
                    
                    if ((bot.getHealth() <= 40 && highestOneshotHealth < bot.getHealth())){
                        highestOneshotHealth = bot.getHealth();
                        bestBot = bot;
                    } else if (highestOneshotHealth == 1 && highestHealth < bot.getHealth()) {
                        highestHealth = bot.getHealth();
                        bestBot = bot;
                    }
                }
            }
            // Tiebreak by enemy health
        }
        //Attack if possible
        if (targetSoldiers() > 5 && rc.canEnvision(AnomalyType.CHARGE)) {
            rc.envision(AnomalyType.CHARGE);
            isRetreating = true;
            return;
        }
        if (rc.canAttack(bestBot.getLocation()) && bestBot.getType() != RobotType.MINER) {
            rc.attack(bestBot.getLocation());
            isRetreating = true;
            return;
        }
    }

    public void tryToCastAnomaly() throws GameActionException {

    }

    public boolean tryToMove(int avgRubble) throws GameActionException {
        rc.setIndicatorString("trying to move: " + target);
        if (!isRetreating) {
            if (rc.readSharedArray(17) < 65534) {
                target = Util.getLocationFromInt(rc.readSharedArray(17) % 10000);
                targetCountdown = 0;
            }
            else if (target == null){
                target = chooseRandomInitialDestination();
                targetCountdown = 0;
            }
            if (me.distanceSquaredTo(target) <= 2 && rc.senseRobotAtLocation(target) != null) {
                return false;
            }
        }
        Direction dir = Direction.CENTER;
        if (target != null){
            dir = pf.findBestDirection(target, avgRubble);
        }
        if (rc.canMove(dir) && dir != Direction.CENTER){
            rc.move(dir);
            me = rc.getLocation();
            return true;
        }
        return false;
    }


    public void moveLowerRubble(boolean toRetreat) throws GameActionException{
        // rc.setIndicatorString("MOVING TO LOWER RUBBLE");
        int bestRubble = rc.senseRubble(me);
        Direction bestDir = Direction.CENTER;
        for (Direction dir: Util.directions){
            if (rc.canMove(dir) && rc.senseRubble(me.add(dir))/10 < bestRubble/10){
                bestDir = dir;
                bestRubble = rc.senseRubble(me.add(bestDir));
            }
            if (rc.canMove(dir) && toRetreat && rc.senseRubble(me.add(dir))/10 == bestRubble/10){
                MapLocation nearestArchonLoc = nearestArchon(me);
                if (Util.distanceMetric(me.add(dir), nearestArchonLoc) <= Util.distanceMetric(me.add(bestDir), nearestArchonLoc)){
                    bestDir = dir;
                    bestRubble = rc.senseRubble(me.add(bestDir));
                }
            }
        }
        if (rc.canMove(bestDir) && bestDir != Direction.CENTER){
            rc.move(bestDir);
        }
    }

    public int soldiersInRange(int r) throws GameActionException {//counts number of enemy soldiers within distance r
        Team opp = rc.getTeam().opponent();
        int cnt = 0;
        for(RobotInfo rob : rc.senseNearbyRobots(r, opp)){
            if(rob.mode == RobotMode.DROID){
                cnt++;
            }
        }
        return cnt;
    }

    public int threateningSoldiersIfMove(Direction dir){//number of enemy soldiers that can attack us if we move in dir
        MapLocation loc = rc.getLocation();
        MapLocation newloc = loc.add(dir);
        int cnt = 0;
        if(Util.inGrid(newloc)){
            for(RobotInfo x : rc.senseNearbyRobots(34, rc.getTeam().opponent())){//vision range
                if(x.type != RobotType.SOLDIER){//only care about soldiers here
                    continue;
                }
                if(newloc.distanceSquaredTo(newloc) <= 13){//enemy soldier can attack new location
                    cnt++;
                }
            }
        }
        return cnt;
    }

    public int targetSoldiersIfMove(Direction dir) {// number of soldiers we can target if we move in dir
        MapLocation loc = rc.getLocation();
        MapLocation newloc = loc.add(dir);
        int cnt = 0;
        if(Util.inGrid(newloc)){
            for(RobotInfo x : rc.senseNearbyRobots(34, rc.getTeam().opponent())){//vision range
                if(x.type != RobotType.SOLDIER){//only care about soldiers here
                    continue;
                }
                if(newloc.distanceSquaredTo(newloc) <= 20){//new location can attack enemy soldier
                    cnt++;
                }
            }
        }
        return cnt;
    }

    public int threateningSoldiers() throws GameActionException {//number of soldiers that could attack the sage
        return soldiersInRange(13);//soldier attacking radius = 13
    }

    public int targetSoldiers() throws GameActionException {//counts number of enemey soldiers that we can attack
        return soldiersInRange(20);//sage attacking radius
    }
    
}