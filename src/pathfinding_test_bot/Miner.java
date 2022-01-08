
package pathfinding_test_bot;

import battlecode.common.*;


public class Miner extends RobotCommon{


    public Miner(RobotController rc) throws GameActionException {
        super(rc);
    }

    public void takeTurn() throws GameActionException{
        me = rc.getLocation();
        //pf takes 39 turns
        Pathfinding pf = new Pathfinding(this);
        GreedyPathfinding gpf = new GreedyPathfinding(this);
        MapLocation finalDestination = new MapLocation(0, 0);
        if (!rc.getLocation().equals(finalDestination)){
            Direction dir = pf.findBestDirection(finalDestination);
            //gpf takes 39 turns
            // Direction dir = gpf.travelTo(finalDestination);
            if (rc.canMove(dir)){
                Util.TURN_COUNTER++;
                rc.move(dir);
            }
        }
        else{
            System.out.println("I MADE IT IN " + Util.TURN_COUNTER + " TURNS");
            System.out.println(rc.getRoundNum());
            rc.resign();
        }
    }
}