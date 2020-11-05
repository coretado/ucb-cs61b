package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Singleton class to pass around a Random object to classes that need to use it
 */
public class SeedGenerator {
    private static Random seedGen;

    public SeedGenerator(Long seed) {
        this.seedGen = new Random(seed);
    }

    /** guaranteed to at least return a value of 1 */
    public static int genNumber(int value) {
        int seedVal = seedGen.nextInt(value);
        return Math.max(1, seedVal);
    }

    /** guaranteed to at least return a value of 3 for cellular automata room construction */
    public static int genDimensionNumber(int value) {
        int seedVal = seedGen.nextInt(value);
        return Math.max(3, seedVal);
    }

    /** return a value between 0-99; 0-14 spawns a corridor */
    public static int genCorridorPercentage() {
        int seedVal = seedGen.nextInt(100);
        return Math.max(1, seedVal);
    }

    /** generate tiles to be used */
    public TETile genTile() {
        int tile = seedGen.nextInt(100);
        if (tile < 55) {
            return Tileset.FLOOR;
        } else {
            return Tileset.WALL;
        }
    }
}
