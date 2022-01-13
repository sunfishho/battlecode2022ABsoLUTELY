
package bot0113;

import battlecode.common.*;


public class Soldier extends RobotCommon{

    static int type;//0 = aggressive, 1 = defensive, 2 = escort?
    static int movesSinceAction;
    static MapLocation target;  
    static boolean onOffense, onDefense;
    static RobotInfo[] nearbyBotsSeen, enemyBotsWithinRange;
    Pathfinding pf = new Pathfinding(this);
    static int teammateSoldiers, enemySoldiers;
    static MapLocation enemySoldierCentroid = new MapLocation(0, 0);


    public Soldier(RobotController rc, int r, MapLocation loc) throws GameActionException {
        super(rc, r, loc);
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
        movesSinceAction = 0;
        //do more stuff later
    }

    public void takeTurn() throws GameActionException {
        me = rc.getLocation();
        // Try to attack someone
        nearbyBotsSeen = rc.senseNearbyRobots(visionRadius);
        enemyBotsWithinRange = rc.senseNearbyRobots(actionRadius, enemyTeam);
        //reset the onOffense, onDefense flags
        onOffense = false;
        onDefense = false;
        observe();
        teammateSoldiers = 0;
        enemySoldiers = 0;
        int enemySoldierCentroidx = 0;
        int enemySoldierCentroidy = 0;
        for (RobotInfo bot : nearbyBotsSeen){
            switch(bot.getType()){
                case SOLDIER:
                    if (bot.getTeam() == myTeam){
                        teammateSoldiers++;
                    }
                    else{
                        enemySoldiers++;
                        MapLocation enemyLoc = bot.getLocation();
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
        if (enemySoldiers == 0){
            tryToMove();
            moveLowerRubble(false);
            attackValuableEnemies();
            return;
        }
        enemySoldierCentroidx /= enemySoldiers;
        enemySoldierCentroidy /= enemySoldiers;
        enemySoldierCentroid = enemySoldierCentroid.translate(enemySoldierCentroidx - enemySoldierCentroid.x, enemySoldierCentroidy - enemySoldierCentroid.y);
        
        // This whole block only runs if we have an enemy in sight
        tryToAttackAndMove();
        // rc.setIndicatorString(teammateSoldiers + " " + enemySoldiers + " " + onOffense + " " + onDefense);
        round++;
        rc.setIndicatorString("target: " + target.x + ", " + target.y);
    }
    //right now this only deals with soldier skirmishes + archon stuff
    public void tryToAttackAndMove() throws GameActionException{
        rc.setIndicatorString(teammateSoldiers + " " + enemySoldiers + " " + onOffense + " " + onDefense);
        if (enemySoldiers == 1 && teammateSoldiers == 0){
            rc.setIndicatorString("1 on 1 combat !");
            for (RobotInfo bot: nearbyBotsSeen){
                if (bot.getType() == RobotType.SOLDIER){
                    oneOnOneCombat(bot);
                    break;
                }
            }
            return;
        }
        else if (teammateSoldiers >= 1){
            rc.setIndicatorString("Group combat !");
            groupCombat(teammateSoldiers, enemySoldiers, enemySoldierCentroid);
            return;
        }
        else{
            onOffense = false;
            attackValuableEnemies();
            retreat(enemySoldierCentroid);
        }
    }

    // Observes if any enemy units nearby
    public void observe() throws GameActionException {
        for (RobotInfo robot : rc.senseNearbyRobots()) {
            if (robot.getTeam() != rc.getTeam() && robot.getType() != RobotType.MINER) {
                int rankClosest = rankOfNearestArchon(robot.getLocation());
                rc.writeSharedArray(17, Util.getIntFromLocation( robot.location) + 10000 * rankClosest);
                rc.writeSharedArray(18, round);
                return;
            }
        }
    }

    //Tries to run away. First checks the point opposite of enemy centroid, and if that's not on the map, try going to your archon.
    //Have really low rubble passability because you want to be on a low rubble square when you attack/move.
    //Actually moves, doesn't just reset target.
    public void retreat(MapLocation enemyCentroid) throws GameActionException{
        rc.setIndicatorString("RETREATING: " + enemyCentroid.x + ", " + enemyCentroid.y);
        int reflectionX = me.x * 2 - enemyCentroid.x;
        int reflectionY = me.y * 2 - enemyCentroid.y;
        if (reflectionX >= 0 && reflectionX < Util.WIDTH && reflectionY >= 0 && reflectionY < Util.HEIGHT){
            target = me.translate(reflectionX - me.x, reflectionY - me.y);
        }
        else{
            target = nearestArchon(me);
        }

        Direction dir = pf.findBestDirection(target, 10);
        //check if we can move and that we're not going onto a horrible square
        if (rc.senseRubble(me.add(dir))/10 - rc.senseRubble(me)/10 > 3){
            moveLowerRubble(true);
            return;
        }
        else{
            if (rc.canMove(dir)){
                rc.setIndicatorLine(me, me.add(dir), 0, 100, 0);
                rc.move(dir);
            }
        }
    }

    public void oneOnOneCombat(RobotInfo soldier) throws GameActionException{
        //move first so cooldown is less when you attack
        //go to your archon if your health is lower than that of your opponent's
        moveLowerRubble(rc.getHealth() <= soldier.health);
        attackValuableEnemies();
        return;
    }

    //For when you have more than one teammate
    public void groupCombat(int teammateSoldiers, int enemySoldiers, MapLocation enemyCentroid) throws GameActionException{
        //average number of hits expected when rushing an archon before perishing
        int AVG_HITS_EXPECTED_WHEN_ATACKING = 3;
        //this is the case where we're attacking an archon but we're down in numbers
        //if the archon is immediately killable, go for it, otherwise, run for your lives
        if (onOffense){
            if (teammateSoldiers < enemySoldiers){
                int minArchonHealth = 100000;
                MapLocation archonTargetLocation = rc.getLocation();
                for (RobotInfo bot: nearbyBotsSeen){
                    if (bot.getType() == RobotType.ARCHON && bot.getTeam() == rc.getTeam().opponent() && rc.canAttack(bot.getLocation())){
                        if (minArchonHealth > bot.getHealth()){
                            archonTargetLocation = bot.getLocation();
                        }
                    }
                }
                //Checking if we can land a fatal blow to the enemy archon
                if (minArchonHealth < teammateSoldiers * 3 * AVG_HITS_EXPECTED_WHEN_ATACKING){
                    target = archonTargetLocation;
                    //swarm the archon
                    //tbh if it's 3 hits from dying (from each teammate) you don't need to care about rubble unless it's like 100 rubble
                    //and pathfinding should take care of this.
                    tryToMove();
                    if (rc.canAttack(target)){
                        rc.attack(target);
                    }
                    //if the archon isn't within range attack something else for now if you can still attack next turn
                    else if (rc.senseRubble(me) < 10){
                        attackValuableEnemies();
                    }
                    return;
                }
                //Here, we probably shouldn't go for the Archon, so we should run.
                else{
                    //remember, if running away, ATTACK FIRST and THEN run on a turn
                    attackValuableEnemies();
                    retreat(enemyCentroid);
                }
            }
            //this is if we have more soldiers than the opponent does
            else{
                moveLowerRubble(false);
                attackValuableEnemies();
            }
        }
        else if (onDefense){
            //get on low rubble ground
            //if we're defending, we can't go backwards because we're defending our archon, so we should
            //just look for a lower rubble square.
            //don't retreat if possible
            moveLowerRubble(false);
            attackValuableEnemies();
        }
        else if (teammateSoldiers < enemySoldiers){
            attackValuableEnemies();
            retreat(enemyCentroid);
        }
        else{
            assert(teammateSoldiers >= enemySoldiers);
            moveLowerRubble(false);
            attackValuableEnemies();
        }
    }

    //Find the enemy highest on our priority list within range and attack
    public void attackValuableEnemies() throws GameActionException{
        enemyBotsWithinRange = rc.senseNearbyRobots(actionRadius, enemyTeam);
        int bestType = 10;
        int highestRubble = 0;
        int lowestHealth = 100000;
        if (enemyBotsWithinRange.length == 0){
            return;
        }
        RobotInfo bestBot = enemyBotsWithinRange[0];
        // Go through list of enemies and find the one we want to attack the most
        for (RobotInfo bot: enemyBotsWithinRange) {
            if (bot.getTeam() == myTeam){
                continue;
            }
            if (bot.getType() == RobotType.ARCHON && bot.getHealth() < 3 * 3 * teammateSoldiers){
                target = bot.getLocation();
                if (rc.canAttack(target)){
                    rc.attack(target);
                }
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
        if (rc.canAttack(bestBot.getLocation())) {
            rc.attack(bestBot.getLocation());
            movesSinceAction = 0;
            round++;
            return;
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
            if (toRetreat && rc.senseRubble(me.add(dir))/10 == bestRubble/10 && rc.canMove(dir)){
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

    //note: maybe should order based on distance to Archon if it's a defensive soldier.
    public void tryToMove() throws GameActionException {
        if (rc.readSharedArray(17) != 65535) {
            target = Util.getLocationFromInt(rc.readSharedArray(17) % 10000);
        }
        else if (target == null){
            target = chooseRandomInitialDestination();
        }
        else if ((rc.canSenseLocation(target) && rc.senseRubble(target) > 30) && !onOffense){
            target = chooseRandomInitialDestination();
        }
        if (me.distanceSquaredTo(target) <= 2 && rc.senseRobotAtLocation(target) != null) {
            return;
        }
        Direction dir = Direction.CENTER;
        if (target != null){
            dir = pf.findBestDirection(target, 20);
        }
        if (rc.canMove(dir)){
            rc.move(dir);
        }

    }
}