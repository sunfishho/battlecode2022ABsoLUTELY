
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
    }

    public Builder(RobotController rc, int r, MapLocation loc, MapLocation t, int type) throws GameActionException{
        super(rc, r, loc);
        target = t;
        isSacrifice = (type == 1);
        labBuilder = (type == 2);
        if (labBuilder) {
            target = Util.getCorner(loc);
        }
    }

    public void takeTurn() throws GameActionException {
        rc.setIndicatorString("Not repairing anything");
        me = rc.getLocation();

        if(isSacrifice) {
            sacrificeTurn();
            return;
        }

        observe();
        observeSymmetry();
        boolean a = tryToRepair();
        boolean b = tryToBuild();
        boolean c = tryToMove();
    }

    public void sacrificeTurn() throws GameActionException {
        // Disintegrate if you are on a no-lead location
        if(rc.senseLead(me) == 0) {
            rc.disintegrate();
        }
        if(me.equals(target) || rc.sense) {
            // If you're at the target and there's already lead, look for a no-lead location around and path towards there
            MapLocation[] nearby = rc.getAllLocationsWithinRadiusSquared(me, 20);
            for(MapLocation l : nearby) {
                if(rc.onTheMap(l) && rc.senseLead(l) == 0) {
                    target = l;
                    break;
                }
            }
            // If you can't find a no-lead location around, try to move outwards
            if(me.equals(target)) {
                LocationInfo best = new LocationInfo(me.add(me.directionTo(archonLocation)));
                for(int dx = -1; dx <= 1; dx++) {
                    for(int dy = -1; dy <= 1; dy++) {
                        MapLocation loc = new MapLocation(me.x + dx, me.y + dy);
                        if(!rc.onTheMap(loc)) continue;
                        LocationInfo there = new LocationInfo(loc);
                        if(best.compareTo(there) < 0) best = there;
                    }
                }
                if(rc.canMove(best.dir)) {
                    rc.move(best.dir);
                    me = rc.getLocation();
                }
            }
        }
        // Move towards target
        Direction dir = pf.findBestDirection(target, 50);
        if (rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
        }
    }

    class LocationInfo {
        final int LOOK_RADIUS = 9;
        final int A = 100, B = -1, C = -1;
        int distArchon; // distance to archon
        int leadAround; // number of squares in radius 2 around with lead > 1
        int rubble; // rubble at location
        Direction dir;

        public LocationInfo(MapLocation loc) throws GameActionException {
            distArchon = loc.distanceSquaredTo(archonLocation);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    MapLocation l = new MapLocation(loc.x + dx, loc.y + dy);
                    if(rc.onTheMap(l) && rc.senseLead(l) > 0) leadAround += rc.senseLead(l);
                }
            }
            rubble = rc.senseRubble(loc);
            dir = me.directionTo(loc);
        }

        public int compareTo(LocationInfo other) throws GameActionException {
            return getRating() - other.getRating();
        }

        public int getRating() throws GameActionException {
            return A * distArchon + B * leadAround + C * rubble;
        }
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
                if (rc.canMutate(loc)) {
                    rc.mutate(loc);
                    return true;
                }
                opt = loc;
                break;
            }
            if(t.equals(RobotType.WATCHTOWER) && !Util.buildingHealthy(RobotType.WATCHTOWER, sq.health, sq.level)) {
                bestType = RobotType.WATCHTOWER;
                opt = loc;
            }
            if(t.equals(RobotType.LABORATORY) && bestType == null &&
                    !Util.buildingHealthy(RobotType.LABORATORY, sq.health, sq.level)){
                if (rc.canMutate(loc)) {
                    rc.mutate(loc);
                    return true;
                }
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
                    labBuilder = false;
                    target = archonLocation;
                }
            }
            
            rc.setIndicatorString("Trying to build lab");
            rc.writeSharedArray(63, 1);
            Direction bestDir = Direction.CENTER;
            int bestRubble = 101;
            
            // Try to find the best location closest to corner and with lowest rubble
            int minDistToCorner = 100;
            MapLocation corner = Util.getCorner(me);
            for (Direction d : Direction.allDirections()) {
                MapLocation loc = me.add(d);
                int distToCorner = loc.distanceSquaredTo(corner);
                if (rc.canSenseLocation(loc) && (rc.senseRubble(loc) < bestRubble || (rc.senseRubble(loc) == bestRubble && distToCorner < minDistToCorner))) {
                    bestRubble = rc.senseRubble(loc);
                    minDistToCorner = distToCorner;
                    bestDir = d;
                }
            }
            if (rc.getTeamLeadAmount(us) >= 180 && rc.canBuildRobot(RobotType.LABORATORY, bestDir) && Util.labElig(me.add(bestDir))) {
                rc.buildRobot(RobotType.LABORATORY, bestDir);
                labBuilder = false;
                rc.writeSharedArray(63, 2);
                target = archonLocation;
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
            if (sq.team.equals(rc.getTeam()) && sq.getMode().equals(RobotMode.PROTOTYPE)) {
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
