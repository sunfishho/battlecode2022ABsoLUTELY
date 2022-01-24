
package first_bot;

import battlecode.common.*;

public class Soldier extends Unit {

    static int type;//0 = aggressive, 1 = defensive, 2 = escort?
    static boolean onOffense, onDefense;
    static RobotInfo[] nearbyBotsSeen, enemyBotsWithinRange;
    static double teammateSoldiers, enemySoldiers;
    static int numEnemies, numTeammates;
    static int loopingIncrement = 1;//experiment w/ this maybe idk
    static int loopingPenalty;//increase rubble tolerance if we're stuck in a loop
    static MapLocation enemySoldierCentroid = new MapLocation(0, 0);
    static int health;
    static int mode; // 0 for normal, 1 for healing, 2 for patrolling, 3 for guarding lead deposit
    static int patrollingRounds;

    public Soldier(RobotController rc, int r, MapLocation loc) throws GameActionException {
        super(rc, r, loc);
        //if no alarm:
        if (rc.readSharedArray(49) < 65534){
            target = Util.getLocationFromInt(rc.readSharedArray(49) % 10000);
        }
        else if (rc.readSharedArray(49) == 65534){
            target = chooseRandomInitialDestination();
            
        }
        else{
            target = new MapLocation(Util.WIDTH / 2, Util.HEIGHT / 2);
            // target = Util.getLocationFromInt(rc.readSharedArray(53)/3 - 1);
        }
        health = 50;
        patrollingRounds = 0;
        mode = 0;
        if (Util.getLocationFromInt(rc.readSharedArray(45)).equals(rc.getLocation())){
            mode = 3;
            rc.writeSharedArray(45, 0);
        }
    }

