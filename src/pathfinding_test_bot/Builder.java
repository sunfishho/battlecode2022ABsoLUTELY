
package pathfinding_test_bot;

import battlecode.common.*;

import java.awt.*;


public class Builder extends RobotCommon{

    public static int archonRank;
    public static MapLocation archonLocation;

    public static boolean isSacrifice = false;

    public Builder(RobotController rc) throws GameActionException{
        super(rc);

        //find parent archon
        for(int i = 0; i < 4; i++) {
            MapLocation archonLoc = Util.getLocationFromInt(rc.readSharedArray(i));
            if(Util.abs(archonLoc.x - me.x) <= 1 && Util.abs(archonLoc.y - me.y) <= 1) {
                archonRank = i + 1;
                archonLocation = archonLoc;
                return;
            }
        }

        //do more stuff later
    }

    //TODO
    public void takeTurn() throws GameActionException {

    }

    //try to repair things in action radius
    public void tryToRepair() throws GameActionException {
        Team us = rc.getTeam();
        RobotType bestType = null;
        MapLocation opt = null;
        for (int dx = 2; dx >= -2; dx--){
            for (int dy = 2; dy >= -2; dy--) {
                if(dx * dx + dy * dy == 8){//out of range
                    continue;
                }
                MapLocation loc = new MapLocation(me.x + dx, me.y + dy);
                RobotInfo sq = rc.senseRobotAtLocation(loc);
                if(sq.getType() == RobotType.ARCHON){
                    bestType = RobotType.ARCHON;
                    opt = loc;
                    break;
                }
                if(sq.getType() == RobotType.WATCHTOWER && bestType != RobotType.ARCHON){
                    bestType = RobotType.WATCHTOWER;
                    opt = loc;
                    break;
                }
                if(sq.getType() == RobotType.LABORATORY && bestType == null){
                    bestType = RobotType.LABORATORY;
                    opt = loc;
                    break;
                }
            }
        }
        if(rc.canRepair(opt)) {
            rc.repair(opt);
        }
    }

    public void tryToBuild() throws GameActionException {//tries to build things in action radius

    }
}