package first_bot;

import java.util.Map;
import java.util.Random;
import battlecode.common.*;

public class MinerFarmer extends Unit {
    int income;
    boolean[][] ourMiners = new boolean[Util.LOC_BASE][Util.LOC_BASE]; // stores locations of ourMiners in radius 18

    // We want farmers to continually pick good spots to mine
    public MinerFarmer(RobotController rc, int r, MapLocation loc) throws GameActionException {
        super(rc, r, loc);
    }

    public void takeTurn() throws GameActionException {
        initialize();
        takeAttendance();
        observe();
        move();
        mine(1);
        report();
    }

    // Resets values to fields
    public void initialize() throws GameActionException {
        me = rc.getLocation();
        income = 0;

        /*MapLocation[] nearby = rc.getAllLocationsWithinRadiusSquared(me, 18);
        for(MapLocation l : nearby) {
            ourMiners[l.x][l.y] = false;
        }
        
        RobotInfo[] neighbors = rc.senseNearbyRobots(me, 18, rc.getTeam());
        for(RobotInfo r : neighbors) {
            if(r.getType() == RobotType.MINER) {
                MapLocation m = r.getLocation();
                ourMiners[m.x][m.y] = true;
            }
        }*/ 
    }

    // Chooses best place to move to 
    public void move() throws GameActionException {
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

        mineLocation = me.translate(1, -1);
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
        final double A = -100, B = -3, C = 3, D = 3;
        int visibleMiners; // # of miners visible in radius LOOK_RADIUS
        int rubble; // rubble level at the square
        double distArchon; // (not-squared) distance to archon
        int leadAround; // number of squares in radius 2 around with lead > 1
        boolean isArchon;
        Direction dir;

        public LocationInfo(MapLocation loc) throws GameActionException {
            MapLocation l = null;

            RobotInfo[] nearby = rc.senseNearbyRobots(loc, LOOK_RADIUS, rc.getTeam());
            for(RobotInfo r : nearby) {
                if(r.getType() == RobotType.MINER) visibleMiners++;
            }

            rubble = rc.senseRubble(loc);
            distArchon = Math.sqrt(loc.distanceSquaredTo(archonLocation));

            l = me.translate(-1, -1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = me.translate(-1, 0);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = me.translate(-1, 1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = me.translate(0, -1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = me.translate(0, 0);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = me.translate(0, 1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = me.translate(1, -1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = me.translate(1, 0);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);
            l = me.translate(1, 1);
            if(rc.onTheMap(l)) leadAround += rc.senseLead(l);

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
            return A * visibleMiners + B * rubble + C * leadAround + D * distArchon;
        }
    }
}