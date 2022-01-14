
package pathfinding_test_bot;

import battlecode.common.*;


public class Miner extends RobotCommon{


    public Miner(RobotController rc) throws GameActionException {
        super(rc);
    }

    public void takeTurn() throws GameActionException{
        me = rc.getLocation();
        Pathfinding pf = new Pathfinding(this);
        MapLocation finalDestination = new MapLocation(2, 17);
        if (!rc.getLocation().equals(finalDestination)){
            Direction dir = pf.findBestDirection(finalDestination, 10);
            if (rc.canMove(dir)){
                rc.move(dir);
            }
        }
        else{
            System.out.println("I MADE IT IN " + rc.getRoundNum() + " TURNS");
            rc.resign();
        }
    }
}