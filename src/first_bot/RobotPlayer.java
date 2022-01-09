package first_bot;

import battlecode.common.*;
import java.util.Random;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {
    RobotController rc;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        RobotCommon robot;
<<<<<<< Updated upstream
        switch(rc.getType()){
            case ARCHON:
                robot = new Archon(rc);
                break;
            case BUILDER:
                robot = new Builder(rc);
                break;
            case LABORATORY:
                robot = new Laboratory(rc);
                break;
            case MINER:
                robot = new Miner(rc);
                break;
            case SAGE:
                robot = new Sage(rc);
                break;
            case SOLDIER:
                robot = new Soldier(rc);
                break;
            default:
                robot = new Watchtower(rc);
                break;
=======
        if(rc.getType() == RobotType.ARCHON) {
            robot = new Archon(rc, -1, rc.getLocation());
        }
        else {
            /*
                If robot is not an Archon, find its archon and establish its subtype. 
                Subtype is communicated as rc.readSharedArray(Util.getArchonMemoryBlock(rank))/Util.MAX_LOC, so default is 0
                if you're just writing down a result from getIntFromLocation.
            */
            RobotInfo archon;
            MapLocation archonLocation = new MapLocation(0, 0);
            int rank = -1;
            for(RobotInfo neighbor : rc.senseNearbyRobots(2, rc.getTeam())) {
                if(neighbor.getType() == RobotType.ARCHON) {
                    archon = neighbor;
                    archonLocation = neighbor.getLocation();
                    break;
                }
            }
            for(int i = 0; i < 4; i++) {
                if(rc.readSharedArray(i) == Util.getIntFromLocation(archonLocation)) {
                    rank = i + 1;
                    break;
                }
            }
            int subtype = rc.readSharedArray(Util.getArchonMemoryBlock(rank)) / Util.MAX_LOC;
            switch(rc.getType()){
                case BUILDER:
                    robot = new Builder(rc, rank, archonLocation);
                    break;
                case LABORATORY:
                    robot = new Laboratory(rc, rank, archonLocation);
                    break;
                case MINER:
                    MapLocation target = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(rank)));
                    switch(subtype) {
                        case 1: 
                            robot = new MinerScout(rc, rank, archonLocation, target);
                            break;
                        case 2: 
                            robot = new MinerForager(rc, rank, archonLocation, target);
                            break;
                        default:
                            robot = new Miner(rc, rank, archonLocation, target);
                            break;
                    }
                    break;
                case SAGE:
                    robot = new Sage(rc, rank, archonLocation);
                    break;
                case SOLDIER:
                    robot = new Soldier(rc, rank, archonLocation);
                    break;
                default:
                    robot = new Watchtower(rc, rank, archonLocation);
                    break;
            }
>>>>>>> Stashed changes
        }
        
        while(true){
            Util.WIDTH = rc.getMapWidth();
            Util.HEIGHT = rc.getMapHeight();
            robot.takeTurn();
            Clock.yield();
        }
    }
}