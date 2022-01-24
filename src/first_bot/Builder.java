
package first_bot;

import battlecode.common.*;

import java.awt.*;


public class Builder extends Unit {

    final int totalLabs = 2, numSteps = 10;
    boolean isSacrifice = false;
    int labsBuilt = 0;
    int steps = 0;
    boolean hasTarget = false;

    public Builder(RobotController rc, int r, MapLocation loc) throws GameActionException{
        super(rc, r, loc);
        target = nearestCorner(loc);
    }

    public Builder(RobotController rc, int r, MapLocation loc, MapLocation t) throws GameActionException{
        super(rc, r, loc);
        target = t;
    }

    public Builder(RobotController rc, int r, MapLocation loc, MapLocation t, int type) throws GameActionException{
        super(rc, r, loc);
        isSacrifice = (type == 1);
        target = t;
    }

    public void takeTurn() throws GameActionException {
        me = rc.getLocation();

        if(isSacrifice) {
            sacrificeTurn();
            return;
        }

        observe();
        observeSymmetry();
        boolean a = tryToRepair();
        if (a){
            return; //should finish repairing the lab or whatever before moving on to build second lab?
        }
        boolean b = tryToBuild();
        boolean c = tryToMove();
    }

    public void sacrificeTurn() throws GameActionException {
        rc.setIndicatorString("" + target);
        if(Util.getIntFromLocation(target) == 0) hasTarget = false;
        // Disintegrate if you are on a no-lead location or in a loop
        if(rc.senseLead(me) == 0) {
            rc.writeSharedArray(48, rc.readSharedArray(48) + 1);
            rc.disintegrate();
        }

        while(hasTarget) {
            if(me.equals(target)) {
                // If you're at the target and there's already lead, look for a no-lead location around and path towards there
                MapLocation[] nearby = rc.getAllLocationsWithinRadiusSquared(me, 20);
                for(MapLocation l : nearby) {
                    if(rc.onTheMap(l) && rc.senseLead(l) == 0) {
                        target = l;
                        break;
                    }
                }
                hasTarget = false;
            }
            else {
                // Otherwise, path towards the target
                Direction dir = pf.findBestDirection(target, 50);
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    me = rc.getLocation();
                }
                break;
            }
        }

