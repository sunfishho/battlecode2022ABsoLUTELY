
package first_bot;

import battlecode.common.*;

import java.util.Random;

public class Sage extends Unit {

    private static boolean isRetreating;
    static RobotInfo[] nearbyBotsSeen, enemyBotsWithinRange;
    static MapLocation enemySoldierCentroid = new MapLocation(0, 0);
    static boolean isHealing;
    static int health;
    static int loopingPenalty = 0;//increase rubble tolerance if we're stuck in a loop
    static int loopingIncrement = 1;
    static int targetCountdown = 0;
    static int nextCharge = 3000;

    public Sage(RobotController rc, int r, MapLocation loc) throws GameActionException {
        super(rc, r, loc);
        isRetreating = false;
        //if no alarm:
        if (rc.readSharedArray(49) < 65534){
            target = Util.getLocationFromInt(rc.readSharedArray(49) % 10000);
        }
        else if (rc.readSharedArray(49) == 65534){
            target = chooseRandomInitialDestination();
        }
        else{
            target = chooseRandomInitialDestination();
        }
        isHealing = false;
        health = RobotType.SAGE.health;
    }

    public void takeTurn() throws GameActionException {
        round = rc.getRoundNum();
        me = rc.getLocation();
        archonLocation = nearestArchon(me);
        health = rc.getHealth();

        AnomalyScheduleEntry[] sched = rc.getAnomalySchedule();
        for (AnomalyScheduleEntry a : sched){
            if(a.anomalyType == AnomalyType.CHARGE && a.roundNumber > round){
                nextCharge = a.roundNumber;
                break;
            }
        }
        nextCharge = 3000;

        observe();
        observeSymmetry();
        if(nextCharge - round < 20){//try to get away from our friends
            System.out.println("RUNNING FROM CHARGE!");
            tryToEscape();
            return;
        }
        if(round > 1850){
            target = Util.getCorner(archonLocation);
            tryToMove(20);
            return;
        }
        if (me.equals(target)){
            target = chooseRandomInitialDestination();
            targetCountdown = 0;
            loopingPenalty = 0;
        }
        if (rc.getActionCooldownTurns() < 20 && !isHealing) {
            isRetreating = false;
        }
        // If low health, go to archon
        if (rc.getHealth() <= 25) {
            if (!isHealing) {
                // reset recentdists
                isHealing = true;
                isRetreating = true;
            }
            target = archonLocation;
            targetCountdown = 0;
            loopingPenalty = 0;
        }
        // If high health, leave archon
        if (isHealing) {
            target = archonLocation;
            targetCountdown = 0;
            loopingPenalty = 0;
            tryToAttack();
            if (me.distanceSquaredTo(archonLocation) > 20) {
                tryToMove(50);
                moveLowerRubble(true);
            }
            if (rc.getHealth() > 95 ) {
                isHealing = false;
                isRetreating = false;
                target = chooseRandomInitialDestination();
                targetCountdown = 0;
                loopingPenalty = 0;
            } else {
                return;
            }
        }
        targetCountdown++;
        switch(checkLoop()){
            case 1: //cycling
                loopingPenalty += loopingIncrement;
                break;
            case 2: //not cycling
                loopingPenalty = 0;
                break;
            default: break;
        }
        if(loopingPenalty > 70){//let's just pick a new target at this point
            target = chooseRandomInitialDestination();
            targetCountdown = 0;
            loopingPenalty = 0;
        }
        if(targetCountdown == 200) {//just pick a new target, taking too long
            target = chooseRandomInitialDestination();
            targetCountdown = 0;
            loopingPenalty = 0;
        }
        nearbyBotsSeen = rc.senseNearbyRobots(visionRadius);
        enemyBotsWithinRange = rc.senseNearbyRobots(actionRadius, enemyTeam);
        numTeammates = 0;
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
                    else{
                        numTeammates++;
                    }
                    break;
                case SAGE:
                    if (bot.getTeam() != myTeam) {
                        MapLocation enemyLoc = bot.getLocation();
                        numEnemies++;
                        enemySoldierCentroidy += enemyLoc.y;
                        enemySoldierCentroidx += enemyLoc.x;
                    }
                    else{
                        numTeammates++;
                    }
                    break;
                case WATCHTOWER:
                    if (bot.getMode() == RobotMode.PORTABLE){
                        continue;
                    }
                    if (bot.getTeam() != myTeam){
                        MapLocation enemyLoc = bot.getLocation();
                        numEnemies++;
                        enemySoldierCentroidy += enemyLoc.y;
                        enemySoldierCentroidx += enemyLoc.x;
                    }
                    else{
                        numTeammates++;
                    }
                default:
            }
        }

        if (shouldSendMinerHere(isRetreating, numTeammates, numEnemies)){
            rc.writeSharedArray(57, Util.getIntFromLocation(me));
        }

        if (numEnemies == 0){
            tryToMove(15 + loopingPenalty);
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
            targetCountdown = 0;
            loopingPenalty = 0;
            tryToMove(20);
        }
        // rc.setIndicatorString(teammateSoldiers + " " + enemySoldiers + " " + onOffense + " " + onDefense);
        rc.setIndicatorString("target2: " + target.x + ", " + target.y + ", " + isHealing + ", " + rc.getActionCooldownTurns());

    }

    // tries to get away from friendly robots to avoid dying to charge
    public void tryToEscape() throws GameActionException {// move away from the centroid of friendly robots?
        double centroidX = 0.0;
        double centroidY = 0.0;
        RobotInfo[] nearbyFriends = rc.senseNearbyRobots(34, rc.getTeam());
        int f = nearbyFriends.length;
        for (int i = 0; i < f; i++){
            MapLocation loc = nearbyFriends[i].getLocation();
            centroidX += loc.x;
            centroidY += loc.y;
        }
        int cX = (int) ((centroidX / (f + 0.0)) + 0.5);
        int cY = (int) ((centroidY / (f + 0.0)) + 0.5);
        MapLocation centroid = new MapLocation(cX, cY);
        Direction dir = centroid.directionTo(me); // move in this direction to get away from centroid
        if(rc.canMove(dir)){
            rc.move(dir);
            me = rc.getLocation();
            return;
        }
        //try left and right directions, still increases distance from centroid
        Direction dirl = dir.rotateLeft();
        Direction dirr = dir.rotateRight();
        if(rc.canMove(dirl)){
            rc.move(dirl);
            me = rc.getLocation();
            return;
        }
        if(rc.canMove(dirr)){
            rc.move(dirr);
            me = rc.getLocation();
        }
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
        int furyScore = 0;
        int bestBotToAttackIdx = -1;
        final int MINER_BUILDER_KILL_BONUS = 10;
        final int SOLDIER_KILL_BONUS = 20;
        final int SAGE_KILL_BONUS = 40;
        final int WATCHTOWER_KILL_BONUS = 80;
        final int LAB_KILL_BONUS = 100;
        final double MINER_BUILDER_HEALTH_MULTIPLIER = 0.5;
        final double SOLDIER_HEALTH_MULTIPLIER = 1;
        final double SAGE_HEALTH_MULTIPLIER = 2;
        final double WATCHTOWER_HEALTH_MULTIPLIER = 4;
        final double LAB_HEALTH_MULTIPLIER = 4;
        int thisAttackScore;

        // Go through list of enemies and find the one we want to attack the most
        for (int idx = 0; idx < enemyBotsWithinRange.length; idx++) {
            switch (enemyBotsWithinRange[idx].getType()){
                case ARCHON: canAttackArchon = true;
                case SOLDIER:
                    if (enemyBotsWithinRange[idx].health <= 11){
                        chargeScore += (enemyBotsWithinRange[idx].health * SOLDIER_HEALTH_MULTIPLIER + SOLDIER_KILL_BONUS);
                    }
                    else{
                        chargeScore += (11 * SOLDIER_HEALTH_MULTIPLIER);
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
                    else{
                        chargeScore += (22 * SAGE_HEALTH_MULTIPLIER);
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
                case WATCHTOWER:
                    if (enemyBotsWithinRange[idx].getMode() == RobotMode.TURRET){
                        if (enemyBotsWithinRange[idx].health <= enemyBotsWithinRange[idx].getType().health / 10){
                            furyScore += (enemyBotsWithinRange[idx].health * WATCHTOWER_HEALTH_MULTIPLIER + WATCHTOWER_KILL_BONUS);
                        }
                        else{
                            furyScore += (enemyBotsWithinRange[idx].getType().health / 10 * WATCHTOWER_HEALTH_MULTIPLIER);
                        }
                    }
                    if (enemyBotsWithinRange[idx].health <= 45){
                        thisAttackScore = (int)(enemyBotsWithinRange[idx].health * WATCHTOWER_HEALTH_MULTIPLIER) + WATCHTOWER_KILL_BONUS;
                    }
                    else{
                        thisAttackScore = (int)(45 * WATCHTOWER_HEALTH_MULTIPLIER);
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

                case LABORATORY:
                    if (enemyBotsWithinRange[idx].getMode() == RobotMode.TURRET){
                        if (enemyBotsWithinRange[idx].health <= enemyBotsWithinRange[idx].getType().health / 10){
                            furyScore += (enemyBotsWithinRange[idx].health * LAB_HEALTH_MULTIPLIER + LAB_KILL_BONUS);
                        }
                        else{
                            furyScore += (enemyBotsWithinRange[idx].getType().health / 10 * LAB_HEALTH_MULTIPLIER);
                        }
                    }
                    if (enemyBotsWithinRange[idx].health <= 45){
                        thisAttackScore = (int)(enemyBotsWithinRange[idx].health * LAB_HEALTH_MULTIPLIER) + LAB_KILL_BONUS;
                    }
                    else{
                        thisAttackScore = (int)(45 * LAB_HEALTH_MULTIPLIER);
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
                        else{
                            chargeScore += ((enemyBotsWithinRange[idx].getType().health * 0.22) * MINER_BUILDER_HEALTH_MULTIPLIER);
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
        
        boolean canSafelyFury = true;
        MapLocation nearestFriendlyArchon = nearestArchon(me);
        if (me.distanceSquaredTo(nearestFriendlyArchon) <= actionRadius){
            canSafelyFury = false;
            //TODO: check later if it's portable or not
        }
        //Checking if it's not worth to attack
        if (attackScore < 30 && chargeScore < 30 && furyScore < 30 && round > 100){
            return;
        }
        if (furyScore > chargeScore && furyScore > attackScore && canSafelyFury && rc.canEnvision(AnomalyType.FURY)){
            rc.envision(AnomalyType.FURY);
        }
        if (chargeScore > attackScore && rc.canEnvision(AnomalyType.CHARGE)){
            rc.envision(AnomalyType.CHARGE);
        }
        if (rc.canEnvision(AnomalyType.FURY) && canAttackArchon){
            //make sure we don't accidentally friendly fire on our archon
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
            if (rc.readSharedArray(49) < 65534) {
                target = Util.getLocationFromInt(rc.readSharedArray(49) % 10000);
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