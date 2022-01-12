
package bot0111;

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
        System.out.println("BUILDER TARGET IS: " + t.x + " " + t.y);
    }

    public Builder(RobotController rc, int r, MapLocation loc, MapLocation t, boolean sac) throws GameActionException{
        super(rc, r, loc);
        target = t;
        isSacrifice = sac;
        System.out.println("BUILDER TARGET IS: " + t.x + " " + t.y);
    }

    public void takeTurn() throws GameActionException {
        rc.setIndicatorString("Not repairing anything");
        observe();
        boolean a = tryToRepair();
        boolean b = tryToBuild();
        boolean c = tryToMove();
    }

    //try to repair things in action radius
    //priority order: archon, watchtower, lab
    public boolean tryToRepair() throws GameActionException {
        Team us = rc.getTeam();
        RobotType bestType = null;
        MapLocation opt = null;
        for (RobotInfo sq : rc.senseNearbyRobots(5)) {//builder action radius = 5
            MapLocation loc = sq.location;
            if (sq == null || sq.getTeam() != us) {
                continue;
            }
            if(sq.getType().equals(RobotType.ARCHON) && sq.getHealth() < 600){
                bestType = RobotType.ARCHON;
                opt = loc;
                break;
            }
            if(sq.getType().equals(RobotType.WATCHTOWER) && (bestType == null || !bestType.equals(RobotType.ARCHON)) && sq.getHealth() < 150) {
                bestType = RobotType.WATCHTOWER;
                opt = loc;
            }
            if(sq.getType().equals(RobotType.LABORATORY) && bestType == null){
                bestType = RobotType.LABORATORY;
                opt = loc;
            }
        }
        if (opt != null) {
            rc.setIndicatorString("Repairing loc: (" + opt.x + ", " + opt.y + ")");
            while (rc.canRepair(opt)) {
                rc.repair(opt);
            }
            return true;
        }
        return false;
        
    }

    public boolean tryToBuild() throws GameActionException {//tries to build things in action radius
        // Dont build if there are prototypes nearby
        Team us = rc.getTeam();
        for (RobotInfo sq : rc.senseNearbyRobots(5)) {
            if (sq.team.equals(us) && sq.getType().equals(RobotType.WATCHTOWER) && sq.getMode().equals(RobotMode.PROTOTYPE)) {
                return true;
            }
        }

        // Counter for number of towers, if too many don't build. 
        // int counter = 0;
        // for(Direction d : Direction.allDirections()){
        //     MapLocation loc = me.add(d);
        //     if (!rc.canSenseLocation(loc)) {
        //         continue;
        //     }
        //     RobotInfo robot = rc.senseRobotAtLocation(loc);
        //     if (robot != null && robot.getTeam().equals(rc.getTeam()) && robot.getMode().equals(RobotMode.DROID) == false) {
        //         counter++;
        //     }
        // }
        // if (counter > 10) {
        //     return false;
        // }

        for(Direction d : Direction.allDirections()){
            MapLocation loc = me.add(d);
            if (rc.getTeamLeadAmount(us) > 280 && rc.canBuildRobot(RobotType.WATCHTOWER, d) && Util.watchtowerElig(loc)){
                rc.buildRobot(RobotType.WATCHTOWER, d);
                return true;
            }
            if (rc.canBuildRobot(RobotType.LABORATORY, d) && Util.labElig(loc)){
                rc.buildRobot(RobotType.LABORATORY, d);
                return true;
            }
        }
        return false;
    }

    public boolean tryToMove() throws GameActionException {
        if (isSacrifice){
            MapLocation loc = rc.getLocation();
            if(rc.senseLead(loc) == 0){
                rc.disintegrate();
                return false;
            }
        }
        // Dont move if there are prototypes nearby
        for (RobotInfo sq : rc.senseNearbyRobots(5)) {
            if (sq.team.equals(rc.getTeam()) && sq.getType().equals(RobotType.WATCHTOWER) && sq.getMode().equals(RobotMode.PROTOTYPE)) {
                return true;
            }
        }
        Pathfinding pf = new Pathfinding(this);
        Direction dir = pf.findBestDirection(target, 20);
        if (rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
            return true;
        }
        return false;
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
