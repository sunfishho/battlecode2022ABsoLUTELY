
package pathfinding_test_bot;

import battlecode.common.*;


public class Miner extends RobotCommon{

    public Miner(RobotController rc) throws GameActionException {
        super(rc);
    }


    public void takeTurn() throws GameActionException{
        me = rc.getLocation();
        //gpf takes 39 turns to (20, 20), 58 turns 137 rounds to (0,0)
        //pf takes 35 turns, round 212 to (20,20), 53 turns 350 rounds to (0,0)
        Pathfinding3 pf3 = new Pathfinding3(this);
        Pathfinding2 pf2 = new Pathfinding2(this);
        Pathfinding pf1 = new Pathfinding(this);
        GreedyPathfinding gpf = new GreedyPathfinding(this);
        MapLocation finalDestination = new MapLocation(0, 0);
        if (!rc.getLocation().equals(finalDestination)){
            // Direction dir = pf3.findBestDirection(finalDestination);
            Direction dir = gpf.travelTo(finalDestination);
            if (rc.canMove(dir)){
                Util.TURN_COUNTER++;
                rc.move(dir);
            }
        }
        else{
            System.out.println("I MADE IT IN " + Util.TURN_COUNTER + " TURNS");
            System.out.println(rc.getRoundNum());
            rc.disintegrate();
        }
    }
}