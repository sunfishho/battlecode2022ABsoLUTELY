
package first_bot;

import battlecode.common.*;


public class Soldier extends RobotCommon{

    static int type;//0 = aggressive, 1 = defensive, 2 = escort?
    static int movesSinceAction;
    static MapLocation target;  
    static boolean onOffense, onDefense;
    static RobotInfo[] nearbyBotsSeen, enemyBotsWithinRange;
    Pathfinding pf = new Pathfinding(this);
    static int teammateSoldiers, enemySoldiers;


    public Soldier(RobotController rc, int r, MapLocation loc) throws GameActionException {
        super(rc, r, loc);
        target = chooseRandomInitialDestination();
        movesSinceAction = 0;
        //do more stuff later
    }

    public void takeTurn() throws GameActionException {
        me = rc.getLocation();
        // Try to attack someone
        nearbyBotsSeen = rc.senseNearbyRobots(visionRadius);
        enemyBotsWithinRange = rc.senseNearbyRobots(actionRadius, enemyTeam);
        observe();
        // This whole block only runs if we have an enemy in sight
        tryToAttackAndMove();
        round++;
    }
    //right now this only deals with soldier skirmishes + archon stuff
    public void tryToAttackAndMove() throws GameActionException{
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
                default:
            }
        }
        if (enemySoldiers == 0){
            tryToMove();
            attackValuableEnemies();
            return;
        }
        enemySoldierCentroidx /= enemySoldiers;
        enemySoldierCentroidy /= enemySoldiers;
        if (enemySoldiers == 1 && teammateSoldiers == 0){
            rc.setIndicatorString("1 on 1 combat !");
            oneOnOneCombat();
            return;
        }
        else if (teammateSoldiers >= 1){
            rc.setIndicatorString("Group combat !");
            groupCombat(teammateSoldiers, enemySoldiers, new MapLocation(enemySoldierCentroidx, enemySoldierCentroidy));
            return;
        }
        else{
            attackValuableEnemies();
            retreat(new MapLocation(enemySoldierCentroidx, enemySoldierCentroidy));
        }
    }

    // Observes if any enemy units nearby
    public void observe() throws GameActionException {
        for (RobotInfo robot : rc.senseNearbyRobots()) {
            if (robot.getTeam() != rc.getTeam() && robot.getType() != RobotType.MINER) {
                rc.writeSharedArray(17, Util.getIntFromLocation( robot.location) + 10000 * rank);
                rc.writeSharedArray(18, round);
                return;
            }
        }
    }

    //Tries to run away. First checks the point opposite of enemy centroid, and if that's not on the map, try going to your archon.
    //Have really low rubble passability because you want to be on a low rubble square when you attack/move.
    //Actually moves, doesn't just reset target.
    public void retreat(MapLocation enemyCentroid) throws GameActionException{
        int reflectionX = me.x * 2 + enemyCentroid.x;
        int reflectionY = me.y * 2 + enemyCentroid.y;
        if (reflectionX >= 0 && reflectionX <= Util.WIDTH && reflectionY >= 0 && reflectionY <= Util.HEIGHT){
            target = me.translate(reflectionX - me.x, reflectionY - me.y);
        }
        else{
            target = archonLocation;
        }

        Direction dir = pf.findBestDirection(target, 10);
        if (rc.canMove(dir)){
            rc.move(dir);
        }
    }

    public void oneOnOneCombat(){
        if (onOffense)
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
            }
            //Here, we probably shouldn't go for the Archon, so we should run.
            else{
                //remember, if running away, ATTACK FIRST and THEN run on a turn
                attackValuableEnemies();
                retreat(enemyCentroid);
            }
        }
        else if (onDefense){
            //get on low rubble ground
            //if we're defending, we can't go backwards because we're defending our archon, so we should
            //just look for a lower rubble square.
            moveLowerRubble();
            attackValuableEnemies();
        }
        else if (teammateSoldiers <= enemySoldiers){
            attackValuableEnemies();
            retreat(enemyCentroid);
        }
        else{
            assert(teammateSoldiers > enemySoldiers);
            moveLowerRubble();
            attackValuableEnemies();
        }
    }

    //Find the enemy highest on our priority list within range and attack
    public void attackValuableEnemies() throws GameActionException{
        int bestType = 10;
        int lowestHealth = 100000;;
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
                rc.attack(target);
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
                lowestHealth = bot.getHealth();
                bestBot = bot;
                break;
            }
            // Tiebreak by enemy health
            int health = bot.getHealth();
            if (bestType == enemyType && health < lowestHealth) {
                lowestHealth = health;
                bestBot = bot;
            }
        }
        //Attack if possible
        if (rc.canAttack(bestBot.getLocation())) {
            rc.attack(bestBot.getLocation());
            movesSinceAction = 0;
            round++;
            return;
        }
    }

    public void moveLowerRubble() throws GameActionException{
        int bestRubble = rc.senseRubble(me);
        Direction bestDir = Direction.CENTER;
        for (Direction dir: Util.directions){
            if (rc.canMove(dir) && rc.senseRubble(me.add(dir)) < bestRubble){
                bestDir = dir;
                bestRubble = rc.senseRubble(me.add(dir));
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
        }else if (this.me.equals(target)){
            target = chooseRandomInitialDestination();
        }
        else if (target == null){
            target = chooseRandomInitialDestination();
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