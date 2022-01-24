
package bot0123;

import battlecode.common.*;


public class Laboratory extends RobotCommon{

    public Laboratory(RobotController rc, int r, MapLocation loc){
        super(rc, r, loc);
    }

    //TODO
    public void takeTurn() throws GameActionException {
        round = rc.getRoundNum();
        if (rc.canTransmute()) {
            rc.transmute();
        }
    }
}