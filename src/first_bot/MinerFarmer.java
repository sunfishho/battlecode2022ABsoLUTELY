package first_bot;

import java.util.Map;
import java.util.Random;
import battlecode.common.*;

public class MinerFarmer extends Unit {
    int income;
    double tetherRadius;

    public MinerFarmer(RobotController rc, int r, MapLocation loc) throws GameActionException {
        super(rc, r, loc);
    }

    public MinerFarmer(RobotController rc, int r, MapLocation loc, MapLocation t) throws GameActionException {
        super(rc, r, loc);
        target = t;
    }

    public void takeTurn() throws GameActionException {
        initialize();
        takeAttendance();
        observe();
        mine(1);
        move();
        report();
    }

    // Resets values to fields
    public void initialize() throws GameActionException {
        me = rc.getLocation();
        archonLocation = nearestArchon(me);
        income = 0;
    }

    public void takeAttendance() throws GameActionException {
        rc.writeSharedArray(41, rc.readSharedArray(41) + 1);
        super.takeAttendance();
    }

    // Chooses best place to move to 
    public void move() throws GameActionException {
        if(target != null) {
            if(checkLoop() != 2) {
                target = null;
            }
            else {
                rc.setIndicatorString("target: " + target);
                Direction dir = pf.findBestDirection(target, 50);
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    me = rc.getLocation();
                }
                if(me == target) {
                    target = null;
                }
                return;
            }
        }

        MapLocation loc = me;
        LocationInfo best = new LocationInfo(loc);
        LocationInfo there = best;

        loc = me.translate(-1, -1);
        if(rc.onTheMap(loc)) {
            there = new LocationInfo(loc);
            if(best.compareTo(there) < 0) best = there;
        }
        loc = me.translate(-1, 0);
        if(rc.onTheMap(loc)) {
            there = new LocationInfo(loc);
            if(best.compareTo(there) < 0) best = there;
        }
        loc = me.translate(-1, 1);
        if(rc.onTheMap(loc)) {
            there = new LocationInfo(loc);
            if(best.compareTo(there) < 0) best = there;
        }
        loc = me.translate(0, -1);
        if(rc.onTheMap(loc)) {
            there = new LocationInfo(loc);
            if(best.compareTo(there) < 0) best = there;
        }
        loc = me.translate(0, 1);
        if(rc.onTheMap(loc)) {
            there = new LocationInfo(loc);
            if(best.compareTo(there) < 0) best = there;
        }
        loc = me.translate(1, -1);
        if(rc.onTheMap(loc)) {
            there = new LocationInfo(loc);
            if(best.compareTo(there) < 0) best = there;
        }
        loc = me.translate(1, 0);
        if(rc.onTheMap(loc)) {
            there = new LocationInfo(loc);
            if(best.compareTo(there) < 0) best = there;
        }
        loc = me.translate(1, 1);
        if(rc.onTheMap(loc)) {
            there = new LocationInfo(loc);
            if(best.compareTo(there) < 0) best = there;
        }

        rc.setIndicatorString("FARMER " + best.dir);
        if(rc.canMove(best.dir)) {
            rc.move(best.dir);
            me = rc.getLocation();
        } 

