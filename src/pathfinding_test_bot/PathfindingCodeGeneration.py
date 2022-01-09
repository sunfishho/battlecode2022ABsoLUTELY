#!/usr/bin/env python3

f = open("src/pathfinding_test_bot/PathfindingCode.out", "w")

string = ""

for i in range (25):
    string += ("static int distances" + str(i // 5)  + str(i % 5) + ";\n")
    string += ("static int prevDistances" + str(i // 5)  + str(i % 5) + ";\n")
    string += ("static int rubbleLevels" + str(i // 5)  + str(i % 5) + ";\n")
    string += "\n"

string += "public void populateArrays(MapLocation target) throws GameActionException{\n"
string += "\tMapLocation mc = new MapLocation(robot.me.x - 3, robot.me.y + 2);\n"
for i in range(5):
    for j in range(5):
        if (j == 0):
            string += "\tmc.translate(1, -4);\n"
        else:
            string += "\tmc.translate(0, 1);\n"
        if (i != 2 or j != 2):
            string += "\tif (mc.x < 0 || mc.y < 0 || mc.x >= Util.WIDTH || mc.y >= Util.HEIGHT || (this.robot.rc.canSenseRobotAtLocation(mc))){\n"
            string += ("\t\trubbleLevels" + str(i) + str(j) + " = 1000000000;\n")
            string += ("\t\tdistances" + str(i) + str(j) + " = 1000000000;\n")
            string += ("\t\tprevDistances" + str(i) + str(j) + " = 1000000000;\n")
            string += "\t}\n"
            string += "\telse{\n"
            string += ("\t\trubbleLevels" + str(i) + str(j) + " = robot.rc.senseRubble(mc) + 10;\n")
            string += "\t\tdistances" + str(i) + str(j) + " = Util.distanceMetric(mc.x, mc.y, target.x, target.y) * AVG_RUBBLE;\n"
            string += ("\t\tprevDistances" + str(i) + str(j) + " = distances" + str(i) + str(j) + ";\n")
            string += ("\t}\n")
        else:
            string += ("\trubbleLevels" + str(i) + str(j) + " = robot.rc.senseRubble(mc) + 10;\n")
            string += "\tdistances" + str(i) + str(j) + " = Util.distanceMetric(mc.x, mc.y, target.x, target.y) * AVG_RUBBLE;\n"
            string += ("\tprevDistances" + str(i) + str(j) + " = distances" + str(i) + str(j) + ";\n")
        
string += "}"
f.write(string)














f.close()