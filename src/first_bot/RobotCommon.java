package first_bot;

import battlecode.common.*;
import javafx.util.converter.LocalDateStringConverter;

import java.util.Random;

public abstract class RobotCommon {

    static RobotController rc;
    static MapLocation me;

    public RobotCommon(RobotController rc){
        this.rc = rc;
        me = rc.getLocation();
    }

    public static int getVisionRadiusSquared(){
        switch(rc.getType()){
            case ARCHON:
            case LABORATORY:
                return 53;
            case BUILDER:
            case MINER:
            case SAGE:
            case SOLDIER:
            case WATCHTOWER:
                return 34;
            default:
                return 20;
        }
    }

    static final Random rng = new Random(6147);
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    abstract void takeTurn() throws GameActionException;
}