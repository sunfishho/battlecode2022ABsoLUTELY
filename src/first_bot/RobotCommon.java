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

    abstract void takeTurn() throws GameActionException;
}