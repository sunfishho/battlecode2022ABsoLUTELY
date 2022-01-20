
package first_bot;

import battlecode.common.*;

//Structure taken from XSquare's Youtube video
public class Soldier extends Unit {

    Pathfinding pf = new Pathfinding(this);


    public Soldier(RobotController rc, int r, MapLocation loc) throws GameActionException {
        super(rc, r, loc);
        //if no alarm:
        if (rc.readSharedArray(17) < 65534){
            target = Util.getLocationFromInt(rc.readSharedArray(17) % 10000);
        }
        else{
            target = chooseRandomInitialDestination();
        }
    }

    public void takeTurn() throws GameActionException {

        if (me.distanceSquaredTo(target) <= 2){
            target = chooseRandomInitialDestination();
        }

        //if there's an alarm for a specific location, set target to the alarm
        if (rc.readSharedArray(17) < 65534) {
            target = Util.getLocationFromInt(rc.readSharedArray(17) % 10000);
            targetCountdown = 0;
        }

        observe();
        observeSymmetry();
        takeAttendance();
        me = rc.getLocation();
        round = rc.getRoundNum();
        
        boolean needSoldierMicro = doSoldierMicro();
        // Figure out where to move if we don't need to fight
        if (!needSoldierMicro){
            Direction dir = pf.findBestDirection(target, 30);
            if (rc.canMove(dir)){
                rc.move(dir);
            }
        }

        // Find an enemy to attack
        RobotInfo[] enemies = rc.senseNearbyRobots(13, enemyTeam);
        int bestEnemyIndex = -1;
        for (int enemyIndex = 0; enemyIndex < enemies.length; enemyIndex++){
            if (rc.canAttack(enemies[enemyIndex].location) && (bestEnemyIndex == -1 || isBetterTargetThan(enemies[enemyIndex], enemies[bestEnemyIndex]))){
                bestEnemyIndex = enemyIndex;
            }
        }
        // Attack it if we can
        if (bestEnemyIndex != -1 && rc.canAttack(enemies[bestEnemyIndex].location)){
            rc.attack(enemies[bestEnemyIndex].location);
        }
        // Debugging indicator string
        rc.setIndicatorString(target.x + " " + target.y + ", " + needSoldierMicro);
        return;
    }

    // Compare bots to see which one we want to attack first
    public boolean isBetterTargetThan(RobotInfo bot1, RobotInfo bot2) throws GameActionException{
        //lower attack order -> better target
        if (Util.getAttackPref(bot1.type) < Util.getAttackPref(bot2.type)) return true;
        if (Util.getAttackPref(bot1.type) > Util.getAttackPref(bot2.type)) return false;
        //lower health -> better target
        if (bot1.health < bot2.health) return true;
        if (bot1.health > bot2.health) return false;
        //higher rubble -> better target
        if (rc.senseRubble(bot1.location) > rc.senseRubble(bot2.location)) return true;
        if (rc.senseRubble(bot1.location) < rc.senseRubble(bot2.location)) return false;
        //lower distance -> better target
        if (me.distanceSquaredTo(bot1.location) < me.distanceSquaredTo(bot2.location)) return true;
        if (me.distanceSquaredTo(bot1.location) > me.distanceSquaredTo(bot2.location)) return false;
        //otherwise tiebreak on ID
        return (bot1.ID <= bot2.ID);
    }