        // If you have no target, greedily move in best direction
        SacrificeInfo best = new SacrificeInfo(me);
        for(int dx = -1; dx <= 1; dx++) {
            for(int dy = -1; dy <= 1; dy++) {
                MapLocation loc = new MapLocation(me.x + dx, me.y + dy);
                if(!rc.onTheMap(loc)) continue;
                SacrificeInfo there = new SacrificeInfo(loc);
                if(best.compareTo(there) < 0) best = there;
            }
        }
        if(best.dir == Direction.CENTER) rc.disintegrate();
        if(rc.canMove(best.dir)) {
            rc.move(best.dir);
            me = rc.getLocation();
        }
    }

    class SacrificeInfo {
        final int LOOK_RADIUS = 9;
        final double A = -50, B = 100, C = -100, D = -1, E = 10000, F = -100000;
        int visibleUnits; // # of units visible in radius LOOK_RADIUS
        double distArchon; // distance to archon
        int leadAround; // number of squares with lead in radius 2
        int rubble; // rubble at location
        int noLead; // does location have no lead, 0 = false, 1 = true
        int isArchon; // is it the archon, 0 = false, 1 = true
        Direction dir;

        public SacrificeInfo(MapLocation loc) throws GameActionException {
            visibleUnits = rc.senseNearbyRobots(loc, LOOK_RADIUS, rc.getTeam()).length;
            distArchon = Math.sqrt(loc.distanceSquaredTo(archonLocation));
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    MapLocation l = loc.translate(dx, dy);
                    if(rc.onTheMap(l) && rc.senseLead(l) > 0) leadAround++;
                }
            }
            rubble = rc.senseRubble(loc);
            RobotInfo r = rc.senseRobotAtLocation(loc);
            if(rc.senseLead(loc) == 0 && r == null) noLead = 1; 
            if(r != null && r.getType() == RobotType.ARCHON) isArchon = 1;
            dir = me.directionTo(loc);
        }

        public double compareTo(SacrificeInfo other) throws GameActionException {
            return getRating() - other.getRating();
        }

        public double getRating() throws GameActionException {
            return A * visibleUnits + B * distArchon + C * leadAround + D * rubble + E * noLead + F * isArchon;
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
            if(t.equals(RobotType.LABORATORY) && bestType == null &&
                    !Util.buildingHealthy(RobotType.LABORATORY, sq.health, sq.level) && sq.getMode() == RobotMode.PROTOTYPE){
                bestType = RobotType.LABORATORY;
                opt = loc;
                break;
            }
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

    // Returns true if you moved this turn
    public boolean tryToBuild() throws GameActionException {
        // Dont build if there are prototypes nearby
        Team us = rc.getTeam();
        for (RobotInfo sq : rc.senseNearbyRobots(5)) {
            if (sq.team.equals(us) && sq.getType().equals(RobotType.WATCHTOWER) && sq.getMode().equals(RobotMode.PROTOTYPE)) {
                return false;
            }
        }

        if(labsBuilt < totalLabs) {
            MapLocation loc = me;
            LaboratoryInfo best = new LaboratoryInfo(loc);
            LaboratoryInfo there = best;
    
            loc = me.translate(-1, -1);
            if(rc.onTheMap(loc)) {
                there = new LaboratoryInfo(loc);
                if(best.compareTo(there) < 0) best = there;
            }
            loc = me.translate(-1, 0);
            if(rc.onTheMap(loc)) {
                there = new LaboratoryInfo(loc);
                if(best.compareTo(there) < 0) best = there;
            }
            loc = me.translate(-1, 1);
            if(rc.onTheMap(loc)) {
                there = new LaboratoryInfo(loc);
                if(best.compareTo(there) < 0) best = there;
            }
            loc = me.translate(0, -1);
            if(rc.onTheMap(loc)) {
                there = new LaboratoryInfo(loc);
                if(best.compareTo(there) < 0) best = there;
            }
            loc = me.translate(0, 1);
            if(rc.onTheMap(loc)) {
                there = new LaboratoryInfo(loc);
                if(best.compareTo(there) < 0) best = there;
            }
            loc = me.translate(1, -1);
            if(rc.onTheMap(loc)) {
                there = new LaboratoryInfo(loc);
                if(best.compareTo(there) < 0) best = there;
            }
            loc = me.translate(1, 0);
            if(rc.onTheMap(loc)) {
                there = new LaboratoryInfo(loc);
                if(best.compareTo(there) < 0) best = there;
            }
            loc = me.translate(1, 1);
            if(rc.onTheMap(loc)) {
                there = new LaboratoryInfo(loc);
                if(best.compareTo(there) < 0) best = there;
            }

            steps++;
            rc.setIndicatorString(steps + " " + best.dir);
            if((steps >= numSteps || best.dir == Direction.CENTER) && rc.canBuildRobot(RobotType.LABORATORY, best.dir)) {
                rc.buildRobot(RobotType.LABORATORY, best.dir);
                labsBuilt++;
                rc.writeSharedArray(43, rc.readSharedArray(43) + 1);
                rc.writeSharedArray(63, rc.readSharedArray(63) + 1);
                if(labsBuilt == totalLabs) target = archonLocation; // final lab
                return true;
            }
            else {
                if (rc.canMove(best.dir)) {
                    rc.move(best.dir);
                    me = rc.getLocation();
                }
            }
        }
        else {
            // // Build watchtowers if you have enough lead, and return to Archon
            // for (Direction d : Direction.allDirections()) {
            //     MapLocation loc = me.add(d);
            //     if (rc.getTeamLeadAmount(us) > 280 && rc.canBuildRobot(RobotType.WATCHTOWER, d) && Util.watchtowerElig(loc)) {
            //         rc.buildRobot(RobotType.WATCHTOWER, d);
            //         return true;
            //     }
            // }
        }
        return false;
    }

    class LaboratoryInfo {
        final int LOOK_RADIUS = 9;
        final double A = -10, B = -10, C = -10, D = -1, E = -10000;
        int visibleBuildings; // number of visible buildings
        int visibleDroids; // number of visible droids
        double distToCorner; // euclidean distance to corner
        int rubble; // amount of rubble at location
        int hasUnit; // 0 if no unit, 1 if yes unit
        Direction dir;

        public LaboratoryInfo(MapLocation loc) throws GameActionException {
            RobotInfo[] visibleUnits = rc.senseNearbyRobots(loc, LOOK_RADIUS, rc.getTeam());
            for(RobotInfo r : visibleUnits) {
                if(r.getMode() == RobotMode.DROID) visibleDroids++;
                else visibleBuildings++;
            }
            distToCorner = Math.sqrt(loc.distanceSquaredTo(nearestCorner(loc)));
            rubble = rc.senseRubble(loc);
            RobotInfo r = rc.senseRobotAtLocation(loc);
            if(r != null) hasUnit = 1;
            dir = me.directionTo(loc);
        }

        public double compareTo(LaboratoryInfo other) throws GameActionException {
            return getRating() - other.getRating();
        }

        public double getRating() throws GameActionException {
            return A * visibleBuildings + B * visibleDroids + C * distToCorner + D * rubble + E * hasUnit;
        }
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
