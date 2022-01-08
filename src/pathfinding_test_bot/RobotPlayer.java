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
        }
        
        while(true){
            robot.takeTurn();
            Clock.yield();
        }
    }
}