
package first_bot;

import battlecode.common.*;


public class Soldier extends RobotCommon{

    static int type;//0 = aggressive, 1 = defensive, 2 = escort?
    public static int archonRank;
    static MapLocation archonLocation, target;


    public Soldier(RobotController rc) throws GameActionException {
        super(rc);
        //find parent archon
        boolean foundArchon = false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if(foundArchon) break;
                MapLocation loc = new MapLocation(me.x + dx, me.y + dy);
                for(int i = 0; i < 4; i++) {
                    if(Util.getIntFromLocation(loc) == rc.readSharedArray(i)) {
                        archonRank = i + 1;
                        archonLocation = loc;
                        //target = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(archonRank)));
                        foundArchon = true;
                        break;
                    }
                }
            }
        }
        //do more stuff later
    }
    public void takeTurn() throws GameActionException {
        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) {
            MapLocation toAttack = enemies[0].location;
            if (rc.canAttack(toAttack)) {
                rc.attack(toAttack);
                return;
            }
        }
        tryToMove();
    }

    public void tryToMove() throws GameActionException {
        Direction dir = Util.directions[rng.nextInt(Util.directions.length)];
        MapLocation loc = rc.getLocation();
        if(type == 0){//aggressive soldier, just go in general direction of closest enemy archon
            //fix this eventually
            int sym = 0;
            //int sym = rc.readSharedArray(1234);//fill with eventual location of symmetry in shared array
            switch(sym){
                case 0://no idea what symmetry is, so move randomly
                    break;
                case 1:
                    dir = loc.directionTo(RobotCommon.nearestEnemyArchon(loc, 1));
                    break;
                case 2:
                    dir = loc.directionTo(RobotCommon.nearestEnemyArchon(loc, 2));
                    break;
                default:
                    dir = loc.directionTo(RobotCommon.nearestEnemyArchon(loc, 3));
                    break;
            }
        }
        int radius = rc.getType().visionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) {
            MapLocation toAttack = enemies[0].location;
            GreedyPathfinding gpf = new GreedyPathfinding(this);
            dir = gpf.exploreNarrowly(toAttack);    
        }
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}