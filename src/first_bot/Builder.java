
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
        observe();
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
        for (RobotInfo sq : rc.senseNearbyRobots(20)) {//builder action radius = 20
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
        while (opt != null && rc.canRepair(opt)) {
            rc.repair(opt);
        }
    }

    public void tryToBuild() throws GameActionException {//tries to build things in action radius
        // Dont build if there are prototypes nearby
        for (RobotInfo sq : rc.senseNearbyRobots()) {
            if (sq.team.equals(rc.getTeam()) == false) {
                continue;
            }
            if(sq.getType().equals(RobotType.BUILDER) && sq.getMode().equals(RobotMode.PROTOTYPE)) {
                return;
            }
        }
        Team us = rc.getTeam();

        // Counter for number of towers, if too many don't build. 
        int counter = 0;
        for(Direction d : Direction.allDirections()){
            MapLocation loc = me.add(d);
            if (!rc.canSenseLocation(loc)) {
                continue;
            }
            RobotInfo robot = rc.senseRobotAtLocation(loc);
            if (robot != null && robot.getTeam().equals(rc.getTeam()) && robot.getMode().equals(RobotMode.DROID) == false) {
                counter++;
            }
        }
        if (counter > 1) {
            return;
        }
        for(Direction d : Direction.allDirections()){
            MapLocation loc = me.add(d);
            if (rc.getTeamLeadAmount(us) > 500 && rc.canBuildRobot(RobotType.WATCHTOWER, d) && Util.watchtowerElig(loc)){
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
        // Dont move if there are prototypes nearby
        for (RobotInfo sq : rc.senseNearbyRobots()) {
            if (sq.team.equals(rc.getTeam()) == false) {
                continue;
            }
            if(sq.getType().equals(RobotType.BUILDER) && sq.getMode().equals(RobotMode.PROTOTYPE)) {
                return;
            }
        }
        Pathfinding pf = new Pathfinding(this);
        Direction dir = pf.findBestDirection(target);
        if (rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
        }
    }
    
    // Observes if any enemy units nearby
    public void observe() throws GameActionException {
        for (RobotInfo robot : rc.senseNearbyRobots()) {
            if (robot.getTeam() != rc.getTeam() && robot.getType() != RobotType.MINER) {
                rc.writeSharedArray(17, Util.getIntFromLocation( robot.location) + 10000 * rank);
                rc.writeSharedArray(18, round);
                return;
            }
        }
    }
}
