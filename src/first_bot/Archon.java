
package first_bot;

import battlecode.common.*;


public class Archon extends RobotCommon{

    // static RobotController rc;

    static int rank = -1; // 0-based

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

    public void takeTurn() throws GameActionException {
        if(rank == -1) establishRank();

        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
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
            rc.setIndicatorString("Trying to build a soldier");
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
        }
    }
}