        /* Original:
        LocationInfo best = new LocationInfo(me);
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
        */
    }

    // Tries to mine in 3x3 square around Miner, and leaves leaveLead amount at location
    public void mine(int leaveLead) throws GameActionException {
        MapLocation mineLocation = me;

        mineLocation = me.translate(-1, -1);
        while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
            rc.mineGold(mineLocation);
        }
        while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
            rc.mineLead(mineLocation);
            income++;
        }
        mineLocation = me.translate(-1, 0);
        while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
            rc.mineGold(mineLocation);
        }
        while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
            rc.mineLead(mineLocation);
            income++;
        }
        mineLocation = me.translate(-1, 1);
        while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
            rc.mineGold(mineLocation);
        }
        while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
            rc.mineLead(mineLocation);
            income++;
        }
        mineLocation = me.translate(0, -1);
        while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
            rc.mineGold(mineLocation);
        }
        while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
            rc.mineLead(mineLocation);
            income++;
        }
        mineLocation = me.translate(0, 0);
        while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
            rc.mineGold(mineLocation);
        }
        while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
            rc.mineLead(mineLocation);
            income++;
        }
        mineLocation = me.translate(0, 1);
        while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
            rc.mineGold(mineLocation);
        }
        while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
            rc.mineLead(mineLocation);
            income++;
        }
        mineLocation = me.translate(1, -1);
        while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
            rc.mineGold(mineLocation);
        }
        while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
            rc.mineLead(mineLocation);
            income++;
        }
        mineLocation = me.translate(1, 0);
        while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
            rc.mineGold(mineLocation);
        }
        while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
            rc.mineLead(mineLocation);
            income++;
        }
        mineLocation = me.translate(1, 1);
        while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
            rc.mineGold(mineLocation);
        }
        while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
            rc.mineLead(mineLocation);
            income++;
        }
        /* Original:
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
                    rc.mineLead(mineLocation);
                    income++;
                }
            }
        }
        */
    }

    // Reports no-lead location to Archon within radius 5, only replaces if closer + reports income
    public void report() throws GameActionException {
        MapLocation res = null;
        MapLocation[] nearby = rc.getAllLocationsWithinRadiusSquared(me, 5);
        for(MapLocation l : nearby) {
            if(rc.onTheMap(l) && rc.senseLead(l) == 0) {
                RobotInfo r = rc.senseRobotAtLocation(l);
                if(r != null && r.getType() == RobotType.ARCHON) continue;
                res = l;
                break;
            }
        }

        if(res == null) return;

        int cur = rc.readSharedArray(Util.getArchonMemoryBlock(rank) + 1);
        MapLocation curLoc = Util.getLocationFromInt(cur);
        if(cur == 0 || archonLocation.distanceSquaredTo(curLoc) > archonLocation.distanceSquaredTo(res)) {
            rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 1, Util.getIntFromLocation(res));
        }
        
        rc.writeSharedArray(62, rc.readSharedArray(62) + income);
    }

    // Stores information about potential locations to move to in radius 2 around
    class LocationInfo {
        final int LOOK_RADIUS = 9;
        final double A = -100, B = -2.5, C = 3, D = 0.1, E = -1000, F = 1000;
        int visibleFarmers; // # of farmers visible in radius LOOK_RADIUS
        int rubble; // rubble level at the square
        double minDistArchon; // (not-squared) distance to archon
        int leadAround; // number of squares in radius 2 around with lead > 1
        double adjacentToArchon; // 0 if not adjacent, squared distance to archon otherwise
        int outsideTether; // 0 if in, 1 if out
        Direction dir;

        public LocationInfo(MapLocation loc) throws GameActionException {
            MapLocation l = null;

            RobotInfo[] nearby = rc.senseNearbyRobots(loc, LOOK_RADIUS, rc.getTeam());
            for(RobotInfo r : nearby) {
                if(r.getType() == RobotType.MINER) visibleFarmers++;
            }
            if(rc.getRoundNum() < 100) visibleFarmers = 0;
            // The assumption is that if we're the only miner near our Archon, we don't count any other miner (no foragers)

            rubble = rc.senseRubble(loc);

            minDistArchon = Math.sqrt(loc.distanceSquaredTo(archonLocation));
            int n = rc.getArchonCount();
            for(int i = 0; i < n; i++) {
                minDistArchon = Math.min(minDistArchon, Math.sqrt(loc.distanceSquaredTo(Util.getLocationFromInt(rc.readSharedArray(i)))));
            }
            System.out.println(loc + " " + minDistArchon);
            if(minDistArchon * minDistArchon <= 4) adjacentToArchon = 4 - minDistArchon * minDistArchon;

            l = loc.translate(-1, -1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = loc.translate(-1, 0);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = loc.translate(-1, 1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = loc.translate(0, -1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = loc.translate(0, 0);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = loc.translate(0, 1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = loc.translate(1, -1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = loc.translate(1, 0);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = loc.translate(1, 1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);

            if(tetherRadius != 0 && minDistArchon > tetherRadius) outsideTether = 1;

            /* Original:
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    MapLocation l = new MapLocation(loc.x + dx, loc.y + dy);
                    if(rc.onTheMap(l) && rc.senseLead(l) > 0) leadAround += rc.senseLead(l);
                }
            }
            */
            dir = me.directionTo(loc);
        }

        public double compareTo(LocationInfo other) throws GameActionException {
            return getRating() - other.getRating();
        }

        public double getRating() throws GameActionException {
            return A * visibleFarmers + B * rubble + C * leadAround + D * minDistArchon + E * adjacentToArchon + F * outsideTether;
        }
    }
}