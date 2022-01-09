package pathfinding_test_bot;
import battlecode.common.*;

public class Pathfinding3 {
    static int distanceSquared;
    RobotCommon robot;
    // static int maxStraightDistance;


    static final int[] dx = new int[] {0, 1, 1, 1, 0, -1, -1, -1};
    static final int[] dy = new int[] {1, 1, 0, -1, -1, -1, 0, 1};


    static int AVG_RUBBLE = 101;

    public Pathfinding3(RobotCommon robot){
        this.robot = robot;
        distanceSquared = robot.rc.getType().visionRadiusSquared;
    }

	static int distances00;
	static int prevDistances00;
	static int rubbleLevels00;

	static int distances01;
	static int prevDistances01;
	static int rubbleLevels01;

	static int distances02;
	static int prevDistances02;
	static int rubbleLevels02;

	static int distances03;
	static int prevDistances03;
	static int rubbleLevels03;

	static int distances04;
	static int prevDistances04;
	static int rubbleLevels04;

	static int distances10;
	static int prevDistances10;
	static int rubbleLevels10;

	static int distances11;
	static int prevDistances11;
	static int rubbleLevels11;

	static int distances12;
	static int prevDistances12;
	static int rubbleLevels12;

	static int distances13;
	static int prevDistances13;
	static int rubbleLevels13;

	static int distances14;
	static int prevDistances14;
	static int rubbleLevels14;

	static int distances20;
	static int prevDistances20;
	static int rubbleLevels20;

	static int distances21;
	static int prevDistances21;
	static int rubbleLevels21;

	static int distances22;
	static int prevDistances22;
	static int rubbleLevels22;

	static int distances23;
	static int prevDistances23;
	static int rubbleLevels23;

	static int distances24;
	static int prevDistances24;
	static int rubbleLevels24;

	static int distances30;
	static int prevDistances30;
	static int rubbleLevels30;

	static int distances31;
	static int prevDistances31;
	static int rubbleLevels31;

	static int distances32;
	static int prevDistances32;
	static int rubbleLevels32;

	static int distances33;
	static int prevDistances33;
	static int rubbleLevels33;

	static int distances34;
	static int prevDistances34;
	static int rubbleLevels34;

	static int distances40;
	static int prevDistances40;
	static int rubbleLevels40;

	static int distances41;
	static int prevDistances41;
	static int rubbleLevels41;

	static int distances42;
	static int prevDistances42;
	static int rubbleLevels42;

	static int distances43;
	static int prevDistances43;
	static int rubbleLevels43;

	static int distances44;
	static int prevDistances44;
	static int rubbleLevels44;

