
package first_bot;

import battlecode.common.*;

import java.util.Random;


public class Sage extends Unit {

    private static boolean isRetreating;
    static RobotInfo[] nearbyBotsSeen, enemyBotsWithinRange;
    static MapLocation enemySoldierCentroid = new MapLocation(0, 0);

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
    }

    //TODO
    public void takeTurn() throws GameActionException {
        round = rc.getRoundNum();
        me = rc.getLocation();
        if (me.equals(target)){
            target = chooseRandomInitialDestination();
        }
        if (rc.getActionCooldownTurns() < 10) {
            isRetreating = false;
        }
        observe();
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
            rc.setIndicatorString("target1: " + target.x + ", " + target.y);
            return;
        }
        enemySoldierCentroidx /= numEnemies;
        enemySoldierCentroidy /= numEnemies;
        enemySoldierCentroid = enemySoldierCentroid.translate((int) enemySoldierCentroidx - enemySoldierCentroid.x, (int) enemySoldierCentroidy - enemySoldierCentroid.y);
        
        // This whole block only runs if we have an enemy in sight
        tryToAttack();
        retreat(enemySoldierCentroid);
        // rc.setIndicatorString(teammateSoldiers + " " + enemySoldiers + " " + onOffense + " " + onDefense);
        rc.setIndicatorString("target2: " + target.x + ", " + target.y);

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
        if (rc.canAttack(bestBot.getLocation()) && bestBot.getType() != RobotType.MINER) {
            rc.attack(bestBot.getLocation());
            isRetreating = true;
            return;
        }
    }

    public void tryToCastAnomaly() throws GameActionException {

    }

    public void tryToMove(int avgRubble) throws GameActionException {
        
        if (rc.readSharedArray(17) < 65534) {
            target = Util.getLocationFromInt(rc.readSharedArray(17) % 10000);
        }
        else if (target == null){
            target = chooseRandomInitialDestination();
        }
        else if ((rc.canSenseLocation(target) && rc.senseRubble(target) > 30)){
            target = chooseRandomInitialDestination();
        }
        if (me.distanceSquaredTo(target) <= 2 && rc.senseRobotAtLocation(target) != null) {
            return;
        }
        Direction dir = Direction.CENTER;
        if (target != null){
            dir = pf.findBestDirection(target, avgRubble);
        }
        if (rc.canMove(dir)){
            rc.move(dir);
        }
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
            if(rob.type == RobotType.SOLDIER){
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