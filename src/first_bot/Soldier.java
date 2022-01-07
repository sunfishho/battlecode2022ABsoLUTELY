
package first_bot;

import battlecode.common.*;


public class Soldier extends RobotCommon{

    static int type;//0 = aggressive, 1 = defensive, 2 = escort?
    public static int archonRank;
    static MapLocation archonLocation, target;
    static MapLocation initialDestination;


    public Soldier(RobotController rc) throws GameActionException {
        super(rc); 
        initialDestination = chooseRandomInitialDestination();
        //find parent archon
        boolean foundArchon = false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if(foundArchon) break;
                MapLocation loc = new MapLocation(me.x + dx, me.y + dy);
                for(int i = 0; i < 4; i++) {
                    if(Util.getIntFromLocation(loc) == rc.readSharedArray(i)) {
                        archonRank = i + 1;
                        archonLocation = loc;
                        //target = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(archonRank)));
                        foundArchon = true;
                        break;
                    }
                }
            }
        }
        //do more stuff later
    }

    public void takeTurn() throws GameActionException {
        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) {
            // Choose the enemy we want to attack
            int bestType = 10;
            int lowestHealth = 100000;
            int bestIndex = -1;
            for (int i = 0; i < enemies.length; i++) {
                int type = -1;
                for (int j = 0; j < 7; j++) {
                    if (enemies[i].getType().equals(Util.attackOrder[j])) {
                        type = j;
                        break;
                    }
                }
                if (type < bestType) {
                    bestType = type;
                    lowestHealth = 100000;
                }
                // Tiebreak by enemy health
                int health = enemies[i].getHealth();
                if (bestType == type && health < lowestHealth) {
                    lowestHealth = health;
                    bestIndex = i;
                }
            }
            MapLocation toAttack = enemies[bestIndex].location;
            if (rc.canAttack(toAttack)) {
                rc.attack(toAttack);
                return;
            }
        }
        tryToMove();
    }
    //note: maybe should order based on distance to Archon if it's a defensive soldier.
    public void tryToMove() throws GameActionException {
        GreedyPathfinding gpf = new GreedyPathfinding(this);
        Direction dir = Direction.CENTER;
        if (initialDestination != null){
            dir = gpf.explore(initialDestination);
        }
        // Direction dir = Util.directions[rng.nextInt(Util.directions.length)];
        MapLocation loc = rc.getLocation();
        if(type == 0){//aggressive soldier, just go in general direction of closest enemy archon
            //fix this eventually
            int sym = 0;
            //int sym = rc.readSharedArray(1234);//fill with eventual location of symmetry in shared array
            switch(sym){
                case 0://no idea what symmetry is, so move randomly
                    break;
                case 1:
                    dir = loc.directionTo(RobotCommon.nearestEnemyArchon(loc, 1));
                    initialDestination = null;
                    break;
                case 2:
                    dir = loc.directionTo(RobotCommon.nearestEnemyArchon(loc, 2));
                    initialDestination = null;
                    break;
                default:
                    dir = loc.directionTo(RobotCommon.nearestEnemyArchon(loc, 3));
                    initialDestination = null;
                    break;
            }
        }
        int visionRadius = rc.getType().visionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(visionRadius, opponent);
        int lowestInOrder = 7;
        int lowestHealth = 10000;
        int visionTargetIdx = 0;
        if (enemies.length > 0) {
            for (int enemyIndex = enemies.length - 1; enemyIndex >= 0; enemyIndex--){
                if (lowestInOrder > Util.getAttackPref(enemies[enemyIndex].getType())){
                    lowestInOrder = Util.getAttackPref(enemies[enemyIndex].getType());
                    lowestHealth = enemies[enemyIndex].getHealth();
                    visionTargetIdx = enemyIndex;
                }
                else if (lowestInOrder == Util.getAttackPref(enemies[enemyIndex].getType())){
                    if (lowestHealth > enemies[enemyIndex].getHealth()){
                        lowestHealth = enemies[enemyIndex].getHealth();
                        visionTargetIdx = enemyIndex;
                    }
                }
            }
            MapLocation toFollow = enemies[visionTargetIdx].location;
            dir = gpf.explore(toFollow);
            initialDestination = null;    
        }
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}