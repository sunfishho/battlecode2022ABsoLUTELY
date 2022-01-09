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
	}


	public void iterate(){
		distances00 -= rubbleLevels00;
		distances00 = Util.min(distances00, prevDistances01);
		distances00 = Util.min(distances00, prevDistances11);
		distances00 = Util.min(distances00, prevDistances10);
		distances00 += rubbleLevels00;
		distances01 -= rubbleLevels01;
		distances01 = Util.min(distances01, prevDistances02);
		distances01 = Util.min(distances01, prevDistances12);
		distances01 = Util.min(distances01, prevDistances11);
		distances01 = Util.min(distances01, prevDistances10);
		distances01 = Util.min(distances01, prevDistances00);
		distances01 += rubbleLevels01;
		distances02 -= rubbleLevels02;
		distances02 = Util.min(distances02, prevDistances03);
		distances02 = Util.min(distances02, prevDistances13);
		distances02 = Util.min(distances02, prevDistances12);
		distances02 = Util.min(distances02, prevDistances11);
		distances02 = Util.min(distances02, prevDistances01);
		distances02 += rubbleLevels02;
		distances03 -= rubbleLevels03;
		distances03 = Util.min(distances03, prevDistances04);
		distances03 = Util.min(distances03, prevDistances14);
		distances03 = Util.min(distances03, prevDistances13);
		distances03 = Util.min(distances03, prevDistances12);
		distances03 = Util.min(distances03, prevDistances02);
		distances03 += rubbleLevels03;
		distances04 -= rubbleLevels04;
		distances04 = Util.min(distances04, prevDistances14);
		distances04 = Util.min(distances04, prevDistances13);
		distances04 = Util.min(distances04, prevDistances03);
		distances04 += rubbleLevels04;
		distances10 -= rubbleLevels10;
		distances10 = Util.min(distances10, prevDistances11);
		distances10 = Util.min(distances10, prevDistances21);
		distances10 = Util.min(distances10, prevDistances20);
		distances10 = Util.min(distances10, prevDistances00);
		distances10 = Util.min(distances10, prevDistances01);
		distances10 += rubbleLevels10;
		distances11 -= rubbleLevels11;
		distances11 = Util.min(distances11, prevDistances12);
		distances11 = Util.min(distances11, prevDistances22);
		distances11 = Util.min(distances11, prevDistances21);
		distances11 = Util.min(distances11, prevDistances20);
		distances11 = Util.min(distances11, prevDistances10);
		distances11 = Util.min(distances11, prevDistances00);
		distances11 = Util.min(distances11, prevDistances01);
		distances11 = Util.min(distances11, prevDistances02);
		distances11 += rubbleLevels11;
		distances12 -= rubbleLevels12;
		distances12 = Util.min(distances12, prevDistances13);
		distances12 = Util.min(distances12, prevDistances23);
		distances12 = Util.min(distances12, prevDistances22);
		distances12 = Util.min(distances12, prevDistances21);
		distances12 = Util.min(distances12, prevDistances11);
		distances12 = Util.min(distances12, prevDistances01);
		distances12 = Util.min(distances12, prevDistances02);
		distances12 = Util.min(distances12, prevDistances03);
		distances12 += rubbleLevels12;
		distances13 -= rubbleLevels13;
		distances13 = Util.min(distances13, prevDistances14);
		distances13 = Util.min(distances13, prevDistances24);
		distances13 = Util.min(distances13, prevDistances23);
		distances13 = Util.min(distances13, prevDistances22);
		distances13 = Util.min(distances13, prevDistances12);
		distances13 = Util.min(distances13, prevDistances02);
		distances13 = Util.min(distances13, prevDistances03);
		distances13 = Util.min(distances13, prevDistances04);
		distances13 += rubbleLevels13;
		distances14 -= rubbleLevels14;
		distances14 = Util.min(distances14, prevDistances24);
		distances14 = Util.min(distances14, prevDistances23);
		distances14 = Util.min(distances14, prevDistances13);
		distances14 = Util.min(distances14, prevDistances03);
		distances14 = Util.min(distances14, prevDistances04);
		distances14 += rubbleLevels14;
		distances20 -= rubbleLevels20;
		distances20 = Util.min(distances20, prevDistances21);
		distances20 = Util.min(distances20, prevDistances31);
		distances20 = Util.min(distances20, prevDistances30);
		distances20 = Util.min(distances20, prevDistances10);
		distances20 = Util.min(distances20, prevDistances11);
		distances20 += rubbleLevels20;
		distances21 -= rubbleLevels21;
		distances21 = Util.min(distances21, prevDistances22);
		distances21 = Util.min(distances21, prevDistances32);
		distances21 = Util.min(distances21, prevDistances31);
		distances21 = Util.min(distances21, prevDistances30);
		distances21 = Util.min(distances21, prevDistances20);
		distances21 = Util.min(distances21, prevDistances10);
		distances21 = Util.min(distances21, prevDistances11);
		distances21 = Util.min(distances21, prevDistances12);
		distances21 += rubbleLevels21;
		distances22 -= rubbleLevels22;
		distances22 = Util.min(distances22, prevDistances23);
		distances22 = Util.min(distances22, prevDistances33);
		distances22 = Util.min(distances22, prevDistances32);
		distances22 = Util.min(distances22, prevDistances31);
		distances22 = Util.min(distances22, prevDistances21);
		distances22 = Util.min(distances22, prevDistances11);
		distances22 = Util.min(distances22, prevDistances12);
		distances22 = Util.min(distances22, prevDistances13);
		distances22 += rubbleLevels22;
		distances23 -= rubbleLevels23;
		distances23 = Util.min(distances23, prevDistances24);
		distances23 = Util.min(distances23, prevDistances34);
		distances23 = Util.min(distances23, prevDistances33);
		distances23 = Util.min(distances23, prevDistances32);
		distances23 = Util.min(distances23, prevDistances22);
		distances23 = Util.min(distances23, prevDistances12);
		distances23 = Util.min(distances23, prevDistances13);
		distances23 = Util.min(distances23, prevDistances14);
		distances23 += rubbleLevels23;
		distances24 -= rubbleLevels24;
		distances24 = Util.min(distances24, prevDistances34);
		distances24 = Util.min(distances24, prevDistances33);
		distances24 = Util.min(distances24, prevDistances23);
		distances24 = Util.min(distances24, prevDistances13);
		distances24 = Util.min(distances24, prevDistances14);
		distances24 += rubbleLevels24;
		distances30 -= rubbleLevels30;
		distances30 = Util.min(distances30, prevDistances31);
		distances30 = Util.min(distances30, prevDistances41);
		distances30 = Util.min(distances30, prevDistances40);
		distances30 = Util.min(distances30, prevDistances20);
		distances30 = Util.min(distances30, prevDistances21);
		distances30 += rubbleLevels30;
		distances31 -= rubbleLevels31;
		distances31 = Util.min(distances31, prevDistances32);
		distances31 = Util.min(distances31, prevDistances42);
		distances31 = Util.min(distances31, prevDistances41);
		distances31 = Util.min(distances31, prevDistances40);
		distances31 = Util.min(distances31, prevDistances30);
		distances31 = Util.min(distances31, prevDistances20);
		distances31 = Util.min(distances31, prevDistances21);
		distances31 = Util.min(distances31, prevDistances22);
		distances31 += rubbleLevels31;
		distances32 -= rubbleLevels32;
		distances32 = Util.min(distances32, prevDistances33);
		distances32 = Util.min(distances32, prevDistances43);
		distances32 = Util.min(distances32, prevDistances42);
		distances32 = Util.min(distances32, prevDistances41);
		distances32 = Util.min(distances32, prevDistances31);
		distances32 = Util.min(distances32, prevDistances21);
		distances32 = Util.min(distances32, prevDistances22);
		distances32 = Util.min(distances32, prevDistances23);
		distances32 += rubbleLevels32;
		distances33 -= rubbleLevels33;
		distances33 = Util.min(distances33, prevDistances34);
		distances33 = Util.min(distances33, prevDistances44);
		distances33 = Util.min(distances33, prevDistances43);
		distances33 = Util.min(distances33, prevDistances42);
		distances33 = Util.min(distances33, prevDistances32);
		distances33 = Util.min(distances33, prevDistances22);
		distances33 = Util.min(distances33, prevDistances23);
		distances33 = Util.min(distances33, prevDistances24);
		distances33 += rubbleLevels33;
		distances34 -= rubbleLevels34;
		distances34 = Util.min(distances34, prevDistances44);
		distances34 = Util.min(distances34, prevDistances43);
		distances34 = Util.min(distances34, prevDistances33);
		distances34 = Util.min(distances34, prevDistances23);
		distances34 = Util.min(distances34, prevDistances24);
		distances34 += rubbleLevels34;
		distances40 -= rubbleLevels40;
		distances40 = Util.min(distances40, prevDistances41);
		distances40 = Util.min(distances40, prevDistances30);
		distances40 = Util.min(distances40, prevDistances31);
		distances40 += rubbleLevels40;
		distances41 -= rubbleLevels41;
		distances41 = Util.min(distances41, prevDistances42);
		distances41 = Util.min(distances41, prevDistances40);
		distances41 = Util.min(distances41, prevDistances30);
		distances41 = Util.min(distances41, prevDistances31);
		distances41 = Util.min(distances41, prevDistances32);
		distances41 += rubbleLevels41;
		distances42 -= rubbleLevels42;
		distances42 = Util.min(distances42, prevDistances43);
		distances42 = Util.min(distances42, prevDistances41);
		distances42 = Util.min(distances42, prevDistances31);
		distances42 = Util.min(distances42, prevDistances32);
		distances42 = Util.min(distances42, prevDistances33);
		distances42 += rubbleLevels42;
		distances43 -= rubbleLevels43;
		distances43 = Util.min(distances43, prevDistances44);
		distances43 = Util.min(distances43, prevDistances42);
		distances43 = Util.min(distances43, prevDistances32);
		distances43 = Util.min(distances43, prevDistances33);
		distances43 = Util.min(distances43, prevDistances34);
		distances43 += rubbleLevels43;
		distances44 -= rubbleLevels44;
		distances44 = Util.min(distances44, prevDistances43);
		distances44 = Util.min(distances44, prevDistances33);
		distances44 = Util.min(distances44, prevDistances34);
		distances44 += rubbleLevels44;
		prevDistances00 -= rubbleLevels00;
		prevDistances00 = Util.min(prevDistances00, distances01);
		prevDistances00 = Util.min(prevDistances00, distances11);
		prevDistances00 = Util.min(prevDistances00, distances10);
		prevDistances00 += rubbleLevels00;
		prevDistances01 -= rubbleLevels01;
		prevDistances01 = Util.min(prevDistances01, distances02);
		prevDistances01 = Util.min(prevDistances01, distances12);
		prevDistances01 = Util.min(prevDistances01, distances11);
		prevDistances01 = Util.min(prevDistances01, distances10);
		prevDistances01 = Util.min(prevDistances01, distances00);
		prevDistances01 += rubbleLevels01;
		prevDistances02 -= rubbleLevels02;
		prevDistances02 = Util.min(prevDistances02, distances03);
		prevDistances02 = Util.min(prevDistances02, distances13);
		prevDistances02 = Util.min(prevDistances02, distances12);
		prevDistances02 = Util.min(prevDistances02, distances11);
		prevDistances02 = Util.min(prevDistances02, distances01);
		prevDistances02 += rubbleLevels02;
		prevDistances03 -= rubbleLevels03;
		prevDistances03 = Util.min(prevDistances03, distances04);
		prevDistances03 = Util.min(prevDistances03, distances14);
		prevDistances03 = Util.min(prevDistances03, distances13);
		prevDistances03 = Util.min(prevDistances03, distances12);
		prevDistances03 = Util.min(prevDistances03, distances02);
		prevDistances03 += rubbleLevels03;
		prevDistances04 -= rubbleLevels04;
		prevDistances04 = Util.min(prevDistances04, distances14);
		prevDistances04 = Util.min(prevDistances04, distances13);
		prevDistances04 = Util.min(prevDistances04, distances03);
		prevDistances04 += rubbleLevels04;
		prevDistances10 -= rubbleLevels10;
		prevDistances10 = Util.min(prevDistances10, distances11);
		prevDistances10 = Util.min(prevDistances10, distances21);
		prevDistances10 = Util.min(prevDistances10, distances20);
		prevDistances10 = Util.min(prevDistances10, distances00);
		prevDistances10 = Util.min(prevDistances10, distances01);
		prevDistances10 += rubbleLevels10;
		prevDistances11 -= rubbleLevels11;
		prevDistances11 = Util.min(prevDistances11, distances12);
		prevDistances11 = Util.min(prevDistances11, distances22);
		prevDistances11 = Util.min(prevDistances11, distances21);
		prevDistances11 = Util.min(prevDistances11, distances20);
		prevDistances11 = Util.min(prevDistances11, distances10);
		prevDistances11 = Util.min(prevDistances11, distances00);
		prevDistances11 = Util.min(prevDistances11, distances01);
		prevDistances11 = Util.min(prevDistances11, distances02);
		prevDistances11 += rubbleLevels11;
		prevDistances12 -= rubbleLevels12;
		prevDistances12 = Util.min(prevDistances12, distances13);
		prevDistances12 = Util.min(prevDistances12, distances23);
		prevDistances12 = Util.min(prevDistances12, distances22);
		prevDistances12 = Util.min(prevDistances12, distances21);
		prevDistances12 = Util.min(prevDistances12, distances11);
		prevDistances12 = Util.min(prevDistances12, distances01);
		prevDistances12 = Util.min(prevDistances12, distances02);
		prevDistances12 = Util.min(prevDistances12, distances03);
		prevDistances12 += rubbleLevels12;
		prevDistances13 -= rubbleLevels13;
		prevDistances13 = Util.min(prevDistances13, distances14);
		prevDistances13 = Util.min(prevDistances13, distances24);
		prevDistances13 = Util.min(prevDistances13, distances23);
		prevDistances13 = Util.min(prevDistances13, distances22);
		prevDistances13 = Util.min(prevDistances13, distances12);
		prevDistances13 = Util.min(prevDistances13, distances02);
		prevDistances13 = Util.min(prevDistances13, distances03);
		prevDistances13 = Util.min(prevDistances13, distances04);
		prevDistances13 += rubbleLevels13;
		prevDistances14 -= rubbleLevels14;
		prevDistances14 = Util.min(prevDistances14, distances24);
		prevDistances14 = Util.min(prevDistances14, distances23);
		prevDistances14 = Util.min(prevDistances14, distances13);
		prevDistances14 = Util.min(prevDistances14, distances03);
		prevDistances14 = Util.min(prevDistances14, distances04);
		prevDistances14 += rubbleLevels14;
		prevDistances20 -= rubbleLevels20;
		prevDistances20 = Util.min(prevDistances20, distances21);
		prevDistances20 = Util.min(prevDistances20, distances31);
		prevDistances20 = Util.min(prevDistances20, distances30);
		prevDistances20 = Util.min(prevDistances20, distances10);
		prevDistances20 = Util.min(prevDistances20, distances11);
		prevDistances20 += rubbleLevels20;
		prevDistances21 -= rubbleLevels21;
		prevDistances21 = Util.min(prevDistances21, distances22);
		prevDistances21 = Util.min(prevDistances21, distances32);
		prevDistances21 = Util.min(prevDistances21, distances31);
		prevDistances21 = Util.min(prevDistances21, distances30);
		prevDistances21 = Util.min(prevDistances21, distances20);
		prevDistances21 = Util.min(prevDistances21, distances10);
		prevDistances21 = Util.min(prevDistances21, distances11);
		prevDistances21 = Util.min(prevDistances21, distances12);
		prevDistances21 += rubbleLevels21;
		prevDistances22 -= rubbleLevels22;
		prevDistances22 = Util.min(prevDistances22, distances23);
		prevDistances22 = Util.min(prevDistances22, distances33);
		prevDistances22 = Util.min(prevDistances22, distances32);
		prevDistances22 = Util.min(prevDistances22, distances31);
		prevDistances22 = Util.min(prevDistances22, distances21);
		prevDistances22 = Util.min(prevDistances22, distances11);
		prevDistances22 = Util.min(prevDistances22, distances12);
		prevDistances22 = Util.min(prevDistances22, distances13);
		prevDistances22 += rubbleLevels22;
		prevDistances23 -= rubbleLevels23;
		prevDistances23 = Util.min(prevDistances23, distances24);
		prevDistances23 = Util.min(prevDistances23, distances34);
		prevDistances23 = Util.min(prevDistances23, distances33);
		prevDistances23 = Util.min(prevDistances23, distances32);
		prevDistances23 = Util.min(prevDistances23, distances22);
		prevDistances23 = Util.min(prevDistances23, distances12);
		prevDistances23 = Util.min(prevDistances23, distances13);
		prevDistances23 = Util.min(prevDistances23, distances14);
		prevDistances23 += rubbleLevels23;
		prevDistances24 -= rubbleLevels24;
		prevDistances24 = Util.min(prevDistances24, distances34);
		prevDistances24 = Util.min(prevDistances24, distances33);
		prevDistances24 = Util.min(prevDistances24, distances23);
		prevDistances24 = Util.min(prevDistances24, distances13);
		prevDistances24 = Util.min(prevDistances24, distances14);
		prevDistances24 += rubbleLevels24;
		prevDistances30 -= rubbleLevels30;
		prevDistances30 = Util.min(prevDistances30, distances31);
		prevDistances30 = Util.min(prevDistances30, distances41);
		prevDistances30 = Util.min(prevDistances30, distances40);
		prevDistances30 = Util.min(prevDistances30, distances20);
		prevDistances30 = Util.min(prevDistances30, distances21);
		prevDistances30 += rubbleLevels30;
		prevDistances31 -= rubbleLevels31;
		prevDistances31 = Util.min(prevDistances31, distances32);
		prevDistances31 = Util.min(prevDistances31, distances42);
		prevDistances31 = Util.min(prevDistances31, distances41);
		prevDistances31 = Util.min(prevDistances31, distances40);
		prevDistances31 = Util.min(prevDistances31, distances30);
		prevDistances31 = Util.min(prevDistances31, distances20);
		prevDistances31 = Util.min(prevDistances31, distances21);
		prevDistances31 = Util.min(prevDistances31, distances22);
		prevDistances31 += rubbleLevels31;
		prevDistances32 -= rubbleLevels32;
		prevDistances32 = Util.min(prevDistances32, distances33);
		prevDistances32 = Util.min(prevDistances32, distances43);
		prevDistances32 = Util.min(prevDistances32, distances42);
		prevDistances32 = Util.min(prevDistances32, distances41);
		prevDistances32 = Util.min(prevDistances32, distances31);
		prevDistances32 = Util.min(prevDistances32, distances21);
		prevDistances32 = Util.min(prevDistances32, distances22);
		prevDistances32 = Util.min(prevDistances32, distances23);
		prevDistances32 += rubbleLevels32;
		prevDistances33 -= rubbleLevels33;
		prevDistances33 = Util.min(prevDistances33, distances34);
		prevDistances33 = Util.min(prevDistances33, distances44);
		prevDistances33 = Util.min(prevDistances33, distances43);
		prevDistances33 = Util.min(prevDistances33, distances42);
		prevDistances33 = Util.min(prevDistances33, distances32);
		prevDistances33 = Util.min(prevDistances33, distances22);
		prevDistances33 = Util.min(prevDistances33, distances23);
		prevDistances33 = Util.min(prevDistances33, distances24);
		prevDistances33 += rubbleLevels33;
		prevDistances34 -= rubbleLevels34;
		prevDistances34 = Util.min(prevDistances34, distances44);
		prevDistances34 = Util.min(prevDistances34, distances43);
		prevDistances34 = Util.min(prevDistances34, distances33);
		prevDistances34 = Util.min(prevDistances34, distances23);
		prevDistances34 = Util.min(prevDistances34, distances24);
		prevDistances34 += rubbleLevels34;
		prevDistances40 -= rubbleLevels40;
		prevDistances40 = Util.min(prevDistances40, distances41);
		prevDistances40 = Util.min(prevDistances40, distances30);
		prevDistances40 = Util.min(prevDistances40, distances31);
		prevDistances40 += rubbleLevels40;
		prevDistances41 -= rubbleLevels41;
		prevDistances41 = Util.min(prevDistances41, distances42);
		prevDistances41 = Util.min(prevDistances41, distances40);
		prevDistances41 = Util.min(prevDistances41, distances30);
		prevDistances41 = Util.min(prevDistances41, distances31);
		prevDistances41 = Util.min(prevDistances41, distances32);
		prevDistances41 += rubbleLevels41;
		prevDistances42 -= rubbleLevels42;
		prevDistances42 = Util.min(prevDistances42, distances43);
		prevDistances42 = Util.min(prevDistances42, distances41);
		prevDistances42 = Util.min(prevDistances42, distances31);
		prevDistances42 = Util.min(prevDistances42, distances32);
		prevDistances42 = Util.min(prevDistances42, distances33);
		prevDistances42 += rubbleLevels42;
		prevDistances43 -= rubbleLevels43;
		prevDistances43 = Util.min(prevDistances43, distances44);
		prevDistances43 = Util.min(prevDistances43, distances42);
		prevDistances43 = Util.min(prevDistances43, distances32);
		prevDistances43 = Util.min(prevDistances43, distances33);
		prevDistances43 = Util.min(prevDistances43, distances34);
		prevDistances43 += rubbleLevels43;
		prevDistances44 -= rubbleLevels44;
		prevDistances44 = Util.min(prevDistances44, distances43);
		prevDistances44 = Util.min(prevDistances44, distances33);
		prevDistances44 = Util.min(prevDistances44, distances34);
		prevDistances44 += rubbleLevels44;
		distances00 -= rubbleLevels00;
		distances00 = Util.min(distances00, prevDistances01);
		distances00 = Util.min(distances00, prevDistances11);
		distances00 = Util.min(distances00, prevDistances10);
		distances00 += rubbleLevels00;
		distances01 -= rubbleLevels01;
		distances01 = Util.min(distances01, prevDistances02);
		distances01 = Util.min(distances01, prevDistances12);
		distances01 = Util.min(distances01, prevDistances11);
		distances01 = Util.min(distances01, prevDistances10);
		distances01 = Util.min(distances01, prevDistances00);
		distances01 += rubbleLevels01;
		distances02 -= rubbleLevels02;
		distances02 = Util.min(distances02, prevDistances03);
		distances02 = Util.min(distances02, prevDistances13);
		distances02 = Util.min(distances02, prevDistances12);
		distances02 = Util.min(distances02, prevDistances11);
		distances02 = Util.min(distances02, prevDistances01);
		distances02 += rubbleLevels02;
		distances03 -= rubbleLevels03;
		distances03 = Util.min(distances03, prevDistances04);
		distances03 = Util.min(distances03, prevDistances14);
		distances03 = Util.min(distances03, prevDistances13);
		distances03 = Util.min(distances03, prevDistances12);
		distances03 = Util.min(distances03, prevDistances02);
		distances03 += rubbleLevels03;
		distances04 -= rubbleLevels04;
		distances04 = Util.min(distances04, prevDistances14);
		distances04 = Util.min(distances04, prevDistances13);
		distances04 = Util.min(distances04, prevDistances03);
		distances04 += rubbleLevels04;
		distances10 -= rubbleLevels10;
		distances10 = Util.min(distances10, prevDistances11);
		distances10 = Util.min(distances10, prevDistances21);
		distances10 = Util.min(distances10, prevDistances20);
		distances10 = Util.min(distances10, prevDistances00);
		distances10 = Util.min(distances10, prevDistances01);
		distances10 += rubbleLevels10;
		distances11 -= rubbleLevels11;
		distances11 = Util.min(distances11, prevDistances12);
		distances11 = Util.min(distances11, prevDistances22);
		distances11 = Util.min(distances11, prevDistances21);
		distances11 = Util.min(distances11, prevDistances20);
		distances11 = Util.min(distances11, prevDistances10);
		distances11 = Util.min(distances11, prevDistances00);
		distances11 = Util.min(distances11, prevDistances01);
		distances11 = Util.min(distances11, prevDistances02);
		distances11 += rubbleLevels11;
		distances12 -= rubbleLevels12;
		distances12 = Util.min(distances12, prevDistances13);
		distances12 = Util.min(distances12, prevDistances23);
		distances12 = Util.min(distances12, prevDistances22);
		distances12 = Util.min(distances12, prevDistances21);
		distances12 = Util.min(distances12, prevDistances11);
		distances12 = Util.min(distances12, prevDistances01);
		distances12 = Util.min(distances12, prevDistances02);
		distances12 = Util.min(distances12, prevDistances03);
		distances12 += rubbleLevels12;
		distances13 -= rubbleLevels13;
		distances13 = Util.min(distances13, prevDistances14);
		distances13 = Util.min(distances13, prevDistances24);
		distances13 = Util.min(distances13, prevDistances23);
		distances13 = Util.min(distances13, prevDistances22);
		distances13 = Util.min(distances13, prevDistances12);
		distances13 = Util.min(distances13, prevDistances02);
		distances13 = Util.min(distances13, prevDistances03);
		distances13 = Util.min(distances13, prevDistances04);
		distances13 += rubbleLevels13;
		distances14 -= rubbleLevels14;
		distances14 = Util.min(distances14, prevDistances24);
		distances14 = Util.min(distances14, prevDistances23);
		distances14 = Util.min(distances14, prevDistances13);
		distances14 = Util.min(distances14, prevDistances03);
		distances14 = Util.min(distances14, prevDistances04);
		distances14 += rubbleLevels14;
		distances20 -= rubbleLevels20;
		distances20 = Util.min(distances20, prevDistances21);
		distances20 = Util.min(distances20, prevDistances31);
		distances20 = Util.min(distances20, prevDistances30);
		distances20 = Util.min(distances20, prevDistances10);
		distances20 = Util.min(distances20, prevDistances11);
		distances20 += rubbleLevels20;
		distances21 -= rubbleLevels21;
		distances21 = Util.min(distances21, prevDistances22);
		distances21 = Util.min(distances21, prevDistances32);
		distances21 = Util.min(distances21, prevDistances31);
		distances21 = Util.min(distances21, prevDistances30);
		distances21 = Util.min(distances21, prevDistances20);
		distances21 = Util.min(distances21, prevDistances10);
		distances21 = Util.min(distances21, prevDistances11);
		distances21 = Util.min(distances21, prevDistances12);
		distances21 += rubbleLevels21;
		distances22 -= rubbleLevels22;
		distances22 = Util.min(distances22, prevDistances23);
		distances22 = Util.min(distances22, prevDistances33);
		distances22 = Util.min(distances22, prevDistances32);
		distances22 = Util.min(distances22, prevDistances31);
		distances22 = Util.min(distances22, prevDistances21);
		distances22 = Util.min(distances22, prevDistances11);
		distances22 = Util.min(distances22, prevDistances12);
		distances22 = Util.min(distances22, prevDistances13);
		distances22 += rubbleLevels22;
		distances23 -= rubbleLevels23;
		distances23 = Util.min(distances23, prevDistances24);
		distances23 = Util.min(distances23, prevDistances34);
		distances23 = Util.min(distances23, prevDistances33);
		distances23 = Util.min(distances23, prevDistances32);
		distances23 = Util.min(distances23, prevDistances22);
		distances23 = Util.min(distances23, prevDistances12);
		distances23 = Util.min(distances23, prevDistances13);
		distances23 = Util.min(distances23, prevDistances14);
		distances23 += rubbleLevels23;
		distances24 -= rubbleLevels24;
		distances24 = Util.min(distances24, prevDistances34);
		distances24 = Util.min(distances24, prevDistances33);
		distances24 = Util.min(distances24, prevDistances23);
		distances24 = Util.min(distances24, prevDistances13);
		distances24 = Util.min(distances24, prevDistances14);
		distances24 += rubbleLevels24;
		distances30 -= rubbleLevels30;
		distances30 = Util.min(distances30, prevDistances31);
		distances30 = Util.min(distances30, prevDistances41);
		distances30 = Util.min(distances30, prevDistances40);
		distances30 = Util.min(distances30, prevDistances20);
		distances30 = Util.min(distances30, prevDistances21);
		distances30 += rubbleLevels30;
		distances31 -= rubbleLevels31;
		distances31 = Util.min(distances31, prevDistances32);
		distances31 = Util.min(distances31, prevDistances42);
		distances31 = Util.min(distances31, prevDistances41);
		distances31 = Util.min(distances31, prevDistances40);
		distances31 = Util.min(distances31, prevDistances30);
		distances31 = Util.min(distances31, prevDistances20);
		distances31 = Util.min(distances31, prevDistances21);
		distances31 = Util.min(distances31, prevDistances22);
		distances31 += rubbleLevels31;
		distances32 -= rubbleLevels32;
		distances32 = Util.min(distances32, prevDistances33);
		distances32 = Util.min(distances32, prevDistances43);
		distances32 = Util.min(distances32, prevDistances42);
		distances32 = Util.min(distances32, prevDistances41);
		distances32 = Util.min(distances32, prevDistances31);
		distances32 = Util.min(distances32, prevDistances21);
		distances32 = Util.min(distances32, prevDistances22);
		distances32 = Util.min(distances32, prevDistances23);
		distances32 += rubbleLevels32;
		distances33 -= rubbleLevels33;
		distances33 = Util.min(distances33, prevDistances34);
		distances33 = Util.min(distances33, prevDistances44);
		distances33 = Util.min(distances33, prevDistances43);
		distances33 = Util.min(distances33, prevDistances42);
		distances33 = Util.min(distances33, prevDistances32);
		distances33 = Util.min(distances33, prevDistances22);
		distances33 = Util.min(distances33, prevDistances23);
		distances33 = Util.min(distances33, prevDistances24);
		distances33 += rubbleLevels33;
		distances34 -= rubbleLevels34;
		distances34 = Util.min(distances34, prevDistances44);
		distances34 = Util.min(distances34, prevDistances43);
		distances34 = Util.min(distances34, prevDistances33);
		distances34 = Util.min(distances34, prevDistances23);
		distances34 = Util.min(distances34, prevDistances24);
		distances34 += rubbleLevels34;
		distances40 -= rubbleLevels40;
		distances40 = Util.min(distances40, prevDistances41);
		distances40 = Util.min(distances40, prevDistances30);
		distances40 = Util.min(distances40, prevDistances31);
		distances40 += rubbleLevels40;
		distances41 -= rubbleLevels41;
		distances41 = Util.min(distances41, prevDistances42);
		distances41 = Util.min(distances41, prevDistances40);
		distances41 = Util.min(distances41, prevDistances30);
		distances41 = Util.min(distances41, prevDistances31);
		distances41 = Util.min(distances41, prevDistances32);
		distances41 += rubbleLevels41;
		distances42 -= rubbleLevels42;
		distances42 = Util.min(distances42, prevDistances43);
		distances42 = Util.min(distances42, prevDistances41);
		distances42 = Util.min(distances42, prevDistances31);
		distances42 = Util.min(distances42, prevDistances32);
		distances42 = Util.min(distances42, prevDistances33);
		distances42 += rubbleLevels42;
		distances43 -= rubbleLevels43;
		distances43 = Util.min(distances43, prevDistances44);
		distances43 = Util.min(distances43, prevDistances42);
		distances43 = Util.min(distances43, prevDistances32);
		distances43 = Util.min(distances43, prevDistances33);
		distances43 = Util.min(distances43, prevDistances34);
		distances43 += rubbleLevels43;
		distances44 -= rubbleLevels44;
		distances44 = Util.min(distances44, prevDistances43);
		distances44 = Util.min(distances44, prevDistances33);
		distances44 = Util.min(distances44, prevDistances34);
		distances44 += rubbleLevels44;
	}


	public Direction findBestDirection(MapLocation target) throws GameActionException{
        System.out.println("start: " + Clock.getBytecodeNum());
		populateArrays(target);
		iterate();
		int minDistance = 1000000000;
		int bestidx = 0;
		if (minDistance > distances23){
			minDistance = distances23;
			bestidx = 0;
		}
		if (minDistance > distances33){
			minDistance = distances33;
			bestidx = 1;
		}
		if (minDistance > distances32){
			minDistance = distances32;
			bestidx = 2;
		}
		if (minDistance > distances31){
			minDistance = distances31;
			bestidx = 3;
		}
		if (minDistance > distances21){
			minDistance = distances21;
			bestidx = 4;
		}
		if (minDistance > distances11){
			minDistance = distances11;
			bestidx = 5;
		}
		if (minDistance > distances12){
			minDistance = distances12;
			bestidx = 6;
		}
		if (minDistance > distances13){
			minDistance = distances13;
			bestidx = 7;
		}
        System.out.println("end: " + Clock.getBytecodeNum());
		return Util.directions[bestidx];
	}
}