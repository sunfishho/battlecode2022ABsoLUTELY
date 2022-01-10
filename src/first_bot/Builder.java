
package first_bot;

import battlecode.common.*;

import java.awt.*;


public class Builder extends RobotCommon{

    public static boolean isSacrifice = false;

    public Builder(RobotController rc, int r, MapLocation loc) throws GameActionException{
        super(rc, r, loc);
    }

    //TODO
    public void takeTurn() throws GameActionException {
        tryToRepair();
        tryToBuild();
        tryToMove();
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
        Team us = rc.getTeam();
        int pb = rc.getTeamLeadAmount(us);
        int au = rc.getTeamGoldAmount(us);
        for(Direction d : Direction.allDirections()){
            MapLocation loc = me.add(d);
            if (rc.canBuildRobot(RobotType.WATCHTOWER, d) && Util.watchtowerElig(loc, pb, au)){
                rc.buildRobot(RobotType.WATCHTOWER, d);
                return;
            }
            if (rc.canBuildRobot(RobotType.LABORATORY, d) && Util.labElig(loc, pb, au)){
                rc.buildRobot(RobotType.LABORATORY, d);
                return;
            }
        }
    }

    public void tryToMove() throws GameActionException {

    }
}