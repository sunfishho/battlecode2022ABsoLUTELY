package bot0117;

import battlecode.common.*;

import java.util.Random;

public abstract class RobotCommon {

    static RobotController rc;
    static MapLocation me, archonLocation;
    static int rank = -1; // for now, Archons continue with their normal rank setting algorithm, other bots will be set through constructor
    static int round = 0;
    static int actionRadius, visionRadius;
    
    Team myTeam, enemyTeam;
    MapLocation[] archonLocationsInitial = new MapLocation[4];
    
    static Pathfinding pf;
    

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
        pf = new Pathfinding(this);
    }

    public static void takeAttendance() throws GameActionException{
        switch(rc.getType()){
            case MINER: 
                rc.writeSharedArray(28, rc.readSharedArray(28) + 1);
                return;
            case SOLDIER: 
                rc.writeSharedArray(29, rc.readSharedArray(29) + 1);
                return;
            default: return;
        }
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

    public static int rankOfNearestArchon(MapLocation loc) throws GameActionException{
        int best = 0;
        int mindist = 69;
        for(int i = 0; i < rc.getArchonCount(); i++){
            MapLocation archonLocation = Util.getLocationFromInt(rc.readSharedArray(i));
            int dist = Util.distanceMetric(archonLocation, loc);
            if(dist < mindist){
                mindist = dist;
                best = i + 1;
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
        int x_coord = rng2.nextInt(rc.getMapWidth() - 2) + 1;
        int y_coord = rng2.nextInt(rc.getMapHeight() - 2) + 1;
        return new MapLocation(x_coord, y_coord);
    }

    static final Random rng = new Random(1);

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

    //if we're near a midline or the center, use rubble counts to try and infer symmetry
    public void observeSymmetry() throws GameActionException{
        MapLocation[] squaresToCheck = new MapLocation[]{rc.getLocation()};
        int length = squaresToCheck.length;
        int currentSymmetry = rc.readSharedArray(16);//current knowledge about symmetry
        //horizontal symmetry not ruled out yet
        if(currentSymmetry % 2 == 1) {
            for (int i = 0; i < length; i++) {
                MapLocation sq = squaresToCheck[i];
                if (!Util.inGrid(sq) || 2 * sq.x >= Util.WIDTH) {//suffices to check squares on one half only
                    continue;
                }
                int pb = rc.senseLead(sq);
                if (rc.canSenseLocation(Util.horizontalRefl(sq))) {
                    if (pb != rc.senseLead(Util.horizontalRefl(sq))) {//horizontal symmetry dead
                        currentSymmetry &= 6;//set horizontal symmetry bit to 0
                        break;
                    }
                }
            }
        }
        //vertical symmetry not ruled out yet
        if((currentSymmetry & 2) > 0) {
            for (int i = 0; i < length; i++) {
                MapLocation sq = squaresToCheck[i];
                if (!Util.inGrid(sq) || 2 * sq.x >= Util.WIDTH) {//suffices to check squares on one half only
                    continue;
                }
                int pb = rc.senseLead(sq);
                if (rc.canSenseLocation(Util.verticalRefl(sq))) {
                    if (pb != rc.senseLead(Util.verticalRefl(sq))) {//horizontal symmetry dead
                        currentSymmetry &= 5;//set vertical symmetry bit to 0
                        break;
                    }
                }
            }
        }
        //rotational symmetry not ruled out yet
        if(currentSymmetry >= 4) {
            for (int i = 0; i < length; i++) {
                MapLocation sq = squaresToCheck[i];
                if (!Util.inGrid(sq) || 2 * sq.x >= Util.WIDTH) {//suffices to check squares on one half only
                    continue;
                }
                int pb = rc.senseLead(sq);
                if (rc.canSenseLocation(Util.centralRefl(sq))) {
                    if (pb != rc.senseLead(Util.centralRefl(sq))) {//horizontal symmetry dead
                        currentSymmetry &= 3;//set rotational symmetry bit to 0
                        break;
                    }
                }
            }
        }
    }

    

}