
package first_bot;

import battlecode.common.*;


public class Watchtower extends RobotCommon{

    public Watchtower(RobotController rc, int r, MapLocation loc){
        super(rc, r, loc);
    }

    //TODO
    public void takeTurn() throws GameActionException {
        rc.setIndicatorString(rc.getMode().toString());
        if (rc.getMode().equals(RobotMode.PORTABLE) && rc.canTransform()) {
            rc.transform();
        }
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
        round++;
    }
}