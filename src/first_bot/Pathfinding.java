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
    //will store the direction used to get to a particular square in bellman-ford
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
                    rubbleLevels[dx][dy] = 10000;
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
    public void bellmanFord(MapLocation destination){
        // MapLocation intermediateDestination = currentLocation;
        // //so our destination is actually within range lol
        // if (abs(currentLocation.x - destination.x) <= 2 && abs(currentLocation.y - destination.y) <= 2){
        //     intermediateDestination = destination;
        // }
        //iters should probably be 1 or 2 because greediness is good
        for (iters = 2; iters >= 0; iters--){
            for (int dx = 4; dx >= 0; dx--){
                for (int dy = 4; dy >= 0; dy--){
                    for (int directionIdx = 7; directionIdx >= 0; directionIdx--){
                        if (dx + dxDiff[directionIdx] >= 0 && dx + dxDiff[directionIdx] <= 4 && 
                            dy + dyDiff[directionIdx] >= 0 && dy + dyDiff[directionIdx] <= 4 && ){
                            if (distances[dx][dy] + rubbleLevels[dx + dxDiff[directionIdx][dy + dyDiff[directionIdx]] < distances[dx + dxDiff[directionIdx]][dy + dyDiff[directionIdx]]){
                                distances[dx + dxDiff[directionIdx]][dy + dyDiff[directionIdx]] = distances[dx][dy] + rubbleLevels[dx][dy];
                                predecessor[dx + dxDiff[directionIdx]][dy + dyDiff[directionIdx]] = directionIdx;
                            }
                        }
                    }
                }
            }
        }
    }

    // NOTE: going to assume that rubble is roughly additive, i.e. that the cost of the path you 
    // take really only depends on the sum of the rubble you went through
    public Direction returnBestDirection(MapLocation destination){
        //if we can't even move, we can't do anything, so just... return lol
        if (RobotController.getMovementCooldownTurns() > 0){
            return CENTER;
        }
        //otherwise, do bellman ford to populate the arrays

        this.bellmanFord(destination);
        //note that for squares bigger than 5x5, this should probably be modified to comparing angles instead of cheaply doing this
        //Need to somehow figure out the intermediate destination, returning CENTER for now
        return CENTER;
    }

}