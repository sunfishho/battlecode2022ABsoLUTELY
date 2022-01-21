package sage_rush_bot;
import battlecode.common.*;

public class Unit extends RobotCommon {
    static double teammateSoldiers, enemySoldiers, numEnemies;
    static boolean isRetreating; // whether retreating
    static MapLocation target; 
    static int targetCountdown = 0;
    public static int locationCounter;
    public static int[] recentDists;
    public static int[] recentLocations;
    /*
    - recentLocations stores 10 most recent locations
    - locationCounter cycles mod 10, each time we move we increment locationCounter and evict recentLocations[locationCounter]
    */

    public Unit (RobotController rc, int r, MapLocation loc) {
        super(rc, r, loc);
        locationCounter = 0;
        recentDists = new int[] {200, 200, 200, 200, 200, 200, 200, 200};
        recentLocations = new int[] {-1, -1, -1, -1, -1, -1, -1, -1};
    }

    public void takeTurn() throws GameActionException {

    }

    public int checkLoop() {//checks recent locations to see if we have looped
        //0 = haven't moved, 1 = cycling, 2 = not cycling
        int loc = Util.getIntFromLocation(rc.getLocation());
        //else, we have moved and need to update recentLocations and check for a loop
        locationCounter = (locationCounter + 1) % recentDists.length;
        int dist = me.distanceSquaredTo(target);
        // If old distance is not farther from new distance, it's probably in a loop
        // System.out.print(rc.getRoundNum() + ", " + rc.getID() + ", [");
        // for (int i = 0; i < recentDists.length; i++) {
        //     System.out.print(recentDists[i] + " ");
        // }
        // System.out.println("]");
        if (recentDists[locationCounter] <= dist) {
            recentLocations[locationCounter] = loc;
            recentDists[locationCounter] = dist;
            return 1;
        }
        recentLocations[locationCounter] = loc;
        recentDists[locationCounter] = dist;
        return 2;
    }

    //Tries to run away. First checks the point opposite of enemy centroid, and if that's not on the map, try going to your archon.
    //Have really low rubble passability because you want to be on a low rubble square when you attack/move.
    //Returns the direction one should retreat in
    public Direction retreat(MapLocation enemyCentroid) throws GameActionException{
        rc.setIndicatorString("RETREATING: " + enemyCentroid.x + ", " + enemyCentroid.y);
        int reflectionX = me.x * 2 - enemyCentroid.x;
        int reflectionY = me.y * 2 - enemyCentroid.y;
        if (reflectionX >= 0 && reflectionX < Util.WIDTH && reflectionY >= 0 && reflectionY < Util.HEIGHT){
            target = me.translate(reflectionX - me.x, reflectionY - me.y);
            targetCountdown = 0;
        }
        else{
            target = nearestArchon(me);
            targetCountdown = 0;
        }

        Direction dir = pf.findBestDirection(target, 25);
        //check if we can move and that we're not going onto a horrible square
        //also, if there's some alternative direction that gets us onto a much better square, take it
        // int bestDistance = 0;
        // Direction dirBest = dir;
        // for (Direction dirAlt : Util.directions){
        //     if (rc.canSenseLocation(me.add(dirAlt)) && rc.senseRubble(me.add(dir))/10 - rc.senseRubble(me.add(dirAlt))/10 > 2 && rc.canMove(dirAlt)){
        //         if (bestDistance < Util.distanceMetric(me.add(dirAlt), enemyCentroid)){
        //             bestDistance = 
        //         }
        //     }
        // }
        if (rc.senseRubble(me.add(dir))/10 - rc.senseRubble(me)/10 > 3){
            return findDirectionLowerRubbleSquare(true);
        }
        else{
            if (rc.canMove(dir)){
                rc.setIndicatorLine(me, me.add(dir), 0, 100, 0);
                return dir;
            }
        }
        return Direction.CENTER;
    }

    //use this when you want to return the best direction to a lower rubble square but are unsure about whether you
    //want to move there just yet
    public Direction findDirectionLowerRubbleSquare(boolean toRetreat) throws GameActionException{
        rc.setIndicatorString("MOVING TO LOWER RUBBLE, target = " + target);
        int bestRubble = rc.senseRubble(me);
        Direction bestDir = Direction.CENTER;
        for (Direction dir: Util.directions){
            if (rc.canMove(dir) && rc.senseRubble(me.add(dir))/10 < bestRubble/10){
                bestDir = dir;
                bestRubble = rc.senseRubble(me.add(bestDir));
            }
            if (rc.canMove(dir) && toRetreat && rc.senseRubble(me.add(dir))/10 == bestRubble/10){
                MapLocation nearestArchonLoc = nearestArchon(me);
                if (Util.distanceMetric(me.add(dir), nearestArchonLoc) <= Util.distanceMetric(me.add(bestDir), nearestArchonLoc)){
                    bestDir = dir;
                    bestRubble = rc.senseRubble(me.add(bestDir));
                }
            }
        }
        return bestDir;
    }

