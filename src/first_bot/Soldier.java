
package first_bot;

import battlecode.common.*;


public class Soldier extends RobotCommon{

    static int type;//0 = aggressive, 1 = defensive, 2 = escort?
    static MapLocation target;  
    static boolean onOffense, onDefense;
    static RobotInfo[] nearbyBotsSeen, enemyBotsWithinRange;
    Pathfinding pf = new Pathfinding(this);
    static double teammateSoldiers, enemySoldiers, numEnemies;
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
        //do more stuff later
    }

    public void takeTurn() throws GameActionException {
        me = rc.getLocation();
        round = rc.getRoundNum();
        if (me.equals(target)){
            target = chooseRandomInitialDestination();
        }
        // Try to attack someone
        nearbyBotsSeen = rc.senseNearbyRobots(visionRadius);
        enemyBotsWithinRange = rc.senseNearbyRobots(actionRadius, enemyTeam);
        //reset the onOffense, onDefense flags
        onOffense = false;
        onDefense = false;
        observe();
        observeSymmetry();
        teammateSoldiers = 0;
        enemySoldiers = 0;
        numEnemies = 0;
        double enemySoldierCentroidx = 0;
        double enemySoldierCentroidy = 0;
        for (RobotInfo bot : nearbyBotsSeen){
            switch(bot.getType()){
                case SOLDIER:
                    double weight = (bot.getHealth()/3 + 5) / (10 + rc.senseRubble(bot.getLocation()) / 10.0) + 0.1;
                    if (bot.getTeam() == myTeam){
                        teammateSoldiers += weight;
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
        if (enemySoldiers < 0.000001){
            tryToMove(30);
            moveLowerRubble(false);
            MapLocation enemy = attackValuableEnemies(false);
            if (enemy != null){
                rc.attack(enemy);
            }
            return;
        }
        enemySoldierCentroidx /= numEnemies;
        enemySoldierCentroidy /= numEnemies;
        enemySoldierCentroid = enemySoldierCentroid.translate((int) enemySoldierCentroidx - enemySoldierCentroid.x, (int) enemySoldierCentroidy - enemySoldierCentroid.y);
        
        // This whole block only runs if we have an enemy in sight
        tryToAttackAndMove();
        // rc.setIndicatorString(teammateSoldiers + " " + enemySoldiers + " " + onOffense + " " + onDefense);
        rc.setIndicatorString("target: " + target.x + ", " + target.y);
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
                if (rc.canMove(dir) && dir != Direction.CENTER){
                    rc.move(dir);
                    me = rc.getLocation();
                }
            }
            else if (rc.senseRubble(me.add(dir)) / 10 + 2 < rc.senseRubble(me) / 10 && me.add(dir).distanceSquaredTo(enemy) <= 13){
                if (rc.canMove(dir) && dir != Direction.CENTER){
                    rc.move(dir);
                    me = rc.getLocation();
                }
                //in case we have a new best enemy to attack
                enemy = attackValuableEnemies(false);
                if (enemy != null){
                    rc.attack(enemy);
                }                
            }
            else{
                if (enemy != null){
                    rc.attack(enemy);
                }
                if (rc.canMove(dir) && dir != Direction.CENTER){
                    rc.move(dir);
                    me = rc.getLocation();
                }
            }
        }
    }

    // Observes if any enemy units nearby
    public void observe() throws GameActionException {
        for (RobotInfo robot: rc.senseNearbyRobots()) {
            if (robot.getTeam() != myTeam){
                switch (robot.getType()){
                    case MINER: continue;
                    case ARCHON: 
                        rc.writeSharedArray(22, Util.getIntFromLocation(robot.getLocation()));
                        rc.writeSharedArray(17, Util.getIntFromLocation( robot.location) + 10000 * rankOfNearestArchon(robot.getLocation()));
                        rc.writeSharedArray(18, round);
                        break;
                    default:
                        rc.writeSharedArray(17, Util.getIntFromLocation( robot.location) + 10000 * rankOfNearestArchon(robot.getLocation()));
                        rc.writeSharedArray(18, round);
                        return;
                }
            }
        }
    }

    //Tries to run away. First checks the point opposite of enemy centroid, and if that's not on the map, try going to your archon.
    //Have really low rubble passability because you want to be on a low rubble square when you attack/move.
    //Returns the direction one should retreat in
    public Direction retreat(MapLocation enemyCentroid) throws GameActionException{
        rc.setIndicatorString("RETREATING: " + enemyCentroid.x + ", " + enemyCentroid.y);
        int reflectionX = me.x * 2 - enemyCentroid.x;
        int reflectionY = me.y * 2 - enemyCentroid.y;
        if (reflectionX >= 0 && reflectionX < Util.WIDTH && reflectionY >= 0 && reflectionY < Util.HEIGHT){
            target = me.translate(reflectionX - me.x, reflectionY - me.y);
        }
        else{
            target = nearestArchon(me);
        }

        Direction dir = pf.findBestDirection(target, 30);
        //check if we can move and that we're not going onto a horrible square
        //also, if there's some alternative direction that gets us onto a much better square, take it
        // int bestDistance = 0;
        // Direction dirBest = dir;
        // for (Direction dirAlt : Util.directions){
        //     if (rc.canSenseLocation(me.add(dirAlt)) && rc.senseRubble(me.add(dir))/10 - rc.senseRubble(me.add(dirAlt))/10 > 2 && rc.canMove(dirAlt)){
        //         if (bestDistance < Util.distanceMetric(me.add(dirAlt), enemyCentroid)){
        //             bestDistance = 
        //         }
        //     }
        // }
        if (rc.senseRubble(me.add(dir))/10 - rc.senseRubble(me)/10 > 3){
            return findDirectionLowerRubbleSquare(true);
        }
        else{
            if (rc.canMove(dir)){
                rc.setIndicatorLine(me, me.add(dir), 0, 100, 0);
                return dir;
            }
        }
        return Direction.CENTER;
    }

    //logic can be improved
    public void oneOnOneCombat(RobotInfo soldier) throws GameActionException{
        //move first so cooldown is less when you attack
        //go to your archon if your health is lower than that of your opponent's
        Direction dir = findDirectionLowerRubbleSquare(rc.getHealth() <= soldier.health);
        MapLocation enemy = attackValuableEnemies(false);
        if (enemy == null){
            if (dir != Direction.CENTER){
                rc.move(dir);
                me = rc.getLocation();
            }
            else{
                return;
            }
            enemy = attackValuableEnemies(false);
            if (enemy != null){
                rc.attack(enemy);
            }
            return;
        }
        if (me.add(dir).distanceSquaredTo(enemy) <= 13){
            if (dir != Direction.CENTER){
                rc.move(dir);
                me = rc.getLocation();
            }
            enemy = attackValuableEnemies(false);
            if (enemy != null){
                rc.attack(enemy);
            }
        }
        else{
            if (enemy != null){
                rc.attack(enemy);
            }
            if (dir != Direction.CENTER){
                rc.move(dir);
                me = rc.getLocation();
            }
        }
    }

    //For when you have more than one teammate
    public void groupCombat(double teammateSoldiers, double enemySoldiers, MapLocation enemyCentroid) throws GameActionException{
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
            enemy = attackValuableEnemies(false);
            if (enemy != null){
                rc.attack(enemy);
            }
        }
        //when we have more soldiers
        if (pushing){    
            //not sure if it's better to move or attack first, i'm assuming moving first is better because of cooldown reasons
            enemy = attackValuableEnemies(true);
            //if no enemies, try to move to your destination
            //if sufficiently far we can probably traverse higher rubble without being too scared
            if (enemy == null && me.distanceSquaredTo(target) >= 40){
                tryToMove(50);
                return;
            }
            //if not sufficiently far then we should be cautious
            else if (enemy == null){
                tryToMove(30);
                return;
            }
            dir = findDirectionLowerRubbleSquare(false);
            if (me.add(dir).distanceSquaredTo(enemy) <= 13){
                if (dir != Direction.CENTER){
                    rc.move(dir);
                    me = rc.getLocation();
                }
                enemy = attackValuableEnemies(false);
                if (enemy != null){
                    rc.attack(enemy);
                }
            }
            else{
                enemy = attackValuableEnemies(false);
                if (enemy != null){
                    rc.attack(enemy);
                }
                if (dir != Direction.CENTER){
                    rc.move(dir);
                    me = rc.getLocation();
                }
            }
        }
        //this is if we have fewer soldiers than the opponent does
        else{
            enemy = attackValuableEnemies(false);
            dir = retreat(enemySoldierCentroid);
            //if stepping onto rubble is better for cooldown, do that
            if (enemy == null){
                if (rc.canMove(dir) && dir != Direction.CENTER){
                    rc.move(dir);
                    me = rc.getLocation();
                }
            }
            else if (rc.senseRubble(me.add(dir)) / 10 + 2 < rc.senseRubble(me) / 10 && me.add(dir).distanceSquaredTo(enemy) <= 13){

                if (rc.canMove(dir) && dir != Direction.CENTER){
                    rc.move(dir);
                    me = rc.getLocation();
                }
                //in case we have a new best enemy to attack
                enemy = attackValuableEnemies(false);
                if (enemy != null){
                    rc.attack(enemy);
                }
            }
            else{
                if (enemy != null){
                    rc.attack(enemy);
                }
                if (rc.canMove(dir) && dir != Direction.CENTER){
                    rc.move(dir);
                    me = rc.getLocation();
                }
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
            if (bot.getTeam() == myTeam){
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
            return target;
        }
        if (rc.canAttack(bestBot.getLocation())) {
            target = bestBot.getLocation();
            return target;
        }
        return null;
    }

    //use this when you are certain that we actually want to move
    public void moveLowerRubble(boolean toRetreat) throws GameActionException{
        rc.setIndicatorString("MOVING TO LOWER RUBBLE, target = " + target);
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
            me = rc.getLocation();
        }
    }

    //use this when you want to return the best direction to a lower rubble square but are unsure about whether you
    //want to move there just yet
    public Direction findDirectionLowerRubbleSquare(boolean toRetreat) throws GameActionException{
        rc.setIndicatorString("MOVING TO LOWER RUBBLE, target = " + target);
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
        return bestDir;
    }

    //note: maybe should order based on distance to Archon if it's a defensive soldier.
    public void tryToMove(int avgRubble) throws GameActionException {
        rc.setIndicatorString("trying to move: " + target);
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
            dir = pf.findBestDirection(target, avgRubble);
        }
        if (rc.canMove(dir)){
            rc.move(dir);
            me = rc.getLocation();
        }

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
}