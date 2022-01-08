package pathfinding_test_bot;
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
    static int[][] prevDistances = new int[5][5];

    static final int[] dx = new int[] {0, 1, 1, 1, 0, -1, -1, -1};
    static final int[] dy = new int[] {1, 1, 0, -1, -1, -1, 0, 1};


    static int AVG_RUBBLE = 60;
    //will store the direction used to get to a particular square in bellman-ford

    public Pathfinding(RobotCommon robot){
        this.robot = robot;
        distanceSquared = robot.rc.getType().visionRadiusSquared;
        // maxStraightDistance = (int) Math.sqrt(distanceSquared);
    }

    //optimization for later: store this in some static thing
    //bug: there are issues when target is at (0,0)
    public void populateArrays(MapLocation target) throws GameActionException{
        System.out.println("start: " + Clock.getBytecodesLeft());
        for (int row = 0; row < 5; row++){
            for (int col = 0; col < 5; col++){
                MapLocation mc = new MapLocation(row - 2 + robot.me.x, col - 2 + robot.me.y);
                //treat something with this rubble level as being impassable
                if (mc.x < 0 || mc.y < 0 || mc.x >= Util.WIDTH || mc.y >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc) && !(row == 2 && col == 2))){
                    rubbleLevels[row][col] = 1000000000;
                    distances[row][col] = 1000000000;
                    continue;
                }
                else{
                    rubbleLevels[row][col] = robot.rc.senseRubble(mc) + 10;
                }
                if (row == 0 || row == 4 || col == 0 || col == 4){
                    distances[row][col] = Util.distanceMetric(mc.x, mc.y, target.x, target.y) * AVG_RUBBLE;
                    prevDistances[row][col] = Util.distanceMetric(mc.x, mc.y, target.x, target.y) * AVG_RUBBLE;
                    robot.rc.setIndicatorString(mc + " " + target + " " + prevDistances[row][col]);
                    if (Util.distanceMetric(mc.x, mc.y, target.x, target.y) == 0){
                        System.out.println(mc + " " + target);
                    }
                }
                else{
                    distances[row][col] = 1000000000;
                    prevDistances[row][col] = 1000000000;
                }
            }
        }
        System.out.println("end: " + Clock.getBytecodesLeft());
    }
    
    //the comparisons can definitely be optimized to apply min fewer number of times
    public void iterate(int numIterations){
        for (int iteration = 0; iteration < numIterations; iteration++){
            for (int row = 0; row < 5; row++){
                for (int col = 0; col < 5; col++){
                    for (int idx = 0; idx < 8; idx++){
                        if (row + dx[idx] < 5 && row + dx[idx] >= 0 && col + dy[idx] < 5 && col + dy[idx] >= 0){
                            distances[row][col] = Util.min(distances[row][col], 
                                                           prevDistances[row + dx[idx]][col + dy[idx]] + rubbleLevels[row + dx[idx]][col + dy[idx]]);
                        }
                    }
                }
            }
            for (int row = 0; row < 5; row++){
                for (int col = 0; col < 5; col++){
                    prevDistances[row][col] = distances[row][col];
                }
            }
        }
    }

    public Direction findBestDirection(MapLocation target) throws GameActionException{
        populateArrays(target);
        iterate(3);
        int minDistance = 1000000000;
        int bestidx = 0;
        String str = "";
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                str += (distances[i][j] + " ");
            }
            str += "\n";
        }
        System.out.println(str);
        for (int idx = 0; idx < 8; idx++){
            if (minDistance > distances[2 + dx[idx]][2 + dy[idx]]){
                minDistance = distances[2 + dx[idx]][2 + dy[idx]];
                bestidx = idx;
            }
        }
        return Util.directions[bestidx];
    }
}