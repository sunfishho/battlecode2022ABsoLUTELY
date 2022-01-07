
package first_bot;

import battlecode.common.*;


public class Soldier extends RobotCommon{

    static int type;//0 = aggressive, 1 = defensive, 2 = escort?
    public static int archonRank;
    static MapLocation archonLocation, target;
    static MapLocation initialDestination;
    static int movesSinceAction;  


    public Soldier(RobotController rc) throws GameActionException {
        super(rc); 
        type = 1;       // Default to defensive
        initialDestination = chooseRandomInitialDestination();
        movesSinceAction = 0;
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
        this.me = rc.getLocation();
        // Try to attack someone
        if (this.me.equals(initialDestination)){
            initialDestination = null;
        }
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        // This whole block only runs if we have an enemy in sight
        if (enemies.length > 0) {
            // Choose the enemy we want to attack
            int bestType = 10;
            int lowestHealth = 100000;
            int bestIndex = -1;
            // Go through list of enemies and find the one we want to attack the most
            for (int i = 0; i < enemies.length; i++) {
                int enemyType = 8;
                for (int j = 0; j < 7; j++) {
                    if (enemies[i].getType().equals(Util.attackOrder[j])) {
                        enemyType = j;
                        break;
                    }
                }
                if (enemyType < bestType) {
                    bestType = enemyType;
                    lowestHealth = 100000;
                }
                // Tiebreak by enemy health
                int health = enemies[i].getHealth();
                if (bestType == enemyType && health < lowestHealth) {
                    lowestHealth = health;
                    bestIndex = i;
                }
            }
            // Go to the enemy we want to attack and attack if possible
            MapLocation toAttack = enemies[bestIndex].location;
            if (rc.canAttack(toAttack)) {
                rc.attack(toAttack);
                movesSinceAction = 0;
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
        // If there's an enemy nearby target it
        // Initialize variables for targeting enemies
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
            // initialDestination = null;    
        }
        int newX = loc.x + dir.dx;
        int newY = loc.y + dir.dy;
        MapLocation newLoc = new MapLocation(newX, newY);
        if (rc.canMove(dir)) {
            rc.move(dir);
            movesSinceAction = 0;
        } else {
            movesSinceAction++;
            if (movesSinceAction > 5) {
                initialDestination = chooseRandomInitialDestination();
                movesSinceAction = 0;
            }
        }
    }
}