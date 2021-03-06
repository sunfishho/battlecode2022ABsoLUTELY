#!/usr/bin/env python3

arraySize = 5
midSize = (arraySize - 1)//2

f = open("src/first_bot/Pathfinding" + str(arraySize) + ".java", "w")

string = """package first_bot;
import battlecode.common.*;

public class Pathfinding""" + str(arraySize) + """{
    static int distanceSquared;
    RobotCommon robot;
    // static int maxStraightDistance;


    static final int[] dx = new int[] {0, 1, 1, 1, 0, -1, -1, -1};
    static final int[] dy = new int[] {1, 1, 0, -1, -1, -1, 0, 1};


    static int AVG_RUBBLE = 60;

    public Pathfinding""" + str(arraySize) + """(RobotCommon robot){
        this.robot = robot;
        distanceSquared = robot.rc.getType().visionRadiusSquared;
    }\n\n"""

for i in range (arraySize * arraySize):
    string += ("\tstatic int distances" + str(i // arraySize)  + str(i % arraySize) + ";\n")
    string += ("\tstatic int prevDistances" + str(i // arraySize)  + str(i % arraySize) + ";\n")
    string += ("\tstatic int rubbleLevels" + str(i // arraySize)  + str(i % arraySize) + ";\n")
    string += "\n"

string += "\tpublic void populateArrays(MapLocation target) throws GameActionException{\n"
string += "\t\tMapLocation mc = robot.me.translate(-" + str(midSize) + ", -" + str(midSize) + ");\n"
string += "\t\tint newrow = mc.x;\n"
string += "\t\tint newcol = mc.y;\n"
string += "\t\tint dxDiff = 0;\n"
string += "\t\tint dyDiff = 0;\n"
for i in range(arraySize):
    for j in range(arraySize):
        if j == 0 and i > 0:
            string += "\t\tnewrow++;\n"
            string += "\t\tnewcol -= " + str(arraySize - 1) + ";\n"
        elif i > 0 or j > 0:
            string += "\t\tnewcol++;\n"
        if (i != midSize or j != midSize):
            string += "\t\tif (newrow < 0 || newcol < 0 || newrow >= Util.WIDTH || newcol >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc.translate(" + str(i) + ", " + str(j) + ")))){\n"
            string += ("\t\t\trubbleLevels" + str(i) + str(j) + " = 1000000000;\n")
            string += ("\t\t\tdistances" + str(i) + str(j) + " = 1000000000;\n")
            string += ("\t\t\tprevDistances" + str(i) + str(j) + " = 1000000000;\n")
            string += "\t\t}\n"
            string += "\t\telse{\n"
            string += ("\t\t\trubbleLevels" + str(i) + str(j) + " = robot.rc.senseRubble(mc.translate(" + str(i) + ", " + str(j) + ")) + 10;\n")
            string += ("\t\t\tdxDiff = newrow - target.x;\n")
            string += ("\t\t\tdyDiff = newcol - target.y;\n")
            string += ("\t\t\tif (dxDiff >= 0){\n")
            string += ("\t\t\t\tif (dyDiff >= 0){\n")
            string += ("\t\t\t\t\tif (dxDiff >= dyDiff){\n")
            string += ("\t\t\t\t\t\tdistances" + str(i) + str(j) + " = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;\n")
            string += ("\t\t\t\t\t}\n")
            string += ("\t\t\t\t\telse{\n")
            string += ("\t\t\t\t\t\tdistances" + str(i) + str(j) + " = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;\n")
            string += ("\t\t\t\t\t}\n")
            string += ("\t\t\t\t}\n")
            string += ("\t\t\t\telse{\n")
            string += ("\t\t\t\t\tdyDiff *= -1;\n")
            string += ("\t\t\t\t\tif (dxDiff >= dyDiff){\n")
            string += ("\t\t\t\t\t\tdistances" + str(i) + str(j) + " = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;\n")
            string += ("\t\t\t\t\t}\n")
            string += ("\t\t\t\t\telse{\n")
            string += ("\t\t\t\t\t\tdistances" + str(i) + str(j) + " = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;\n")
            string += ("\t\t\t\t\t}\n")
            string += ("\t\t\t\t}\n")
            string += ("\t\t\t}\n")
            string += ("\t\t\telse{\n")
            string += ("\t\t\t\tdxDiff *= -1;\n")
            string += ("\t\t\t\tif (dyDiff >= 0){\n")
            string += ("\t\t\t\t\tif (dxDiff >= dyDiff){\n")
            string += ("\t\t\t\t\t\tdistances" + str(i) + str(j) + " = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;\n")
            string += ("\t\t\t\t\t}\n")
            string += ("\t\t\t\t\telse{\n")
            string += ("\t\t\t\t\t\tdistances" + str(i) + str(j) + " = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;\n")
            string += ("\t\t\t\t\t}\n")
            string += ("\t\t\t\t}\n")
            string += ("\t\t\t\telse{\n")
            string += ("\t\t\t\t\tdyDiff *= -1;\n")
            string += ("\t\t\t\t\tif(dxDiff >= dyDiff){\n")
            string += ("\t\t\t\t\t\tdistances" + str(i) + str(j) + " = (dxDiff + dyDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;\n")
            string += ("\t\t\t\t\t}\n")
            string += ("\t\t\t\t\telse{\n")
            string += ("\t\t\t\t\t\tdistances" + str(i) + str(j) + " = (dyDiff + dxDiff / Util.DISTANCE_WEIGHT_RECIPROCAL) * AVG_RUBBLE;\n")
            string += ("\t\t\t\t\t}\n")
            string += ("\t\t\t\t}\n")
            string += ("\t\t\t}\n")
            string += ("\t\t\tprevDistances" + str(i) + str(j) + " = distances" + str(i) + str(j) + ";\n")
            string += ("\t\t}\n")
        else:
            string += ("\t\trubbleLevels" + str(i) + str(j) + " = robot.rc.senseRubble(mc.translate(" + str(i) + ", " + str(j) + ")) + 10;\n")
            string += "\t\tdistances" + str(i) + str(j) + " = Util.distanceMetric(newrow, newcol, target.x, target.y) * AVG_RUBBLE;\n"
            string += ("\t\tprevDistances" + str(i) + str(j) + " = distances" + str(i) + str(j) + ";\n")
string += "\t}"
        
string +="\n\n\n"

dx = [0, 1, 1, 1, 0, -1, -1, -1]
dy = [1, 1, 0, -1, -1, -1, 0, 1]

string += "\tpublic void iterate(){\n"
for i in range(arraySize):
    for j in range(arraySize):
        string += ("\t\tdistances" + str(i) + str(j) + " -= rubbleLevels" + str(i) + str(j) + ";\n")
        for k in range(8):
            if (i + dx[k] >= 0 and j + dy[k] >= 0 and i + dx[k] < arraySize and j + dy[k] < arraySize):
                string += ("\t\tif (distances" + str(i) + str(j) + " > prevDistances" + str(i + dx[k]) + str(j + dy[k]) + "){\n")
                string += ("\t\t\tdistances" + str(i) + str(j) + " = prevDistances" + str(i + dx[k]) + str(j+dy[k]) + ";\n")
                string += ("\t\t}\n")
        string += ("\t\tdistances" + str(i) + str(j) + " += rubbleLevels" + str(i) + str(j) + ";\n")
for i in range(arraySize):
    for j in range(arraySize):
        string += ("\t\tprevDistances" + str(i) + str(j) + " -= rubbleLevels" + str(i) + str(j) + ";\n")
        for k in range(8):
            if (i + dx[k] >= 0 and j + dy[k] >= 0 and i + dx[k] < arraySize and j + dy[k] < arraySize):
                string += ("\t\tif (prevDistances" + str(i) + str(j) + " > distances" + str(i + dx[k]) + str(j + dy[k]) + "){\n")
                string += ("\t\t\tprevDistances" + str(i) + str(j) + " = distances" + str(i + dx[k]) + str(j+dy[k]) + ";\n")
                string += ("\t\t}\n")
        string += ("\t\tprevDistances" + str(i) + str(j) + " += rubbleLevels" + str(i) + str(j) + ";\n")
for i in range(arraySize):
    for j in range(arraySize):
        string += ("\t\tdistances" + str(i) + str(j) + " -= rubbleLevels" + str(i) + str(j) + ";\n")
        for k in range(8):
            if (i + dx[k] >= 0 and j + dy[k] >= 0 and i + dx[k] < arraySize and j + dy[k] < arraySize):
                string += ("\t\tif (distances" + str(i) + str(j) + " > prevDistances" + str(i + dx[k]) + str(j + dy[k]) + "){\n")
                string += ("\t\t\tdistances" + str(i) + str(j) + " = prevDistances" + str(i + dx[k]) + str(j+dy[k]) + ";\n")
                string += ("\t\t}\n")
        string += ("\t\tdistances" + str(i) + str(j) + " += rubbleLevels" + str(i) + str(j) + ";\n")

for i in range(arraySize):
    for j in range(arraySize):
        string += ("\t\tprevDistances" + str(i) + str(j) + " -= rubbleLevels" + str(i) + str(j) + ";\n")
        for k in range(8):
            if (i + dx[k] >= 0 and j + dy[k] >= 0 and i + dx[k] < arraySize and j + dy[k] < arraySize):
                string += ("\t\tif (prevDistances" + str(i) + str(j) + " > distances" + str(i + dx[k]) + str(j + dy[k]) + "){\n")
                string += ("\t\t\tprevDistances" + str(i) + str(j) + " = distances" + str(i + dx[k]) + str(j+dy[k]) + ";\n")
                string += ("\t\t}\n")
        string += ("\t\tprevDistances" + str(i) + str(j) + " += rubbleLevels" + str(i) + str(j) + ";\n")
for i in range(arraySize):
    for j in range(arraySize):
        string += ("\t\tdistances" + str(i) + str(j) + " -= rubbleLevels" + str(i) + str(j) + ";\n")
        for k in range(8):
            if (i + dx[k] >= 0 and j + dy[k] >= 0 and i + dx[k] < arraySize and j + dy[k] < arraySize):
                string += ("\t\tif (distances" + str(i) + str(j) + " > prevDistances" + str(i + dx[k]) + str(j + dy[k]) + "){\n")
                string += ("\t\t\tdistances" + str(i) + str(j) + " = prevDistances" + str(i + dx[k]) + str(j+dy[k]) + ";\n")
                string += ("\t\t}\n")
        string += ("\t\tdistances" + str(i) + str(j) + " += rubbleLevels" + str(i) + str(j) + ";\n")




string += "\t}\n\n\n"


string += "\tpublic Direction findBestDirection(MapLocation target) throws GameActionException{\n"
string += "\t\tpopulateArrays(target);\n"
string += "\t\titerate();\n"
string += "\t\tint minDistance = 1000000000;\n"
string += "\t\tint bestidx = 0;\n"
for k in range(8):
    string += "\t\tif (minDistance > prevDistances" + str(midSize + dx[k]) + str(midSize + dy[k]) + "){\n"
    string += "\t\t\tminDistance = prevDistances" + str(midSize + dx[k]) + str(midSize + dy[k]) + ";\n"
    string += "\t\t\tbestidx = " + str(k) + ";\n"
    string += "\t\t}\n"
string += "\t\treturn Util.directions[bestidx];\n"
string += "\t}"
string += "\n}"
f.write(string)














f.close()