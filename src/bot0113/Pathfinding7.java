package bot0113;
import battlecode.common.*;

public class Pathfinding7{
    static int distanceSquared;
    RobotCommon robot;
    // static int maxStraightDistance;


    static final int[] dx = new int[] {0, 1, 1, 1, 0, -1, -1, -1};
    static final int[] dy = new int[] {1, 1, 0, -1, -1, -1, 0, 1};


    static int AVG_RUBBLE = 40;

    public Pathfinding7(RobotCommon robot){
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

	static int distances05;
	static int prevDistances05;
	static int rubbleLevels05;

	static int distances06;
	static int prevDistances06;
	static int rubbleLevels06;

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

	static int distances15;
	static int prevDistances15;
	static int rubbleLevels15;

	static int distances16;
	static int prevDistances16;
	static int rubbleLevels16;

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

	static int distances25;
	static int prevDistances25;
	static int rubbleLevels25;

	static int distances26;
	static int prevDistances26;
	static int rubbleLevels26;

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

	static int distances35;
	static int prevDistances35;
	static int rubbleLevels35;

	static int distances36;
	static int prevDistances36;
	static int rubbleLevels36;

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

	static int distances45;
	static int prevDistances45;
	static int rubbleLevels45;

	static int distances46;
	static int prevDistances46;
	static int rubbleLevels46;

	static int distances50;
	static int prevDistances50;
	static int rubbleLevels50;

	static int distances51;
	static int prevDistances51;
	static int rubbleLevels51;

	static int distances52;
	static int prevDistances52;
	static int rubbleLevels52;

	static int distances53;
	static int prevDistances53;
	static int rubbleLevels53;

	static int distances54;
	static int prevDistances54;
	static int rubbleLevels54;

	static int distances55;
	static int prevDistances55;
	static int rubbleLevels55;

	static int distances56;
	static int prevDistances56;
	static int rubbleLevels56;

	static int distances60;
	static int prevDistances60;
	static int rubbleLevels60;

	static int distances61;
	static int prevDistances61;
	static int rubbleLevels61;

	static int distances62;
	static int prevDistances62;
	static int rubbleLevels62;

	static int distances63;
	static int prevDistances63;
	static int rubbleLevels63;

	static int distances64;
	static int prevDistances64;
	static int rubbleLevels64;

	static int distances65;
	static int prevDistances65;
	static int rubbleLevels65;

	static int distances66;
	static int prevDistances66;
	static int rubbleLevels66;

	public void populateArrays(MapLocation target) throws GameActionException{
		MapLocation mc = robot.me.translate(-3, -3);
		int newrow = mc.x;
		int newcol = mc.y;
		int dxDiff = 0;
		int dyDiff = 0;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(0, 0)))){
			rubbleLevels00 = 1000000000;
			distances00 = 1000000000;
			prevDistances00 = 1000000000;
		}
		else{
			rubbleLevels00 = robot.rc.senseRubble(mc.translate(0, 0)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances00 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances00 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances00 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances00 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances00 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances00 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances00 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances00 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances01 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances01 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances01 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances01 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances01 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances01 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances01 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances01 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances02 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances02 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances02 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances02 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances02 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances02 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances02 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances02 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances03 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances03 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances03 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances03 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances03 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances03 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances03 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances03 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances04 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances04 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances04 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances04 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances04 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances04 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances04 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances04 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances04 = distances04;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(0, 5)))){
			rubbleLevels05 = 1000000000;
			distances05 = 1000000000;
			prevDistances05 = 1000000000;
		}
		else{
			rubbleLevels05 = robot.rc.senseRubble(mc.translate(0, 5)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances05 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances05 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances05 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances05 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances05 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances05 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances05 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances05 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances05 = distances05;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(0, 6)))){
			rubbleLevels06 = 1000000000;
			distances06 = 1000000000;
			prevDistances06 = 1000000000;
		}
		else{
			rubbleLevels06 = robot.rc.senseRubble(mc.translate(0, 6)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances06 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances06 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances06 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances06 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances06 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances06 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances06 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances06 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances06 = distances06;
		}
		newrow++;
		newcol -= 6;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(1, 0)))){
			rubbleLevels10 = 1000000000;
			distances10 = 1000000000;
			prevDistances10 = 1000000000;
		}
		else{
			rubbleLevels10 = robot.rc.senseRubble(mc.translate(1, 0)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances10 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances10 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances10 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances10 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances10 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances10 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances10 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances10 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances11 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances11 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances11 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances11 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances11 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances11 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances11 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances11 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances12 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances12 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances12 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances12 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances12 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances12 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances12 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances12 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances13 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances13 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances13 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances13 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances13 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances13 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances13 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances13 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances14 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances14 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances14 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances14 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances14 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances14 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances14 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances14 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances14 = distances14;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(1, 5)))){
			rubbleLevels15 = 1000000000;
			distances15 = 1000000000;
			prevDistances15 = 1000000000;
		}
		else{
			rubbleLevels15 = robot.rc.senseRubble(mc.translate(1, 5)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances15 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances15 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances15 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances15 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances15 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances15 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances15 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances15 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances15 = distances15;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(1, 6)))){
			rubbleLevels16 = 1000000000;
			distances16 = 1000000000;
			prevDistances16 = 1000000000;
		}
		else{
			rubbleLevels16 = robot.rc.senseRubble(mc.translate(1, 6)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances16 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances16 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances16 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances16 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances16 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances16 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances16 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances16 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances16 = distances16;
		}
		newrow++;
		newcol -= 6;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(2, 0)))){
			rubbleLevels20 = 1000000000;
			distances20 = 1000000000;
			prevDistances20 = 1000000000;
		}
		else{
			rubbleLevels20 = robot.rc.senseRubble(mc.translate(2, 0)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances20 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances20 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances20 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances20 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances20 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances20 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances20 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances20 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances21 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances21 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances21 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances21 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances21 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances21 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances21 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances21 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances21 = distances21;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(2, 2)))){
			rubbleLevels22 = 1000000000;
			distances22 = 1000000000;
			prevDistances22 = 1000000000;
		}
		else{
			rubbleLevels22 = robot.rc.senseRubble(mc.translate(2, 2)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances22 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances22 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances22 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances22 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances22 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances22 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances22 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances22 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances22 = distances22;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(2, 3)))){
			rubbleLevels23 = 1000000000;
			distances23 = 1000000000;
			prevDistances23 = 1000000000;
		}
		else{
			rubbleLevels23 = robot.rc.senseRubble(mc.translate(2, 3)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances23 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances23 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances23 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances23 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances23 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances23 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances23 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances23 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances24 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances24 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances24 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances24 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances24 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances24 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances24 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances24 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances24 = distances24;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(2, 5)))){
			rubbleLevels25 = 1000000000;
			distances25 = 1000000000;
			prevDistances25 = 1000000000;
		}
		else{
			rubbleLevels25 = robot.rc.senseRubble(mc.translate(2, 5)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances25 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances25 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances25 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances25 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances25 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances25 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances25 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances25 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances25 = distances25;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(2, 6)))){
			rubbleLevels26 = 1000000000;
			distances26 = 1000000000;
			prevDistances26 = 1000000000;
		}
		else{
			rubbleLevels26 = robot.rc.senseRubble(mc.translate(2, 6)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances26 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances26 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances26 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances26 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances26 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances26 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances26 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances26 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances26 = distances26;
		}
		newrow++;
		newcol -= 6;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(3, 0)))){
			rubbleLevels30 = 1000000000;
			distances30 = 1000000000;
			prevDistances30 = 1000000000;
		}
		else{
			rubbleLevels30 = robot.rc.senseRubble(mc.translate(3, 0)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances30 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances30 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances30 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances30 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances30 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances30 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances30 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances30 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances31 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances31 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances31 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances31 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances31 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances31 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances31 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances31 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances32 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances32 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances32 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances32 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances32 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances32 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances32 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances32 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances32 = distances32;
		}
		newcol++;
		rubbleLevels33 = robot.rc.senseRubble(mc.translate(3, 3)) + 10;
		distances33 = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;
		prevDistances33 = distances33;
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(3, 4)))){
			rubbleLevels34 = 1000000000;
			distances34 = 1000000000;
			prevDistances34 = 1000000000;
		}
		else{
			rubbleLevels34 = robot.rc.senseRubble(mc.translate(3, 4)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances34 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances34 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances34 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances34 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances34 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances34 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances34 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances34 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances34 = distances34;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(3, 5)))){
			rubbleLevels35 = 1000000000;
			distances35 = 1000000000;
			prevDistances35 = 1000000000;
		}
		else{
			rubbleLevels35 = robot.rc.senseRubble(mc.translate(3, 5)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances35 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances35 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances35 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances35 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances35 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances35 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances35 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances35 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances35 = distances35;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(3, 6)))){
			rubbleLevels36 = 1000000000;
			distances36 = 1000000000;
			prevDistances36 = 1000000000;
		}
		else{
			rubbleLevels36 = robot.rc.senseRubble(mc.translate(3, 6)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances36 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances36 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances36 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances36 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances36 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances36 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances36 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances36 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances36 = distances36;
		}
		newrow++;
		newcol -= 6;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(4, 0)))){
			rubbleLevels40 = 1000000000;
			distances40 = 1000000000;
			prevDistances40 = 1000000000;
		}
		else{
			rubbleLevels40 = robot.rc.senseRubble(mc.translate(4, 0)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances40 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances40 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances40 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances40 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances40 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances40 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances40 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances40 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances41 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances41 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances41 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances41 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances41 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances41 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances41 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances41 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances42 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances42 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances42 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances42 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances42 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances42 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances42 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances42 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances43 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances43 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances43 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances43 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances43 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances43 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances43 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances43 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
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
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances44 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances44 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances44 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances44 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances44 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances44 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances44 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances44 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances44 = distances44;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(4, 5)))){
			rubbleLevels45 = 1000000000;
			distances45 = 1000000000;
			prevDistances45 = 1000000000;
		}
		else{
			rubbleLevels45 = robot.rc.senseRubble(mc.translate(4, 5)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances45 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances45 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances45 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances45 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances45 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances45 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances45 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances45 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances45 = distances45;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(4, 6)))){
			rubbleLevels46 = 1000000000;
			distances46 = 1000000000;
			prevDistances46 = 1000000000;
		}
		else{
			rubbleLevels46 = robot.rc.senseRubble(mc.translate(4, 6)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances46 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances46 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances46 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances46 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances46 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances46 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances46 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances46 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances46 = distances46;
		}
		newrow++;
		newcol -= 6;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(5, 0)))){
			rubbleLevels50 = 1000000000;
			distances50 = 1000000000;
			prevDistances50 = 1000000000;
		}
		else{
			rubbleLevels50 = robot.rc.senseRubble(mc.translate(5, 0)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances50 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances50 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances50 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances50 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances50 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances50 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances50 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances50 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances50 = distances50;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(5, 1)))){
			rubbleLevels51 = 1000000000;
			distances51 = 1000000000;
			prevDistances51 = 1000000000;
		}
		else{
			rubbleLevels51 = robot.rc.senseRubble(mc.translate(5, 1)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances51 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances51 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances51 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances51 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances51 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances51 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances51 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances51 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances51 = distances51;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(5, 2)))){
			rubbleLevels52 = 1000000000;
			distances52 = 1000000000;
			prevDistances52 = 1000000000;
		}
		else{
			rubbleLevels52 = robot.rc.senseRubble(mc.translate(5, 2)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances52 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances52 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances52 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances52 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances52 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances52 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances52 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances52 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances52 = distances52;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(5, 3)))){
			rubbleLevels53 = 1000000000;
			distances53 = 1000000000;
			prevDistances53 = 1000000000;
		}
		else{
			rubbleLevels53 = robot.rc.senseRubble(mc.translate(5, 3)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances53 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances53 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances53 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances53 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances53 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances53 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances53 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances53 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances53 = distances53;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(5, 4)))){
			rubbleLevels54 = 1000000000;
			distances54 = 1000000000;
			prevDistances54 = 1000000000;
		}
		else{
			rubbleLevels54 = robot.rc.senseRubble(mc.translate(5, 4)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances54 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances54 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances54 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances54 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances54 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances54 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances54 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances54 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances54 = distances54;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(5, 5)))){
			rubbleLevels55 = 1000000000;
			distances55 = 1000000000;
			prevDistances55 = 1000000000;
		}
		else{
			rubbleLevels55 = robot.rc.senseRubble(mc.translate(5, 5)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances55 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances55 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances55 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances55 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances55 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances55 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances55 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances55 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances55 = distances55;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(5, 6)))){
			rubbleLevels56 = 1000000000;
			distances56 = 1000000000;
			prevDistances56 = 1000000000;
		}
		else{
			rubbleLevels56 = robot.rc.senseRubble(mc.translate(5, 6)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances56 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances56 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances56 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances56 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances56 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances56 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances56 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances56 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances56 = distances56;
		}
		newrow++;
		newcol -= 6;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(6, 0)))){
			rubbleLevels60 = 1000000000;
			distances60 = 1000000000;
			prevDistances60 = 1000000000;
		}
		else{
			rubbleLevels60 = robot.rc.senseRubble(mc.translate(6, 0)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances60 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances60 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances60 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances60 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances60 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances60 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances60 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances60 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances60 = distances60;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(6, 1)))){
			rubbleLevels61 = 1000000000;
			distances61 = 1000000000;
			prevDistances61 = 1000000000;
		}
		else{
			rubbleLevels61 = robot.rc.senseRubble(mc.translate(6, 1)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances61 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances61 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances61 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances61 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances61 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances61 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances61 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances61 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances61 = distances61;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(6, 2)))){
			rubbleLevels62 = 1000000000;
			distances62 = 1000000000;
			prevDistances62 = 1000000000;
		}
		else{
			rubbleLevels62 = robot.rc.senseRubble(mc.translate(6, 2)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances62 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances62 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances62 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances62 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances62 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances62 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances62 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances62 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances62 = distances62;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(6, 3)))){
			rubbleLevels63 = 1000000000;
			distances63 = 1000000000;
			prevDistances63 = 1000000000;
		}
		else{
			rubbleLevels63 = robot.rc.senseRubble(mc.translate(6, 3)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances63 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances63 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances63 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances63 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances63 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances63 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances63 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances63 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances63 = distances63;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(6, 4)))){
			rubbleLevels64 = 1000000000;
			distances64 = 1000000000;
			prevDistances64 = 1000000000;
		}
		else{
			rubbleLevels64 = robot.rc.senseRubble(mc.translate(6, 4)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances64 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances64 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances64 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances64 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances64 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances64 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances64 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances64 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances64 = distances64;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(6, 5)))){
			rubbleLevels65 = 1000000000;
			distances65 = 1000000000;
			prevDistances65 = 1000000000;
		}
		else{
			rubbleLevels65 = robot.rc.senseRubble(mc.translate(6, 5)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances65 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances65 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances65 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances65 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances65 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances65 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances65 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances65 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances65 = distances65;
		}
		newcol++;
		if (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(6, 6)))){
			rubbleLevels66 = 1000000000;
			distances66 = 1000000000;
			prevDistances66 = 1000000000;
		}
		else{
			rubbleLevels66 = robot.rc.senseRubble(mc.translate(6, 6)) + 10;
			dxDiff = newrow - target.x;
			dyDiff = newcol - target.y;
			if (dxDiff >= 0){
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances66 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances66 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if (dxDiff >= dyDiff){
						distances66 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances66 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			else{
				dxDiff *= -1;
				if (dyDiff >= 0){
					if (dxDiff >= dyDiff){
						distances66 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances66 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
				else{
					dyDiff *= -1;
					if(dxDiff >= dyDiff){
						distances66 = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
					else{
						distances66 = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;
					}
				}
			}
			prevDistances66 = distances66;
		}
	}


	public void iterate(){
		distances00 -= rubbleLevels00;
		if (distances00 > prevDistances01){
			distances00 = prevDistances01;
		}
		if (distances00 > prevDistances11){
			distances00 = prevDistances11;
		}
		if (distances00 > prevDistances10){
			distances00 = prevDistances10;
		}
		distances00 += rubbleLevels00;
		distances01 -= rubbleLevels01;
		if (distances01 > prevDistances02){
			distances01 = prevDistances02;
		}
		if (distances01 > prevDistances12){
			distances01 = prevDistances12;
		}
		if (distances01 > prevDistances11){
			distances01 = prevDistances11;
		}
		if (distances01 > prevDistances10){
			distances01 = prevDistances10;
		}
		if (distances01 > prevDistances00){
			distances01 = prevDistances00;
		}
		distances01 += rubbleLevels01;
		distances02 -= rubbleLevels02;
		if (distances02 > prevDistances03){
			distances02 = prevDistances03;
		}
		if (distances02 > prevDistances13){
			distances02 = prevDistances13;
		}
		if (distances02 > prevDistances12){
			distances02 = prevDistances12;
		}
		if (distances02 > prevDistances11){
			distances02 = prevDistances11;
		}
		if (distances02 > prevDistances01){
			distances02 = prevDistances01;
		}
		distances02 += rubbleLevels02;
		distances03 -= rubbleLevels03;
		if (distances03 > prevDistances04){
			distances03 = prevDistances04;
		}
		if (distances03 > prevDistances14){
			distances03 = prevDistances14;
		}
		if (distances03 > prevDistances13){
			distances03 = prevDistances13;
		}
		if (distances03 > prevDistances12){
			distances03 = prevDistances12;
		}
		if (distances03 > prevDistances02){
			distances03 = prevDistances02;
		}
		distances03 += rubbleLevels03;
		distances04 -= rubbleLevels04;
		if (distances04 > prevDistances05){
			distances04 = prevDistances05;
		}
		if (distances04 > prevDistances15){
			distances04 = prevDistances15;
		}
		if (distances04 > prevDistances14){
			distances04 = prevDistances14;
		}
		if (distances04 > prevDistances13){
			distances04 = prevDistances13;
		}
		if (distances04 > prevDistances03){
			distances04 = prevDistances03;
		}
		distances04 += rubbleLevels04;
		distances05 -= rubbleLevels05;
		if (distances05 > prevDistances06){
			distances05 = prevDistances06;
		}
		if (distances05 > prevDistances16){
			distances05 = prevDistances16;
		}
		if (distances05 > prevDistances15){
			distances05 = prevDistances15;
		}
		if (distances05 > prevDistances14){
			distances05 = prevDistances14;
		}
		if (distances05 > prevDistances04){
			distances05 = prevDistances04;
		}
		distances05 += rubbleLevels05;
		distances06 -= rubbleLevels06;
		if (distances06 > prevDistances16){
			distances06 = prevDistances16;
		}
		if (distances06 > prevDistances15){
			distances06 = prevDistances15;
		}
		if (distances06 > prevDistances05){
			distances06 = prevDistances05;
		}
		distances06 += rubbleLevels06;
		distances10 -= rubbleLevels10;
		if (distances10 > prevDistances11){
			distances10 = prevDistances11;
		}
		if (distances10 > prevDistances21){
			distances10 = prevDistances21;
		}
		if (distances10 > prevDistances20){
			distances10 = prevDistances20;
		}
		if (distances10 > prevDistances00){
			distances10 = prevDistances00;
		}
		if (distances10 > prevDistances01){
			distances10 = prevDistances01;
		}
		distances10 += rubbleLevels10;
		distances11 -= rubbleLevels11;
		if (distances11 > prevDistances12){
			distances11 = prevDistances12;
		}
		if (distances11 > prevDistances22){
			distances11 = prevDistances22;
		}
		if (distances11 > prevDistances21){
			distances11 = prevDistances21;
		}
		if (distances11 > prevDistances20){
			distances11 = prevDistances20;
		}
		if (distances11 > prevDistances10){
			distances11 = prevDistances10;
		}
		if (distances11 > prevDistances00){
			distances11 = prevDistances00;
		}
		if (distances11 > prevDistances01){
			distances11 = prevDistances01;
		}
		if (distances11 > prevDistances02){
			distances11 = prevDistances02;
		}
		distances11 += rubbleLevels11;
		distances12 -= rubbleLevels12;
		if (distances12 > prevDistances13){
			distances12 = prevDistances13;
		}
		if (distances12 > prevDistances23){
			distances12 = prevDistances23;
		}
		if (distances12 > prevDistances22){
			distances12 = prevDistances22;
		}
		if (distances12 > prevDistances21){
			distances12 = prevDistances21;
		}
		if (distances12 > prevDistances11){
			distances12 = prevDistances11;
		}
		if (distances12 > prevDistances01){
			distances12 = prevDistances01;
		}
		if (distances12 > prevDistances02){
			distances12 = prevDistances02;
		}
		if (distances12 > prevDistances03){
			distances12 = prevDistances03;
		}
		distances12 += rubbleLevels12;
		distances13 -= rubbleLevels13;
		if (distances13 > prevDistances14){
			distances13 = prevDistances14;
		}
		if (distances13 > prevDistances24){
			distances13 = prevDistances24;
		}
		if (distances13 > prevDistances23){
			distances13 = prevDistances23;
		}
		if (distances13 > prevDistances22){
			distances13 = prevDistances22;
		}
		if (distances13 > prevDistances12){
			distances13 = prevDistances12;
		}
		if (distances13 > prevDistances02){
			distances13 = prevDistances02;
		}
		if (distances13 > prevDistances03){
			distances13 = prevDistances03;
		}
		if (distances13 > prevDistances04){
			distances13 = prevDistances04;
		}
		distances13 += rubbleLevels13;
		distances14 -= rubbleLevels14;
		if (distances14 > prevDistances15){
			distances14 = prevDistances15;
		}
		if (distances14 > prevDistances25){
			distances14 = prevDistances25;
		}
		if (distances14 > prevDistances24){
			distances14 = prevDistances24;
		}
		if (distances14 > prevDistances23){
			distances14 = prevDistances23;
		}
		if (distances14 > prevDistances13){
			distances14 = prevDistances13;
		}
		if (distances14 > prevDistances03){
			distances14 = prevDistances03;
		}
		if (distances14 > prevDistances04){
			distances14 = prevDistances04;
		}
		if (distances14 > prevDistances05){
			distances14 = prevDistances05;
		}
		distances14 += rubbleLevels14;
		distances15 -= rubbleLevels15;
		if (distances15 > prevDistances16){
			distances15 = prevDistances16;
		}
		if (distances15 > prevDistances26){
			distances15 = prevDistances26;
		}
		if (distances15 > prevDistances25){
			distances15 = prevDistances25;
		}
		if (distances15 > prevDistances24){
			distances15 = prevDistances24;
		}
		if (distances15 > prevDistances14){
			distances15 = prevDistances14;
		}
		if (distances15 > prevDistances04){
			distances15 = prevDistances04;
		}
		if (distances15 > prevDistances05){
			distances15 = prevDistances05;
		}
		if (distances15 > prevDistances06){
			distances15 = prevDistances06;
		}
		distances15 += rubbleLevels15;
		distances16 -= rubbleLevels16;
		if (distances16 > prevDistances26){
			distances16 = prevDistances26;
		}
		if (distances16 > prevDistances25){
			distances16 = prevDistances25;
		}
		if (distances16 > prevDistances15){
			distances16 = prevDistances15;
		}
		if (distances16 > prevDistances05){
			distances16 = prevDistances05;
		}
		if (distances16 > prevDistances06){
			distances16 = prevDistances06;
		}
		distances16 += rubbleLevels16;
		distances20 -= rubbleLevels20;
		if (distances20 > prevDistances21){
			distances20 = prevDistances21;
		}
		if (distances20 > prevDistances31){
			distances20 = prevDistances31;
		}
		if (distances20 > prevDistances30){
			distances20 = prevDistances30;
		}
		if (distances20 > prevDistances10){
			distances20 = prevDistances10;
		}
		if (distances20 > prevDistances11){
			distances20 = prevDistances11;
		}
		distances20 += rubbleLevels20;
		distances21 -= rubbleLevels21;
		if (distances21 > prevDistances22){
			distances21 = prevDistances22;
		}
		if (distances21 > prevDistances32){
			distances21 = prevDistances32;
		}
		if (distances21 > prevDistances31){
			distances21 = prevDistances31;
		}
		if (distances21 > prevDistances30){
			distances21 = prevDistances30;
		}
		if (distances21 > prevDistances20){
			distances21 = prevDistances20;
		}
		if (distances21 > prevDistances10){
			distances21 = prevDistances10;
		}
		if (distances21 > prevDistances11){
			distances21 = prevDistances11;
		}
		if (distances21 > prevDistances12){
			distances21 = prevDistances12;
		}
		distances21 += rubbleLevels21;
		distances22 -= rubbleLevels22;
		if (distances22 > prevDistances23){
			distances22 = prevDistances23;
		}
		if (distances22 > prevDistances33){
			distances22 = prevDistances33;
		}
		if (distances22 > prevDistances32){
			distances22 = prevDistances32;
		}
		if (distances22 > prevDistances31){
			distances22 = prevDistances31;
		}
		if (distances22 > prevDistances21){
			distances22 = prevDistances21;
		}
		if (distances22 > prevDistances11){
			distances22 = prevDistances11;
		}
		if (distances22 > prevDistances12){
			distances22 = prevDistances12;
		}
		if (distances22 > prevDistances13){
			distances22 = prevDistances13;
		}
		distances22 += rubbleLevels22;
		distances23 -= rubbleLevels23;
		if (distances23 > prevDistances24){
			distances23 = prevDistances24;
		}
		if (distances23 > prevDistances34){
			distances23 = prevDistances34;
		}
		if (distances23 > prevDistances33){
			distances23 = prevDistances33;
		}
		if (distances23 > prevDistances32){
			distances23 = prevDistances32;
		}
		if (distances23 > prevDistances22){
			distances23 = prevDistances22;
		}
		if (distances23 > prevDistances12){
			distances23 = prevDistances12;
		}
		if (distances23 > prevDistances13){
			distances23 = prevDistances13;
		}
		if (distances23 > prevDistances14){
			distances23 = prevDistances14;
		}
		distances23 += rubbleLevels23;
		distances24 -= rubbleLevels24;
		if (distances24 > prevDistances25){
			distances24 = prevDistances25;
		}
		if (distances24 > prevDistances35){
			distances24 = prevDistances35;
		}
		if (distances24 > prevDistances34){
			distances24 = prevDistances34;
		}
		if (distances24 > prevDistances33){
			distances24 = prevDistances33;
		}
		if (distances24 > prevDistances23){
			distances24 = prevDistances23;
		}
		if (distances24 > prevDistances13){
			distances24 = prevDistances13;
		}
		if (distances24 > prevDistances14){
			distances24 = prevDistances14;
		}
		if (distances24 > prevDistances15){
			distances24 = prevDistances15;
		}
		distances24 += rubbleLevels24;
		distances25 -= rubbleLevels25;
		if (distances25 > prevDistances26){
			distances25 = prevDistances26;
		}
		if (distances25 > prevDistances36){
			distances25 = prevDistances36;
		}
		if (distances25 > prevDistances35){
			distances25 = prevDistances35;
		}
		if (distances25 > prevDistances34){
			distances25 = prevDistances34;
		}
		if (distances25 > prevDistances24){
			distances25 = prevDistances24;
		}
		if (distances25 > prevDistances14){
			distances25 = prevDistances14;
		}
		if (distances25 > prevDistances15){
			distances25 = prevDistances15;
		}
		if (distances25 > prevDistances16){
			distances25 = prevDistances16;
		}
		distances25 += rubbleLevels25;
		distances26 -= rubbleLevels26;
		if (distances26 > prevDistances36){
			distances26 = prevDistances36;
		}
		if (distances26 > prevDistances35){
			distances26 = prevDistances35;
		}
		if (distances26 > prevDistances25){
			distances26 = prevDistances25;
		}
		if (distances26 > prevDistances15){
			distances26 = prevDistances15;
		}
		if (distances26 > prevDistances16){
			distances26 = prevDistances16;
		}
		distances26 += rubbleLevels26;
		distances30 -= rubbleLevels30;
		if (distances30 > prevDistances31){
			distances30 = prevDistances31;
		}
		if (distances30 > prevDistances41){
			distances30 = prevDistances41;
		}
		if (distances30 > prevDistances40){
			distances30 = prevDistances40;
		}
		if (distances30 > prevDistances20){
			distances30 = prevDistances20;
		}
		if (distances30 > prevDistances21){
			distances30 = prevDistances21;
		}
		distances30 += rubbleLevels30;
		distances31 -= rubbleLevels31;
		if (distances31 > prevDistances32){
			distances31 = prevDistances32;
		}
		if (distances31 > prevDistances42){
			distances31 = prevDistances42;
		}
		if (distances31 > prevDistances41){
			distances31 = prevDistances41;
		}
		if (distances31 > prevDistances40){
			distances31 = prevDistances40;
		}
		if (distances31 > prevDistances30){
			distances31 = prevDistances30;
		}
		if (distances31 > prevDistances20){
			distances31 = prevDistances20;
		}
		if (distances31 > prevDistances21){
			distances31 = prevDistances21;
		}
		if (distances31 > prevDistances22){
			distances31 = prevDistances22;
		}
		distances31 += rubbleLevels31;
		distances32 -= rubbleLevels32;
		if (distances32 > prevDistances33){
			distances32 = prevDistances33;
		}
		if (distances32 > prevDistances43){
			distances32 = prevDistances43;
		}
		if (distances32 > prevDistances42){
			distances32 = prevDistances42;
		}
		if (distances32 > prevDistances41){
			distances32 = prevDistances41;
		}
		if (distances32 > prevDistances31){
			distances32 = prevDistances31;
		}
		if (distances32 > prevDistances21){
			distances32 = prevDistances21;
		}
		if (distances32 > prevDistances22){
			distances32 = prevDistances22;
		}
		if (distances32 > prevDistances23){
			distances32 = prevDistances23;
		}
		distances32 += rubbleLevels32;
		distances33 -= rubbleLevels33;
		if (distances33 > prevDistances34){
			distances33 = prevDistances34;
		}
		if (distances33 > prevDistances44){
			distances33 = prevDistances44;
		}
		if (distances33 > prevDistances43){
			distances33 = prevDistances43;
		}
		if (distances33 > prevDistances42){
			distances33 = prevDistances42;
		}
		if (distances33 > prevDistances32){
			distances33 = prevDistances32;
		}
		if (distances33 > prevDistances22){
			distances33 = prevDistances22;
		}
		if (distances33 > prevDistances23){
			distances33 = prevDistances23;
		}
		if (distances33 > prevDistances24){
			distances33 = prevDistances24;
		}
		distances33 += rubbleLevels33;
		distances34 -= rubbleLevels34;
		if (distances34 > prevDistances35){
			distances34 = prevDistances35;
		}
		if (distances34 > prevDistances45){
			distances34 = prevDistances45;
		}
		if (distances34 > prevDistances44){
			distances34 = prevDistances44;
		}
		if (distances34 > prevDistances43){
			distances34 = prevDistances43;
		}
		if (distances34 > prevDistances33){
			distances34 = prevDistances33;
		}
		if (distances34 > prevDistances23){
			distances34 = prevDistances23;
		}
		if (distances34 > prevDistances24){
			distances34 = prevDistances24;
		}
		if (distances34 > prevDistances25){
			distances34 = prevDistances25;
		}
		distances34 += rubbleLevels34;
		distances35 -= rubbleLevels35;
		if (distances35 > prevDistances36){
			distances35 = prevDistances36;
		}
		if (distances35 > prevDistances46){
			distances35 = prevDistances46;
		}
		if (distances35 > prevDistances45){
			distances35 = prevDistances45;
		}
		if (distances35 > prevDistances44){
			distances35 = prevDistances44;
		}
		if (distances35 > prevDistances34){
			distances35 = prevDistances34;
		}
		if (distances35 > prevDistances24){
			distances35 = prevDistances24;
		}
		if (distances35 > prevDistances25){
			distances35 = prevDistances25;
		}
		if (distances35 > prevDistances26){
			distances35 = prevDistances26;
		}
		distances35 += rubbleLevels35;
		distances36 -= rubbleLevels36;
		if (distances36 > prevDistances46){
			distances36 = prevDistances46;
		}
		if (distances36 > prevDistances45){
			distances36 = prevDistances45;
		}
		if (distances36 > prevDistances35){
			distances36 = prevDistances35;
		}
		if (distances36 > prevDistances25){
			distances36 = prevDistances25;
		}
		if (distances36 > prevDistances26){
			distances36 = prevDistances26;
		}
		distances36 += rubbleLevels36;
		distances40 -= rubbleLevels40;
		if (distances40 > prevDistances41){
			distances40 = prevDistances41;
		}
		if (distances40 > prevDistances51){
			distances40 = prevDistances51;
		}
		if (distances40 > prevDistances50){
			distances40 = prevDistances50;
		}
		if (distances40 > prevDistances30){
			distances40 = prevDistances30;
		}
		if (distances40 > prevDistances31){
			distances40 = prevDistances31;
		}
		distances40 += rubbleLevels40;
		distances41 -= rubbleLevels41;
		if (distances41 > prevDistances42){
			distances41 = prevDistances42;
		}
		if (distances41 > prevDistances52){
			distances41 = prevDistances52;
		}
		if (distances41 > prevDistances51){
			distances41 = prevDistances51;
		}
		if (distances41 > prevDistances50){
			distances41 = prevDistances50;
		}
		if (distances41 > prevDistances40){
			distances41 = prevDistances40;
		}
		if (distances41 > prevDistances30){
			distances41 = prevDistances30;
		}
		if (distances41 > prevDistances31){
			distances41 = prevDistances31;
		}
		if (distances41 > prevDistances32){
			distances41 = prevDistances32;
		}
		distances41 += rubbleLevels41;
		distances42 -= rubbleLevels42;
		if (distances42 > prevDistances43){
			distances42 = prevDistances43;
		}
		if (distances42 > prevDistances53){
			distances42 = prevDistances53;
		}
		if (distances42 > prevDistances52){
			distances42 = prevDistances52;
		}
		if (distances42 > prevDistances51){
			distances42 = prevDistances51;
		}
		if (distances42 > prevDistances41){
			distances42 = prevDistances41;
		}
		if (distances42 > prevDistances31){
			distances42 = prevDistances31;
		}
		if (distances42 > prevDistances32){
			distances42 = prevDistances32;
		}
		if (distances42 > prevDistances33){
			distances42 = prevDistances33;
		}
		distances42 += rubbleLevels42;
		distances43 -= rubbleLevels43;
		if (distances43 > prevDistances44){
			distances43 = prevDistances44;
		}
		if (distances43 > prevDistances54){
			distances43 = prevDistances54;
		}
		if (distances43 > prevDistances53){
			distances43 = prevDistances53;
		}
		if (distances43 > prevDistances52){
			distances43 = prevDistances52;
		}
		if (distances43 > prevDistances42){
			distances43 = prevDistances42;
		}
		if (distances43 > prevDistances32){
			distances43 = prevDistances32;
		}
		if (distances43 > prevDistances33){
			distances43 = prevDistances33;
		}
		if (distances43 > prevDistances34){
			distances43 = prevDistances34;
		}
		distances43 += rubbleLevels43;
		distances44 -= rubbleLevels44;
		if (distances44 > prevDistances45){
			distances44 = prevDistances45;
		}
		if (distances44 > prevDistances55){
			distances44 = prevDistances55;
		}
		if (distances44 > prevDistances54){
			distances44 = prevDistances54;
		}
		if (distances44 > prevDistances53){
			distances44 = prevDistances53;
		}
		if (distances44 > prevDistances43){
			distances44 = prevDistances43;
		}
		if (distances44 > prevDistances33){
			distances44 = prevDistances33;
		}
		if (distances44 > prevDistances34){
			distances44 = prevDistances34;
		}
		if (distances44 > prevDistances35){
			distances44 = prevDistances35;
		}
		distances44 += rubbleLevels44;
		distances45 -= rubbleLevels45;
		if (distances45 > prevDistances46){
			distances45 = prevDistances46;
		}
		if (distances45 > prevDistances56){
			distances45 = prevDistances56;
		}
		if (distances45 > prevDistances55){
			distances45 = prevDistances55;
		}
		if (distances45 > prevDistances54){
			distances45 = prevDistances54;
		}
		if (distances45 > prevDistances44){
			distances45 = prevDistances44;
		}
		if (distances45 > prevDistances34){
			distances45 = prevDistances34;
		}
		if (distances45 > prevDistances35){
			distances45 = prevDistances35;
		}
		if (distances45 > prevDistances36){
			distances45 = prevDistances36;
		}
		distances45 += rubbleLevels45;
		distances46 -= rubbleLevels46;
		if (distances46 > prevDistances56){
			distances46 = prevDistances56;
		}
		if (distances46 > prevDistances55){
			distances46 = prevDistances55;
		}
		if (distances46 > prevDistances45){
			distances46 = prevDistances45;
		}
		if (distances46 > prevDistances35){
			distances46 = prevDistances35;
		}
		if (distances46 > prevDistances36){
			distances46 = prevDistances36;
		}
		distances46 += rubbleLevels46;
		distances50 -= rubbleLevels50;
		if (distances50 > prevDistances51){
			distances50 = prevDistances51;
		}
		if (distances50 > prevDistances61){
			distances50 = prevDistances61;
		}
		if (distances50 > prevDistances60){
			distances50 = prevDistances60;
		}
		if (distances50 > prevDistances40){
			distances50 = prevDistances40;
		}
		if (distances50 > prevDistances41){
			distances50 = prevDistances41;
		}
		distances50 += rubbleLevels50;
		distances51 -= rubbleLevels51;
		if (distances51 > prevDistances52){
			distances51 = prevDistances52;
		}
		if (distances51 > prevDistances62){
			distances51 = prevDistances62;
		}
		if (distances51 > prevDistances61){
			distances51 = prevDistances61;
		}
		if (distances51 > prevDistances60){
			distances51 = prevDistances60;
		}
		if (distances51 > prevDistances50){
			distances51 = prevDistances50;
		}
		if (distances51 > prevDistances40){
			distances51 = prevDistances40;
		}
		if (distances51 > prevDistances41){
			distances51 = prevDistances41;
		}
		if (distances51 > prevDistances42){
			distances51 = prevDistances42;
		}
		distances51 += rubbleLevels51;
		distances52 -= rubbleLevels52;
		if (distances52 > prevDistances53){
			distances52 = prevDistances53;
		}
		if (distances52 > prevDistances63){
			distances52 = prevDistances63;
		}
		if (distances52 > prevDistances62){
			distances52 = prevDistances62;
		}
		if (distances52 > prevDistances61){
			distances52 = prevDistances61;
		}
		if (distances52 > prevDistances51){
			distances52 = prevDistances51;
		}
		if (distances52 > prevDistances41){
			distances52 = prevDistances41;
		}
		if (distances52 > prevDistances42){
			distances52 = prevDistances42;
		}
		if (distances52 > prevDistances43){
			distances52 = prevDistances43;
		}
		distances52 += rubbleLevels52;
		distances53 -= rubbleLevels53;
		if (distances53 > prevDistances54){
			distances53 = prevDistances54;
		}
		if (distances53 > prevDistances64){
			distances53 = prevDistances64;
		}
		if (distances53 > prevDistances63){
			distances53 = prevDistances63;
		}
		if (distances53 > prevDistances62){
			distances53 = prevDistances62;
		}
		if (distances53 > prevDistances52){
			distances53 = prevDistances52;
		}
		if (distances53 > prevDistances42){
			distances53 = prevDistances42;
		}
		if (distances53 > prevDistances43){
			distances53 = prevDistances43;
		}
		if (distances53 > prevDistances44){
			distances53 = prevDistances44;
		}
		distances53 += rubbleLevels53;
		distances54 -= rubbleLevels54;
		if (distances54 > prevDistances55){
			distances54 = prevDistances55;
		}
		if (distances54 > prevDistances65){
			distances54 = prevDistances65;
		}
		if (distances54 > prevDistances64){
			distances54 = prevDistances64;
		}
		if (distances54 > prevDistances63){
			distances54 = prevDistances63;
		}
		if (distances54 > prevDistances53){
			distances54 = prevDistances53;
		}
		if (distances54 > prevDistances43){
			distances54 = prevDistances43;
		}
		if (distances54 > prevDistances44){
			distances54 = prevDistances44;
		}
		if (distances54 > prevDistances45){
			distances54 = prevDistances45;
		}
		distances54 += rubbleLevels54;
		distances55 -= rubbleLevels55;
		if (distances55 > prevDistances56){
			distances55 = prevDistances56;
		}
		if (distances55 > prevDistances66){
			distances55 = prevDistances66;
		}
		if (distances55 > prevDistances65){
			distances55 = prevDistances65;
		}
		if (distances55 > prevDistances64){
			distances55 = prevDistances64;
		}
		if (distances55 > prevDistances54){
			distances55 = prevDistances54;
		}
		if (distances55 > prevDistances44){
			distances55 = prevDistances44;
		}
		if (distances55 > prevDistances45){
			distances55 = prevDistances45;
		}
		if (distances55 > prevDistances46){
			distances55 = prevDistances46;
		}
		distances55 += rubbleLevels55;
		distances56 -= rubbleLevels56;
		if (distances56 > prevDistances66){
			distances56 = prevDistances66;
		}
		if (distances56 > prevDistances65){
			distances56 = prevDistances65;
		}
		if (distances56 > prevDistances55){
			distances56 = prevDistances55;
		}
		if (distances56 > prevDistances45){
			distances56 = prevDistances45;
		}
		if (distances56 > prevDistances46){
			distances56 = prevDistances46;
		}
		distances56 += rubbleLevels56;
		distances60 -= rubbleLevels60;
		if (distances60 > prevDistances61){
			distances60 = prevDistances61;
		}
		if (distances60 > prevDistances50){
			distances60 = prevDistances50;
		}
		if (distances60 > prevDistances51){
			distances60 = prevDistances51;
		}
		distances60 += rubbleLevels60;
		distances61 -= rubbleLevels61;
		if (distances61 > prevDistances62){
			distances61 = prevDistances62;
		}
		if (distances61 > prevDistances60){
			distances61 = prevDistances60;
		}
		if (distances61 > prevDistances50){
			distances61 = prevDistances50;
		}
		if (distances61 > prevDistances51){
			distances61 = prevDistances51;
		}
		if (distances61 > prevDistances52){
			distances61 = prevDistances52;
		}
		distances61 += rubbleLevels61;
		distances62 -= rubbleLevels62;
		if (distances62 > prevDistances63){
			distances62 = prevDistances63;
		}
		if (distances62 > prevDistances61){
			distances62 = prevDistances61;
		}
		if (distances62 > prevDistances51){
			distances62 = prevDistances51;
		}
		if (distances62 > prevDistances52){
			distances62 = prevDistances52;
		}
		if (distances62 > prevDistances53){
			distances62 = prevDistances53;
		}
		distances62 += rubbleLevels62;
		distances63 -= rubbleLevels63;
		if (distances63 > prevDistances64){
			distances63 = prevDistances64;
		}
		if (distances63 > prevDistances62){
			distances63 = prevDistances62;
		}
		if (distances63 > prevDistances52){
			distances63 = prevDistances52;
		}
		if (distances63 > prevDistances53){
			distances63 = prevDistances53;
		}
		if (distances63 > prevDistances54){
			distances63 = prevDistances54;
		}
		distances63 += rubbleLevels63;
		distances64 -= rubbleLevels64;
		if (distances64 > prevDistances65){
			distances64 = prevDistances65;
		}
		if (distances64 > prevDistances63){
			distances64 = prevDistances63;
		}
		if (distances64 > prevDistances53){
			distances64 = prevDistances53;
		}
		if (distances64 > prevDistances54){
			distances64 = prevDistances54;
		}
		if (distances64 > prevDistances55){
			distances64 = prevDistances55;
		}
		distances64 += rubbleLevels64;
		distances65 -= rubbleLevels65;
		if (distances65 > prevDistances66){
			distances65 = prevDistances66;
		}
		if (distances65 > prevDistances64){
			distances65 = prevDistances64;
		}
		if (distances65 > prevDistances54){
			distances65 = prevDistances54;
		}
		if (distances65 > prevDistances55){
			distances65 = prevDistances55;
		}
		if (distances65 > prevDistances56){
			distances65 = prevDistances56;
		}
		distances65 += rubbleLevels65;
		distances66 -= rubbleLevels66;
		if (distances66 > prevDistances65){
			distances66 = prevDistances65;
		}
		if (distances66 > prevDistances55){
			distances66 = prevDistances55;
		}
		if (distances66 > prevDistances56){
			distances66 = prevDistances56;
		}
		distances66 += rubbleLevels66;
		prevDistances00 -= rubbleLevels00;
		if (prevDistances00 > distances01){
			prevDistances00 = distances01;
		}
		if (prevDistances00 > distances11){
			prevDistances00 = distances11;
		}
		if (prevDistances00 > distances10){
			prevDistances00 = distances10;
		}
		prevDistances00 += rubbleLevels00;
		prevDistances01 -= rubbleLevels01;
		if (prevDistances01 > distances02){
			prevDistances01 = distances02;
		}
		if (prevDistances01 > distances12){
			prevDistances01 = distances12;
		}
		if (prevDistances01 > distances11){
			prevDistances01 = distances11;
		}
		if (prevDistances01 > distances10){
			prevDistances01 = distances10;
		}
		if (prevDistances01 > distances00){
			prevDistances01 = distances00;
		}
		prevDistances01 += rubbleLevels01;
		prevDistances02 -= rubbleLevels02;
		if (prevDistances02 > distances03){
			prevDistances02 = distances03;
		}
		if (prevDistances02 > distances13){
			prevDistances02 = distances13;
		}
		if (prevDistances02 > distances12){
			prevDistances02 = distances12;
		}
		if (prevDistances02 > distances11){
			prevDistances02 = distances11;
		}
		if (prevDistances02 > distances01){
			prevDistances02 = distances01;
		}
		prevDistances02 += rubbleLevels02;
		prevDistances03 -= rubbleLevels03;
		if (prevDistances03 > distances04){
			prevDistances03 = distances04;
		}
		if (prevDistances03 > distances14){
			prevDistances03 = distances14;
		}
		if (prevDistances03 > distances13){
			prevDistances03 = distances13;
		}
		if (prevDistances03 > distances12){
			prevDistances03 = distances12;
		}
		if (prevDistances03 > distances02){
			prevDistances03 = distances02;
		}
		prevDistances03 += rubbleLevels03;
		prevDistances04 -= rubbleLevels04;
		if (prevDistances04 > distances05){
			prevDistances04 = distances05;
		}
		if (prevDistances04 > distances15){
			prevDistances04 = distances15;
		}
		if (prevDistances04 > distances14){
			prevDistances04 = distances14;
		}
		if (prevDistances04 > distances13){
			prevDistances04 = distances13;
		}
		if (prevDistances04 > distances03){
			prevDistances04 = distances03;
		}
		prevDistances04 += rubbleLevels04;
		prevDistances05 -= rubbleLevels05;
		if (prevDistances05 > distances06){
			prevDistances05 = distances06;
		}
		if (prevDistances05 > distances16){
			prevDistances05 = distances16;
		}
		if (prevDistances05 > distances15){
			prevDistances05 = distances15;
		}
		if (prevDistances05 > distances14){
			prevDistances05 = distances14;
		}
		if (prevDistances05 > distances04){
			prevDistances05 = distances04;
		}
		prevDistances05 += rubbleLevels05;
		prevDistances06 -= rubbleLevels06;
		if (prevDistances06 > distances16){
			prevDistances06 = distances16;
		}
		if (prevDistances06 > distances15){
			prevDistances06 = distances15;
		}
		if (prevDistances06 > distances05){
			prevDistances06 = distances05;
		}
		prevDistances06 += rubbleLevels06;
		prevDistances10 -= rubbleLevels10;
		if (prevDistances10 > distances11){
			prevDistances10 = distances11;
		}
		if (prevDistances10 > distances21){
			prevDistances10 = distances21;
		}
		if (prevDistances10 > distances20){
			prevDistances10 = distances20;
		}
		if (prevDistances10 > distances00){
			prevDistances10 = distances00;
		}
		if (prevDistances10 > distances01){
			prevDistances10 = distances01;
		}
		prevDistances10 += rubbleLevels10;
		prevDistances11 -= rubbleLevels11;
		if (prevDistances11 > distances12){
			prevDistances11 = distances12;
		}
		if (prevDistances11 > distances22){
			prevDistances11 = distances22;
		}
		if (prevDistances11 > distances21){
			prevDistances11 = distances21;
		}
		if (prevDistances11 > distances20){
			prevDistances11 = distances20;
		}
		if (prevDistances11 > distances10){
			prevDistances11 = distances10;
		}
		if (prevDistances11 > distances00){
			prevDistances11 = distances00;
		}
		if (prevDistances11 > distances01){
			prevDistances11 = distances01;
		}
		if (prevDistances11 > distances02){
			prevDistances11 = distances02;
		}
		prevDistances11 += rubbleLevels11;
		prevDistances12 -= rubbleLevels12;
		if (prevDistances12 > distances13){
			prevDistances12 = distances13;
		}
		if (prevDistances12 > distances23){
			prevDistances12 = distances23;
		}
		if (prevDistances12 > distances22){
			prevDistances12 = distances22;
		}
		if (prevDistances12 > distances21){
			prevDistances12 = distances21;
		}
		if (prevDistances12 > distances11){
			prevDistances12 = distances11;
		}
		if (prevDistances12 > distances01){
			prevDistances12 = distances01;
		}
		if (prevDistances12 > distances02){
			prevDistances12 = distances02;
		}
		if (prevDistances12 > distances03){
			prevDistances12 = distances03;
		}
		prevDistances12 += rubbleLevels12;
		prevDistances13 -= rubbleLevels13;
		if (prevDistances13 > distances14){
			prevDistances13 = distances14;
		}
		if (prevDistances13 > distances24){
			prevDistances13 = distances24;
		}
		if (prevDistances13 > distances23){
			prevDistances13 = distances23;
		}
		if (prevDistances13 > distances22){
			prevDistances13 = distances22;
		}
		if (prevDistances13 > distances12){
			prevDistances13 = distances12;
		}
		if (prevDistances13 > distances02){
			prevDistances13 = distances02;
		}
		if (prevDistances13 > distances03){
			prevDistances13 = distances03;
		}
		if (prevDistances13 > distances04){
			prevDistances13 = distances04;
		}
		prevDistances13 += rubbleLevels13;
		prevDistances14 -= rubbleLevels14;
		if (prevDistances14 > distances15){
			prevDistances14 = distances15;
		}
		if (prevDistances14 > distances25){
			prevDistances14 = distances25;
		}
		if (prevDistances14 > distances24){
			prevDistances14 = distances24;
		}
		if (prevDistances14 > distances23){
			prevDistances14 = distances23;
		}
		if (prevDistances14 > distances13){
			prevDistances14 = distances13;
		}
		if (prevDistances14 > distances03){
			prevDistances14 = distances03;
		}
		if (prevDistances14 > distances04){
			prevDistances14 = distances04;
		}
		if (prevDistances14 > distances05){
			prevDistances14 = distances05;
		}
		prevDistances14 += rubbleLevels14;
		prevDistances15 -= rubbleLevels15;
		if (prevDistances15 > distances16){
			prevDistances15 = distances16;
		}
		if (prevDistances15 > distances26){
			prevDistances15 = distances26;
		}
		if (prevDistances15 > distances25){
			prevDistances15 = distances25;
		}
		if (prevDistances15 > distances24){
			prevDistances15 = distances24;
		}
		if (prevDistances15 > distances14){
			prevDistances15 = distances14;
		}
		if (prevDistances15 > distances04){
			prevDistances15 = distances04;
		}
		if (prevDistances15 > distances05){
			prevDistances15 = distances05;
		}
		if (prevDistances15 > distances06){
			prevDistances15 = distances06;
		}
		prevDistances15 += rubbleLevels15;
		prevDistances16 -= rubbleLevels16;
		if (prevDistances16 > distances26){
			prevDistances16 = distances26;
		}
		if (prevDistances16 > distances25){
			prevDistances16 = distances25;
		}
		if (prevDistances16 > distances15){
			prevDistances16 = distances15;
		}
		if (prevDistances16 > distances05){
			prevDistances16 = distances05;
		}
		if (prevDistances16 > distances06){
			prevDistances16 = distances06;
		}
		prevDistances16 += rubbleLevels16;
		prevDistances20 -= rubbleLevels20;
		if (prevDistances20 > distances21){
			prevDistances20 = distances21;
		}
		if (prevDistances20 > distances31){
			prevDistances20 = distances31;
		}
		if (prevDistances20 > distances30){
			prevDistances20 = distances30;
		}
		if (prevDistances20 > distances10){
			prevDistances20 = distances10;
		}
		if (prevDistances20 > distances11){
			prevDistances20 = distances11;
		}
		prevDistances20 += rubbleLevels20;
		prevDistances21 -= rubbleLevels21;
		if (prevDistances21 > distances22){
			prevDistances21 = distances22;
		}
		if (prevDistances21 > distances32){
			prevDistances21 = distances32;
		}
		if (prevDistances21 > distances31){
			prevDistances21 = distances31;
		}
		if (prevDistances21 > distances30){
			prevDistances21 = distances30;
		}
		if (prevDistances21 > distances20){
			prevDistances21 = distances20;
		}
		if (prevDistances21 > distances10){
			prevDistances21 = distances10;
		}
		if (prevDistances21 > distances11){
			prevDistances21 = distances11;
		}
		if (prevDistances21 > distances12){
			prevDistances21 = distances12;
		}
		prevDistances21 += rubbleLevels21;
		prevDistances22 -= rubbleLevels22;
		if (prevDistances22 > distances23){
			prevDistances22 = distances23;
		}
		if (prevDistances22 > distances33){
			prevDistances22 = distances33;
		}
		if (prevDistances22 > distances32){
			prevDistances22 = distances32;
		}
		if (prevDistances22 > distances31){
			prevDistances22 = distances31;
		}
		if (prevDistances22 > distances21){
			prevDistances22 = distances21;
		}
		if (prevDistances22 > distances11){
			prevDistances22 = distances11;
		}
		if (prevDistances22 > distances12){
			prevDistances22 = distances12;
		}
		if (prevDistances22 > distances13){
			prevDistances22 = distances13;
		}
		prevDistances22 += rubbleLevels22;
		prevDistances23 -= rubbleLevels23;
		if (prevDistances23 > distances24){
			prevDistances23 = distances24;
		}
		if (prevDistances23 > distances34){
			prevDistances23 = distances34;
		}
		if (prevDistances23 > distances33){
			prevDistances23 = distances33;
		}
		if (prevDistances23 > distances32){
			prevDistances23 = distances32;
		}
		if (prevDistances23 > distances22){
			prevDistances23 = distances22;
		}
		if (prevDistances23 > distances12){
			prevDistances23 = distances12;
		}
		if (prevDistances23 > distances13){
			prevDistances23 = distances13;
		}
		if (prevDistances23 > distances14){
			prevDistances23 = distances14;
		}
		prevDistances23 += rubbleLevels23;
		prevDistances24 -= rubbleLevels24;
		if (prevDistances24 > distances25){
			prevDistances24 = distances25;
		}
		if (prevDistances24 > distances35){
			prevDistances24 = distances35;
		}
		if (prevDistances24 > distances34){
			prevDistances24 = distances34;
		}
		if (prevDistances24 > distances33){
			prevDistances24 = distances33;
		}
		if (prevDistances24 > distances23){
			prevDistances24 = distances23;
		}
		if (prevDistances24 > distances13){
			prevDistances24 = distances13;
		}
		if (prevDistances24 > distances14){
			prevDistances24 = distances14;
		}
		if (prevDistances24 > distances15){
			prevDistances24 = distances15;
		}
		prevDistances24 += rubbleLevels24;
		prevDistances25 -= rubbleLevels25;
		if (prevDistances25 > distances26){
			prevDistances25 = distances26;
		}
		if (prevDistances25 > distances36){
			prevDistances25 = distances36;
		}
		if (prevDistances25 > distances35){
			prevDistances25 = distances35;
		}
		if (prevDistances25 > distances34){
			prevDistances25 = distances34;
		}
		if (prevDistances25 > distances24){
			prevDistances25 = distances24;
		}
		if (prevDistances25 > distances14){
			prevDistances25 = distances14;
		}
		if (prevDistances25 > distances15){
			prevDistances25 = distances15;
		}
		if (prevDistances25 > distances16){
			prevDistances25 = distances16;
		}
		prevDistances25 += rubbleLevels25;
		prevDistances26 -= rubbleLevels26;
		if (prevDistances26 > distances36){
			prevDistances26 = distances36;
		}
		if (prevDistances26 > distances35){
			prevDistances26 = distances35;
		}
		if (prevDistances26 > distances25){
			prevDistances26 = distances25;
		}
		if (prevDistances26 > distances15){
			prevDistances26 = distances15;
		}
		if (prevDistances26 > distances16){
			prevDistances26 = distances16;
		}
		prevDistances26 += rubbleLevels26;
		prevDistances30 -= rubbleLevels30;
		if (prevDistances30 > distances31){
			prevDistances30 = distances31;
		}
		if (prevDistances30 > distances41){
			prevDistances30 = distances41;
		}
		if (prevDistances30 > distances40){
			prevDistances30 = distances40;
		}
		if (prevDistances30 > distances20){
			prevDistances30 = distances20;
		}
		if (prevDistances30 > distances21){
			prevDistances30 = distances21;
		}
		prevDistances30 += rubbleLevels30;
		prevDistances31 -= rubbleLevels31;
		if (prevDistances31 > distances32){
			prevDistances31 = distances32;
		}
		if (prevDistances31 > distances42){
			prevDistances31 = distances42;
		}
		if (prevDistances31 > distances41){
			prevDistances31 = distances41;
		}
		if (prevDistances31 > distances40){
			prevDistances31 = distances40;
		}
		if (prevDistances31 > distances30){
			prevDistances31 = distances30;
		}
		if (prevDistances31 > distances20){
			prevDistances31 = distances20;
		}
		if (prevDistances31 > distances21){
			prevDistances31 = distances21;
		}
		if (prevDistances31 > distances22){
			prevDistances31 = distances22;
		}
		prevDistances31 += rubbleLevels31;
		prevDistances32 -= rubbleLevels32;
		if (prevDistances32 > distances33){
			prevDistances32 = distances33;
		}
		if (prevDistances32 > distances43){
			prevDistances32 = distances43;
		}
		if (prevDistances32 > distances42){
			prevDistances32 = distances42;
		}
		if (prevDistances32 > distances41){
			prevDistances32 = distances41;
		}
		if (prevDistances32 > distances31){
			prevDistances32 = distances31;
		}
		if (prevDistances32 > distances21){
			prevDistances32 = distances21;
		}
		if (prevDistances32 > distances22){
			prevDistances32 = distances22;
		}
		if (prevDistances32 > distances23){
			prevDistances32 = distances23;
		}
		prevDistances32 += rubbleLevels32;
		prevDistances33 -= rubbleLevels33;
		if (prevDistances33 > distances34){
			prevDistances33 = distances34;
		}
		if (prevDistances33 > distances44){
			prevDistances33 = distances44;
		}
		if (prevDistances33 > distances43){
			prevDistances33 = distances43;
		}
		if (prevDistances33 > distances42){
			prevDistances33 = distances42;
		}
		if (prevDistances33 > distances32){
			prevDistances33 = distances32;
		}
		if (prevDistances33 > distances22){
			prevDistances33 = distances22;
		}
		if (prevDistances33 > distances23){
			prevDistances33 = distances23;
		}
		if (prevDistances33 > distances24){
			prevDistances33 = distances24;
		}
		prevDistances33 += rubbleLevels33;
		prevDistances34 -= rubbleLevels34;
		if (prevDistances34 > distances35){
			prevDistances34 = distances35;
		}
		if (prevDistances34 > distances45){
			prevDistances34 = distances45;
		}
		if (prevDistances34 > distances44){
			prevDistances34 = distances44;
		}
		if (prevDistances34 > distances43){
			prevDistances34 = distances43;
		}
		if (prevDistances34 > distances33){
			prevDistances34 = distances33;
		}
		if (prevDistances34 > distances23){
			prevDistances34 = distances23;
		}
		if (prevDistances34 > distances24){
			prevDistances34 = distances24;
		}
		if (prevDistances34 > distances25){
			prevDistances34 = distances25;
		}
		prevDistances34 += rubbleLevels34;
		prevDistances35 -= rubbleLevels35;
		if (prevDistances35 > distances36){
			prevDistances35 = distances36;
		}
		if (prevDistances35 > distances46){
			prevDistances35 = distances46;
		}
		if (prevDistances35 > distances45){
			prevDistances35 = distances45;
		}
		if (prevDistances35 > distances44){
			prevDistances35 = distances44;
		}
		if (prevDistances35 > distances34){
			prevDistances35 = distances34;
		}
		if (prevDistances35 > distances24){
			prevDistances35 = distances24;
		}
		if (prevDistances35 > distances25){
			prevDistances35 = distances25;
		}
		if (prevDistances35 > distances26){
			prevDistances35 = distances26;
		}
		prevDistances35 += rubbleLevels35;
		prevDistances36 -= rubbleLevels36;
		if (prevDistances36 > distances46){
			prevDistances36 = distances46;
		}
		if (prevDistances36 > distances45){
			prevDistances36 = distances45;
		}
		if (prevDistances36 > distances35){
			prevDistances36 = distances35;
		}
		if (prevDistances36 > distances25){
			prevDistances36 = distances25;
		}
		if (prevDistances36 > distances26){
			prevDistances36 = distances26;
		}
		prevDistances36 += rubbleLevels36;
		prevDistances40 -= rubbleLevels40;
		if (prevDistances40 > distances41){
			prevDistances40 = distances41;
		}
		if (prevDistances40 > distances51){
			prevDistances40 = distances51;
		}
		if (prevDistances40 > distances50){
			prevDistances40 = distances50;
		}
		if (prevDistances40 > distances30){
			prevDistances40 = distances30;
		}
		if (prevDistances40 > distances31){
			prevDistances40 = distances31;
		}
		prevDistances40 += rubbleLevels40;
		prevDistances41 -= rubbleLevels41;
		if (prevDistances41 > distances42){
			prevDistances41 = distances42;
		}
		if (prevDistances41 > distances52){
			prevDistances41 = distances52;
		}
		if (prevDistances41 > distances51){
			prevDistances41 = distances51;
		}
		if (prevDistances41 > distances50){
			prevDistances41 = distances50;
		}
		if (prevDistances41 > distances40){
			prevDistances41 = distances40;
		}
		if (prevDistances41 > distances30){
			prevDistances41 = distances30;
		}
		if (prevDistances41 > distances31){
			prevDistances41 = distances31;
		}
		if (prevDistances41 > distances32){
			prevDistances41 = distances32;
		}
		prevDistances41 += rubbleLevels41;
		prevDistances42 -= rubbleLevels42;
		if (prevDistances42 > distances43){
			prevDistances42 = distances43;
		}
		if (prevDistances42 > distances53){
			prevDistances42 = distances53;
		}
		if (prevDistances42 > distances52){
			prevDistances42 = distances52;
		}
		if (prevDistances42 > distances51){
			prevDistances42 = distances51;
		}
		if (prevDistances42 > distances41){
			prevDistances42 = distances41;
		}
		if (prevDistances42 > distances31){
			prevDistances42 = distances31;
		}
		if (prevDistances42 > distances32){
			prevDistances42 = distances32;
		}
		if (prevDistances42 > distances33){
			prevDistances42 = distances33;
		}
		prevDistances42 += rubbleLevels42;
		prevDistances43 -= rubbleLevels43;
		if (prevDistances43 > distances44){
			prevDistances43 = distances44;
		}
		if (prevDistances43 > distances54){
			prevDistances43 = distances54;
		}
		if (prevDistances43 > distances53){
			prevDistances43 = distances53;
		}
		if (prevDistances43 > distances52){
			prevDistances43 = distances52;
		}
		if (prevDistances43 > distances42){
			prevDistances43 = distances42;
		}
		if (prevDistances43 > distances32){
			prevDistances43 = distances32;
		}
		if (prevDistances43 > distances33){
			prevDistances43 = distances33;
		}
		if (prevDistances43 > distances34){
			prevDistances43 = distances34;
		}
		prevDistances43 += rubbleLevels43;
		prevDistances44 -= rubbleLevels44;
		if (prevDistances44 > distances45){
			prevDistances44 = distances45;
		}
		if (prevDistances44 > distances55){
			prevDistances44 = distances55;
		}
		if (prevDistances44 > distances54){
			prevDistances44 = distances54;
		}
		if (prevDistances44 > distances53){
			prevDistances44 = distances53;
		}
		if (prevDistances44 > distances43){
			prevDistances44 = distances43;
		}
		if (prevDistances44 > distances33){
			prevDistances44 = distances33;
		}
		if (prevDistances44 > distances34){
			prevDistances44 = distances34;
		}
		if (prevDistances44 > distances35){
			prevDistances44 = distances35;
		}
		prevDistances44 += rubbleLevels44;
		prevDistances45 -= rubbleLevels45;
		if (prevDistances45 > distances46){
			prevDistances45 = distances46;
		}
		if (prevDistances45 > distances56){
			prevDistances45 = distances56;
		}
		if (prevDistances45 > distances55){
			prevDistances45 = distances55;
		}
		if (prevDistances45 > distances54){
			prevDistances45 = distances54;
		}
		if (prevDistances45 > distances44){
			prevDistances45 = distances44;
		}
		if (prevDistances45 > distances34){
			prevDistances45 = distances34;
		}
		if (prevDistances45 > distances35){
			prevDistances45 = distances35;
		}
		if (prevDistances45 > distances36){
			prevDistances45 = distances36;
		}
		prevDistances45 += rubbleLevels45;
		prevDistances46 -= rubbleLevels46;
		if (prevDistances46 > distances56){
			prevDistances46 = distances56;
		}
		if (prevDistances46 > distances55){
			prevDistances46 = distances55;
		}
		if (prevDistances46 > distances45){
			prevDistances46 = distances45;
		}
		if (prevDistances46 > distances35){
			prevDistances46 = distances35;
		}
		if (prevDistances46 > distances36){
			prevDistances46 = distances36;
		}
		prevDistances46 += rubbleLevels46;
		prevDistances50 -= rubbleLevels50;
		if (prevDistances50 > distances51){
			prevDistances50 = distances51;
		}
		if (prevDistances50 > distances61){
			prevDistances50 = distances61;
		}
		if (prevDistances50 > distances60){
			prevDistances50 = distances60;
		}
		if (prevDistances50 > distances40){
			prevDistances50 = distances40;
		}
		if (prevDistances50 > distances41){
			prevDistances50 = distances41;
		}
		prevDistances50 += rubbleLevels50;
		prevDistances51 -= rubbleLevels51;
		if (prevDistances51 > distances52){
			prevDistances51 = distances52;
		}
		if (prevDistances51 > distances62){
			prevDistances51 = distances62;
		}
		if (prevDistances51 > distances61){
			prevDistances51 = distances61;
		}
		if (prevDistances51 > distances60){
			prevDistances51 = distances60;
		}
		if (prevDistances51 > distances50){
			prevDistances51 = distances50;
		}
		if (prevDistances51 > distances40){
			prevDistances51 = distances40;
		}
		if (prevDistances51 > distances41){
			prevDistances51 = distances41;
		}
		if (prevDistances51 > distances42){
			prevDistances51 = distances42;
		}
		prevDistances51 += rubbleLevels51;
		prevDistances52 -= rubbleLevels52;
		if (prevDistances52 > distances53){
			prevDistances52 = distances53;
		}
		if (prevDistances52 > distances63){
			prevDistances52 = distances63;
		}
		if (prevDistances52 > distances62){
			prevDistances52 = distances62;
		}
		if (prevDistances52 > distances61){
			prevDistances52 = distances61;
		}
		if (prevDistances52 > distances51){
			prevDistances52 = distances51;
		}
		if (prevDistances52 > distances41){
			prevDistances52 = distances41;
		}
		if (prevDistances52 > distances42){
			prevDistances52 = distances42;
		}
		if (prevDistances52 > distances43){
			prevDistances52 = distances43;
		}
		prevDistances52 += rubbleLevels52;
		prevDistances53 -= rubbleLevels53;
		if (prevDistances53 > distances54){
			prevDistances53 = distances54;
		}
		if (prevDistances53 > distances64){
			prevDistances53 = distances64;
		}
		if (prevDistances53 > distances63){
			prevDistances53 = distances63;
		}
		if (prevDistances53 > distances62){
			prevDistances53 = distances62;
		}
		if (prevDistances53 > distances52){
			prevDistances53 = distances52;
		}
		if (prevDistances53 > distances42){
			prevDistances53 = distances42;
		}
		if (prevDistances53 > distances43){
			prevDistances53 = distances43;
		}
		if (prevDistances53 > distances44){
			prevDistances53 = distances44;
		}
		prevDistances53 += rubbleLevels53;
		prevDistances54 -= rubbleLevels54;
		if (prevDistances54 > distances55){
			prevDistances54 = distances55;
		}
		if (prevDistances54 > distances65){
			prevDistances54 = distances65;
		}
		if (prevDistances54 > distances64){
			prevDistances54 = distances64;
		}
		if (prevDistances54 > distances63){
			prevDistances54 = distances63;
		}
		if (prevDistances54 > distances53){
			prevDistances54 = distances53;
		}
		if (prevDistances54 > distances43){
			prevDistances54 = distances43;
		}
		if (prevDistances54 > distances44){
			prevDistances54 = distances44;
		}
		if (prevDistances54 > distances45){
			prevDistances54 = distances45;
		}
		prevDistances54 += rubbleLevels54;
		prevDistances55 -= rubbleLevels55;
		if (prevDistances55 > distances56){
			prevDistances55 = distances56;
		}
		if (prevDistances55 > distances66){
			prevDistances55 = distances66;
		}
		if (prevDistances55 > distances65){
			prevDistances55 = distances65;
		}
		if (prevDistances55 > distances64){
			prevDistances55 = distances64;
		}
		if (prevDistances55 > distances54){
			prevDistances55 = distances54;
		}
		if (prevDistances55 > distances44){
			prevDistances55 = distances44;
		}
		if (prevDistances55 > distances45){
			prevDistances55 = distances45;
		}
		if (prevDistances55 > distances46){
			prevDistances55 = distances46;
		}
		prevDistances55 += rubbleLevels55;
		prevDistances56 -= rubbleLevels56;
		if (prevDistances56 > distances66){
			prevDistances56 = distances66;
		}
		if (prevDistances56 > distances65){
			prevDistances56 = distances65;
		}
		if (prevDistances56 > distances55){
			prevDistances56 = distances55;
		}
		if (prevDistances56 > distances45){
			prevDistances56 = distances45;
		}
		if (prevDistances56 > distances46){
			prevDistances56 = distances46;
		}
		prevDistances56 += rubbleLevels56;
		prevDistances60 -= rubbleLevels60;
		if (prevDistances60 > distances61){
			prevDistances60 = distances61;
		}
		if (prevDistances60 > distances50){
			prevDistances60 = distances50;
		}
		if (prevDistances60 > distances51){
			prevDistances60 = distances51;
		}
		prevDistances60 += rubbleLevels60;
		prevDistances61 -= rubbleLevels61;
		if (prevDistances61 > distances62){
			prevDistances61 = distances62;
		}
		if (prevDistances61 > distances60){
			prevDistances61 = distances60;
		}
		if (prevDistances61 > distances50){
			prevDistances61 = distances50;
		}
		if (prevDistances61 > distances51){
			prevDistances61 = distances51;
		}
		if (prevDistances61 > distances52){
			prevDistances61 = distances52;
		}
		prevDistances61 += rubbleLevels61;
		prevDistances62 -= rubbleLevels62;
		if (prevDistances62 > distances63){
			prevDistances62 = distances63;
		}
		if (prevDistances62 > distances61){
			prevDistances62 = distances61;
		}
		if (prevDistances62 > distances51){
			prevDistances62 = distances51;
		}
		if (prevDistances62 > distances52){
			prevDistances62 = distances52;
		}
		if (prevDistances62 > distances53){
			prevDistances62 = distances53;
		}
		prevDistances62 += rubbleLevels62;
		prevDistances63 -= rubbleLevels63;
		if (prevDistances63 > distances64){
			prevDistances63 = distances64;
		}
		if (prevDistances63 > distances62){
			prevDistances63 = distances62;
		}
		if (prevDistances63 > distances52){
			prevDistances63 = distances52;
		}
		if (prevDistances63 > distances53){
			prevDistances63 = distances53;
		}
		if (prevDistances63 > distances54){
			prevDistances63 = distances54;
		}
		prevDistances63 += rubbleLevels63;
		prevDistances64 -= rubbleLevels64;
		if (prevDistances64 > distances65){
			prevDistances64 = distances65;
		}
		if (prevDistances64 > distances63){
			prevDistances64 = distances63;
		}
		if (prevDistances64 > distances53){
			prevDistances64 = distances53;
		}
		if (prevDistances64 > distances54){
			prevDistances64 = distances54;
		}
		if (prevDistances64 > distances55){
			prevDistances64 = distances55;
		}
		prevDistances64 += rubbleLevels64;
		prevDistances65 -= rubbleLevels65;
		if (prevDistances65 > distances66){
			prevDistances65 = distances66;
		}
		if (prevDistances65 > distances64){
			prevDistances65 = distances64;
		}
		if (prevDistances65 > distances54){
			prevDistances65 = distances54;
		}
		if (prevDistances65 > distances55){
			prevDistances65 = distances55;
		}
		if (prevDistances65 > distances56){
			prevDistances65 = distances56;
		}
		prevDistances65 += rubbleLevels65;
		prevDistances66 -= rubbleLevels66;
		if (prevDistances66 > distances65){
			prevDistances66 = distances65;
		}
		if (prevDistances66 > distances55){
			prevDistances66 = distances55;
		}
		if (prevDistances66 > distances56){
			prevDistances66 = distances56;
		}
		prevDistances66 += rubbleLevels66;
		distances00 -= rubbleLevels00;
		if (distances00 > prevDistances01){
			distances00 = prevDistances01;
		}
		if (distances00 > prevDistances11){
			distances00 = prevDistances11;
		}
		if (distances00 > prevDistances10){
			distances00 = prevDistances10;
		}
		distances00 += rubbleLevels00;
		distances01 -= rubbleLevels01;
		if (distances01 > prevDistances02){
			distances01 = prevDistances02;
		}
		if (distances01 > prevDistances12){
			distances01 = prevDistances12;
		}
		if (distances01 > prevDistances11){
			distances01 = prevDistances11;
		}
		if (distances01 > prevDistances10){
			distances01 = prevDistances10;
		}
		if (distances01 > prevDistances00){
			distances01 = prevDistances00;
		}
		distances01 += rubbleLevels01;
		distances02 -= rubbleLevels02;
		if (distances02 > prevDistances03){
			distances02 = prevDistances03;
		}
		if (distances02 > prevDistances13){
			distances02 = prevDistances13;
		}
		if (distances02 > prevDistances12){
			distances02 = prevDistances12;
		}
		if (distances02 > prevDistances11){
			distances02 = prevDistances11;
		}
		if (distances02 > prevDistances01){
			distances02 = prevDistances01;
		}
		distances02 += rubbleLevels02;
		distances03 -= rubbleLevels03;
		if (distances03 > prevDistances04){
			distances03 = prevDistances04;
		}
		if (distances03 > prevDistances14){
			distances03 = prevDistances14;
		}
		if (distances03 > prevDistances13){
			distances03 = prevDistances13;
		}
		if (distances03 > prevDistances12){
			distances03 = prevDistances12;
		}
		if (distances03 > prevDistances02){
			distances03 = prevDistances02;
		}
		distances03 += rubbleLevels03;
		distances04 -= rubbleLevels04;
		if (distances04 > prevDistances05){
			distances04 = prevDistances05;
		}
		if (distances04 > prevDistances15){
			distances04 = prevDistances15;
		}
		if (distances04 > prevDistances14){
			distances04 = prevDistances14;
		}
		if (distances04 > prevDistances13){
			distances04 = prevDistances13;
		}
		if (distances04 > prevDistances03){
			distances04 = prevDistances03;
		}
		distances04 += rubbleLevels04;
		distances05 -= rubbleLevels05;
		if (distances05 > prevDistances06){
			distances05 = prevDistances06;
		}
		if (distances05 > prevDistances16){
			distances05 = prevDistances16;
		}
		if (distances05 > prevDistances15){
			distances05 = prevDistances15;
		}
		if (distances05 > prevDistances14){
			distances05 = prevDistances14;
		}
		if (distances05 > prevDistances04){
			distances05 = prevDistances04;
		}
		distances05 += rubbleLevels05;
		distances06 -= rubbleLevels06;
		if (distances06 > prevDistances16){
			distances06 = prevDistances16;
		}
		if (distances06 > prevDistances15){
			distances06 = prevDistances15;
		}
		if (distances06 > prevDistances05){
			distances06 = prevDistances05;
		}
		distances06 += rubbleLevels06;
		distances10 -= rubbleLevels10;
		if (distances10 > prevDistances11){
			distances10 = prevDistances11;
		}
		if (distances10 > prevDistances21){
			distances10 = prevDistances21;
		}
		if (distances10 > prevDistances20){
			distances10 = prevDistances20;
		}
		if (distances10 > prevDistances00){
			distances10 = prevDistances00;
		}
		if (distances10 > prevDistances01){
			distances10 = prevDistances01;
		}
		distances10 += rubbleLevels10;
		distances11 -= rubbleLevels11;
		if (distances11 > prevDistances12){
			distances11 = prevDistances12;
		}
		if (distances11 > prevDistances22){
			distances11 = prevDistances22;
		}
		if (distances11 > prevDistances21){
			distances11 = prevDistances21;
		}
		if (distances11 > prevDistances20){
			distances11 = prevDistances20;
		}
		if (distances11 > prevDistances10){
			distances11 = prevDistances10;
		}
		if (distances11 > prevDistances00){
			distances11 = prevDistances00;
		}
		if (distances11 > prevDistances01){
			distances11 = prevDistances01;
		}
		if (distances11 > prevDistances02){
			distances11 = prevDistances02;
		}
		distances11 += rubbleLevels11;
		distances12 -= rubbleLevels12;
		if (distances12 > prevDistances13){
			distances12 = prevDistances13;
		}
		if (distances12 > prevDistances23){
			distances12 = prevDistances23;
		}
		if (distances12 > prevDistances22){
			distances12 = prevDistances22;
		}
		if (distances12 > prevDistances21){
			distances12 = prevDistances21;
		}
		if (distances12 > prevDistances11){
			distances12 = prevDistances11;
		}
		if (distances12 > prevDistances01){
			distances12 = prevDistances01;
		}
		if (distances12 > prevDistances02){
			distances12 = prevDistances02;
		}
		if (distances12 > prevDistances03){
			distances12 = prevDistances03;
		}
		distances12 += rubbleLevels12;
		distances13 -= rubbleLevels13;
		if (distances13 > prevDistances14){
			distances13 = prevDistances14;
		}
		if (distances13 > prevDistances24){
			distances13 = prevDistances24;
		}
		if (distances13 > prevDistances23){
			distances13 = prevDistances23;
		}
		if (distances13 > prevDistances22){
			distances13 = prevDistances22;
		}
		if (distances13 > prevDistances12){
			distances13 = prevDistances12;
		}
		if (distances13 > prevDistances02){
			distances13 = prevDistances02;
		}
		if (distances13 > prevDistances03){
			distances13 = prevDistances03;
		}
		if (distances13 > prevDistances04){
			distances13 = prevDistances04;
		}
		distances13 += rubbleLevels13;
		distances14 -= rubbleLevels14;
		if (distances14 > prevDistances15){
			distances14 = prevDistances15;
		}
		if (distances14 > prevDistances25){
			distances14 = prevDistances25;
		}
		if (distances14 > prevDistances24){
			distances14 = prevDistances24;
		}
		if (distances14 > prevDistances23){
			distances14 = prevDistances23;
		}
		if (distances14 > prevDistances13){
			distances14 = prevDistances13;
		}
		if (distances14 > prevDistances03){
			distances14 = prevDistances03;
		}
		if (distances14 > prevDistances04){
			distances14 = prevDistances04;
		}
		if (distances14 > prevDistances05){
			distances14 = prevDistances05;
		}
		distances14 += rubbleLevels14;
		distances15 -= rubbleLevels15;
		if (distances15 > prevDistances16){
			distances15 = prevDistances16;
		}
		if (distances15 > prevDistances26){
			distances15 = prevDistances26;
		}
		if (distances15 > prevDistances25){
			distances15 = prevDistances25;
		}
		if (distances15 > prevDistances24){
			distances15 = prevDistances24;
		}
		if (distances15 > prevDistances14){
			distances15 = prevDistances14;
		}
		if (distances15 > prevDistances04){
			distances15 = prevDistances04;
		}
		if (distances15 > prevDistances05){
			distances15 = prevDistances05;
		}
		if (distances15 > prevDistances06){
			distances15 = prevDistances06;
		}
		distances15 += rubbleLevels15;
		distances16 -= rubbleLevels16;
		if (distances16 > prevDistances26){
			distances16 = prevDistances26;
		}
		if (distances16 > prevDistances25){
			distances16 = prevDistances25;
		}
		if (distances16 > prevDistances15){
			distances16 = prevDistances15;
		}
		if (distances16 > prevDistances05){
			distances16 = prevDistances05;
		}
		if (distances16 > prevDistances06){
			distances16 = prevDistances06;
		}
		distances16 += rubbleLevels16;
		distances20 -= rubbleLevels20;
		if (distances20 > prevDistances21){
			distances20 = prevDistances21;
		}
		if (distances20 > prevDistances31){
			distances20 = prevDistances31;
		}
		if (distances20 > prevDistances30){
			distances20 = prevDistances30;
		}
		if (distances20 > prevDistances10){
			distances20 = prevDistances10;
		}
		if (distances20 > prevDistances11){
			distances20 = prevDistances11;
		}
		distances20 += rubbleLevels20;
		distances21 -= rubbleLevels21;
		if (distances21 > prevDistances22){
			distances21 = prevDistances22;
		}
		if (distances21 > prevDistances32){
			distances21 = prevDistances32;
		}
		if (distances21 > prevDistances31){
			distances21 = prevDistances31;
		}
		if (distances21 > prevDistances30){
			distances21 = prevDistances30;
		}
		if (distances21 > prevDistances20){
			distances21 = prevDistances20;
		}
		if (distances21 > prevDistances10){
			distances21 = prevDistances10;
		}
		if (distances21 > prevDistances11){
			distances21 = prevDistances11;
		}
		if (distances21 > prevDistances12){
			distances21 = prevDistances12;
		}
		distances21 += rubbleLevels21;
		distances22 -= rubbleLevels22;
		if (distances22 > prevDistances23){
			distances22 = prevDistances23;
		}
		if (distances22 > prevDistances33){
			distances22 = prevDistances33;
		}
		if (distances22 > prevDistances32){
			distances22 = prevDistances32;
		}
		if (distances22 > prevDistances31){
			distances22 = prevDistances31;
		}
		if (distances22 > prevDistances21){
			distances22 = prevDistances21;
		}
		if (distances22 > prevDistances11){
			distances22 = prevDistances11;
		}
		if (distances22 > prevDistances12){
			distances22 = prevDistances12;
		}
		if (distances22 > prevDistances13){
			distances22 = prevDistances13;
		}
		distances22 += rubbleLevels22;
		distances23 -= rubbleLevels23;
		if (distances23 > prevDistances24){
			distances23 = prevDistances24;
		}
		if (distances23 > prevDistances34){
			distances23 = prevDistances34;
		}
		if (distances23 > prevDistances33){
			distances23 = prevDistances33;
		}
		if (distances23 > prevDistances32){
			distances23 = prevDistances32;
		}
		if (distances23 > prevDistances22){
			distances23 = prevDistances22;
		}
		if (distances23 > prevDistances12){
			distances23 = prevDistances12;
		}
		if (distances23 > prevDistances13){
			distances23 = prevDistances13;
		}
		if (distances23 > prevDistances14){
			distances23 = prevDistances14;
		}
		distances23 += rubbleLevels23;
		distances24 -= rubbleLevels24;
		if (distances24 > prevDistances25){
			distances24 = prevDistances25;
		}
		if (distances24 > prevDistances35){
			distances24 = prevDistances35;
		}
		if (distances24 > prevDistances34){
			distances24 = prevDistances34;
		}
		if (distances24 > prevDistances33){
			distances24 = prevDistances33;
		}
		if (distances24 > prevDistances23){
			distances24 = prevDistances23;
		}
		if (distances24 > prevDistances13){
			distances24 = prevDistances13;
		}
		if (distances24 > prevDistances14){
			distances24 = prevDistances14;
		}
		if (distances24 > prevDistances15){
			distances24 = prevDistances15;
		}
		distances24 += rubbleLevels24;
		distances25 -= rubbleLevels25;
		if (distances25 > prevDistances26){
			distances25 = prevDistances26;
		}
		if (distances25 > prevDistances36){
			distances25 = prevDistances36;
		}
		if (distances25 > prevDistances35){
			distances25 = prevDistances35;
		}
		if (distances25 > prevDistances34){
			distances25 = prevDistances34;
		}
		if (distances25 > prevDistances24){
			distances25 = prevDistances24;
		}
		if (distances25 > prevDistances14){
			distances25 = prevDistances14;
		}
		if (distances25 > prevDistances15){
			distances25 = prevDistances15;
		}
		if (distances25 > prevDistances16){
			distances25 = prevDistances16;
		}
		distances25 += rubbleLevels25;
		distances26 -= rubbleLevels26;
		if (distances26 > prevDistances36){
			distances26 = prevDistances36;
		}
		if (distances26 > prevDistances35){
			distances26 = prevDistances35;
		}
		if (distances26 > prevDistances25){
			distances26 = prevDistances25;
		}
		if (distances26 > prevDistances15){
			distances26 = prevDistances15;
		}
		if (distances26 > prevDistances16){
			distances26 = prevDistances16;
		}
		distances26 += rubbleLevels26;
		distances30 -= rubbleLevels30;
		if (distances30 > prevDistances31){
			distances30 = prevDistances31;
		}
		if (distances30 > prevDistances41){
			distances30 = prevDistances41;
		}
		if (distances30 > prevDistances40){
			distances30 = prevDistances40;
		}
		if (distances30 > prevDistances20){
			distances30 = prevDistances20;
		}
		if (distances30 > prevDistances21){
			distances30 = prevDistances21;
		}
		distances30 += rubbleLevels30;
		distances31 -= rubbleLevels31;
		if (distances31 > prevDistances32){
			distances31 = prevDistances32;
		}
		if (distances31 > prevDistances42){
			distances31 = prevDistances42;
		}
		if (distances31 > prevDistances41){
			distances31 = prevDistances41;
		}
		if (distances31 > prevDistances40){
			distances31 = prevDistances40;
		}
		if (distances31 > prevDistances30){
			distances31 = prevDistances30;
		}
		if (distances31 > prevDistances20){
			distances31 = prevDistances20;
		}
		if (distances31 > prevDistances21){
			distances31 = prevDistances21;
		}
		if (distances31 > prevDistances22){
			distances31 = prevDistances22;
		}
		distances31 += rubbleLevels31;
		distances32 -= rubbleLevels32;
		if (distances32 > prevDistances33){
			distances32 = prevDistances33;
		}
		if (distances32 > prevDistances43){
			distances32 = prevDistances43;
		}
		if (distances32 > prevDistances42){
			distances32 = prevDistances42;
		}
		if (distances32 > prevDistances41){
			distances32 = prevDistances41;
		}
		if (distances32 > prevDistances31){
			distances32 = prevDistances31;
		}
		if (distances32 > prevDistances21){
			distances32 = prevDistances21;
		}
		if (distances32 > prevDistances22){
			distances32 = prevDistances22;
		}
		if (distances32 > prevDistances23){
			distances32 = prevDistances23;
		}
		distances32 += rubbleLevels32;
		distances33 -= rubbleLevels33;
		if (distances33 > prevDistances34){
			distances33 = prevDistances34;
		}
		if (distances33 > prevDistances44){
			distances33 = prevDistances44;
		}
		if (distances33 > prevDistances43){
			distances33 = prevDistances43;
		}
		if (distances33 > prevDistances42){
			distances33 = prevDistances42;
		}
		if (distances33 > prevDistances32){
			distances33 = prevDistances32;
		}
		if (distances33 > prevDistances22){
			distances33 = prevDistances22;
		}
		if (distances33 > prevDistances23){
			distances33 = prevDistances23;
		}
		if (distances33 > prevDistances24){
			distances33 = prevDistances24;
		}
		distances33 += rubbleLevels33;
		distances34 -= rubbleLevels34;
		if (distances34 > prevDistances35){
			distances34 = prevDistances35;
		}
		if (distances34 > prevDistances45){
			distances34 = prevDistances45;
		}
		if (distances34 > prevDistances44){
			distances34 = prevDistances44;
		}
		if (distances34 > prevDistances43){
			distances34 = prevDistances43;
		}
		if (distances34 > prevDistances33){
			distances34 = prevDistances33;
		}
		if (distances34 > prevDistances23){
			distances34 = prevDistances23;
		}
		if (distances34 > prevDistances24){
			distances34 = prevDistances24;
		}
		if (distances34 > prevDistances25){
			distances34 = prevDistances25;
		}
		distances34 += rubbleLevels34;
		distances35 -= rubbleLevels35;
		if (distances35 > prevDistances36){
			distances35 = prevDistances36;
		}
		if (distances35 > prevDistances46){
			distances35 = prevDistances46;
		}
		if (distances35 > prevDistances45){
			distances35 = prevDistances45;
		}
		if (distances35 > prevDistances44){
			distances35 = prevDistances44;
		}
		if (distances35 > prevDistances34){
			distances35 = prevDistances34;
		}
		if (distances35 > prevDistances24){
			distances35 = prevDistances24;
		}
		if (distances35 > prevDistances25){
			distances35 = prevDistances25;
		}
		if (distances35 > prevDistances26){
			distances35 = prevDistances26;
		}
		distances35 += rubbleLevels35;
		distances36 -= rubbleLevels36;
		if (distances36 > prevDistances46){
			distances36 = prevDistances46;
		}
		if (distances36 > prevDistances45){
			distances36 = prevDistances45;
		}
		if (distances36 > prevDistances35){
			distances36 = prevDistances35;
		}
		if (distances36 > prevDistances25){
			distances36 = prevDistances25;
		}
		if (distances36 > prevDistances26){
			distances36 = prevDistances26;
		}
		distances36 += rubbleLevels36;
		distances40 -= rubbleLevels40;
		if (distances40 > prevDistances41){
			distances40 = prevDistances41;
		}
		if (distances40 > prevDistances51){
			distances40 = prevDistances51;
		}
		if (distances40 > prevDistances50){
			distances40 = prevDistances50;
		}
		if (distances40 > prevDistances30){
			distances40 = prevDistances30;
		}
		if (distances40 > prevDistances31){
			distances40 = prevDistances31;
		}
		distances40 += rubbleLevels40;
		distances41 -= rubbleLevels41;
		if (distances41 > prevDistances42){
			distances41 = prevDistances42;
		}
		if (distances41 > prevDistances52){
			distances41 = prevDistances52;
		}
		if (distances41 > prevDistances51){
			distances41 = prevDistances51;
		}
		if (distances41 > prevDistances50){
			distances41 = prevDistances50;
		}
		if (distances41 > prevDistances40){
			distances41 = prevDistances40;
		}
		if (distances41 > prevDistances30){
			distances41 = prevDistances30;
		}
		if (distances41 > prevDistances31){
			distances41 = prevDistances31;
		}
		if (distances41 > prevDistances32){
			distances41 = prevDistances32;
		}
		distances41 += rubbleLevels41;
		distances42 -= rubbleLevels42;
		if (distances42 > prevDistances43){
			distances42 = prevDistances43;
		}
		if (distances42 > prevDistances53){
			distances42 = prevDistances53;
		}
		if (distances42 > prevDistances52){
			distances42 = prevDistances52;
		}
		if (distances42 > prevDistances51){
			distances42 = prevDistances51;
		}
		if (distances42 > prevDistances41){
			distances42 = prevDistances41;
		}
		if (distances42 > prevDistances31){
			distances42 = prevDistances31;
		}
		if (distances42 > prevDistances32){
			distances42 = prevDistances32;
		}
		if (distances42 > prevDistances33){
			distances42 = prevDistances33;
		}
		distances42 += rubbleLevels42;
		distances43 -= rubbleLevels43;
		if (distances43 > prevDistances44){
			distances43 = prevDistances44;
		}
		if (distances43 > prevDistances54){
			distances43 = prevDistances54;
		}
		if (distances43 > prevDistances53){
			distances43 = prevDistances53;
		}
		if (distances43 > prevDistances52){
			distances43 = prevDistances52;
		}
		if (distances43 > prevDistances42){
			distances43 = prevDistances42;
		}
		if (distances43 > prevDistances32){
			distances43 = prevDistances32;
		}
		if (distances43 > prevDistances33){
			distances43 = prevDistances33;
		}
		if (distances43 > prevDistances34){
			distances43 = prevDistances34;
		}
		distances43 += rubbleLevels43;
		distances44 -= rubbleLevels44;
		if (distances44 > prevDistances45){
			distances44 = prevDistances45;
		}
		if (distances44 > prevDistances55){
			distances44 = prevDistances55;
		}
		if (distances44 > prevDistances54){
			distances44 = prevDistances54;
		}
		if (distances44 > prevDistances53){
			distances44 = prevDistances53;
		}
		if (distances44 > prevDistances43){
			distances44 = prevDistances43;
		}
		if (distances44 > prevDistances33){
			distances44 = prevDistances33;
		}
		if (distances44 > prevDistances34){
			distances44 = prevDistances34;
		}
		if (distances44 > prevDistances35){
			distances44 = prevDistances35;
		}
		distances44 += rubbleLevels44;
		distances45 -= rubbleLevels45;
		if (distances45 > prevDistances46){
			distances45 = prevDistances46;
		}
		if (distances45 > prevDistances56){
			distances45 = prevDistances56;
		}
		if (distances45 > prevDistances55){
			distances45 = prevDistances55;
		}
		if (distances45 > prevDistances54){
			distances45 = prevDistances54;
		}
		if (distances45 > prevDistances44){
			distances45 = prevDistances44;
		}
		if (distances45 > prevDistances34){
			distances45 = prevDistances34;
		}
		if (distances45 > prevDistances35){
			distances45 = prevDistances35;
		}
		if (distances45 > prevDistances36){
			distances45 = prevDistances36;
		}
		distances45 += rubbleLevels45;
		distances46 -= rubbleLevels46;
		if (distances46 > prevDistances56){
			distances46 = prevDistances56;
		}
		if (distances46 > prevDistances55){
			distances46 = prevDistances55;
		}
		if (distances46 > prevDistances45){
			distances46 = prevDistances45;
		}
		if (distances46 > prevDistances35){
			distances46 = prevDistances35;
		}
		if (distances46 > prevDistances36){
			distances46 = prevDistances36;
		}
		distances46 += rubbleLevels46;
		distances50 -= rubbleLevels50;
		if (distances50 > prevDistances51){
			distances50 = prevDistances51;
		}
		if (distances50 > prevDistances61){
			distances50 = prevDistances61;
		}
		if (distances50 > prevDistances60){
			distances50 = prevDistances60;
		}
		if (distances50 > prevDistances40){
			distances50 = prevDistances40;
		}
		if (distances50 > prevDistances41){
			distances50 = prevDistances41;
		}
		distances50 += rubbleLevels50;
		distances51 -= rubbleLevels51;
		if (distances51 > prevDistances52){
			distances51 = prevDistances52;
		}
		if (distances51 > prevDistances62){
			distances51 = prevDistances62;
		}
		if (distances51 > prevDistances61){
			distances51 = prevDistances61;
		}
		if (distances51 > prevDistances60){
			distances51 = prevDistances60;
		}
		if (distances51 > prevDistances50){
			distances51 = prevDistances50;
		}
		if (distances51 > prevDistances40){
			distances51 = prevDistances40;
		}
		if (distances51 > prevDistances41){
			distances51 = prevDistances41;
		}
		if (distances51 > prevDistances42){
			distances51 = prevDistances42;
		}
		distances51 += rubbleLevels51;
		distances52 -= rubbleLevels52;
		if (distances52 > prevDistances53){
			distances52 = prevDistances53;
		}
		if (distances52 > prevDistances63){
			distances52 = prevDistances63;
		}
		if (distances52 > prevDistances62){
			distances52 = prevDistances62;
		}
		if (distances52 > prevDistances61){
			distances52 = prevDistances61;
		}
		if (distances52 > prevDistances51){
			distances52 = prevDistances51;
		}
		if (distances52 > prevDistances41){
			distances52 = prevDistances41;
		}
		if (distances52 > prevDistances42){
			distances52 = prevDistances42;
		}
		if (distances52 > prevDistances43){
			distances52 = prevDistances43;
		}
		distances52 += rubbleLevels52;
		distances53 -= rubbleLevels53;
		if (distances53 > prevDistances54){
			distances53 = prevDistances54;
		}
		if (distances53 > prevDistances64){
			distances53 = prevDistances64;
		}
		if (distances53 > prevDistances63){
			distances53 = prevDistances63;
		}
		if (distances53 > prevDistances62){
			distances53 = prevDistances62;
		}
		if (distances53 > prevDistances52){
			distances53 = prevDistances52;
		}
		if (distances53 > prevDistances42){
			distances53 = prevDistances42;
		}
		if (distances53 > prevDistances43){
			distances53 = prevDistances43;
		}
		if (distances53 > prevDistances44){
			distances53 = prevDistances44;
		}
		distances53 += rubbleLevels53;
		distances54 -= rubbleLevels54;
		if (distances54 > prevDistances55){
			distances54 = prevDistances55;
		}
		if (distances54 > prevDistances65){
			distances54 = prevDistances65;
		}
		if (distances54 > prevDistances64){
			distances54 = prevDistances64;
		}
		if (distances54 > prevDistances63){
			distances54 = prevDistances63;
		}
		if (distances54 > prevDistances53){
			distances54 = prevDistances53;
		}
		if (distances54 > prevDistances43){
			distances54 = prevDistances43;
		}
		if (distances54 > prevDistances44){
			distances54 = prevDistances44;
		}
		if (distances54 > prevDistances45){
			distances54 = prevDistances45;
		}
		distances54 += rubbleLevels54;
		distances55 -= rubbleLevels55;
		if (distances55 > prevDistances56){
			distances55 = prevDistances56;
		}
		if (distances55 > prevDistances66){
			distances55 = prevDistances66;
		}
		if (distances55 > prevDistances65){
			distances55 = prevDistances65;
		}
		if (distances55 > prevDistances64){
			distances55 = prevDistances64;
		}
		if (distances55 > prevDistances54){
			distances55 = prevDistances54;
		}
		if (distances55 > prevDistances44){
			distances55 = prevDistances44;
		}
		if (distances55 > prevDistances45){
			distances55 = prevDistances45;
		}
		if (distances55 > prevDistances46){
			distances55 = prevDistances46;
		}
		distances55 += rubbleLevels55;
		distances56 -= rubbleLevels56;
		if (distances56 > prevDistances66){
			distances56 = prevDistances66;
		}
		if (distances56 > prevDistances65){
			distances56 = prevDistances65;
		}
		if (distances56 > prevDistances55){
			distances56 = prevDistances55;
		}
		if (distances56 > prevDistances45){
			distances56 = prevDistances45;
		}
		if (distances56 > prevDistances46){
			distances56 = prevDistances46;
		}
		distances56 += rubbleLevels56;
		distances60 -= rubbleLevels60;
		if (distances60 > prevDistances61){
			distances60 = prevDistances61;
		}
		if (distances60 > prevDistances50){
			distances60 = prevDistances50;
		}
		if (distances60 > prevDistances51){
			distances60 = prevDistances51;
		}
		distances60 += rubbleLevels60;
		distances61 -= rubbleLevels61;
		if (distances61 > prevDistances62){
			distances61 = prevDistances62;
		}
		if (distances61 > prevDistances60){
			distances61 = prevDistances60;
		}
		if (distances61 > prevDistances50){
			distances61 = prevDistances50;
		}
		if (distances61 > prevDistances51){
			distances61 = prevDistances51;
		}
		if (distances61 > prevDistances52){
			distances61 = prevDistances52;
		}
		distances61 += rubbleLevels61;
		distances62 -= rubbleLevels62;
		if (distances62 > prevDistances63){
			distances62 = prevDistances63;
		}
		if (distances62 > prevDistances61){
			distances62 = prevDistances61;
		}
		if (distances62 > prevDistances51){
			distances62 = prevDistances51;
		}
		if (distances62 > prevDistances52){
			distances62 = prevDistances52;
		}
		if (distances62 > prevDistances53){
			distances62 = prevDistances53;
		}
		distances62 += rubbleLevels62;
		distances63 -= rubbleLevels63;
		if (distances63 > prevDistances64){
			distances63 = prevDistances64;
		}
		if (distances63 > prevDistances62){
			distances63 = prevDistances62;
		}
		if (distances63 > prevDistances52){
			distances63 = prevDistances52;
		}
		if (distances63 > prevDistances53){
			distances63 = prevDistances53;
		}
		if (distances63 > prevDistances54){
			distances63 = prevDistances54;
		}
		distances63 += rubbleLevels63;
		distances64 -= rubbleLevels64;
		if (distances64 > prevDistances65){
			distances64 = prevDistances65;
		}
		if (distances64 > prevDistances63){
			distances64 = prevDistances63;
		}
		if (distances64 > prevDistances53){
			distances64 = prevDistances53;
		}
		if (distances64 > prevDistances54){
			distances64 = prevDistances54;
		}
		if (distances64 > prevDistances55){
			distances64 = prevDistances55;
		}
		distances64 += rubbleLevels64;
		distances65 -= rubbleLevels65;
		if (distances65 > prevDistances66){
			distances65 = prevDistances66;
		}
		if (distances65 > prevDistances64){
			distances65 = prevDistances64;
		}
		if (distances65 > prevDistances54){
			distances65 = prevDistances54;
		}
		if (distances65 > prevDistances55){
			distances65 = prevDistances55;
		}
		if (distances65 > prevDistances56){
			distances65 = prevDistances56;
		}
		distances65 += rubbleLevels65;
		distances66 -= rubbleLevels66;
		if (distances66 > prevDistances65){
			distances66 = prevDistances65;
		}
		if (distances66 > prevDistances55){
			distances66 = prevDistances55;
		}
		if (distances66 > prevDistances56){
			distances66 = prevDistances56;
		}
		distances66 += rubbleLevels66;
	}


	public Direction findBestDirection(MapLocation target, int avgDistance) throws GameActionException{
		populateArrays(target);
		iterate();
		int minDistance = 1000000000;
		int bestidx = 0;
		if (minDistance > distances34){
			minDistance = distances34;
			bestidx = 0;
		}
		if (minDistance > distances44){
			minDistance = distances44;
			bestidx = 1;
		}
		if (minDistance > distances43){
			minDistance = distances43;
			bestidx = 2;
		}
		if (minDistance > distances42){
			minDistance = distances42;
			bestidx = 3;
		}
		if (minDistance > distances32){
			minDistance = distances32;
			bestidx = 4;
		}
		if (minDistance > distances22){
			minDistance = distances22;
			bestidx = 5;
		}
		if (minDistance > distances23){
			minDistance = distances23;
			bestidx = 6;
		}
		if (minDistance > distances24){
			minDistance = distances24;
			bestidx = 7;
		}
		return Util.directions[bestidx];
	}
}