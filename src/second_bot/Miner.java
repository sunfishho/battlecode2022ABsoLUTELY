
package second_bot;

import battlecode.common.*;


public class Miner extends RobotCommon{
    public static MapLocation target;
    public static boolean reachedTarget;

    public Miner(RobotController rc, int r, MapLocation loc, MapLocation t) {
        super(rc, r, loc);
        target = t;
    }
    
    public void takeTurn() throws GameActionException {
        rc.setIndicatorString("MINER: " + me + " " + archonLocation + " " + target + " " + reachedTarget);
        observe();

        // test heuristic: die every 100 rounds if you're not on lattice or you're on a zero lead location
        
        if(round % 50 == 0 && (Util.onLattice(Util.getIntFromLocation(me)) == false
            && rc.senseLead(me) == 0)) {
            rc.disintegrate();  
        }

        // Case when Archon could not assign a Location to the Miner
        if(target.equals(archonLocation)) {
            explore();
            tryToWriteTarget();
            tryToMine(1);
            round++;
            return;
        }
        
        if(!reachedTarget && me.equals(target)) {
            reachedTarget = true;
        }

        if(!reachedTarget) {
            tryToMove();
        }

        tryToMine(1);
        round++;
    }

    // Observes if any enemy units nearby
    public void observe() throws GameActionException {
        for (RobotInfo robot : rc.senseNearbyRobots()) {
            if (robot.getTeam() != rc.getTeam() && robot.getType() != RobotType.MINER) {
                rc.writeSharedArray(17, Util.getIntFromLocation( robot.location) + 10000 * rank);
                rc.writeSharedArray(18, round);
                return;
            }
        }
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
        
        Direction dirLeft = dir.rotateLeft();
        Direction dirRight = dir.rotateRight();

        if(rc.canMove(dir)) {
            rc.move(dir);
        } else if (rc.canMove(dirLeft)) {
            rc.move(dirLeft);
        } else if (rc.canMove(dirRight)) {
            rc.move(dirRight);
        }
        me = rc.getLocation();
    }

    // When exploring, the Miner should write the furthest gold/lead location it can see to shared array.
    public void tryToWriteTarget() throws GameActionException {
        MapLocation[] goldLocations = rc.senseNearbyLocationsWithGold(getVisionRadiusSquared());
        int numGoldLocations = goldLocations.length;
        boolean change = false;

        if(numGoldLocations > 0) {
            MapLocation bestLoc = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(rank) + 1));
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
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 1, Util.moveOnLattice(Util.getIntFromLocation(bestLoc)));
                return;
            }
        }

        MapLocation[] leadLocations = rc.senseNearbyLocationsWithLead(getVisionRadiusSquared());
        int numLeadLocations = leadLocations.length;

        if(numLeadLocations > 0) {
            MapLocation bestLoc = Util.getLocationFromInt(rc.readSharedArray(Util.getArchonMemoryBlock(rank) + 1));
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
                rc.writeSharedArray(Util.getArchonMemoryBlock(rank) + 1, Util.moveOnLattice(Util.getIntFromLocation(bestLoc)));
            }
        }
    }

    // Tries to mine in 3x3 square around Miner, and leaves leaveLead amount at location
    public void tryToMine(int leaveLead) throws GameActionException {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                while (rc.canMineGold(mineLocation) && rc.senseGold(mineLocation) > 0) {
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation) && rc.senseLead(mineLocation) > leaveLead) {
                    rc.mineLead(mineLocation);
                }
            }
        }
    }

    // Moves toward target
    public void tryToMove() throws GameActionException {
        Pathfinding pf = new Pathfinding(this);
        Direction dir = pf.findBestDirection(target);
        if (rc.canMove(dir)) {
            rc.move(dir);
            me = rc.getLocation();
        }
    }
}