    public void takeTurn() throws GameActionException {
        me = rc.getLocation();
        archonLocation = nearestArchon(me);
        // Update important fields
        if (mode == 2) {
            patrollingRounds++;
        }
        if (patrollingRounds > 100) {
            mode = 0;
            if (rc.getHealth() < 12) {
                isRetreating = true;
            }
        }
        if (rc.getHealth() > health && mode != 3) {
            mode = 1;
        }
        if (mode == 3){
            if (me.distanceSquaredTo(nearestArchon(me)) > 5){
                Direction dir = pf.findBestDirection(nearestArchon(me), 50);
                target = me.add(dir);
                if (rc.canMove(dir)){
                    rc.move(dir);
                }
            }
        }
        health = rc.getHealth();
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
        targetCountdown++;
        if (targetCountdown == 150){
            target = chooseRandomInitialDestination();
            targetCountdown = 0;
            loopingPenalty = 0;
        }
        takeAttendance();
        me = rc.getLocation();
        round = rc.getRoundNum();
        if (me.equals(target)){
            target = chooseRandomInitialDestination();
            targetCountdown = 0;
        }
        // Try to attack someone
        nearbyBotsSeen = rc.senseNearbyRobots(visionRadius);
        enemyBotsWithinRange = rc.senseNearbyRobots(actionRadius, enemyTeam);
        // If previously not on offense and low health set target to nearest archon
        if (rc.getHealth() < 12 && mode != 2) {
            if (!isRetreating) {
                // reset recentdists
                recentDists = new int[] {200, 200, 200, 200, 200, 200, 200, 200};
                isRetreating = true;
            }
            target = archonLocation;
        }
        //reset the onOffense, onDefense flags
        onOffense = false;
        onDefense = false;
        observe();
        observeSymmetry();
        teammateSoldiers = 0;
        enemySoldiers = 0;
        numEnemies = 0;
        numTeammates = 0;
        double enemySoldierCentroidx = 0;
        double enemySoldierCentroidy = 0;
        for (RobotInfo bot : nearbyBotsSeen){
            switch(bot.getType()){
                case SOLDIER:
                    double weight = (bot.getHealth()/3 + 5) / (10 + rc.senseRubble(bot.getLocation()) / 10.0) + 0.1;
                    if (bot.getTeam() == myTeam){
                        teammateSoldiers += weight;
                        numTeammates++;
                    }
                    else{
                        enemySoldiers += weight;
                        MapLocation enemyLoc = bot.getLocation();
                        numEnemies++;
                        enemySoldierCentroidy += enemyLoc.y;
                        enemySoldierCentroidx += enemyLoc.x;
                    }
                    break;
                case WATCHTOWER:
                    if (bot.getMode() == RobotMode.PORTABLE){
                        continue;
                    }
                    weight = (1.1 * bot.getHealth()/3 + 5) / (10 + rc.senseRubble(bot.getLocation()) / 10.0) + 0.1;
                    if (bot.getTeam() == myTeam){
                        teammateSoldiers += weight;
                        numTeammates++;
                    }
                    else{
                        enemySoldiers += weight;
                        MapLocation enemyLoc = bot.getLocation();
                        numEnemies++;
                        enemySoldierCentroidy += enemyLoc.y;
                        enemySoldierCentroidx += enemyLoc.x;
                    }
                    break;
                case ARCHON:
                    if (bot.getTeam() == myTeam){
                        onDefense = true;
                    }
                    else{
                        onOffense = true;
                    }
                    break;
                case MINER:
                    if (bot.getTeam() != myTeam){
                        onOffense = true;
                    }
                    break;
                default:
            }
        }
        if (shouldSendMinerHere(isRetreating, numTeammates, numEnemies)){
            rc.writeSharedArray(57, Util.getIntFromLocation(me));
        }
        if (isRetreating) {
            attack();
            target = archonLocation;
            if (me.distanceSquaredTo(archonLocation) > 13) {
                tryToMove(30 + 5 * loopingPenalty);
                int crowdCount = 0;
                for (RobotInfo robot : rc.senseNearbyRobots(20, rc.getTeam())) {
                    if (robot.getLocation().distanceSquaredTo(target) <= 20 && robot.getMode() == RobotMode.DROID && robot.getHealth() < robot.getType().health - 10) {
                        crowdCount++;
                    }
                }

                if (mode != 1 && crowdCount > 3) {
                    if (crowdCount > 5){
                        System.out.println("Disintegrating myself");
                        if (rc.senseLead(me) == 0){
                            rc.disintegrate();
                        }
                    }
                    // We can't get healed by the archon so patrol instead
                    mode = 2;
                    isRetreating = false;
                    target = chooseRandomInitialDestination();
                }
                moveLowerRubble(true);
            }
            if (rc.getHealth() > 45 && mode != 3) {
                isRetreating = false;
                mode = 0;                
                target = chooseRandomInitialDestination();
            } else {
                return;
            }
        }
        // Act normal

        if (enemySoldiers < 0.000001){
            if (goAroundNeighbors()){
                tryToMove(10);
            }
            tryToMove(30 + loopingPenalty);
            moveLowerRubble(false);
            attack();
            rc.setIndicatorString("target: " + target.x + ", " + target.y + "; " + isRetreating + ", " + mode);
            return;
        }
        enemySoldierCentroidx = (int) ((enemySoldierCentroidx / (numEnemies + 0.0)) + 0.5);
        enemySoldierCentroidy = (int) ((enemySoldierCentroidy / (numEnemies + 0.0)) + 0.5);
        enemySoldierCentroid = enemySoldierCentroid.translate((int) enemySoldierCentroidx - enemySoldierCentroid.x, (int) enemySoldierCentroidy - enemySoldierCentroid.y);
        
        // This whole block only runs if we have an enemy in sight
        tryToAttackAndMove();
        // rc.setIndicatorString(teammateSoldiers + " " + enemySoldiers + " " + onOffense + " " + onDefense);
        rc.setIndicatorString("target: " + target.x + ", " + target.y + "; " + isRetreating + ", " + mode);
    }
    //right now this only deals with soldier skirmishes + archon stuff
    public void tryToAttackAndMove() throws GameActionException{
        
        rc.setIndicatorString(teammateSoldiers + " " + enemySoldiers + " " + onOffense + " " + onDefense);
        //1 on 1 combat
        if (numEnemies == 1 && teammateSoldiers < 0.000001){
            rc.setIndicatorString("1 on 1 combat !");
            for (RobotInfo bot: nearbyBotsSeen){
                if (bot.getType() == RobotType.SOLDIER){
                    oneOnOneCombat(bot);
                    break;
                }
            }
            return;
        }
        //if we have a teammate
        else if (teammateSoldiers > 0.000001){
            rc.setIndicatorString("Group combat !");
            groupCombat(teammateSoldiers, enemySoldiers, enemySoldierCentroid);
            return;
        }
        //it's 1vn, where n>1
        else{
            onOffense = false;
            MapLocation enemy = attackValuableEnemies(false);
            Direction dir = retreat(enemySoldierCentroid);
            //if stepping onto rubble is better for cooldown, do that
            if (enemy == null){
                move(dir);
                attack();
            }
            else if (rc.senseRubble(me.add(dir)) / 10 + 2 < rc.senseRubble(me) / 10 && me.add(dir).distanceSquaredTo(enemy) <= 13){
                move(dir);
                //in case we have a new best enemy to attack
                attack();         
            }
            else{
                attack(enemy);
                move(dir);
            }
        }
    }

    

