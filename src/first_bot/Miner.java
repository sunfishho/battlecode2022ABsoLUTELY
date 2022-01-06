
package first_bot;

import battlecode.common.*;


public class Miner extends RobotCommon{
    public static int archonRank;
    public static MapLocation archonLocation, target;
    public static boolean reachedTarget;

    public Miner(RobotController rc) throws GameActionException {
        super(rc);

        boolean foundArchon = false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if(foundArchon) break;
                MapLocation loc = new MapLocation(me.x + dx, me.y + dy);
                for(int i = 0; i < 4; i++) {
                    if(Util.getIntFromLocation(loc) == rc.readSharedArray(i)) {
                        archonRank = i + 1;
                        archonLocation = loc;
                        target = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(archonRank)));
                        foundArchon = true;
                        break;
                    }
                }
            }
        }
        if(!foundArchon) {
            System.out.println("I have not found rank!");
        }
    }
    
    public void takeTurn() throws GameActionException {
        // Try to mine on squares around us.

        rc.setIndicatorString(me + " " + archonRank + " " + target + " " + reachedTarget);

        // Case when Archon could not assign a Location to the Miner
        if(target.equals(archonRank)) {
            explore();
            tryToWriteTarget();
            return;
        }
        
        if(!reachedTarget && me.equals(target)) {
            reachedTarget = true;
            System.out.println("Reached target.");
        }

        if(!reachedTarget) {
            tryToMove();
        }

        tryToMine();
    }

    // When the Archon has no valid targets for Miner, it should explore until it reaches a far away lead location.
    public void explore() throws GameActionException {
        Direction dir = Util.directions[rng.nextInt(Util.directions.length)];
        if(rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
        }
    }

    // When exploring, the Miner should write the furthest gold/lead location it can see to shared array.
    public void tryToWriteTarget() throws GameActionException {
        MapLocation[] goldLocations = rc.senseNearbyLocationsWithGold(Util.MINER_VISION_RADIUS);
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
                rc.writeSharedArray(Util.getArchonMemoryBlock(archonRank) + 1, Util.getIntFromLocation(bestLoc));
            }
        }

        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead(Util.MINER_VISION_RADIUS);
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
                rc.writeSharedArray(Util.getArchonMemoryBlock(archonRank) + 1, Util.getIntFromLocation(bestLoc));
            }
        }
    }

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

    public void tryToMove() throws GameActionException {
        GreedyPathfinding gpf = new GreedyPathfinding(this);
        Direction dir = gpf.exploreNarrowly(target);
        if (rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
        }
    }
}