    // Returns true if soldier micro is needed
    public static boolean doSoldierMicro() throws GameActionException{
        
        SoldierMicroInfo[] soldierMicroInfo = new SoldierMicroInfo[9];
        for (int dirIdx = 0; dirIdx < 9; dirIdx++){
            //create a soldierMicroInfo for each possible direction the soldier can move in
            if (rc.canSenseLocation(me.add(Util.directions[dirIdx]))){
                soldierMicroInfo[dirIdx] = new SoldierMicroInfo(rc, me.add(Util.directions[dirIdx]), Util.directions[dirIdx], 20, target);   
            }
            else{
                soldierMicroInfo[dirIdx] = null;
            }
        }
        // Get all enemies
        RobotInfo[] enemies = rc.senseNearbyRobots(20, rc.getTeam().opponent());
        if (enemies.length == 0){
            return false;
        }
        
        // For each soldier micro, update minDistToEnemy
        for (int dirIdx = 0; dirIdx < 9; dirIdx++){
            if (soldierMicroInfo[dirIdx] != null){
                for (int enemyIndex = 0; enemyIndex < enemies.length; enemyIndex++){
                    soldierMicroInfo[dirIdx].update(enemies[enemyIndex]);   
                }
            }
        }
        
        int bestChoiceIndex = -1;
        double bestEval = -1000000;
        double curEval;
        for (int dirIdx = 0; dirIdx < 9; dirIdx++){
            if (rc.getID() == 11494 && me.equals(new MapLocation(12, 16))) System.out.println(soldierMicroInfo[dirIdx]);
            if (soldierMicroInfo[dirIdx] != null){
                if (rc.getID() == 11494 && me.equals(new MapLocation(12, 16))) System.out.println(dirIdx);
                if (rc.canMove(Util.directions[dirIdx]) || bestChoiceIndex < 0){
                    curEval = soldierMicroInfo[dirIdx].findEval();
                    if (rc.getID() == 11494 && me.equals(new MapLocation(12, 16))){
                        System.out.println(curEval + " " + Util.directions[dirIdx]);
                    }
                    if (curEval > bestEval){
                        bestEval = curEval;
                        bestChoiceIndex = dirIdx;
                    }
                }
            }
        }
        if (bestChoiceIndex != -1 && Util.directions[bestChoiceIndex] != Direction.CENTER){
            if (enemies.length > 0){
                if (Util.directions[bestChoiceIndex] == Direction.CENTER) {
                    return true;
                }
                if (rc.canMove(Util.directions[bestChoiceIndex])) rc.move(Util.directions[bestChoiceIndex]);
                return true;
            }
        }
        return false;
    }

    
    
}

class SoldierMicroInfo{
    double numEnemiesCanBeAttackedBy;
    Direction dirTraveled;
    int minDistToEnemy;
    int numPassiveEnemies = 0;
    MapLocation loc, target;
    RobotController rc;
    int rubbleLevel;
    int rubbleTolerance;
    int numTeammates;
    // add this to the eval if we're out of the enemies's attack radius but inside their vision radius
    final int BARELY_IN_VISION_BONUS = 500;
    final int ENEMY_PENALTY = 2000;
    final int TEAMMATE_MULTPLIER = 2000;
    final double NON_DIAGONAL_BONUS = 200;
    final int ABLE_TO_SHOOT_BONUS = 1000;
    final double ADDITIONAL_TARGET_PENALTY = 0.3;
    final int RUBBLE_PENALTY = 15000;
    //how much less scared we are of being in attack range of sages
    final double WATCHTOWER_MULTIPLIER = 1.5;
    final double SAGE_MULTIPLIER = 0.3;
    final int PASSIVE_ENEMY_BONUS = 500;
    final int ENEMY_DIST_PENALTY = 10;

    //note loc is NOT the location of rc, it is the hypothetical location we would move to
    //minDistToEnemy ideally is something between 13 and 20, we want to line up just outside of range
    public SoldierMicroInfo(RobotController rc, MapLocation loc, Direction dirTraveled, int rubbleTolerance, MapLocation target) throws GameActionException{
        this.rc = rc;
        this.loc = loc;
        minDistToEnemy = 100000;
        this.numEnemiesCanBeAttackedBy = 0;
        rubbleLevel = rc.senseRubble(loc);
        this.dirTraveled = dirTraveled;
        this.rubbleTolerance = rubbleTolerance;
        this.target = target;
    }

    

    //please only call this on enemy bots
    // Distance to enemy aggressive bots (only include those who can attack us right now)
    public void update(RobotInfo bot) throws GameActionException{
        MapLocation botLocation = bot.getLocation();
        switch (bot.getType()){
            case SOLDIER: 
                minDistToEnemy = Math.min(minDistToEnemy, loc.distanceSquaredTo(botLocation));
                //update numEnemies if the soldier can attack us
                if (loc.distanceSquaredTo(botLocation) <= 13){
                    numEnemiesCanBeAttackedBy += (10 / (10 + rc.senseRubble(botLocation)) * bot.health / 50 + ADDITIONAL_TARGET_PENALTY);
                }
            case WATCHTOWER:
                minDistToEnemy = Math.min(minDistToEnemy, loc.distanceSquaredTo(botLocation));
                if (loc.distanceSquaredTo(botLocation) <= 20){
                    numEnemiesCanBeAttackedBy += ((10 / (10 + rc.senseRubble(botLocation)) * bot.health / 50 + ADDITIONAL_TARGET_PENALTY) * WATCHTOWER_MULTIPLIER);
                }
            case SAGE:
                minDistToEnemy = Math.min(minDistToEnemy, loc.distanceSquaredTo(botLocation));
                if (loc.distanceSquaredTo(botLocation) <= 25){
                    numEnemiesCanBeAttackedBy += ((10 / (10 + rc.senseRubble(botLocation)) * bot.health / 50 + ADDITIONAL_TARGET_PENALTY) * SAGE_MULTIPLIER);
                }
            //otherwise it is a passive enemy, so we update the passive enemy counter if it is in range and we can attack
            default:
                if (loc.distanceSquaredTo(botLocation) <= 20){
                    numPassiveEnemies++;
                }
        }
        if (minDistToEnemy == 100000){
            minDistToEnemy = 0;
        }
    }
    /*
    List of considerations we should take into place:
    
    */