    //logic can be improved
    public void oneOnOneCombat(RobotInfo soldier) throws GameActionException{
        loopingPenalty = 0;
        //move first so cooldown is less when you attack
        //go to your archon if your health is lower than that of your opponent's
        Direction dir = findDirectionLowerRubbleSquare(rc.getHealth() <= soldier.health || me.distanceSquaredTo(archonLocation) <= 20);
        MapLocation enemy = soldier.location;
        if (soldier.health * (rc.senseRubble(me)/10 + 1) < rc.getHealth() * (rc.senseRubble(me)/10 + 1)) {
            target = enemy;
            tryToMove(20);
            attack();
            return;
        }
        if (enemy == null){
            move(dir);
            attack();
        } else if (me.add(dir).distanceSquaredTo(enemy) <= 13){
            attack();
            move(dir);
        }
        else{
            move(dir);
            attack();
        }
    }

    //For when you have more than one teammate
    public void groupCombat(double teammateSoldiers, double enemySoldiers, MapLocation enemyCentroid) throws GameActionException{
        loopingPenalty = 0;
        //average number of hits expected when rushing an archon before perishing
        // int AVG_HITS_EXPECTED_WHEN_ATACKING = 3;
        //this is the case where we're attacking an archon but we're down in numbers
        //if the archon is immediately killable, go for it, otherwise, run for your lives
        MapLocation enemy;
        Direction dir;
        boolean pushing = teammateSoldiers >= enemySoldiers;
        if (onDefense){
            //get on low rubble ground
            //if we're defending, we can't go backwards because we're defending our archon, so we should
            //just look for a lower rubble square.
            //don't retreat if possible
            //always good to do it in this order
            moveLowerRubble(false);
            attack();
        }
        //when we have more soldiers
        if (pushing){    
            //not sure if it's better to move or attack first, i'm assuming moving first is better because of cooldown reasons
            enemy = attackValuableEnemies(true);
            //if no enemies, try to move to your destination
            //if sufficiently far we can probably traverse higher rubble without being too scared
            if (enemy == null && me.distanceSquaredTo(target) >= 40){
                tryToMove(40 + loopingPenalty);
                return;
            }
            //if not sufficiently far then we should be cautious
            else if (enemy == null){
                tryToMove(20 + loopingPenalty);
                return;
            }
            dir = findDirectionLowerRubbleSquare(false);
            if (me.add(dir).distanceSquaredTo(enemy) <= 13){
                move(dir);
                attack();
            }
            else{
                attack();
                move(dir);
            }
        }
        //this is if we have fewer soldiers than the opponent does
        else{
            enemy = attackValuableEnemies(false);
            dir = retreat(enemySoldierCentroid);
            //if stepping onto rubble is better for cooldown, do that
            if (enemy == null){
                move(dir);
                attack();
            }
            else if (rc.senseRubble(me.add(dir)) / 10 < rc.senseRubble(me) / 10 && me.add(dir).distanceSquaredTo(enemy) <= 13){
                move(dir);
                //in case we have a new best enemy to attack
                attack();
            }
            else{
                attack(enemy);
                move(dir);
            }
        }
    }

