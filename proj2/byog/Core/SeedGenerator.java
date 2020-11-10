package byog.Core;

import java.util.Random;

/**
 * Singleton class to pass around a Random object to classes that need to use it
 */
public class SeedGenerator {
    private static Random seedGen;

    public SeedGenerator(Long seed) {
        seedGen = new Random(seed);
    }

    /** guaranteed to at least return a value of 3 for rectangular/square room construction */
    public static int genDimension(int value) {
        int seedVal = seedGen.nextInt(value);
        return Math.max(3, seedVal);
    }

    public static int genOrigin(int value) {
        return seedGen.nextInt(value);
    }

    /** randomly generate a corridor at a 30% clip */
    public static boolean generateCorridor() {
        int seedVal = seedGen.nextInt(100);
        return seedVal < 30;
    }

    /** 50/50 odds (for determining if a corridor will be vertical or horizontal) */
    public static boolean coinflip() {
        return seedGen.nextInt(1) == 0;
    }

    public static int boundByOneAndInput(int input) {
        return 1 + seedGen.nextInt(input);
    }
}
