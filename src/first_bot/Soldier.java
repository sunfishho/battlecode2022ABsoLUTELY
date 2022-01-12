
package first_bot;

import battlecode.common.*;


public class Soldier extends RobotCommon{

    static int type;//0 = aggressive, 1 = defensive, 2 = escort?
    static MapLocation initialDestination;
    static int movesSinceAction;  


    public Soldier(RobotController rc, int r, MapLocation loc) throws GameActionException {
        super(rc, r, loc); 
        type = 1;       // Default to defensive
        initialDestination = chooseRandomInitialDestination();
        movesSinceAction = 0;
        //do more stuff later
    }

    public void takeTurn() throws GameActionException {
        me = rc.getLocation();
        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        observe();
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
                round++;
                return;
            }
        }
        tryToMove();
        round++;
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

    //note: maybe should order based on distance to Archon if it's a defensive soldier.
    public void tryToMove() throws GameActionException {
        if (rc.readSharedArray(17) != 65535) {
            initialDestination = Util.getLocationFromInt(rc.readSharedArray(17) % 10000);
        }else if (this.me.equals(initialDestination)){
            initialDestination = chooseRandomInitialDestination();
            if (rc.getID() == 13087){
                System.out.println(me + " " + rc.getLocation() + " " + initialDestination);
            }
        }
        Pathfinding pf = new Pathfinding(this);
        Direction dir = Direction.CENTER;
        if (initialDestination != null){
            dir = pf.findBestDirection(initialDestination, 20);
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
            dir = pf.findBestDirection(toFollow, 20);
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
        me = newLoc;
    }
}