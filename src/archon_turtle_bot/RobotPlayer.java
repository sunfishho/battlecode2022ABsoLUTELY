package archon_turtle_bot;

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
        Util.HEIGHT = rc.getMapHeight();
        Util.WIDTH = rc.getMapWidth();
        RobotCommon robot;
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
            int rank = 0;
            for(RobotInfo neighbor : rc.senseNearbyRobots(2, rc.getTeam())) {
                if(neighbor.getType() == RobotType.ARCHON) {
                    MapLocation loc = neighbor.getLocation();
                    for(int i = 0; i < 4; i++) {
                        if(rc.readSharedArray(i) == Util.getIntFromLocation(loc)) {
                            rank = i + 1;
                            break;
                        }
                    }
                    int typeCommunicated = rc.readSharedArray(Util.getArchonMemoryBlock(rank) + 2);
                    RobotType r = RobotType.MINER;
                    switch(typeCommunicated) {
                        case 3:
                            r = RobotType.SAGE;
                            break;
                        case 2:
                            r = RobotType.SOLDIER;
                            break;
                        case 1:
                            r = RobotType.BUILDER;
                            break;
                        default:
                            r = RobotType.MINER;
                            break;
                    }
                    if(r.equals(rc.getType())) {
                        archon = neighbor;
                        archonLocation = neighbor.getLocation();
                        break;
                    }
                }
            }
            int readValue = rc.readSharedArray(Util.getArchonMemoryBlock(rank));
            int subtype = readValue / Util.MAX_LOC;
            int targetValue = readValue % Util.MAX_LOC;
            MapLocation target = Util.getLocationFromInt(targetValue);
            switch(rc.getType()){
                case BUILDER:
                    switch(subtype) {
                        case 1:
                            robot = new Builder(rc, rank, archonLocation, target, 1);
                            break;
                        default:
                            robot = new Builder(rc, rank, archonLocation);
                            break;
                    }
                    break;
                case LABORATORY:
                    robot = new Laboratory(rc, rank, archonLocation);
                    break;
                case MINER:
                    switch(subtype) {
                        case 1:
                            if(targetValue == 0) robot = new Miner(rc, rank, archonLocation);
                            else robot = new Miner(rc, rank, archonLocation, target);
                            break;
                        default:
                            if(targetValue == 0) robot = new MinerFarmer(rc, rank, archonLocation);
                            else robot = new MinerFarmer(rc, rank, archonLocation, target);
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
        }
        
        while(true){
            Util.WIDTH = rc.getMapWidth();
            Util.HEIGHT = rc.getMapHeight();
            if (rc.getType() == RobotType.SOLDIER || rc.getType() == RobotType.SAGE || rc.getType() == RobotType.MINER){
                rc.writeSharedArray(40, rc.readSharedArray(40) + rc.getHealth());
            }
            robot.takeTurn();
            Clock.yield();
        }
    }
}