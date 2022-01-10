package bot0109v2;

import battlecode.common.*;

import java.util.Random;

public abstract class RobotCommon {

    static RobotController rc;
    static MapLocation me, archonLocation;
    static int rank = -1; // for now, Archons continue with their normal rank setting algorithm, other bots will be set through constructor
    static int round = 0;

    public RobotCommon(RobotController myRC, int r, MapLocation loc){
        rc = myRC;
        me = rc.getLocation();
        rank = r;
        archonLocation = loc;
        round = rc.getRoundNum();
    }

    public static MapLocation nearestEnemyArchon(MapLocation loc, int sym) throws GameActionException{//returns closest enemy archon to loc if sym is determined, assuming enemy has not moved archons
        MapLocation ans = new MapLocation(-1, -1);
        if(sym == 0){
            return ans;
        }
        int w = rc.getMapWidth();
        int h = rc.getMapHeight();
        if (sym == 1){//symmetric w.r.t. vertical midline
            MapLocation mirror = new MapLocation(w - 1 - loc.x, loc.y);
            MapLocation mirrorans = nearestArchon(mirror);
            ans = new MapLocation(w-1-mirrorans.x, mirrorans.y);
        }
        if(sym == 2){//symmetric w.r.t. horizontal midline
            MapLocation mirror = new MapLocation(loc.x, h-1-loc.y);
            MapLocation mirrorans = nearestArchon(mirror);
            ans = new MapLocation(mirrorans.x, h-1-mirrorans.y);
        }
        if(sym == 3){//symmetric w.r.t. center
            MapLocation mirror = new MapLocation(w-1-loc.x, h-1-loc.y);
            MapLocation mirrorans = nearestArchon(mirror);
            ans = new MapLocation(w-1-mirrorans.x, h-1-mirrorans.y);
        }
        return ans;
    }

    public static MapLocation nearestArchon(MapLocation loc) throws GameActionException{//return nearest archon
        MapLocation best = new MapLocation(-1, -1);
        int mindist = 69;
        for(int i = 0; i < rc.getArchonCount(); i++){
            MapLocation archonLocation = Util.getLocationFromInt(rc.readSharedArray(i));
            int dist = Util.distanceMetric(archonLocation, loc);
            if(dist < mindist){
                mindist = dist;
                best = archonLocation;
            }
        }
        return best;
    }

    public static int getVisionRadiusSquared(){
        switch(rc.getType()){
            case ARCHON:
                return 34;
            case LABORATORY:
                return 53;
            case BUILDER:
            case MINER:
                return 20;
            case SAGE:
            case SOLDIER:
            case WATCHTOWER:
                return 34;
            default:
                return 20;
        }
    }

    public static MapLocation chooseRandomInitialDestination(){
        Random rng2 = new Random(rc.getRoundNum());
        int[] initialDestinationPossibilities = new int[] {0, 1, 2, 3, 4};
        int x_coord = rng2.nextInt(rc.getMapWidth() * 4 / 5) + rc.getMapWidth() * 1 / 10;
        int y_coord = rng2.nextInt(rc.getMapHeight() * 4 / 5) + rc.getMapHeight() * 1 / 10;
        return new MapLocation(x_coord, y_coord);
    }

    static final Random rng = new Random(6147);

    abstract void takeTurn() throws GameActionException;
}