    //need to implement: add bonus to being on edge

    public double findEval() throws GameActionException{
        // System.out.println("Beginning of findEval: " + Clock.getBytecodesLeft());
        double eval = 0; // positive = better
        //if we're on a better rubble square than in the other scenario we should be happy
        eval -= RUBBLE_PENALTY * (rubbleLevel/10);
        double myWeight = 10 / (10 + rc.senseRubble(loc)) * rc.getHealth() / 50 + ADDITIONAL_TARGET_PENALTY;
        if (numEnemiesCanBeAttackedBy > 0){
            eval -= ENEMY_PENALTY * (numEnemiesCanBeAttackedBy - myWeight);
        }

        //if there are more teammates around me, go for it
        RobotInfo[] teammates = rc.senseNearbyRobots(13, rc.getTeam());
        for (int idx = 0; idx < teammates.length; idx++){
            if (teammates[idx].getType() == RobotType.SOLDIER || teammates[idx].getType() == RobotType.WATCHTOWER){
                eval += (10 / (10 + rc.senseRubble(teammates[idx].location)) * teammates[idx].health + ADDITIONAL_TARGET_PENALTY) * TEAMMATE_MULTPLIER;
            }
        }
        if (rc.getID() == 11494 && rc.getLocation().equals(new MapLocation(12, 16))) System.out.println("eval is " + eval + " after teammate/enemy considerations");
        //ideal location is between vision and action radius
        if (minDistToEnemy > 13 && minDistToEnemy < 20){
            eval += BARELY_IN_VISION_BONUS;
        }

        if (rc.getID() == 11494 && rc.getLocation().equals(new MapLocation(12, 16))) System.out.println("eval is " + eval + " after barely in vision bonus");

        

        //if we have some enemy that can attack us, add a penalty if we can't actually shoot when we get there
        // also, add a penalty for how far away this enemy is
        //if there is some passive enemy in view but no dangerous enemy, add a bonus
        if (numEnemiesCanBeAttackedBy == 0){
            eval += PASSIVE_ENEMY_BONUS;
        }
        else{
            eval -= ABLE_TO_SHOOT_BONUS * (rc.getActionCooldownTurns() / 10);
            eval -= ENEMY_DIST_PENALTY * minDistToEnemy;
        }

        if (rc.getID() == 11494 && rc.getLocation().equals(new MapLocation(12, 16))) System.out.println("eval is " + eval + " after passive_enemy_bonus, able_to_shoot bonus, distance penalty");


        //want to move closer to the target if we can
        eval -= rubbleTolerance * Util.distanceMetric(loc, target);

        if (rc.getID() == 11494 && rc.getLocation().equals(new MapLocation(12, 16))) System.out.println("eval is " + eval + " after distance penalty");

        // prefer not going diagonally, because if you go diagonally it's possible some soldier that didn't used to be in vision radius
        // is now within attack radius and will kill you
        switch (dirTraveled){
            case NORTH: eval += NON_DIAGONAL_BONUS;
            case EAST: eval += NON_DIAGONAL_BONUS;
            case SOUTH: eval += NON_DIAGONAL_BONUS;
            case WEST: eval += NON_DIAGONAL_BONUS;
            default: break;
        }

        if (rc.getID() == 11494 && rc.getLocation().equals(new MapLocation(12, 16))) System.out.println("eval is " + eval + " after nondiagonal bonus");

        return eval;
    }
    
    // public boolean isBetterThan(SoldierMicroInfo micro) throws GameActionException{
    //     double myEval = findEval();
    //     double microEval = micro.findEval();
    //     return myEval >= microEval;
    // }
}