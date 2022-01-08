
package first_bot;

import battlecode.common.*;


public class Miner extends RobotCommon{
    public static int archonRank;
    public static MapLocation archonLocation, target;
    public static boolean reachedTarget;

    //this is all only for scouts
    public static boolean isScout = false;
    public static int[] rubbleSeen = new int[60];
    //if isScout is true, this stores if the scout is going horizontally or vertically
    public static boolean scoutTravelingHorizontally;


    public Miner(RobotController rc) throws GameActionException {
        super(rc);
        //find parent archon
        for(int i = 0; i < 4; i++) {
            MapLocation archonLoc = Util.getLocationFromInt(rc.readSharedArray(i));
            if(Util.abs(archonLoc.x - me.x) <= 1 && Util.abs(archonLoc.y - me.y) <= 1) {
                archonRank = i + 1;
                archonLocation = archonLoc;
                int possibleScoutingTarget = rc.readSharedArray(Util.getArchonMemoryBlock(archonRank) + 2);
                if (possibleScoutingTarget != 0){
                    isScout = true;
                    target = Util.getLocationFromInt(possibleScoutingTarget);
                    scoutTravelingHorizontally = (Util.abs(target.x - me.x) < Util.abs(target.y - me.y));
                    for (int idx = 59; idx >= 0; idx--){
                        rubbleSeen[idx] = -1;
                    }
                }
                else{
                    target = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(archonRank)));
                    return;
                }
            }
        }
    }

    public void doScoutRoutine() throws GameActionException{
        GreedyPathfinding gpf = new GreedyPathfinding(this);
        System.out.println(target);
        Direction dir = gpf.travelTo(target);
        rc.setIndicatorString(dir + " " + me + " " + target + isScout);
        if (rc.canMove(dir)){
            rc.move(dir);
        }
        if (scoutTravelingHorizontally){
            findRubbleHeightsOnLine();
        }
    }

    //this is extremely wasteful but it's a scout and can't really do much else so...
    public void findRubbleHeightsOnLine() throws GameActionException{
        if (scoutTravelingHorizontally){
            int coord = target.y;
            //coord is the y coord that's fixed
            for (int xcoord = 0; xcoord < Util.WIDTH; xcoord++){
                if (rc.canSenseLocation(new MapLocation(xcoord, coord))){
                    rubbleSeen[xcoord] = rc.senseRubble(new MapLocation(xcoord, coord));
                    if (rubbleSeen[Util.WIDTH - xcoord - 1] != -1 && rubbleSeen[Util.WIDTH - xcoord - 1] != rubbleSeen[xcoord]){
                        reportSymmetryBroken();
                    }
                }
            }
        }
        else{
            int coord = target.x;
            for (int ycoord = 0; ycoord < Util.WIDTH; ycoord++){
                if (rc.canSenseLocation(new MapLocation(ycoord, coord))){
                    rubbleSeen[ycoord] = rc.senseRubble(new MapLocation(ycoord, coord));
                    if (rubbleSeen[Util.HEIGHT - ycoord - 1] != -1 && rubbleSeen[Util.HEIGHT - ycoord - 1] != rubbleSeen[ycoord]){
                        reportSymmetryBroken();
                    }
                }
            }
        }
    }

    public void reportSymmetryBroken(){
        return;
    }
    
    public void takeTurn() throws GameActionException {
        me = rc.getLocation();
        if (isScout){
            doScoutRoutine();
            return;
        }

        /*
            // Suicide if too many nearby miners
            int radius = rc.getType().actionRadiusSquared;
            Team ourTeam = rc.getTeam();
            RobotInfo[] enemies = rc.senseNearbyRobots(radius, ourTeam);
            int numMiners = 0;
            for (int i = 0; i < enemies.length; i++) {
                if (enemies[i].getType().equals(RobotType.MINER)) {
                    numMiners++;
                }
            }
            if ((numMiners > 1 && rc.senseLead(rc.getLocation()) == 0) || numMiners > 3) {
                rc.disintegrate();
                return;
            }
        */

        rc.setIndicatorString(me + " " + archonLocation + " " + target + " " + reachedTarget);

        // test heuristic: die every 100 rounds if you're not on lattice or you're on a zero lead location near Archon
        int round = rc.getRoundNum();
        if(round > 300 && round % 100 == 0 && (Util.onLattice(Util.getIntFromLocation(me)) == false
            || rc.senseLead(me) == 0) && me.distanceSquaredTo(archonLocation) <= round/100 * 20) {
            rc.disintegrate();
        }

        // Case when Archon could not assign a Location to the Miner

        /*
            Current goal: avoid initial miner overlap using 2x2 lattice (stay on even int locations)
        */
        if(target.equals(archonLocation)) {
            explore();
            tryToWriteTarget();
            tryToMine();
            return;
        }
        
        if(!reachedTarget && me.equals(target)) {
            reachedTarget = true;
        }

        if(!reachedTarget) {
            tryToMove();
        }

        tryToMine();
    }

    // When the Archon has no valid targets for Miner, it should explore until it reaches a far away lead location.
    public void explore() throws GameActionException {
        // stay put if you're on lattice and you can mine
        if(Util.onLattice(Util.getIntFromLocation(me))) {
            MapLocation loc = me;
            if(rc.senseLead(loc) > 0) {
                target = me;
                return;
            }
            for(int i = 0; i < 8; i++) {
                loc = new MapLocation(me.x + Util.dxDiff[i], me.y + Util.dyDiff[i]);
                if(rc.onTheMap(loc) && rc.senseLead(loc) > 0) {
                    target = me;
                    return;
                }
            }
        }
        // otherwise, explore with higher chance of moving away from Archon
        int dirIndex = rng.nextInt(Util.directions.length + 4);
        Direction dir = Direction.CENTER;
        if(dirIndex < Util.directions.length) {
            dir = Util.directions[dirIndex];
        }
        else {
            dir = me.directionTo(archonLocation).opposite();
        }
        if(rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
        }
    }


    // When exploring, the Miner should write the furthest gold/lead location it can see to shared array.
    public void tryToWriteTarget() throws GameActionException {
        MapLocation[] goldLocations = rc.senseNearbyLocationsWithGold(getVisionRadiusSquared());
        int numGoldLocations = goldLocations.length;
        boolean change = false;

        if(numGoldLocations > 0) {
            MapLocation bestLoc = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(archonRank) + 1));
            int bestDist = bestLoc.distanceSquaredTo(archonLocation);

            for(int i = 0; i < numGoldLocations; i++) {
                MapLocation newLoc = goldLocations[i];
                int newDist = archonLocation.distanceSquaredTo(newLoc);
                if(newDist > bestDist) {
                    bestDist = newDist;
                    bestLoc = newLoc;
                    change = true;
                }
            }

            if(change) {
                rc.writeSharedArray(Util.getArchonMemoryBlock(archonRank) + 1, Util.moveOnLattice(Util.getIntFromLocation(bestLoc)));
                return;
            }
        }

        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead(getVisionRadiusSquared());
        int numLeadLocations = leadLocations.length;

        if(numLeadLocations > 0) {
            MapLocation bestLoc = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(archonRank) + 1));
            int bestDist = bestLoc.distanceSquaredTo(archonLocation);

            for(int i = 0; i < numLeadLocations; i++) {
                MapLocation newLoc = leadLocations[i];
                int newDist = archonLocation.distanceSquaredTo(newLoc);
                if(newDist > bestDist) {
                    bestDist = newDist;
                    bestLoc = newLoc;
                    change = true;
                }
            }

            if(change) {
                rc.writeSharedArray(Util.getArchonMemoryBlock(archonRank) + 1, Util.moveOnLattice(Util.getIntFromLocation(bestLoc)));
            }
        }
    }

    // Tries to mine in 3x3 square around Miner
    public void tryToMine() throws GameActionException {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > 1) { // keep 1 lead for generation
                    rc.mineLead(mineLocation);
                }
            }
        }
    }

    // Moves toward target
    public void tryToMove() throws GameActionException {
        GreedyPathfinding gpf = new GreedyPathfinding(this);
        Direction dir = gpf.travelTo(target);
        if (rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
        }
    }
}