	public void populateArrays(MapLocation target) throws GameActionException{
        System.out.println("start: " + Clock.getBytecodesLeft());
		MapLocation mc = new MapLocation(robot.me.x - 2, robot.me.y - 2);
		int newrow = mc.x;
		int newcol = mc.y;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(0, 0)))){
			rubbleLevels00 = 1000000000;
			distances00 = 1000000000;
			prevDistances00 = 1000000000;
		}
		else{
			rubbleLevels00 = robot.rc.senseRubble(mc.translate(0, 0)) + 10;
			distances00 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances00 = distances00;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(0, 1)))){
			rubbleLevels01 = 1000000000;
			distances01 = 1000000000;
			prevDistances01 = 1000000000;
		}
		else{
			rubbleLevels01 = robot.rc.senseRubble(mc.translate(0, 1)) + 10;
			distances01 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances01 = distances01;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(0, 2)))){
			rubbleLevels02 = 1000000000;
			distances02 = 1000000000;
			prevDistances02 = 1000000000;
		}
		else{
			rubbleLevels02 = robot.rc.senseRubble(mc.translate(0, 2)) + 10;
			distances02 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances02 = distances02;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(0, 3)))){
			rubbleLevels03 = 1000000000;
			distances03 = 1000000000;
			prevDistances03 = 1000000000;
		}
		else{
			rubbleLevels03 = robot.rc.senseRubble(mc.translate(0, 3)) + 10;
			distances03 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances03 = distances03;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(0, 4)))){
			rubbleLevels04 = 1000000000;
			distances04 = 1000000000;
			prevDistances04 = 1000000000;
		}
		else{
			rubbleLevels04 = robot.rc.senseRubble(mc.translate(0, 4)) + 10;
			distances04 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances04 = distances04;
		}
		newrow++;
		newcol -= 4;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(1, 0)))){
			rubbleLevels10 = 1000000000;
			distances10 = 1000000000;
			prevDistances10 = 1000000000;
		}
		else{
			rubbleLevels10 = robot.rc.senseRubble(mc.translate(1, 0)) + 10;
			distances10 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances10 = distances10;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(1, 1)))){
			rubbleLevels11 = 1000000000;
			distances11 = 1000000000;
			prevDistances11 = 1000000000;
		}
		else{
			rubbleLevels11 = robot.rc.senseRubble(mc.translate(1, 1)) + 10;
			distances11 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances11 = distances11;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(1, 2)))){
			rubbleLevels12 = 1000000000;
			distances12 = 1000000000;
			prevDistances12 = 1000000000;
		}
		else{
			rubbleLevels12 = robot.rc.senseRubble(mc.translate(1, 2)) + 10;
			distances12 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances12 = distances12;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(1, 3)))){
			rubbleLevels13 = 1000000000;
			distances13 = 1000000000;
			prevDistances13 = 1000000000;
		}
		else{
			rubbleLevels13 = robot.rc.senseRubble(mc.translate(1, 3)) + 10;
			distances13 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances13 = distances13;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(1, 4)))){
			rubbleLevels14 = 1000000000;
			distances14 = 1000000000;
			prevDistances14 = 1000000000;
		}
		else{
			rubbleLevels14 = robot.rc.senseRubble(mc.translate(1, 4)) + 10;
			distances14 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances14 = distances14;
		}
		newrow++;
		newcol -= 4;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(2, 0)))){
			rubbleLevels20 = 1000000000;
			distances20 = 1000000000;
			prevDistances20 = 1000000000;
		}
		else{
			rubbleLevels20 = robot.rc.senseRubble(mc.translate(2, 0)) + 10;
			distances20 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances20 = distances20;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(2, 1)))){
			rubbleLevels21 = 1000000000;
			distances21 = 1000000000;
			prevDistances21 = 1000000000;
		}
		else{
			rubbleLevels21 = robot.rc.senseRubble(mc.translate(2, 1)) + 10;
			distances21 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances21 = distances21;
		}
		newcol++;
			rubbleLevels22 = robot.rc.senseRubble(mc.translate(2, 2)) + 10;
			distances22 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances22 = distances22;
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(2, 3)))){
			rubbleLevels23 = 1000000000;
			distances23 = 1000000000;
			prevDistances23 = 1000000000;
		}
		else{
			rubbleLevels23 = robot.rc.senseRubble(mc.translate(2, 3)) + 10;
			distances23 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances23 = distances23;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(2, 4)))){
			rubbleLevels24 = 1000000000;
			distances24 = 1000000000;
			prevDistances24 = 1000000000;
		}
		else{
			rubbleLevels24 = robot.rc.senseRubble(mc.translate(2, 4)) + 10;
			distances24 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances24 = distances24;
		}
		newrow++;
		newcol -= 4;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(3, 0)))){
			rubbleLevels30 = 1000000000;
			distances30 = 1000000000;
			prevDistances30 = 1000000000;
		}
		else{
			rubbleLevels30 = robot.rc.senseRubble(mc.translate(3, 0)) + 10;
			distances30 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances30 = distances30;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(3, 1)))){
			rubbleLevels31 = 1000000000;
			distances31 = 1000000000;
			prevDistances31 = 1000000000;
		}
		else{
			rubbleLevels31 = robot.rc.senseRubble(mc.translate(3, 1)) + 10;
			distances31 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances31 = distances31;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(3, 2)))){
			rubbleLevels32 = 1000000000;
			distances32 = 1000000000;
			prevDistances32 = 1000000000;
		}
		else{
			rubbleLevels32 = robot.rc.senseRubble(mc.translate(3, 2)) + 10;
			distances32 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances32 = distances32;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(3, 3)))){
			rubbleLevels33 = 1000000000;
			distances33 = 1000000000;
			prevDistances33 = 1000000000;
		}
		else{
			rubbleLevels33 = robot.rc.senseRubble(mc.translate(3, 3)) + 10;
			distances33 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances33 = distances33;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(3, 4)))){
			rubbleLevels34 = 1000000000;
			distances34 = 1000000000;
			prevDistances34 = 1000000000;
		}
		else{
			rubbleLevels34 = robot.rc.senseRubble(mc.translate(3, 4)) + 10;
			distances34 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances34 = distances34;
		}
		newrow++;
		newcol -= 4;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(4, 0)))){
			rubbleLevels40 = 1000000000;
			distances40 = 1000000000;
			prevDistances40 = 1000000000;
		}
		else{
			rubbleLevels40 = robot.rc.senseRubble(mc.translate(4, 0)) + 10;
			distances40 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances40 = distances40;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(4, 1)))){
			rubbleLevels41 = 1000000000;
			distances41 = 1000000000;
			prevDistances41 = 1000000000;
		}
		else{
			rubbleLevels41 = robot.rc.senseRubble(mc.translate(4, 1)) + 10;
			distances41 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances41 = distances41;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(4, 2)))){
			rubbleLevels42 = 1000000000;
			distances42 = 1000000000;
			prevDistances42 = 1000000000;
		}
		else{
			rubbleLevels42 = robot.rc.senseRubble(mc.translate(4, 2)) + 10;
			distances42 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances42 = distances42;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(4, 3)))){
			rubbleLevels43 = 1000000000;
			distances43 = 1000000000;
			prevDistances43 = 1000000000;
		}
		else{
			rubbleLevels43 = robot.rc.senseRubble(mc.translate(4, 3)) + 10;
			distances43 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances43 = distances43;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(4, 4)))){
			rubbleLevels44 = 1000000000;
			distances44 = 1000000000;
			prevDistances44 = 1000000000;
		}
		else{
			rubbleLevels44 = robot.rc.senseRubble(mc.translate(4, 4)) + 10;
			distances44 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
			prevDistances44 = distances44;
		}
        System.out.println("end: " + Clock.getBytecodesLeft());
	}
}