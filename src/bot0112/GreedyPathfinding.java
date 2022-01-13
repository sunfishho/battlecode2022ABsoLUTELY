package bot0112;
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

    public GreedyPathfinding(RobotCommon robot){
        this.robot = robot;
        distanceSquared = robot.rc.getType().visionRadiusSquared;
        populateArrays();
    }

    public void populateArrays(){
        for (int dx = 4; dx >= 0; dx--){
            for (int dy = 4; dy >= 0; dy--){

                MapLocation mc = new MapLocation(dx - 2 + robot.me.x, dy - 2 + robot.me.y);
                distances[dx][dy] = 1000000;
                if (mc.x < 0 || mc.y < 0 || mc.x >= this.robot.rc.getMapWidth() || mc.y >= this.robot.rc.getMapHeight()){
                    rubbleLevels[dx][dy] = 10000;
                    continue;
                }
                try {
                    if (robot.rc.canSenseLocation(mc)) {
                        rubbleLevels[dx][dy] = robot.rc.senseRubble(mc);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                //if the spot is occupied, pretend like the rubble level is just a really large number
                if (this.robot.rc.canSenseRobotAtLocation(mc) && (dx != 2 || dy != 2)){
                    rubbleLevels[dx][dy] = 10000;
                }
            }
        }
        distances[2][2] = 0;
    }


    
    public static int[][] narrowPossibilities = {{0, 7, 1}, 
                                                 {1, 0, 2}, 
                                                 {2, 1, 3}, 
                                                 {3, 2, 4}, 
                                                 {4, 3, 5}, 
                                                 {5, 4, 6}, 
                                                 {6, 5, 7}, 
                                                 {7, 6, 0}};

    //Later, weight the different directions differently depending on how far you've gone/how far you have left to go
    //if there's a giant rubble-y block in front, turn in a diff direction
    public Direction exploreNarrowly(MapLocation destination){
        if (robot.me.distanceSquaredTo(destination) <= 1){
            return robot.me.directionTo(destination);
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

    public Direction travelTo(MapLocation destination){
        int avgPassingRubbleValue = 20;
        MapLocation current = robot.me;
        Direction straightDirection = robot.me.directionTo(destination);
        if (current.distanceSquaredTo(destination) <= 1){
            return straightDirection;
        }
        Direction mainDirection = straightDirection;
        int bestDistance = 1000000;
        int directionIdx = Util.getDirectionIndex(mainDirection);
        int bestDirectionIdx = (directionIdx + 4) % 8;
        for (int idx = 2; idx >= 0; idx--){
            int directionBeingTriedIdx = narrowPossibilities[directionIdx][idx];
            int rubbleLevel = rubbleLevels[Util.dxDiff[directionBeingTriedIdx] + 2][Util.dyDiff[directionBeingTriedIdx] + 2];
            if (bestDistance > rubbleLevel + Util.distanceMetric(current.x, current.y, destination.x, destination.y) * avgPassingRubbleValue){
                bestDistance = rubbleLevel + Util.distanceMetric(current.x, current.y, destination.x, destination.y) * avgPassingRubbleValue;
                bestDirectionIdx = narrowPossibilities[directionIdx][idx];
            }
        }
        if (rubbleLevels[Util.dxDiff[bestDirectionIdx] + 2][Util.dyDiff[bestDirectionIdx] + 2] - rubbleLevels[2][2] <= Util.WALL_HEIGHT_DIFF){
            return Util.directions[bestDirectionIdx];
        }
        int rubblelevel1, rubblelevel2, rubblelevel3, rubblelevel4, rubblelevel5;
        rubblelevel1 = rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][2]] * 2 + 2][Util.dyDiff[narrowPossibilities[directionIdx][2]] * 2 + 2];
        rubblelevel2 = rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][2]] + Util.dxDiff[narrowPossibilities[directionIdx][1]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][2]] + Util.dyDiff[narrowPossibilities[directionIdx][1]] + 2];
        rubblelevel3 = rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][1]] * 2 + 2][Util.dyDiff[narrowPossibilities[directionIdx][1]] * 2 + 2];
        rubblelevel4 = rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][1]] + Util.dxDiff[narrowPossibilities[directionIdx][0]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][1]] + Util.dyDiff[narrowPossibilities[directionIdx][0]] + 2];
        rubblelevel5 = rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][0]] * 2 + 2][Util.dyDiff[narrowPossibilities[directionIdx][0]] * 2 + 2];
        //expected distances to destination after taking each one
        int distance1 = rubblelevel1 + Util.distanceMetric(Util.dxDiff[narrowPossibilities[directionIdx][2]] * 2 + 2, Util.dyDiff[narrowPossibilities[directionIdx][2]] * 2 + 2, destination.x, destination.y) * avgPassingRubbleValue;
        int distance2 = rubblelevel2 + Util.distanceMetric(Util.dxDiff[narrowPossibilities[directionIdx][2]] + Util.dxDiff[narrowPossibilities[directionIdx][1]] + 2, Util.dyDiff[narrowPossibilities[directionIdx][2]] + Util.dyDiff[narrowPossibilities[directionIdx][1]] + 2, destination.x, destination.y) * avgPassingRubbleValue;
        int distance3 = rubblelevel3 + Util.distanceMetric(Util.dxDiff[narrowPossibilities[directionIdx][1]] * 2 + 2, Util.dyDiff[narrowPossibilities[directionIdx][1]] * 2 + 2, destination.x, destination.y) * avgPassingRubbleValue;
        int distance4 = rubblelevel4 + Util.distanceMetric(Util.dxDiff[narrowPossibilities[directionIdx][1]] + Util.dxDiff[narrowPossibilities[directionIdx][0]] + 2, Util.dyDiff[narrowPossibilities[directionIdx][1]] + Util.dyDiff[narrowPossibilities[directionIdx][0]] + 2, destination.x, destination.y) * avgPassingRubbleValue;
        int distance5 = rubblelevel5 + Util.distanceMetric(Util.dxDiff[narrowPossibilities[directionIdx][0]] * 2 + 2, Util.dyDiff[narrowPossibilities[directionIdx][0]] * 2 + 2, destination.x, destination.y) * avgPassingRubbleValue;
        int bestsecondrubblelevel = Util.min(Util.min(rubblelevel1, rubblelevel2), Util.min(rubblelevel3, Util.min(rubblelevel4, rubblelevel5)));
        int bestdistance = Util.min(Util.min(distance1, distance2), Util.min(distance3, Util.min(distance4, distance5)));
        //check if there's a hole in the second layer of cells
        if (bestsecondrubblelevel - rubbleLevels[2][2] <= Util.WALL_HEIGHT_DIFF){
            if (bestdistance == distance1) return Util.directions[narrowPossibilities[directionIdx][2]];
            if (bestdistance == distance2){
                //check what's the fastest way to reach that second layer tile
                if (rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][2]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][2]]+ 2] > rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][1]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][1]]+ 2]){
                    return Util.directions[narrowPossibilities[directionIdx][1]];
                }
                return Util.directions[narrowPossibilities[directionIdx][2]];
            }
            if (bestdistance == distance3) return Util.directions[bestDirectionIdx];
            if (bestdistance == distance4){
                //check what's the fastest way to reach that second layer tile
                if (rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][1]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][1]]+ 2] > rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][0]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][0]]+ 2]){
                    return Util.directions[narrowPossibilities[directionIdx][0]];
                }
               return Util.directions[narrowPossibilities[directionIdx][1]];
            }
            if (bestdistance == distance5) return Util.directions[narrowPossibilities[directionIdx][0]];
     
        }
        return straightDirection;
    }
}