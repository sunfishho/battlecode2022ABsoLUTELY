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

    static final int[] dx = new int[] {0, 1, 1, 1, 0, -1, -1, -1};
    static final int[] dy = new int[] {1, 1, 0, -1, -1, -1, 0, 1};


    static int AVG_RUBBLE = 20;
    //will store the direction used to get to a particular square in bellman-ford

    public Pathfinding(RobotCommon robot){
        this.robot = robot;
        distanceSquared = robot.rc.getType().visionRadiusSquared;
        // maxStraightDistance = (int) Math.sqrt(distanceSquared);
    }

    //optimization for later: store this in a 
    public void populateArrays(MapLocation target) throws GameActionException{
        for (int row = 0; row < 5; row++){
            for (int col = 0; col < 5; col++){
                MapLocation mc = new MapLocation(row - 2 + robot.me.x, col - 2 + robot.me.y);
                rubbleLevels[row][col] = robot.rc.senseRubble(mc);
                //treat something with this rubble level as being impassable
                if (mc.x < 0 || mc.y < 0 || mc.x >= Util.WIDTH || mc.y >= Util.HEIGHT || this.robot.rc.canSenseRobotAtLocation(mc)){
                    rubbleLevels[row][col] = 10000;
                }
                if (row == 0 || row == 4 || col == 0 || col == 4){
                    distances[row][col] = Util.distanceMetric(mc.x, mc.y, target.x, target.y) * AVG_RUBBLE;
                }
                else{
                    distances[row][col] = 1000000000;
                }
            }
        }
        distances[2][2] = 0;
    }
    
    //the comparisons can definitely be optimized to apply min fewer number of times
    public void iterate(int numIterations){
        for (int iteration = 0; iteration < numIterations; iteration++){
            for (int row = 0; row < 5; row++){
                for (int col = 0; col < 5; col++){
                    for (int idx = 0; idx < 8; idx++){
                        if (row + dx[idx] < 5 && row + dx[idx] >= 0 && col + dy[idx] < 5 && col + dy[idx] >= 0){
                            distances[row][col] = Util.min(distances[row][col], 
                                                           distances[row + dx[idx]][col + dy[idx]] + rubbleLevels[row + dx[idx]][col + dy[idx]]);
                        }
                    }
                }
            }
        }
    }

    public Direction findBestDirection(MapLocation target) throws GameActionException{
        populateArrays(target);
        iterate(3);
        int minDistance = 1000000000;
        int bestidx = 0;
        for (int idx = 0; idx < 8; idx++){
            if (minDistance > distances[2 + dx[idx]][2 + dy[idx]]){
                minDistance = distances[2 + dx[idx]][2 + dy[idx]];
                bestidx = idx;
            }
        }
        return Util.directions[bestidx];
    }
}