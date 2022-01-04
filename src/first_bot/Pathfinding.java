package first_bot;
import battlecode.common.*;



//future optimizations:
//hardcode in dx/dy values for each one
//perform loop unrolling

public class Pathfinding {
    static int distanceSquared;
    static MapLocation currentLocation;
    RobotCommon robot;
    // static int maxStraightDistance;

    static int[5][5] rubbleLevels;
    static int[5][5] distances;
    static int[5][5] predecessor; 

    public Pathfinding(RobotCommon robot){
        this.robot = robot;
        distanceSquared = getVisionRadiusSquared(robot);
        currentLocation = robot.rc.getLocation();
        // maxStraightDistance = (int) Math.sqrt(distanceSquared);
    }

    public void populateArrays(){
        for (int dx = 4; dx >= 0; dx--){
            for (int dy = 4; dy >= 0; dy--){
                rubbleLevels[dx][dy] = RobotController.senseRubble(MapLocation.add(MapLocation(dx - 2, dy - 2), currentLocation));
                //if the spot is occupied, pretend like the rubble level is just a really large number
                if (RobotController.canSenseRobotAtLocation(MapLocation.add(MapLocation(dx - 2, dy - 2), currentLocation)){
                    rubbleLevels[dx][dy] = 100000;
                }
                distances[dx][dy] = 1000000;
            }
        }
        distances[0][0] = 0;
    }

    static final int dxDiff[8] = {-1, -1, -1, 0, 0, 1, 1, 1};
    static final int dyDiff[8] = {-1, 0, 1, -1, 1, -1, 0, 1};

    //http://web.mit.edu/agrebe/www/battlecode/20/index.html#navigation see navigation section
    //also not 100% sure the iteration order is best here (going from one corner to the other in bellman-ford)
    public static void bellmanFord(MapLocation destination){
        //iters should probably be 1 or 2 because greediness is good
        for (iters = 2; iters >= 0; iters--){
            for (int dx = 4; dx >= 0; dx--){
                for (int dy = 4; dy >= 0; dy--){
                    for (int directionIdx = 7; directionIdx >= 0; directionIdx--){
                        if (dx + dxDiff[directionIdx] >= 0 && dx + dxDiff[directionIdx] <= 4 && 
                            dy + dyDiff[directionIdx] >= 0 && dy + dyDiff[directionIdx] <= 4 && ){
                            if (distances[dx][dy] + rubbleLevels[dx][dy] < distances[dx + dxDiff[directionIdx]][dy + dyDiff[directionIdx]]){
                                distances[dx + dxDiff[directionIdx]][dy + dyDiff[directionIdx]] = distances[dx][dy] + rubbleLevels[dx][dy];
                                predecessor[dx][dy] = directionIdx;
                            }
                        }
                    }
                }
            }
        }
        // Direction optDirection = MapLocation.directionTo(destination);
        // int optEndX, optEndY;
        // switch(optDirection){
        //     case NORTH:
        //         optEndX = 2;
        //         optEndY = 4;
        //         break;
        //     case EAST:
        //         optEndX = 4;
        //         optEndY = 2;
        //     case SOUTH:
        //         optEndX = 2;
        //         optEndY = 0;
        //     case WEST:
        //         optEndX = 0;
        //         optEndY = 2;
        //     case NORTHWEST:
        //         optEndX = 4;
        //         optEndY = 4;
        //     case SOUTHWEST:
        //         optEndX = 4;
        //         optEndY = 0;
        //     case SOUTHEAST:
        //         optEndX = 0;
        //         optEndY = 0;
        //     default:
        //         optEndX = 0;
        //         optEndY = 4;
        // }
        // while (optEndX != 0 && optEndY != 0){

        // }
    }

}