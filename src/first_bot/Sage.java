
package first_bot;

import battlecode.common.*;
import java.util.Random;


public class Sage extends RobotCommon{

    public Sage(RobotController rc, int r, MapLocation loc){
        super(rc, r, loc);
    }

    //TODO
    public void takeTurn() throws GameActionException {

    }

    public void tryToAttack() throws GameActionException {

    }

    public void tryToCastAnomaly() throws GameActionException {

    }

    public void tryToMove() throws GameActionException {
        //if there are enemy soldiers in attacking range that can't attack the sage, stay
        if (threateningSoldiers() == 0 && targetSoldiers() > 0){
            return;
        }
        //if can be attacked by >=1 enemy soldiers, run!
        Direction bestDir = Direction.CENTER;
        if (threateningSoldiers() > 0){//find direction that minimizes new # of threatening soldiers
            int minAttackers = 100;
            for(Direction dir : Direction.allDirections()){
                int attackers = threateningSoldiersIfMove(dir);
                if(rc.canMove(dir) && attackers < minAttackers){
                    bestDir = dir;
                    minAttackers = attackers;
                }
            }
            rc.move(bestDir);
        }
        if(soldiersInRange(34) > 0){//enemy soldier in sight! move to maximize # soldiers we can attack (>=1 for sure)
            int maxTargets = 0;
            for(Direction dir : Direction.allDirections()){
                int targets = targetSoldiersIfMove(dir);
                if(rc.canMove(dir) && targets < maxTargets){
                    bestDir = dir;
                    maxTargets = targets;
                }
            }
            rc.move(bestDir);
        }
        //else move randomly
        Random random = new Random(rc.getRoundNum());
        Direction dir = Direction.allDirections()[random.nextInt(9)];
        int resign = 50;
        while(rc.canMove(dir) && resign > 0){
            resign--;
            dir = Direction.allDirections()[random.nextInt(9)];
        }
        rc.move(dir);
    }

    public int soldiersInRange(int r) throws GameActionException {//counts number of enemy soldiers within distance r
        Team opp = rc.getTeam().opponent();
        int cnt = 0;
        for(RobotInfo rob : rc.senseNearbyRobots(r, opp)){
            if(rob.type == RobotType.SOLDIER){
                cnt++;
            }
        }
        return cnt;
    }

    public int threateningSoldiersIfMove(Direction dir){//number of enemy soldiers that can attack us if we move in dir
        MapLocation loc = rc.getLocation();
        MapLocation newloc = loc.add(dir);
        int cnt = 0;
        if(Util.inGrid(newloc)){
            for(RobotInfo x : rc.senseNearbyRobots(34, rc.getTeam().opponent())){//vision range
                if(x.type != RobotType.SOLDIER){//only care about soldiers here
                    continue;
                }
                if(newloc.distanceSquaredTo(newloc) <= 13){//enemy soldier can attack new location
                    cnt++;
                }
            }
        }
        return cnt;
    }

    public int targetSoldiersIfMove(Direction dir) {// number of soldiers we can target if we move in dir
        MapLocation loc = rc.getLocation();
        MapLocation newloc = loc.add(dir);
        int cnt = 0;
        if(Util.inGrid(newloc)){
            for(RobotInfo x : rc.senseNearbyRobots(34, rc.getTeam().opponent())){//vision range
                if(x.type != RobotType.SOLDIER){//only care about soldiers here
                    continue;
                }
                if(newloc.distanceSquaredTo(newloc) <= 20){//new location can attack enemy soldier
                    cnt++;
                }
            }
        }
        return cnt;
    }

    public int threateningSoldiers() throws GameActionException {//number of soldiers that could attack the sage
        return soldiersInRange(13);//soldier attacking radius = 13
    }

    public int targetSoldiers() throws GameActionException {//counts number of enemey soldiers that we can attack
        return soldiersInRange(20);//sage attacking radius
    }
}