package first_bot;
import battlecode.common.*;



//future optimizations:
//hardcode in dx/dy values for each one
//perform loop unrolling

public class Pathfinding {
    static int distanceSquared;
    RobotCommon robot;
    // static int maxStraightDistance;

    static int[][] rubbleLevels = new int[5][5];
    static int[][] distances = new int[5][5];
    //will store the direction used to get to a particular square in bellman-ford
    static int[][] predecessor = new int[5][5]; 

    public Pathfinding(RobotCommon robot){
        this.robot = robot;
        distanceSquared = robot.rc.getType().visionRadiusSquared;
        // maxStraightDistance = (int) Math.sqrt(distanceSquared);
    }

    public void populateArrays(){
        for (int dx = 4; dx >= 0; dx--){
            for (int dy = 4; dy >= 0; dy--){
                MapLocation mc = new MapLocation(dx - 2 + robot.me.x, dy - 2 + robot.me.y);
                predecessor[dx][dy] = -1;
                try {
                    rubbleLevels[dx][dy] = robot.rc.senseRubble(mc);
                } catch (Exception e){
                    e.printStackTrace();
                }
                //if the spot is occupied, pretend like the rubble level is just a really large number
                if (mc.x < 0 || mc.y < 0 || mc.x >= this.robot.rc.getMapWidth() || mc.y >= this.robot.rc.getMapHeight() || this.robot.rc.canSenseRobotAtLocation(mc)){
                    rubbleLevels[dx][dy] = 10000;
                }
                distances[dx][dy] = 1000000;
            }
        }
        distances[0][0] = 0;
    }
    

    //http://web.mit.edu/agrebe/www/battlecode/20/index.html#navigation see navigation section
    //also not 100% sure the iteration order is best here (going from one corner to the other in bellman-ford)
    public void bellmanFord(MapLocation destination){
        // MapLocation intermediateDestination = currentLocation;
        // //so our destination is actually within range lol
        // if (abs(currentLocation.x - destination.x) <= 2 && abs(currentLocation.y - destination.y) <= 2){
        //     intermediateDestination = destination;
        // }
        //iters should probably be 1 or 2 because greediness is good
        for (int iters = Util.NUM_ITERATIONS_BELLMAN_FORD - 1; iters >= 0; iters--){
            for (int dx = 4; dx >= 0; dx--){
                for (int dy = 4; dy >= 0; dy--){
                    for (int directionIdx = 7; directionIdx >= 0; directionIdx--){
                        if (dx + Util.dxDiff[directionIdx] >= 0 && dx + Util.dxDiff[directionIdx] <= 4 && 
                            dy + Util.dyDiff[directionIdx] >= 0 && dy + Util.dyDiff[directionIdx] <= 4){
                            if (distances[dx][dy] + rubbleLevels[dx + Util.dxDiff[directionIdx]][dy + Util.dyDiff[directionIdx]] < distances[dx + Util.dxDiff[directionIdx]][dy + Util.dyDiff[directionIdx]]){
                                distances[dx + Util.dxDiff[directionIdx]][dy + Util.dyDiff[directionIdx]] = distances[dx][dy] + rubbleLevels[dx][dy];
                                predecessor[dx + Util.dxDiff[directionIdx]][dy + Util.dyDiff[directionIdx]] = directionIdx;
                            }
                        }
                    }
                }
            }
        }
    }

    public MapLocation getPriorSquare(MapLocation mL){
        int directionIdx = predecessor[mL.x][mL.y];
        MapLocation mL2 = new MapLocation(mL.x - Util.dxDiff[directionIdx], mL.y - Util.dyDiff[directionIdx]);
        return mL2;
    }

    // public boolean isWall (int xIndex, int yIndex){
    //     int directionIdx = predecessor[xIndex][yIndex];
    //     MapLocation priorSquare = getPriorSquare(new MapLocation(xIndex, yIndex));
    //     try {
    //         return rubbleLevels[xIndex][yIndex] - rubbleLevels[priorSquare.x][priorSquare.y] >= Util.WALL_DEFINITION_CUTOFF;
    //     } catch(Exception e){
    //         e.printStackTrace();
    //         return false;
    //     }
    // }



    public int taxicab(int x1, int y1, int x2, int y2){
        return Util.abs(x2 - x1) + Util.abs(y2 - y1);
    }


    // NOTE: going to assume that rubble is roughly additive, i.e. that the cost of the path you 
    // take really only depends on the sum of the rubble you went through
    public Direction returnBestDirection(MapLocation destination) throws GameActionException{
        //if we can't even move, we can't do anything, so just... return lol
        if (robot.rc.getMovementCooldownTurns() > 0){
            return Direction.CENTER;
        }
        //otherwise, do bellman ford to populate the arrays

        this.bellmanFord(destination);
        //note that for squares bigger than 5x5, this should probably be modified to comparing angles instead of cheaply doing this
        //Need to somehow figure out the intermediate destination, returning CENTER for now

        //Iterate over all the boundary squares

        int bestWeightedTravelDistance = 1000000;
        int bestExitSquareX = 0, bestExitSquareY = 0;
        for (int xIdx = 4; xIdx >= 0; xIdx--){
            if (bestWeightedTravelDistance > (distances[xIdx][0] + taxicab(xIdx, 0, destination.x, destination.y))){
                bestWeightedTravelDistance = distances[xIdx][0] + taxicab(xIdx, 0, destination.x, destination.y);
                bestExitSquareX = xIdx;
                bestExitSquareY = 0;
            }
            if (bestWeightedTravelDistance > (distances[xIdx][4] + taxicab(xIdx, 4, destination.x, destination.y))){
                bestWeightedTravelDistance = distances[xIdx][4] + taxicab(xIdx, 4, destination.x, destination.y);
                bestExitSquareX = xIdx;
                bestExitSquareY = 4;
            }
        }
        for (int yIdx = 4; yIdx >= 0; yIdx--){
            if (bestWeightedTravelDistance > (distances[yIdx][0] + taxicab(yIdx, 0, destination.x, destination.y))){
                bestWeightedTravelDistance = distances[yIdx][0] + taxicab(yIdx, 0, destination.x, destination.y);
                bestExitSquareX = 0;
                bestExitSquareY = yIdx;
            }
            if (bestWeightedTravelDistance > (distances[yIdx][4] + taxicab(yIdx, 4, destination.x, destination.y))){
                bestWeightedTravelDistance = distances[yIdx][4] + taxicab(yIdx, 4, destination.x, destination.y);
                bestExitSquareX = 4;
                bestExitSquareY = yIdx;
            }
        }

        int priorX = bestExitSquareX;
        int priorY = bestExitSquareY;
        Direction toMoveIn = Direction.CENTER;
        while (priorX != 0 || priorY != 0){
            int directionIdx = predecessor[priorX][priorY];
            priorX -= Util.dxDiff[directionIdx];
            priorY -= Util.dyDiff[directionIdx];
            toMoveIn = Util.getDirectionFromIndex(directionIdx);
        }
        return toMoveIn;
    }
}