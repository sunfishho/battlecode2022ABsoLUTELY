
package sage_rush_bot;

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

    public void takeTurn() throws GameActionException {
        round = rc.getRoundNum();
        me = rc.getLocation();
        health = rc.getHealth();
        if (me.equals(target)){
            target = chooseRandomInitialDestination();
        }
        if (rc.getActionCooldownTurns() < 20) {
            isRetreating = false;
        }
        observe();
        // If low health, go to archon
        if (rc.getHealth() < 20) {
            if (!isHealing) {
                // reset recentdists
                isHealing = true;
            }
            target = archonLocation;
        }
        // If high health, leave archon
        if (isHealing) {
            target = archonLocation;
            tryToAttack();
            if (me.distanceSquaredTo(archonLocation) > 20) {
                tryToMove(25);
                int crowdCount = 0;
                for (RobotInfo robot : rc.senseNearbyRobots(34, rc.getTeam())) {
                    //-5 for health check because soldiers often don't heal all the way to full
                    if (robot.getLocation().distanceSquaredTo(target) <= 20 && robot.getMode() == RobotMode.DROID && robot.getHealth() < robot.getType().health - 5) {
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
                isHealing = false;
                
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
                case SAGE:
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
            tryToMove(15);
            moveLowerRubble(false);
            tryToAttack();
            rc.setIndicatorString("target1: " + target.x + ", " + target.y + ", " + isHealing + ", " + rc.getActionCooldownTurns());
            return;
        }
        enemySoldierCentroidx = (int) ((enemySoldierCentroidx / (numEnemies + 0.0)) + 0.5);
        enemySoldierCentroidy = (int) ((enemySoldierCentroidy / (numEnemies + 0.0)) + 0.5);
        enemySoldierCentroid = enemySoldierCentroid.translate((int) enemySoldierCentroidx - enemySoldierCentroid.x, (int) enemySoldierCentroidy - enemySoldierCentroid.y);
        
        // This whole block only runs if we have an enemy in sight
        tryToAttack();
        if (rc.getActionCooldownTurns() > 3) {
            Direction dir = retreat(enemySoldierCentroid);
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
        } else {
            target = enemySoldierCentroid;
            tryToMove(20);
        }
        // rc.setIndicatorString(teammateSoldiers + " " + enemySoldiers + " " + onOffense + " " + onDefense);
        rc.setIndicatorString("target2: " + target.x + ", " + target.y + ", " + isHealing + ", " + rc.getActionCooldownTurns());

    }
    
    public void tryToAttack() throws GameActionException {
        enemyBotsWithinRange = rc.senseNearbyRobots(actionRadius, enemyTeam);
        if (enemyBotsWithinRange.length == 0){
            return;
        }
        boolean canAttackArchon = false;
        //want to maximize score: sum of damage from action + 20 points for every kill of a soldier, 40 points for every kill of a sage, 5 points for every kill of a miner
        int chargeScore = 0;
        int attackScore = 0;
        int bestBotToAttackIdx = -1;
        final int MINER_BUILDER_KILL_BONUS = 5;
        final int SOLDIER_KILL_BONUS = 20;
        final int SAGE_KILL_BONUS = 40;
        final double MINER_BUILDER_HEALTH_MULTIPLIER = 0.5;
        final double SOLDIER_HEALTH_MULTIPLIER = 1;
        final double SAGE_HEALTH_MULTIPLIER = 2;
        int thisAttackScore;

        // Go through list of enemies and find the one we want to attack the most
        for (int idx = 0; idx < enemyBotsWithinRange.length; idx++) {
            switch (enemyBotsWithinRange[idx].getType()){
                case ARCHON: canAttackArchon = true;
                case SOLDIER:
                    if (enemyBotsWithinRange[idx].health <= 11){
                        chargeScore += (enemyBotsWithinRange[idx].health * SOLDIER_HEALTH_MULTIPLIER + SOLDIER_KILL_BONUS);
                    }
                    if (enemyBotsWithinRange[idx].health <= 45){
                        thisAttackScore = (int)(enemyBotsWithinRange[idx].health * SOLDIER_HEALTH_MULTIPLIER) + SOLDIER_KILL_BONUS;
                    }
                    else{
                        thisAttackScore = (int)(45 * SOLDIER_HEALTH_MULTIPLIER);
                    }

                    if (thisAttackScore > attackScore){
                        attackScore = thisAttackScore;
                        bestBotToAttackIdx = idx;
                    }
                    else if (thisAttackScore == attackScore){
                        //tiebreak on distance to the sage
                        if (Util.distanceMetric(me, enemyBotsWithinRange[bestBotToAttackIdx].location) > Util.distanceMetric(me, enemyBotsWithinRange[idx].location)){
                            bestBotToAttackIdx = idx;
                        }
                    }
                    break;
                case SAGE:
                    if (enemyBotsWithinRange[idx].health <= 22){
                        chargeScore += (enemyBotsWithinRange[idx].health * SAGE_HEALTH_MULTIPLIER + SAGE_KILL_BONUS);
                    }
                    if (enemyBotsWithinRange[idx].health <= 45){
                        thisAttackScore = (int)(enemyBotsWithinRange[idx].health * SAGE_HEALTH_MULTIPLIER) + SAGE_KILL_BONUS;
                    }
                    else{
                        thisAttackScore = (int)(45 * SAGE_HEALTH_MULTIPLIER);
                    }

                    if (thisAttackScore > attackScore){
                        attackScore = thisAttackScore;
                        bestBotToAttackIdx = idx;
                    }
                    else if (thisAttackScore == attackScore){
                        //tiebreak on rubble for sages
                        if (rc.senseRubble(enemyBotsWithinRange[bestBotToAttackIdx].location) > rc.senseRubble(enemyBotsWithinRange[idx].location)){
                            bestBotToAttackIdx = idx;
                        }
                    }
                default:
                    if (enemyBotsWithinRange[idx].getType() == RobotType.MINER || enemyBotsWithinRange[idx].getType() == RobotType.BUILDER){
                        if (enemyBotsWithinRange[idx].health <= (int) (enemyBotsWithinRange[idx].getType().health * 0.22)){
                            chargeScore += (enemyBotsWithinRange[idx].health * MINER_BUILDER_HEALTH_MULTIPLIER + MINER_BUILDER_KILL_BONUS);
                        }
                        thisAttackScore = (int)(enemyBotsWithinRange[idx].health * MINER_BUILDER_HEALTH_MULTIPLIER) + MINER_BUILDER_KILL_BONUS;
                        if (thisAttackScore > attackScore){
                            attackScore = thisAttackScore;
                            bestBotToAttackIdx = idx;
                        }
                        else if (thisAttackScore == attackScore){
                            //tiebreak on rubble for sages
                            if (rc.senseRubble(enemyBotsWithinRange[bestBotToAttackIdx].location) > rc.senseRubble(enemyBotsWithinRange[idx].location)){
                                bestBotToAttackIdx = idx;
                            }
                        }
                    }
            }
        }
        
        //Checking if it's not worth to attack
        if (Math.max(attackScore, chargeScore) < 30 && round > 100){
            return;
        }
        if (chargeScore > attackScore && rc.canEnvision(AnomalyType.CHARGE)){
            rc.envision(AnomalyType.CHARGE);
        }
        if (rc.canEnvision(AnomalyType.FURY) && canAttackArchon){
            //make sure we don't accidentally friendly fire on our archon
            MapLocation nearestFriendlyArchon = nearestArchon(me);
            boolean canSafelyFury = true;
            if (me.distanceSquaredTo(nearestFriendlyArchon) <= actionRadius){
                canSafelyFury = false;
                //TODO: check later if it's portable or not
            }
            if (canSafelyFury){
                rc.envision(AnomalyType.FURY);
                return;
            }
        }
        if (rc.canAttack(enemyBotsWithinRange[bestBotToAttackIdx].getLocation())) {
            rc.attack(enemyBotsWithinRange[bestBotToAttackIdx].getLocation());
        }
        return;
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
                if(newloc.distanceSquaredTo(newloc) <= 25){//new location can attack enemy soldier
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
        return soldiersInRange(25);//sage attacking radius
    }
    
}