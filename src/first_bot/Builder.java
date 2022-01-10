
package first_bot;

import battlecode.common.*;

import java.awt.*;


public class Builder extends RobotCommon{

    public static boolean isSacrifice = false;
    public static MapLocation target = null;

    public Builder(RobotController rc, int r, MapLocation loc) throws GameActionException{
        super(rc, r, loc);
        
        rc.transform();
    }

    public Builder(RobotController rc, int r, MapLocation loc, MapLocation t) throws GameActionException{
        super(rc, r, loc);
        target = t;
    }

    public void takeTurn() throws GameActionException {
        tryToRepair();
        tryToBuild();
        tryToMove();
    }

    //try to repair things in action radius
    //priority order: archon, watchtower, lab
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
        for(Direction d : Direction.allDirections()){
            MapLocation loc = me.add(d);
            if (rc.canBuildRobot(RobotType.WATCHTOWER, d) && Util.watchtowerElig(loc)){
                rc.buildRobot(RobotType.WATCHTOWER, d);
                return;
            }
            if (rc.canBuildRobot(RobotType.LABORATORY, d) && Util.labElig(loc)){
                rc.buildRobot(RobotType.LABORATORY, d);
                return;
            }
        }
    }

    public void tryToMove() throws GameActionException {
        if (isSacrifice){
            MapLocation loc = rc.getLocation();
            if(rc.senseLead(loc) == 0){
                rc.disintegrate();
            }
        }
        Pathfinding pf = new Pathfinding(this);
        Direction dir = pf.findBestDirection(target);
        if (rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
        }
    }
}