package first_bot;
import battlecode.common.*;



//future optimizations:
//hardcode in dx/dy values for each one
//perform loop unrolling

public class GreedyPathfinding {
    static int distanceSquared;
    RobotCommon robot;
    // static int maxStraightDistance;

    static int[][] rubbleLevels = new int[5][5];
    static int[][] distances = new int[5][5];
    //will store the direction used to get to a particular square in bellman-ford
    static int[][] predecessor = new int[5][5]; 

    public GreedyPathfinding(RobotCommon robot){
        this.robot = robot;
        distanceSquared = robot.rc.getType().visionRadiusSquared;
        populateArrays();
    }

    public void populateArrays(){
        for (int dx = 4; dx >= 0; dx--){
            for (int dy = 4; dy >= 0; dy--){

                MapLocation mc = new MapLocation(dx - 2 + robot.me.x, dy - 2 + robot.me.y);
                predecessor[dx][dy] = -1;
                distances[dx][dy] = 1000000;
                if (mc.x < 0 || mc.y < 0 || mc.x >= this.robot.rc.getMapWidth() || mc.y >= this.robot.rc.getMapHeight()){
                    rubbleLevels[dx][dy] = 10000;
                    continue;
                }
                try {
                    rubbleLevels[dx][dy] = robot.rc.senseRubble(mc);
                } catch (Exception e){
                    e.printStackTrace();
                }
                //if the spot is occupied, pretend like the rubble level is just a really large number
                if (this.robot.rc.canSenseRobotAtLocation(mc)){
                    rubbleLevels[dx][dy] = 10000;
                }
            }
        }
        distances[2][2] = 0;
    }


    
    public static int[][] narrowPossibilities = {{7, 0, 1}, 
                                                 {0, 1, 2}, 
                                                 {1, 2, 3}, 
                                                 {2, 3, 4}, 
                                                 {3, 4, 5}, 
                                                 {4, 5, 6}, 
                                                 {5, 6, 7}, 
                                                 {6, 7, 0}};

    //if widely, consider the 5 directions around you
    //Later, weight the different directions differently depending on how far you've gone/how far you have left to go
    public Direction exploreNarrowly(MapLocation destination){
        if (robot.me.equals(destination)){
            return Direction.CENTER;
        }
        Direction mainDirection = robot.me.directionTo(destination);
        int bestDistance = 1000000;
        int bestDirectionIdx = -1;
        int directionIdx = Util.getDirectionIndex(mainDirection);
        for (int idx = 2; idx >= 0; idx--){
            int directionBeingTriedIdx = narrowPossibilities[directionIdx][idx];
            int rubbleLevel = rubbleLevels[Util.dxDiff[directionBeingTriedIdx] + 2][Util.dyDiff[directionBeingTriedIdx] + 2];
            if (bestDistance > rubbleLevel){
                bestDistance = rubbleLevel;
                bestDirectionIdx = narrowPossibilities[directionIdx][idx];
            }
            // if (robot.me.x == 2 && robot.me.y == 35){
            //     System.out.println(rubbleLevel);
            //     System.out.println(Util.dxDiff[directionBeingTriedIdx] + " " + Util.dyDiff[directionBeingTriedIdx]);
            //     System.out.println("directionBeingTriedIdx: " + directionBeingTriedIdx);
            //     System.out.println("bestDirectionIdx: " + bestDirectionIdx);
            //     System.out.println(Util.directions[bestDirectionIdx]);
            // }
        }
        return Util.directions[bestDirectionIdx];
    }



    public int distanceMetric(int x1, int y1, int x2, int y2){
        return Util.max(Util.abs(x2 - x1), Util.abs(y2 - y1));
    }


    public Direction returnBestDirection(MapLocation destination){
        return robot.me.directionTo(destination);
    }
}