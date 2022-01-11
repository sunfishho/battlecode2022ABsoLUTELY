package first_bot;

import battlecode.common.*;

public class MinerScout extends Miner {
    //this is all only for scouts
    public static int[] rubbleSeen = new int[60];
    //if isScout is true, this stores if the scout is going horizontally or vertically
    public static boolean scoutTravelingHorizontally, hasReachedHalfway;
    public static MapLocation halfTarget;

    public MinerScout(RobotController rc, int r, MapLocation loc, MapLocation t) throws GameActionException {
        super(rc, r, loc, t);
        target = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(rank)) % Util.MAX_LOC);
        scoutTravelingHorizontally = (Util.abs(target.x - me.x) > Util.abs(target.y - me.y));
        for (int idx = 59; idx >= 0; idx--){
            rubbleSeen[idx] = -1;
        }
        halfTarget = new MapLocation((target.x + me.x)/2, (target.y + me.y)/2);
    }

    public void doScoutRoutine() throws GameActionException {
        tryToMine(1);
        Pathfinding pf = new Pathfinding(this);
        Direction dir = Direction.CENTER;
        if (!hasReachedHalfway){
            dir = pf.findBestDirection(halfTarget);
        }
        else{
            dir = pf.findBestDirection(target);
        }
        if (me.distanceSquaredTo(halfTarget) <= 1){
            hasReachedHalfway = true;
        }
        if (me.distanceSquaredTo(target) <= 1){
            rc.disintegrate();
        }
        rc.setIndicatorString("MINER SCOUT: " + dir + " " + me + " " + target + " " + true);
        if (rc.canMove(dir)){
            rc.move(dir);
        }
        findRubbleHeightsOnLine();
    }

    public void findRubbleHeightsOnLine() throws GameActionException{
        if (scoutTravelingHorizontally){
            int coord = target.y;
            //coord is the y coord that's fixed
            for (int xcoord = 0; xcoord < Util.WIDTH; xcoord++){
                if (rc.canSenseLocation(new MapLocation(xcoord, coord))){
                    rubbleSeen[xcoord] = rc.senseRubble(new MapLocation(xcoord, coord));
                    if (rubbleSeen[Util.WIDTH - xcoord - 1] != -1 && rubbleSeen[Util.WIDTH - xcoord - 1] != rubbleSeen[xcoord]){
                        reportSymmetryBroken();
                        rc.disintegrate();
                    }
                }
            }
        }
        else{
            int coord = target.x;
            for (int ycoord = 0; ycoord < Util.WIDTH; ycoord++){
                if (rc.canSenseLocation(new MapLocation(coord, ycoord))){
                    rubbleSeen[ycoord] = rc.senseRubble(new MapLocation(coord, ycoord));
                    if (rubbleSeen[Util.HEIGHT - ycoord - 1] != -1 && rubbleSeen[Util.HEIGHT - ycoord - 1] != rubbleSeen[ycoord]){
                        reportSymmetryBroken();
                        rc.disintegrate();
                    }
                }
            }
        }
    }

    public void reportSymmetryBroken() throws GameActionException{
        int memoryIndex = Util.getSymmetryMemoryBlock();
        if (scoutTravelingHorizontally){
            rc.writeSharedArray(memoryIndex, rc.readSharedArray(memoryIndex) - 1);
            System.out.println("NEW SYMMETRY RECORDING: " + rc.readSharedArray(16));
            return;
        }
        else {
            rc.writeSharedArray(memoryIndex, rc.readSharedArray(memoryIndex) - 2);
            System.out.println("NEW SYMMETRY RECORDING: " + rc.readSharedArray(16));
            return;
        }
    }

    public void takeTurn() throws GameActionException {
        me = rc.getLocation();
        doScoutRoutine();
    }
}
