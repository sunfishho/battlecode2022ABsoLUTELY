package second_bot;

import battlecode.common.*;

public class MinerForager extends Miner {
    public MinerForager(RobotController rc, int r, MapLocation loc, MapLocation t) throws GameActionException {
        super(rc, r, loc, t);
    }

    public void takeTurn() throws GameActionException {
        me = rc.getLocation();
        rc.setIndicatorString("MINER FORAGER: " + me + " " + archonLocation + " " + target + " " + reachedTarget);

        // if there's any location with lead > 1, move towards it
        // otherwise, proceed towards target

        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead(getVisionRadiusSquared());
        Pathfinding pf = new Pathfinding(this);
        int i = 0;
        // don't go back whne you see 1 or you might loop back
        while(i < leadLocations.length && rc.senseLead(leadLocations[i]) <= 6) i++; 

        Direction dir = Direction.CENTER;
        if(i != leadLocations.length) dir = pf.findBestDirection(leadLocations[i]);
        else dir = pf.findBestDirection(target);

        if (rc.canMove(dir)) {
            rc.move(dir);
        }

        // try to mine at location, leave 0 lead if you're more than half-way to target
        if(me.distanceSquaredTo(target) > me.distanceSquaredTo(archonLocation)) tryToMine(1);
        else tryToMine(0);
    }
}
