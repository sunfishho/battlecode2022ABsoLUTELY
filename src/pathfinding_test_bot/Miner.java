
package first_bot;

import battlecode.common.*;


public class Miner extends RobotCommon{
    public static int archonRank;
    public static MapLocation archonLocation, target;
    public static boolean reachedTarget;

    //this is all only for scouts
    public static boolean isScout = false;
    public static int[] rubbleSeen = new int[60];
    //if isScout is true, this stores if the scout is going horizontally or vertically
    public static boolean scoutTravelingHorizontally, hasReachedHalfway;
    public static MapLocation halfTarget;


    public Miner(RobotController rc) throws GameActionException {
        super(rc);
    }

    public void takeTurn() throws GameActionException{
        Pathfinding pf = new Pathfinding(this);
        MapLocation finalDestination = new MapLocation(20, 20);
        if (!rc.getLocation().equals(finalDestination)){
            rc.move(pf.findBestDirection(finalDestination));
        }
        else{
            System.out.println("I MADE IT IN " + rc.getRoundNum() + " TURNS");
            rc.resign();
        }
    }
}