
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
        else if (rc.readSharedArray(17) == 65534){
            target = chooseRandomInitialDestination();
            
        }
        else{
            target = chooseRandomInitialDestination();
            // target = Util.getLocationFromInt(rc.readSharedArray(21)/3 - 1);
        }
        //do more stuff later
    }

    public void takeTurn() throws GameActionException {
        //make sure to reset the location of the soldier
        me = rc.getLocation();
    }

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

        return (bot1.ID <= bot2.ID);
    }

    public static void doSoldierMicro() throws GameActionException{
        SoldierMicroInfo[] soldierMicroInfo = new SoldierMicroInfo[9];
        for (int dirIdx = 0; dirIdx < 9; dirIdx++){
            //create a soldierMicroInfo for each possible direction the soldier can move in
            soldierMicroInfo[dirIdx] = new SoldierMicroInfo(rc, me.add(Direction.values()[dirIdx]));

        }

        RobotInfo[] enemies = rc.senseNearbyRobots(20, rc.getTeam().opponent());
        for (int enemyIndex = 0; enemyIndex < enemies.length; enemyIndex++){
            for (int dirIdx = 0; dirIdx < 9; dirIdx++){
                soldierMicroInfo[dirIdx].update(enemies[enemyIndex]);
            }
        }
        int bestChoiceIndex = -1;
        for (int dirIdx = 0; dirIdx < 9; dirIdx++){
            if (rc.canMove(Direction.values()[dirIdx]) && (bestChoiceIndex < 0 || soldierMicroInfo[dirIdx].isBetterThan(soldierMicroInfo[bestChoiceIndex]))){
                bestChoiceIndex = dirIdx;
            }
        }
        if (bestChoiceIndex != -1 && Util.directions[bestChoiceIndex] != Direction.CENTER){
            rc.move(Direction.values()[bestChoiceIndex]);
            //put attacking code in here later
            return;
        }
    }

    
    
}

class SoldierMicroInfo{
    int numEnemies;
    int minDistToEnemy;
    MapLocation loc;
    RobotController rc;
    int rubbleLevel;

    //note loc is NOT the location of rc, it is the hypothetical location we would move to
    public SoldierMicroInfo(RobotController rc, MapLocation loc) throws GameActionException{
        this.rc = rc;
        this.loc = loc;
        minDistToEnemy = 100000;
        numEnemies = 0;
        rubbleLevel = rc.senseRubble(loc);
    }

    

    //please only call this on enemy bots
    public void update(RobotInfo bot){
        MapLocation botLocation = bot.getLocation();
        switch (bot.getType()){
            case SOLDIER: 
                if (botLocation.distanceSquaredTo(loc) <= 13){
                    minDistToEnemy = Math.min(minDistToEnemy, loc.distanceSquaredTo(botLocation));
                }
            case WATCHTOWER:
                if (botLocation.distanceSquaredTo(loc) <= 20){
                    minDistToEnemy = Math.min(minDistToEnemy, loc.distanceSquaredTo(botLocation));
                }
            case SAGE:
                if (botLocation.distanceSquaredTo(loc) <= 20){
                    minDistToEnemy = Math.min(minDistToEnemy, loc.distanceSquaredTo(botLocation));
                }
            default:
        }
    }
    /*
    List of considerations we should take into place:
    1. number of teammates/number of enemies
    2. rubble level
    3. distance to nearest enemy
    4. if enemy is getting healee
    */
    
    public boolean isBetterThan(SoldierMicroInfo micro){
        if (this.numEnemies < micro.numEnemies) return true;
        if (this.numEnemies > micro.numEnemies) return true;
        //if we're on a worse rubble square than in the other scenario we should be happy
        if (rubbleLevel/10 < micro.rubbleLevel/10) return true;
        if (rubbleLevel/10 > micro.rubbleLevel/10) return false;
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

        return (minDistToEnemy >= micro.minDistToEnemy);
    }
}