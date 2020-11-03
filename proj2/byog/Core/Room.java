package byog.Core;

import byog.TileEngine.TETile;
import edu.princeton.cs.algs4.ST;

public class Room {
    // these values can be adjusted to change the room generation
    private static final int ROOM_WIDTH = 8;
    private static final int ROOM_HEIGHT = 3;


    private ST<String, TETile> tiles;
    private Coordinate origin;
    private SeedGenerator seedGen;
    private int height;
    private int width;

    public Room(SeedGenerator seedGen, Coordinate origin) {
        this.seedGen = seedGen;
        this.height = this.seedGen.genNumber(ROOM_HEIGHT);
        this.width = this.seedGen.genNumber(ROOM_WIDTH);
        this.origin = origin;
        this.tiles = new ST<>();
    }

    /**
     * Should generate a room and assign its tiles to the Symbol Table
     * - the current idea I have right now is to try to use Cellular Automata to randomly generate
     * a room with tiles of floor and walls; then check each tile if it surrounded by a majority of wall tiles
     * change it's type to a tile type of wall (if its a floor) and vice versa; one thing I am unsure of
     * is how to "trim" the walls so that the wall thickness is only 1, or if I should even worry about that at all
     *
     * Another thing this generate room function will have to do is randomly create and attach a corridor to the
     * room
     */
    private void generateRoom() {
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                String tileKey = (origin.getX() + x) + "," + (origin.getY() + y);
                tiles.put(tileKey, seedGen.genTile());
            }
        }
    }
}
