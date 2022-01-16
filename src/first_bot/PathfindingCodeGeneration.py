#!/usr/bin/env python3

f = open("src/first_bot/Pathfinding.java", "w")

string = """package first_bot;
import battlecode.common.*;
import java.lang.Math;

public class Pathfinding {
    static int distanceSquared;
    RobotCommon robot;
    // static int maxStraightDistance;


    static final int[] dx = new int[] {0, 1, 1, 1, 0, -1, -1, -1};
    static final int[] dy = new int[] {1, 1, 0, -1, -1, -1, 0, 1};



    public Pathfinding(RobotCommon robot){
        this.robot = robot;
        distanceSquared = robot.rc.getType().visionRadiusSquared;
    }\n\n"""

for i in range (25):
    string += ("\tstatic int distances" + str(i // 5)  + str(i % 5) + ";\n")
    string += ("\tstatic int prevDistances" + str(i // 5)  + str(i % 5) + ";\n")
    string += ("\tstatic int rubbleLevels" + str(i // 5)  + str(i % 5) + ";\n")
    string += "\n"

string += "\tpublic void populateArrays(MapLocation target, int avgRubble) throws GameActionException{\n"
string += "\t\tMapLocation mc = robot.me.translate(-2, -2);\n"
string += "\t\tint newrow = mc.x;\n"
string += "\t\tint newcol = mc.y;\n"
string += "\t\tint dxDiff = 0;\n"
string += "\t\tint dyDiff = 0;\n"
for i in range(5):
    for j in range(5):
        if j == 0 and i > 0:
            string += "\t\tnewrow++;\n"
            string += "\t\tnewcol -= 4;\n"
        elif i > 0 or j > 0:
            string += "\t\tnewcol++;\n"
        if (i != 2 or j != 2):
            string += "\t\tif (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(" + str(i) + ", " + str(j) + ")))){\n"
            string += ("\t\t\trubbleLevels" + str(i) + str(j) + " = 1000000000;\n")
            string += ("\t\t\tdistances" + str(i) + str(j) + " = 1000000000;\n")
            string += ("\t\t\tprevDistances" + str(i) + str(j) + " = 1000000000;\n")
            string += "\t\t}\n"
            string += "\t\telse{\n"
            string += ("\t\t\trubbleLevels" + str(i) + str(j) + " = robot.rc.senseRubble(mc.translate(" + str(i) + ", " + str(j) + ")) + 10;\n")
            string += ("\t\t\tdxDiff = Math.abs(newrow - target.x);\n")
            string += ("\t\t\tdyDiff = Math.abs(newcol - target.y);\n")
            string += ("\t\t\tdistances" + str(i) + str(j) + " = (Math.max(dxDiff, dyDiff) + Math.min(dxDiff, dyDiff) / Util.DISTANCE_WEIGHT_RECIPROCAL) * avgRubble + rubbleLevels" + str(i) + str(j) + ";\n")
            string += ("\t\t\tprevDistances" + str(i) + str(j) + " = distances" + str(i) + str(j) + ";\n")
            string += ("\t\t}\n")
        else:
            string += ("\t\trubbleLevels" + str(i) + str(j) + " = robot.rc.senseRubble(mc.translate(" + str(i) + ", " + str(j) + ")) + 10;\n")
            string += "\t\tdistances" + str(i) + str(j) + " = Util.distanceMetric(newrow, newcol, target.x, target.y) * avgRubble;\n"
            string += ("\t\tprevDistances" + str(i) + str(j) + " = distances" + str(i) + str(j) + ";\n")
string += "\t}"
        
string +="\n\n\n"

dx = [0, 1, 1, 1, 0, -1, -1, -1]
dy = [1, 1, 0, -1, -1, -1, 0, 1]

string += "\tpublic void iterate(){\n"
for i in range(5):
    for j in range(5):
        string += ("\t\tdistances" + str(i) + str(j) + " -= rubbleLevels" + str(i) + str(j) + ";\n")
        for k in range(8):
            if (i + dx[k] >= 0 and j + dy[k] >= 0 and i + dx[k] < 5 and j + dy[k] < 5):
                string += ("\t\tif (distances" + str(i) + str(j) + " > prevDistances" + str(i + dx[k]) + str(j + dy[k]) + "){\n")
                string += ("\t\t\tdistances" + str(i) + str(j) + " = prevDistances" + str(i + dx[k]) + str(j+dy[k]) + ";\n")
                string += ("\t\t}\n")
        string += ("\t\tdistances" + str(i) + str(j) + " += rubbleLevels" + str(i) + str(j) + ";\n")
for i1 in range(5):
    for j1 in range(5):
        i = i1
        j = j1
        string += ("\t\tprevDistances" + str(i) + str(j) + " -= rubbleLevels" + str(i) + str(j) + ";\n")
        for k in range(8):
            if (i + dx[k] >= 0 and j + dy[k] >= 0 and i + dx[k] < 5 and j + dy[k] < 5):
                string += ("\t\tif (prevDistances" + str(i) + str(j) + " > distances" + str(i + dx[k]) + str(j + dy[k]) + "){\n")
                string += ("\t\t\tprevDistances" + str(i) + str(j) + " = distances" + str(i + dx[k]) + str(j+dy[k]) + ";\n")
                string += ("\t\t}\n")
        string += ("\t\tprevDistances" + str(i) + str(j) + " += rubbleLevels" + str(i) + str(j) + ";\n")
for i1 in range(3):
    for j1 in range(3):
        i = i1 + 1
        j = j1 + 1
        string += ("\t\tdistances" + str(i) + str(j) + " -= rubbleLevels" + str(i) + str(j) + ";\n")
        for k in range(8):
            if (i + dx[k] >= 0 and j + dy[k] >= 0 and i + dx[k] < 5 and j + dy[k] < 5):
                string += ("\t\tif (distances" + str(i) + str(j) + " > prevDistances" + str(i + dx[k]) + str(j + dy[k]) + "){\n")
                string += ("\t\t\tdistances" + str(i) + str(j) + " = prevDistances" + str(i + dx[k]) + str(j+dy[k]) + ";\n")
                string += ("\t\t}\n")
        string += ("\t\tdistances" + str(i) + str(j) + " += rubbleLevels" + str(i) + str(j) + ";\n")





string += "\t}\n\n\n"


string += "\tpublic Direction findBestDirection(MapLocation target, int avgRubble) throws GameActionException{\n"
string += "\t\tpopulateArrays(target, avgRubble);\n"
string += "\t\titerate();\n"
string += "\t\tint minDistance = 1000000000;\n"
string += "\t\tint bestidx = 0;\n"
for k in range(8):
    string += "\t\tif (minDistance > distances" + str(2 + dx[k]) + str(2 + dy[k]) + "){\n"
    string += "\t\t\tminDistance = distances" + str(2 + dx[k]) + str(2 + dy[k]) + ";\n"
    string += "\t\t\tbestidx = " + str(k) + ";\n"
    string += "\t\t}\n"
string += "\t\treturn Util.directions[bestidx];\n"
string += "\t}"
string += "\n}"
f.write(string)














f.close()