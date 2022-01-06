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
                    rubbleLevels[dx][dy] = robot.rc.senseRubble(mc);
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

    public Direction explore(MapLocation destination){
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
        if (bestDistance - rubbleLevels[2][2] <= Util.WALL_HEIGHT_DIFF){
            // System.out.println("Case 0: " + bestDistance + rubbleLevels[2][2] + robot.me);
            return Util.directions[bestDirectionIdx];
        }
        int rubblelevel1, rubblelevel2, rubblelevel3, rubblelevel4, rubblelevel5;
        rubblelevel1 = rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][2]] * 2 + 2][Util.dyDiff[narrowPossibilities[directionIdx][2]] * 2 + 2];
        rubblelevel2 = rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][2]] + Util.dxDiff[narrowPossibilities[directionIdx][1]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][2]] + Util.dyDiff[narrowPossibilities[directionIdx][1]] + 2];
        rubblelevel3 = rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][1]] * 2 + 2][Util.dyDiff[narrowPossibilities[directionIdx][1]] * 2 + 2];
        rubblelevel4 = rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][1]] + Util.dxDiff[narrowPossibilities[directionIdx][0]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][1]] + Util.dyDiff[narrowPossibilities[directionIdx][0]] + 2];
        rubblelevel5 = rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][0]] * 2 + 2][Util.dyDiff[narrowPossibilities[directionIdx][0]] * 2 + 2];
        int bestsecondrubblelevel = Util.min(Util.min(rubblelevel1, rubblelevel2), Util.min(rubblelevel3, Util.min(rubblelevel4, rubblelevel5)));
        if (bestsecondrubblelevel - rubbleLevels[2][2] <= Util.WALL_HEIGHT_DIFF){
            // System.out.println("Case 1: " + robot.me);
            if (bestsecondrubblelevel == rubblelevel1) return Util.directions[narrowPossibilities[directionIdx][2]];
            if (bestsecondrubblelevel == rubblelevel2){
                if (rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][2]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][2]]+ 2] > rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][1]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][1]]+ 2]){
                    return Util.directions[narrowPossibilities[directionIdx][1]];
                }
                return Util.directions[narrowPossibilities[directionIdx][2]];
            }
            if (bestsecondrubblelevel == rubblelevel3) return Util.directions[bestDirectionIdx];
            if (bestsecondrubblelevel == rubblelevel4){
                if (rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][1]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][1]]+ 2] > rubbleLevels[Util.dxDiff[narrowPossibilities[directionIdx][0]] + 2][Util.dyDiff[narrowPossibilities[directionIdx][0]]+ 2]){
                    return Util.directions[narrowPossibilities[directionIdx][0]];
                }
               return Util.directions[narrowPossibilities[directionIdx][1]];
            }
            if (bestsecondrubblelevel == rubblelevel5) return Util.directions[narrowPossibilities[directionIdx][0]];
        }
        //Well, there's a block in front of us... so turn right maybe?
        int newDirectionIdx = (narrowPossibilities[directionIdx][2] + 1) % 8;
        //if it's ok, just go that way
        if (rubbleLevels[Util.dxDiff[newDirectionIdx] + 2][Util.dyDiff[newDirectionIdx] + 2] - rubbleLevels[2][2] <= Util.WALL_HEIGHT_DIFF){
            // System.out.println("Case 2: " + robot.me);
            return Util.directions[newDirectionIdx];
        }
        newDirectionIdx = (narrowPossibilities[directionIdx][0] + 7) % 8;
        //check the other direction
        if (rubbleLevels[Util.dxDiff[newDirectionIdx] + 2][Util.dyDiff[newDirectionIdx] + 2] - rubbleLevels[2][2] <= Util.WALL_HEIGHT_DIFF){
            // System.out.println("Case 3: " + robot.me);
            return Util.directions[newDirectionIdx];
        }
        // System.out.println("Case 4: " + robot.me);
        return Util.directions[bestDirectionIdx];
        //in this case, try the other direction lol
    }
}