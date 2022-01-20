
package first_bot;

import battlecode.common.*;

import java.awt.*;


public class Builder extends Unit {

    public static boolean isSacrifice = false;
    public static boolean labBuilder = true;
    public static boolean labBuilt = false;

    public Builder(RobotController rc, int r, MapLocation loc) throws GameActionException{
        super(rc, r, loc);
        labBuilder = true;

    }

    public Builder(RobotController rc, int r, MapLocation loc, MapLocation t) throws GameActionException{
        super(rc, r, loc);
        labBuilder = true;
        target = nearestCorner(loc);
        System.out.println("BUILDER TARGET IS: " + target.x + " " + target.y);
    }

    public Builder(RobotController rc, int r, MapLocation loc, MapLocation t, int type) throws GameActionException{
        super(rc, r, loc);
        target = t;
        isSacrifice = (type == 1);
        labBuilder = (type == 2);
        if (labBuilder) {
            target = Util.getCorner(loc);
        }
        System.out.println("BUILDER TARGET IS: " + t.x + " " + t.y);
    }

    public void takeTurn() throws GameActionException {
        rc.setIndicatorString("Not repairing anything");
        me = rc.getLocation();
        observe();
        observeSymmetry();
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
            if (sq.getTeam() != us) {
                continue;
            }
            RobotType t = sq.getType();
            if(t.equals(RobotType.ARCHON) && !Util.buildingHealthy(RobotType.ARCHON, sq.health, sq.level)){
                opt = loc;
                break;
            }
            if(t.equals(RobotType.WATCHTOWER) && !Util.buildingHealthy(RobotType.WATCHTOWER, sq.health, sq.level)) {
                bestType = RobotType.WATCHTOWER;
                opt = loc;
            }
            if(t.equals(RobotType.LABORATORY) && bestType == null &&
                    !Util.buildingHealthy(RobotType.LABORATORY, sq.health, sq.level)){
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

        if (!labBuilder) {
            for (Direction d : Direction.allDirections()) {
                MapLocation loc = me.add(d);
                if (rc.getTeamLeadAmount(us) > 280 && rc.canBuildRobot(RobotType.WATCHTOWER, d) && Util.watchtowerElig(loc)) {
                    rc.buildRobot(RobotType.WATCHTOWER, d);
                    return true;
                }
            }
        }
        else{
            rc.setIndicatorString("Trying to go to lab");
            //if we sense any labs nearby, don't build a lab (for solitude) and disintegrate
            if (target.distanceSquaredTo(me) >= 1) {
                return false;
            }
            for(RobotInfo r : rc.senseNearbyRobots(20, us)){
                if(r.type == RobotType.LABORATORY){
                    rc.disintegrate();
                }
            }
            
            rc.setIndicatorString("Trying to build lab");
            rc.writeSharedArray(31, 1);
            for (Direction d : Direction.allDirections()) {
                MapLocation loc = me.add(d);
                if (rc.getTeamLeadAmount(us) > 350 && rc.canBuildRobot(RobotType.LABORATORY, d) && Util.labElig(loc)) {
                    rc.buildRobot(RobotType.LABORATORY, d);
                    labBuilder = false;
                    rc.writeSharedArray(31, 0);
                    return true;
                }
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
        Direction dir = pf.findBestDirection(target, 50);
        if (rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
            return true;
        }
        return false;
    }
    
}