    // Observes if any enemy units nearby, returns true if this is true
    public boolean observe() throws GameActionException {
        boolean hasNearby = false;
        for (RobotInfo robot: rc.senseNearbyRobots()) {
            if (robot.getTeam() != myTeam){
                switch (robot.getType()){
                    case MINER: continue;
                    case ARCHON: 
                        rc.writeSharedArray(22, Util.getIntFromLocation(robot.getLocation()));
                        rc.writeSharedArray(17, Util.getIntFromLocation( robot.location) + 10000 * rankOfNearestArchon(robot.getLocation()));
                        rc.writeSharedArray(18, round);
                        if (rc.getType() == RobotType.MINER || rc.getType() == RobotType.BUILDER) {
                            isRetreating = true;
                        }
                        return hasNearby == true;
                    default:
                        rc.writeSharedArray(17, Util.getIntFromLocation( robot.location) + 10000 * rankOfNearestArchon(robot.getLocation()));
                        rc.writeSharedArray(18, round);
                        if (rc.getType() == RobotType.MINER || rc.getType() == RobotType.BUILDER) {
                            isRetreating = true;
                        }
                        return true;
                }
            }
        }
        return hasNearby;
    }

    //use this when you are certain that we actually want to move
    public void moveLowerRubble(boolean toRetreat) throws GameActionException{
        rc.setIndicatorString("MOVING TO LOWER RUBBLE, target = " + target);
        int bestRubble = rc.senseRubble(me);
        Direction bestDir = Direction.CENTER;
        for (Direction dir: Util.directions){
            if (rc.canMove(dir) && rc.senseRubble(me.add(dir))/10 < bestRubble/10){
                bestDir = dir;
                bestRubble = rc.senseRubble(me.add(bestDir));
            }
            if (rc.canMove(dir) && toRetreat && rc.senseRubble(me.add(dir))/10 == bestRubble/10){
                MapLocation nearestArchonLoc = nearestArchon(me);
                if (Util.distanceMetric(me.add(dir), nearestArchonLoc) <= Util.distanceMetric(me.add(bestDir), nearestArchonLoc)){
                    bestDir = dir;
                    bestRubble = rc.senseRubble(me.add(bestDir));
                }
            }
        }
        if (rc.canMove(bestDir) && bestDir != Direction.CENTER){
            rc.move(bestDir);
            me = rc.getLocation();
        }
    }

    public boolean isBlockedByNeighbors() throws GameActionException{
        Direction dir = me.directionTo(target);
        int dirIndex = Util.getDirectionIndex(dir);
        MapLocation squareInFront = me.add(dir);
        for (int i = -1; i <= 1; i++){
            MapLocation wallSquare = squareInFront.translate(i * Util.dxDiff[(dirIndex + 2) % 8], i * Util.dyDiff[(dirIndex + 2) % 8]);
            if (rc.canSenseLocation(wallSquare) && !rc.canSenseRobotAtLocation(wallSquare)){
                return false;
            }
            //if one end of the wall is an edge of the square, go the other way
        }
        return true;
    }

    public boolean goAroundNeighbors() throws GameActionException{
        if (!isBlockedByNeighbors()){
            return false;
        }
        Direction dir = me.directionTo(target);
        int dirIndex = Util.getDirectionIndex(dir);
        MapLocation poss1, poss2;
        poss1 = new MapLocation(me.x + 2 * Util.dxDiff[(dirIndex + 2) % 8], me.y + 2 * Util.dyDiff[(dirIndex + 2) % 8]);
        poss2 = new MapLocation(me.x - 2 * Util.dxDiff[(dirIndex + 2) % 8], me.y - 2 * Util.dyDiff[(dirIndex + 2) % 8]);
        //now, try to go to the place closer to the center of the map
        if (Util.distanceMetric(poss1, new MapLocation(Util.WIDTH / 2, Util.HEIGHT / 2)) <= Util.distanceMetric(poss2, new MapLocation(Util.WIDTH / 2, Util.HEIGHT / 2))){
            if (Util.isOnMap(poss1)){
                target.translate(2 * Util.dxDiff[(dirIndex + 2) % 8], 2 * Util.dyDiff[(dirIndex + 2) % 8]);
                return true;
            }
            else{
                if (Util.isOnMap(poss2)){
                    target.translate(2 * Util.dxDiff[(dirIndex + 2) % 8], 2 * Util.dyDiff[(dirIndex + 2) % 8]);
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        else{
            if (Util.isOnMap(poss2)){
                target.translate(-2 * Util.dxDiff[(dirIndex + 2) % 8], -2 * Util.dyDiff[(dirIndex + 2) % 8]);
                return true;
            }
            else{
                if (Util.isOnMap(poss1)){
                    target.translate(2 * Util.dxDiff[(dirIndex + 2) % 8], 2 * Util.dyDiff[(dirIndex + 2) % 8]);
                    return true;
                }
                else{
                    return false;
                }
            }
        }
    }

}
