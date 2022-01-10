package bot0109v2;

import battlecode.common.*;

public class MinerLattice extends Miner {
    public MinerLattice(RobotController rc, int r, MapLocation loc, MapLocation t) throws GameActionException {
        super(rc, r, loc, t);
        System.out.println("MINER LATTICE: " + me + " " + archonLocation + " " + target + " " + reachedTarget);
    }

    public void takeTurn() throws GameActionException {
        me = rc.getLocation();
        rc.setIndicatorString("MINER LATTICE: " + me + " " + archonLocation + " " + target + " " + reachedTarget);

        /*
            general movement behavior:
                - if you see an open spot on lattice and you are not on the lattice, move towards it and stay
                - else if you see a spot with no lead, move towards it and disintegrate (prioritize on lattice spots)
                - else (you have no target or your target is occupied by a miner),
                    test a lattice point that is away from original archon location
                    if that's new go for it
                    otherwise disintegrate oops (maybe better behavior later)

            every 20 turns, if you are on the lattice, update target to be the lattice point that is away from 
                your original archon location
                - if your new target currently has a miner stay put
                - ^ this behavior will cause the movement to go in waves (i predict), best i can do for now
                - if your new target is out of bounds, check if there are any open lattice locations around
            
            report back empty locations to archon
                - closest locations win
        */

        MapLocation newTarget = findNewTarget();

        tryToMine(1);
    }

    // Follows the logic flowchart for assigning a new target
    public MapLocation findNewTarget() throws GameActionException {
        /*int bestDist = me.distanceSquaredTo(target);
        int bestValue = 3; // 3 - original target, 2 - no lead spot, 1 - no lead spot on lattice, 0 - lead spot on lattice
        MapLocation bestTarget = target;
        MapLocation[] visible = rc.getAllLocationsWithinRadiusSquared(me, bestDist - 1);
            // function automatically uses minimum of vision radius and distance, and we only care about closer locations
        for(MapLocation loc : visible) {
            if(rc.isLocationOccupied()) continue;
            int curDist = me.distanceSquaredTo(loc);
            int curValue = 3;
            if(rc.senseLead(loc) == 0) curValue = 2;
            
            switch(bestCase) {
                case 4:
                case 3:
                    if(rc.senseLead(loc) == 0 && curDist < bestDist && (bestCase > 3 || (bestCase == 3 && curDist < bestDist))) {
                        bestDist = curDist;
                        bestTarget = loc;
                        bestCase = 3;
                    }
                case 2:
                    if(rc.senseLead(loc) == 0 && Util.onLattice(loc) && (bestCase > 2 || (bestCase == 2 && curDist < bestDist))) {
                        bestDist = curDist;
                        bestTarget = loc;
                        bestCase = 2;
                    }
                case 1:
                    if(rc.senseLead(loc) > 0 && Util.onLattice(loc) && (bestCase > 1 || (bestCase == 1 && curDist < bestDist))) {
                        bestDist = curDist;
                        bestTarget = loc;
                        bestCase = 1;
                    }
                default:
                    break;
            }
        }*/
        return target;
    }

    // Takes in a current location on lattice and returns new one opposite from the Archon
    public MapLocation findNewOnLattice(MapLocation cur) throws GameActionException{
        Direction dir = archonLocation.directionTo(cur);
        return Util.moveOnLattice(cur.translate(3 * dir.getDeltaX(), 3 * dir.getDeltaY())); // moveOnLattice for edge cases
    }
}
