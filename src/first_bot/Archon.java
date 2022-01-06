
package first_bot;

import battlecode.common.*;


public class Archon extends RobotCommon{

    // static RobotController rc;

    static int rank = -1; // 0-based
    static boolean checkedNearby = false;
    static MapLocation home;

    public Archon(RobotController rc){
        super(rc);
    }

    // Establish an order between the Archons by writing to the shared array.
    public void establishRank() throws GameActionException {
        for(int i = 0; i < 4; i++) {
            if(rc.readSharedArray(i) == 0) {
                rc.writeSharedArray(i, Util.getIntFromLocation(me));
                rank = i;
                break;
            }
        }
    }

    //see if any nearby squares have significantly less rubble
    public void relocCheck() throws GameActionException {
        MapLocation loc = rc.getLocation();
        int lx = loc.x;
        int ly = loc.y;
        int bestd = 0;
        int bestr = rc.senseRubble(loc);
        MapLocation newhome = loc;
        for(int dx = 2; dx >= -2; dx--){
            for(int dy = 2; dy >= -2; dy--){
                int d = Util.max(Util.abs(dx), Util.abs(dy));
                if(d == 0){
                    continue;
                }
                MapLocation mc = new MapLocation(dx - 2 + lx, dy - 2 + ly);
                try{//see if mc is a viable place to move to
                    int rub = rc.senseRubble(mc);
                    if((10 + rub) * (100 - 10 * bestd) < (10 + bestr) * (100 - 10 * d)){
                        bestd = d;
                        bestr = rub;
                        newhome = mc;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        home = newhome;
    }

    public void takeTurn() throws GameActionException {
        if(rank == -1) establishRank();

        if(!checkedNearby){
            relocCheck();
            checkedNearby = true;
        }

        if(me != home){//we should try moving to a nearby place with less rubble
            Direction dir = me.directionTo(home);
            if(rc.canMove(dir)){
                rc.move(dir);
                me = rc.getLocation();
            }
        }

        rc.setIndicatorString(Integer.toString(rank));

        // Pick a direction to build in.
        Direction dir = Util.directions[rng.nextInt(Util.directions.length)];
        if (rng.nextBoolean()) {
            // Let's try to build a miner.
            // rc.setIndicatorString("Trying to build a miner");
            if (rc.canBuildRobot(RobotType.MINER, dir)) {
                rc.buildRobot(RobotType.MINER, dir);

                for(int dx = -5; dx <= 5; dx++) {
                    for(int dy = -5; dy <= 5; dy++) {
                        MapLocation target = new MapLocation(me.x + dx, me.y + dy);
                        if(rc.canSenseLocation(target) && rc.senseLead(target) > 0) {
                            rc.writeSharedArray(Util.getArchonMemoryBlock(rank), Util.getIntFromLocation(target));
                            break;
                        }
                    }
                }

            }
        } else {
            // Let's try to build a soldier.
            // rc.setIndicatorString("Trying to build a soldier");
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
        }
    }
}