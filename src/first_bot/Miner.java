
package first_bot;

import battlecode.common.*;


public class Miner extends RobotCommon{
    public static int archonRank;
    public static MapLocation target;
    public static boolean reachedTarget;

    public Miner(RobotController rc) throws GameActionException {
        super(rc);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation loc = new MapLocation(me.x + dx, me.y + dy);
                for(int i = 0; i < 4; i++) {
                    if(Util.getIntFromLocation(loc) == rc.readSharedArray(i)) {
                        archonRank = i;
                        target = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(archonRank)));
                        break;
                    }
                }
            }
        }
    }
    
    public void takeTurn() throws GameActionException {
        // Try to mine on squares around us.

        rc.setIndicatorString(rc.getLocation() + " " + Integer.toString(archonRank) + " " + target.toString() + " " + Boolean.toString(reachedTarget));

        if(rc.getLocation().equals(target)) reachedTarget = true;

        tryToMine();
        if(!reachedTarget) tryToMove();
    }

    public void tryToMine() throws GameActionException {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                // Notice that the Miner's action cooldown is very low.
                // You can mine multiple times per turn!
                while (rc.canMineGold(mineLocation)) {
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation)) {
                    rc.mineLead(mineLocation);
                }
            }
        }
    }

    public void tryToMove() throws GameActionException {
        // Direction dir = directions[rng.nextInt(directions.length)];
        Pathfinding pf = new Pathfinding(this);
        Direction dir = pf.returnBestDirection(new MapLocation(10, 10));
        if (rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
        }
    }
}