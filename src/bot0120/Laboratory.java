
package bot0120;

import battlecode.common.*;


public class Laboratory extends RobotCommon{

    public Laboratory(RobotController rc, int r, MapLocation loc){
        super(rc, r, loc);
    }

    //TODO
    public void takeTurn() throws GameActionException {
        round = rc.getRoundNum();
        if (rc.canTransmute() && round % 16 < 10) {
            rc.transmute();
        }
    }
}