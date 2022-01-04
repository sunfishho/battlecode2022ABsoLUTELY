
package first_bot;

import battlecode.common.*;


public class Soldier extends RobotCommon{

    public Soldier(RobotController rc){
        super(rc);
        //do more stuff later
    }
    public void takeTurn() throws GameActionException {
        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) {
            MapLocation toAttack = enemies[0].location;
            if (rc.canAttack(toAttack)) {
                rc.attack(toAttack);
            }
        }
    }
}