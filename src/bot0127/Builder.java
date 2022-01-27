package bot0127;

import battlecode.common.*;

import java.awt.*;
import java.util.*;


public class Builder extends Unit {
    boolean isSacrifice = false, hasTarget = false, foundLabLocation = false;
    int totalLabs = 16, numSteps = 5, labsBuilt = 0, steps = 0;

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
        rc.setIndicatorString(rank + " " + rc.readSharedArray(63));
        me = rc.getLocation();
        archonLocation = nearestArchon(me);
        boolean a = tryToRepair();

        if(!a && isSacrifice) {
            sacrificeTurn();
            return;
        }

        observe();
        observeSymmetry();
        if(a) {
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
                if (best.compareTo(there) == 0) {
                    Random rng2 = new Random(rc.getRoundNum() + rc.getID() * 1000);
                    if (rng2.nextInt(8) == 1) {
                        best = there;
                    }
                }
            }
        }
        if(best.dir == Direction.CENTER) rc.disintegrate();
        if(Clock.getBytecodesLeft() <= 50) Clock.yield();
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
        int isOccupied; // is it occupied, 0 = false, 1 = true
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
            if(r != null && loc != me) isOccupied = 1;
            dir = me.directionTo(loc);
        }

        public double compareTo(SacrificeInfo other) throws GameActionException {
            return getRating() - other.getRating();
        }

        public double getRating() throws GameActionException {
            return A * visibleUnits + B * distArchon + C * leadAround + D * rubble + E * noLead + F * isOccupied;
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
            rc.setIndicatorString(rank + " repairing loc: (" + opt.x + ", " + opt.y + ")");
            while (rc.canRepair(opt)) {
                rc.repair(opt);
            }
            if(!(rc.senseRobotAtLocation(opt).getMode() == RobotMode.PROTOTYPE) && labsBuilt == totalLabs) {
                target = archonLocation; // final lab
            }
            return true;
        }
        return false;
        
    }

    // Returns true if you moved this turn
    public boolean tryToBuild() throws GameActionException {
        if(labsBuilt < totalLabs && ((rc.readSharedArray(63) % 10000) % 101 != 0)) {
            rc.setIndicatorString("HI");
            return tryToBuildLaboratory();
        }
        // else 
        //     for (Direction d : Direction.allDirections()) {
        //         MapLocation loc = me.add(d);
        //         if (rc.getTeamLeadAmount(us) > 280 && rc.canBuildRobot(RobotType.WATCHTOWER, d) && Util.watchtowerElig(loc)) {
        //             rc.buildRobot(RobotType.WATCHTOWER, d);
        //             return true;
        //         }
        //     }
        // }
        return false;
    }

    public boolean tryToBuildLaboratory() throws GameActionException {
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
        rc.setIndicatorString(rank + " " + steps + " " + best.dir);
        if(best.dir == Direction.CENTER) {
            // we think best location is where we are currently at
            foundLabLocation = true;
        }
        else if((steps >= numSteps || foundLabLocation) && rc.canBuildRobot(RobotType.LABORATORY, best.dir)) {
            // we've either exceeded step count or found lab location and moved off
            rc.buildRobot(RobotType.LABORATORY, best.dir);
            rc.writeSharedArray(63, rc.readSharedArray(63) + 1);
            labsBuilt++;
            foundLabLocation = false;
            steps = 0;
            numSteps = 5;
            return true;
        }
        else {
            if(foundLabLocation) {
                // we move off of our optimal location towards archon
                Direction dir = me.directionTo(archonLocation);
                if(rc.canMove(dir)) {
                    rc.move(dir);
                    me = rc.getLocation();
                }
            }
            else {
                // we path towards best spot
                if (rc.canMove(best.dir)) {
                    rc.move(best.dir);
                    me = rc.getLocation();
                }
            }
        }
        return false;
    }

    class LaboratoryInfo {
        final int LOOK_RADIUS = 9;
        final double A = -20, B = -14, C = 10000, D = -10000, E = 6, F = 14, G = -10;
        int visibleUnits;
        double distToCorner; // euclidean distance to corner
        double distToArchon; // euclidean distance to archon
        double distToCenter; // euclidean distance to center
        int rubble; // amount of rubble at location
        double avgRubble; // avg in radius 2 around location
        int hasUnit; // 0 if no unit, 1 if yes unit
        Direction dir;

        public LaboratoryInfo(MapLocation loc) throws GameActionException {
            visibleUnits = rc.senseNearbyRobots(loc, LOOK_RADIUS, rc.getTeam()).length;
            distToCorner = Math.sqrt(loc.distanceSquaredTo(nearestCorner(loc)));
            rubble = rc.senseRubble(loc);
            MapLocation[] neighborhood = rc.getAllLocationsWithinRadiusSquared(loc, 2);
            for(MapLocation l : neighborhood) {
                avgRubble += rc.senseRubble(loc);
            }
            avgRubble /= neighborhood.length;
            RobotInfo r = rc.senseRobotAtLocation(loc);
            if(r != null) hasUnit = 1;
            dir = me.directionTo(loc);
            distToArchon = Math.sqrt(loc.distanceSquaredTo(archonLocation));
            distToCenter = Math.sqrt(loc.distanceSquaredTo(new MapLocation(Util.WIDTH / 2, Util.HEIGHT / 2)));
        }

        public double compareTo(LaboratoryInfo other) throws GameActionException {
            return getRating() - other.getRating();
        }

        public double getRating() throws GameActionException {
            return A * visibleUnits + B * distToCorner + C / (rubble + 10) + D * hasUnit + E * distToArchon + F * distToCenter + G * avgRubble;
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
        Direction dir = pf.findBestDirection(target, 50);
        if (rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
            return true;
        }
        return false;
    }
    
}
