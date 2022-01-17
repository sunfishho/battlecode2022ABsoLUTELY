
package pathfinding_test_bot;

import battlecode.common.*;


public class Archon extends RobotCommon{

    // static RobotController rc;

    static boolean checkedNearby = false;
    static MapLocation home;
    static int rank = -1; // 1-based
    static int[] knownMap = new int[62 * 60];
    static boolean wroteArchons;
    static int numScoutsSent;
    static boolean isScoutingDone = false;
    /*
        Values of important locations are stored on the map, negative values correspond to opponent:
            1-4: archons of corresponding rank
            5: miner sent to location
    */
    static boolean builtMinersLast; // true if miners were built on the previous turn

    public Archon(RobotController rc) throws GameActionException{
        super(rc);
    }

    public void takeTurn() throws GameActionException{
        // if (rc.getRoundNum() == 1 && rc.getTeamLeadAmount(rc.getTeam()) == 200){
        //     rc.buildRobot(RobotType.MINER, Direction.EAST);
        // }
    }
}