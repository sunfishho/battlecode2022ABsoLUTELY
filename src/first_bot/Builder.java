
package first_bot;

import battlecode.common.*;

import java.awt.*;


public class Builder extends RobotCommon{

    public static boolean isSacrifice = false;

    public Builder(RobotController rc, int r, MapLocation loc) throws GameActionException{
        super(rc, r, loc);
        
        rc.transform();
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
        for (RobotInfo sq : rc.senseNearbyRobots()) {
            MapLocation loc = sq.location;
            if (sq == null) {
                continue;
            }
            if(sq.getType().equals(RobotType.ARCHON)){
                bestType = RobotType.ARCHON;
                opt = loc;
                break;
            }
            if(sq.getType().equals(RobotType.WATCHTOWER) && (bestType == null || !bestType.equals(RobotType.ARCHON))) {
                bestType = RobotType.WATCHTOWER;
                opt = loc;
                break;
            }
            if(sq.getType().equals(RobotType.LABORATORY) && bestType == null){
                bestType = RobotType.LABORATORY;
                opt = loc;
                break;
            }
        }
        if(opt != null && rc.canRepair(opt)) {
            rc.repair(opt);
        }
    }

    public void tryToBuild() throws GameActionException {//tries to build things in action radius
        Team us = rc.getTeam();
        int pb = rc.getTeamLeadAmount(us);
        int au = rc.getTeamGoldAmount(us);
        for(Direction d : Direction.allDirections()){
            MapLocation loc = me.add(d);
            if (rc.getTeamLeadAmount(rc.getTeam()) > 500 && rc.canBuildRobot(RobotType.WATCHTOWER, d) && Util.watchtowerElig(loc, pb, au)){
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
        
        Direction dir = Util.directions[rng.nextInt(Util.directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
        }
    }
}