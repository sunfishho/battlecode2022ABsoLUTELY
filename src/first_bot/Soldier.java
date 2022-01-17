
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
                soldierMicroInfo[dirIdx] = new SoldierMicroInfo(rc, me.add(Util.directions[dirIdx]));   
            }
            else{
                soldierMicroInfo[dirIdx] = null;
            }
        }
        // Get all enemies
        RobotInfo[] enemies = rc.senseNearbyRobots(20, rc.getTeam().opponent());
        
        // For each soldier micro, update minDistToEnemy
        for (int dirIdx = 0; dirIdx < 9; dirIdx++){
            if (soldierMicroInfo[dirIdx] != null){
                for (int enemyIndex = 0; enemyIndex < enemies.length; enemyIndex++){
                    soldierMicroInfo[dirIdx].update(enemies[enemyIndex]);   
                }
            }
        }
        
        int bestChoiceIndex = -1;
        for (int dirIdx = 0; dirIdx < 9; dirIdx++){
            if (soldierMicroInfo[dirIdx] != null){
                if (rc.canMove(Util.directions[dirIdx]) && (bestChoiceIndex < 0 || soldierMicroInfo[dirIdx].isBetterThan(soldierMicroInfo[bestChoiceIndex]))){
                    bestChoiceIndex = dirIdx;
                }
            }
        }
        if (bestChoiceIndex != -1 && Util.directions[bestChoiceIndex] != Direction.CENTER){
            if (enemies.length > 0){
                if (Util.directions[bestChoiceIndex] == Direction.CENTER) {
                    return true;
                }
                rc.move(Util.directions[bestChoiceIndex]);
                return true;
            }
        }
        return false;
    }

    
    
}

class SoldierMicroInfo{
    int numEnemies, numTeammates;
    int minDistToEnemy;
    int minDistToPassiveEnemy;
    MapLocation loc;
    RobotController rc;
    int rubbleLevel;

    //note loc is NOT the location of rc, it is the hypothetical location we would move to
    public SoldierMicroInfo(RobotController rc, MapLocation loc) throws GameActionException{
        this.rc = rc;
        this.loc = loc;
        minDistToEnemy = 100000;
        minDistToPassiveEnemy = 100000;

        numTeammates = rc.senseNearbyRobots(loc, 13, rc.getTeam()).length;
        rubbleLevel = rc.senseRubble(loc);
    }

    

    //please only call this on enemy bots
    // Distance to enemy aggressive bots (only include those who can attack us right now)
    public void update(RobotInfo bot){
        MapLocation botLocation = bot.getLocation();
        switch (bot.getType()){
            case SOLDIER: 
                if (botLocation.distanceSquaredTo(loc) <= 13){
                    minDistToEnemy = Math.min(minDistToEnemy, Util.distanceMetric(loc,botLocation));
                    numEnemies++;
                }
            case WATCHTOWER:
                if (botLocation.distanceSquaredTo(loc) <= 20){
                    minDistToEnemy = Math.min(minDistToEnemy, Util.distanceMetric(loc,botLocation));
                    numEnemies++;
                }
            case SAGE:
                if (botLocation.distanceSquaredTo(loc) <= 20){
                    minDistToEnemy = Math.min(minDistToEnemy, Util.distanceMetric(loc,botLocation));
                    numEnemies++;
                }
            case MINER:
                minDistToPassiveEnemy = Math.min(minDistToPassiveEnemy, Util.distanceMetric(loc,botLocation));
            case ARCHON:
                minDistToPassiveEnemy = Math.min(minDistToPassiveEnemy, Util.distanceMetric(loc,botLocation));
            case LABORATORY:
                minDistToPassiveEnemy = Math.min(minDistToPassiveEnemy, Util.distanceMetric(loc,botLocation));
            case BUILDER:
                minDistToPassiveEnemy = Math.min(minDistToPassiveEnemy, Util.distanceMetric(loc,botLocation));
            default:
        }
    }
    /*
    List of considerations we should take into place:
    1. number of enemies
    2. rubble level
    2. number of teammates
    3. distance to nearest enemy
    4. if enemy is getting healed maybe idk
    */
    
    public boolean isBetterThan(SoldierMicroInfo micro){
        int metric = 0; // positive = better
        //if we're on a better rubble square than in the other scenario we should be happy
        metric -= 1000 * (rubbleLevel/10);
        metric += 1000 * (micro.rubbleLevel/10);
        
        //if there are more teammates there go there
        metric += 500 * numTeammates;
        metric -= 500 * micro.numTeammates;
        metric += 300 * numEnemies;
        metric -= 300 * micro.numEnemies;

        if (this.numEnemies == 0 && micro.numEnemies == 0) {
            
            metric += 600 * minDistToPassiveEnemy;
            metric -= 600 * minDistToPassiveEnemy;
        }
        
        if (metric > 0) {
            return true;
        }
        if (metric < 0) {
            return false;
        }
        
        if (rc.isActionReady()){
            if (!micro.rc.isActionReady()){
                //if this can attack but micro can't
                return true;
            }
            else{
                //if both can attack
                return (minDistToEnemy >= micro.minDistToEnemy);
            }
        }
        //if micro can attack but we can't
        if (micro.rc.isActionReady()){
            return false;
        }
        //prefer to have soldiers farther away
        return (minDistToEnemy >= micro.minDistToEnemy);
    }
}