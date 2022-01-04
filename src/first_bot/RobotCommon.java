package first_bot;

import battlecode.common.*;
import java.util.Random;

public abstract class RobotCommon {

    static RobotController rc;

    public RobotCommon(RobotController rc){
        this.rc = rc;
        //do more stuff later
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