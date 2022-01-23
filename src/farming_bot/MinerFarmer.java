package farming_bot;

import java.util.Map;
import java.util.Random;
import battlecode.common.*;

public class MinerFarmer extends Unit {
    // We want farmers to continually pick good spots to mine
    public MinerFarmer(RobotController rc, int r, MapLocation loc) throws GameActionException {
        super(rc, r, loc);
    }

    public void takeTurn() throws GameActionException {
        me = rc.getLocation();
        mine(1);
        move();
        mine(1);
        report();
    }

    // Chooses best place to move to 
    public void move() throws GameActionException {
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
    }

    // Tries to mine in 3x3 square around Miner, and leaves leaveLead amount at location
    public void mine(int leaveLead) throws GameActionException {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
                    rc.mineLead(mineLocation);
                }
            }
        }
    }

    // Reports no-lead location to Archon within radius 5, only replaces if closer
    public void report() throws GameActionException {
        MapLocation res = null;
        MapLocation[] nearby = rc.getAllLocationsWithinRadiusSquared(me, 5);
        for(MapLocation l : nearby) {
            if(rc.onTheMap(l) && rc.senseLead(l) == 0) {
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
    }

    // Stores information about potential locations to move to in radius 2 around
    class LocationInfo {
        final int LOOK_RADIUS = 9;
        final int A = -100, B = -10, C = 1, D = 1;
        int visibleMiners; // # of miners visible in radius LOOK_RADIUS
        int rubble; // rubble level at the square
        int distArchon; // distance to archon
        int leadAround; // number of squares in radius 2 around with lead > 1
        Direction dir;

        public LocationInfo(MapLocation loc) throws GameActionException {
            RobotInfo[] nearby = rc.senseNearbyRobots(loc, LOOK_RADIUS, rc.getTeam());
            for(RobotInfo r : nearby) {
                if(r.getType() == RobotType.MINER) visibleMiners++;
            }
            rubble = rc.senseRubble(loc);
            distArchon = loc.distanceSquaredTo(archonLocation);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    MapLocation l = new MapLocation(loc.x + dx, loc.y + dy);
                    if(rc.onTheMap(l) && rc.senseLead(l) > 0) leadAround += rc.senseLead(l);
                }
            }
            dir = me.directionTo(loc);
        }

        public int compareTo(LocationInfo other) throws GameActionException {
            return getRating() - other.getRating();
        }

        public int getRating() throws GameActionException {
            return A * visibleMiners + B * rubble + C * leadAround + D * distArchon;
        }
    }
}