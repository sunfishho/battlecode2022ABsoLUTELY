package first_bot;
import battlecode.common.*;

public class Util {
    static int ARCHON_MEMORY_SIZE = 1;
    static int wall_definition_cutoff = 50;

    public static MapLocation getLocationFromInt(int loc) {
        return new MapLocation(loc / 64, loc % 64);
    }

    public static int getIntFromLocation(MapLocation loc) {
        return loc.x * 64 + loc.y;
    }
}