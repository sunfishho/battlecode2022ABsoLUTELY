package bot0112;

import battlecode.common.*;

import java.util.Random;

public abstract class RobotCommon {

    static RobotController rc;
    static MapLocation me, archonLocation;
    static int rank = -1; // for now, Archons continue with their normal rank setting algorithm, other bots will be set through constructor
    static int round = 0;
    static int actionRadius, visionRadius;
    Team myTeam, enemyTeam;

    public RobotCommon(RobotController myRC, int r, MapLocation loc){
        rc = myRC;
        me = rc.getLocation();
        rank = r;
        archonLocation = loc;
        round = rc.getRoundNum();
        myTeam = rc.getTeam();
        enemyTeam = myTeam.opponent();
        actionRadius = rc.getType().actionRadiusSquared;
        visionRadius = rc.getType().visionRadiusSquared;
    }

    public static MapLocation nearestEnemyArchon(MapLocation loc, int sym) throws GameActionException{//returns closest enemy archon to loc if sym is determined, assuming enemy has not moved archons
        MapLocation ans = new MapLocation(-1, -1);
        if(sym == 0){
            return ans;
        }
        int w = rc.getMapWidth();
        int h = rc.getMapHeight();
        if (sym == 1){//symmetric w.r.t. vertical midline
            MapLocation mirror = Util.horizontalRefl(loc);
            MapLocation mirrorans = nearestArchon(mirror);
            ans = Util.horizontalRefl(mirrorans);
        }
        if(sym == 2){//symmetric w.r.t. horizontal midline
            MapLocation mirror = Util.verticalRefl(loc);
            MapLocation mirrorans = nearestArchon(mirror);
            ans = Util.verticalRefl(mirrorans);
        }
        if(sym == 3){//symmetric w.r.t. center
            MapLocation mirror = Util.centralRefl(loc);
            MapLocation mirrorans = nearestArchon(mirror);
            ans = Util.centralRefl(mirrorans);
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

    //if we view array as 1024 bits rather than 64 ints

    public static void updateBitsSharedArray(int st, int en, long x) throws GameActionException{//write x into bits in [st, en]
        for(int i = 0; i < 64; i++){
            int a = 16 * i;
            int b = 16 * i + 15;
            if(a > en){
                break;
            }
            if(b < st){
                continue;
            }
            int am = Util.max(a, st);
            int bm = Util.min(b, en);
            int t = rc.readSharedArray(i);
            for(int j = a; j <= b; j++){
                if(j < am || j > bm){
                    continue;
                }
                int k = en - j;//index of bit in x
                int pow = (1 << (b-j));
                if((x & (1L << k)) > 0){
                    t |= pow;
                }
                else{
                    t &= (65535 - pow);
                }
            }
            rc.writeSharedArray(i, t);
        }
    }

    public static long readBitsSharedArray(int st, int en) throws GameActionException{
        long res = 0;
        for(int i = 0; i < 64; i++){
            int a = 16 * i;
            int b = 16 * i + 15;
            if(a > en){
                break;
            }
            if(b < st){
                continue;
            }
            int am = Util.max(a, st);
            int bm = Util.min(b, en);
            int t = rc.readSharedArray(i);
            for(int j = a; j <= b; j++){
                if(j < am || j > bm){
                    continue;
                }
                int k = en - j;//index of bit in res
                int pow = (1 << (b-j));
                if ((t & pow) > 0){
                    res += (1L << k);
                }
            }
        }
        return res;
    }
}