    //Find the enemy highest on our priority list within range and return its square
    public MapLocation attackValuableEnemies(boolean inVisionNotAttack) throws GameActionException{
        if (inVisionNotAttack){
            enemyBotsWithinRange = rc.senseNearbyRobots(visionRadius, enemyTeam);
        }
        else{
            enemyBotsWithinRange = rc.senseNearbyRobots(actionRadius, enemyTeam);
        }
        int bestType = 10;
        int highestRubble = 0;
        int lowestHealth = 100000;
        if (enemyBotsWithinRange.length == 0){
            return null;
        }
        RobotInfo bestBot = enemyBotsWithinRange[0];
        // Go through list of enemies and find the one we want to attack the most
        for (RobotInfo bot: enemyBotsWithinRange) {
            // We only want patrols to fight miners
            if (bot.getTeam() == myTeam){
                continue;
            }
            if (mode == 2 && bot.getType() != RobotType.MINER) {
                continue;
            }
            // if (bot.getType() == RobotType.ARCHON && bot.getHealth() < 3 * 3 * teammateSoldiers){
            //     target = bot.getLocation();
            //     if (rc.canAttack(target)){
            //         rc.attack(target);
            //     }
            // }
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
                lowestHealth = bot.getHealth();
                bestBot = bot;
                continue;
            }
            else if (bestType == enemyType){
                if (highestRubble < rc.senseRubble(bot.getLocation())){
                    highestRubble = rc.senseRubble(bot.getLocation());
                    lowestHealth = bot.getHealth();
                    bestBot = bot;
                }
                else if (highestRubble == rc.senseRubble(bot.getLocation())){
                    if (lowestHealth < bot.getHealth()){
                        lowestHealth = bot.getHealth();
                        bestBot = bot;
                    }
                }
            }
            // Tiebreak by enemy health
        }
        //Attack if possible
        if (inVisionNotAttack){
            target = bestBot.getLocation();
            targetCountdown = 0;
            return target;
        }
        if (rc.canAttack(bestBot.getLocation())) {
            target = bestBot.getLocation();
            targetCountdown = 0;
            return target;
        }
        return null;
    }

    
    //note: maybe should order based on distance to Archon if it's a defensive soldier.
    // returns if move was made
    public boolean tryToMove(int avgRubble) throws GameActionException {
        rc.setIndicatorString("trying to move: " + target);
        if (!isRetreating) {
            if (rc.readSharedArray(49) < 65534 && mode != 2) {
                target = Util.getLocationFromInt(rc.readSharedArray(49) % 10000);
                targetCountdown = 0;
            }
            else if (target == null){
                target = chooseRandomInitialDestination();
                targetCountdown = 0;
            }
            else if ((rc.canSenseLocation(target) && rc.senseRubble(target) > avgRubble + 20) && !onOffense){
                target = chooseRandomInitialDestination();
                targetCountdown = 0;
            }
            if (me.distanceSquaredTo(target) <= 2 && rc.senseRobotAtLocation(target) != null) {
                return false;
            }
        }
        Direction dir = Direction.CENTER;
        if (target != null && rc.isMovementReady()){
            dir = pf.findBestDirection(target, avgRubble);
        }
        return move(dir);
    }

    //how many enemy soldiers can attack us if we move in direction dir?
    public int attackedBy(Direction dir) {
        MapLocation newloc = me.add(dir);
        int cnt = 0;
        if(Util.inGrid(newloc)){
            for(RobotInfo x : rc.senseNearbyRobots(20, rc.getTeam().opponent())){//vision range
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
    // Find and attack enemy if possible
    public void attack() throws GameActionException {
        MapLocation enemy = attackValuableEnemies(false);
        if (enemy != null){
            rc.attack(enemy);
            loopingPenalty = 0;
        }
    }
    // Attack enemy
    public void attack(MapLocation enemy) throws GameActionException {
        if (enemy != null){
            rc.attack(enemy);
            loopingPenalty = 0;
        }
    }
    // Move if possible
    public boolean move(Direction dir) throws GameActionException {
        if (rc.canMove(dir) && dir != Direction.CENTER){
            rc.move(dir);
            me = rc.getLocation();
            return true;
        }
        return false;
    }
}