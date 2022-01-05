
package first_bot;

import battlecode.common.*;


public class Miner extends RobotCommon{

    public MapLocation me;
    public Miner(RobotController rc){
        super(rc);
        me = rc.getLocation();
        //do more stuff later
    }
    
    public void takeTurn() throws GameActionException {
        // Try to mine on squares around us.
        tryToMine();
        tryToMove();
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
        
    }

    public void checkIfLeadDepositsInVision() throws GameActionException {

    }
}