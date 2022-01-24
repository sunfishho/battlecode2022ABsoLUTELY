
package bot0123;

import battlecode.common.*;


public class Watchtower extends RobotCommon{

    static MapLocation initialDestination;
    static int movesSinceAction;  
    static int selfAlarmCounter;

    public Watchtower(RobotController rc, int r, MapLocation loc){
        super(rc, r, loc);
        initialDestination = chooseRandomInitialDestination();
        movesSinceAction = 0;
        selfAlarmCounter = 0;
    }   

    public void takeTurn() throws GameActionException {
        rc.setIndicatorString(rc.getMode().toString());
        me = rc.getLocation();
        // Try to attack someone
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
                round++;
                return;
            }
        }
        int visionRadius = rc.getType().actionRadiusSquared;
        enemies = rc.senseNearbyRobots(visionRadius, opponent);
        if (enemies.length == 0) {
            selfAlarmCounter++;
        } else {
            selfAlarmCounter = 0;
        }
        if (selfAlarmCounter < 3 && rc.getMode().equals(RobotMode.PORTABLE) && rc.canTransform()) {
            rc.transform();
        } else if (selfAlarmCounter >= 3 && rc.getMode().equals(RobotMode.TURRET) && rc.canTransform()) {
            rc.transform();
        }
        tryToMove();
        round++;
    }
    //note: maybe should order based on distance to Archon if it's a defensive soldier.
    public void tryToMove() throws GameActionException {
        if (rc.readSharedArray(49) != 65535) {
            initialDestination = Util.getLocationFromInt(rc.readSharedArray(49) % 10000);
        }
        Direction dir = Direction.CENTER;
        if (initialDestination != null){
            dir = pf.findBestDirection(initialDestination, 20);
        }
        // Direction dir = Util.directions[rng.nextInt(Util.directions.length)];
        MapLocation loc = rc.getLocation();
        
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
            dir = pf.findBestDirection(toFollow, 20);
        }
        int newX = loc.x + dir.dx;
        int newY = loc.y + dir.dy;
        MapLocation newLoc = new MapLocation(newX, newY);
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
        me = newLoc;
    }
}