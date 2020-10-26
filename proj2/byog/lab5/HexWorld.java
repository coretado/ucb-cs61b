package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Arrays;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private final TETile[][] world = new TETile[WIDTH][HEIGHT];
    private final TERenderer ter = new TERenderer();
    private final int size;

    public HexWorld(int size) {
        this.size = size;
        ter.initialize(WIDTH, HEIGHT);
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void addHexagon(int posx, int posy, int size) {

    }

    /* accepts number to print and number to skip? */
    private void printLine(int b, int s, int h, TETile tileType) {
        for (int i = 0; i < s + b; i += 1) {
            if (i < b) {
                continue;
            }
            world[i][h] = tileType;
        }
    }

    @Test
    public void testPrintLine() {
        TETile[][] world = new TETile[4][4];
        for (int x = 0; x < 4; x += 1) {
            for (int y = 0; y < 4; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        int max = calcMax(2);
        int[] rows = hexHelper(2);
        for (int h = 0; h < 4; h += 1) {
            printLine(max - rows[h], rows[h], h, Tileset.GRASS);
        }
        for (int h = 0; h < 4; h += 1) {
            int buffer = max - rows[h];
            int actual = buffer + rows[h];
            for (int x = 0; x < actual; x += 1) {
                if (x < buffer) {
                    continue;
                }
                assertEquals(world[x][h], Tileset.GRASS);
            }
        }
    }

    /* calculates the max hex from a given s */
    private int calcMax(int s) {
        return ((s - 1) * 2) + s;
    }

    @Test
    public void testCalcMax() {
        assertEquals(4, calcMax(2));
        assertEquals(7, calcMax(3));
        assertEquals(10, calcMax(4));
        assertEquals(13, calcMax(5));
    }

    /* creates an array of ints that can be iterated over to print hexagons */
    private static int[] hexHelper(int s) {
        int[] hex = new int[s * 2];
        int backtracker = (s * 2) - 1;
        int numHex = s;
        for (int i = 0; i < s; i++) {
            hex[i] = numHex;
            hex[backtracker] = numHex;
            numHex += 2;
            backtracker -= 1;
        }
        return hex;
    }

    @Test
    public void testHexHelper() {
        // yeah its hard coded arrays, cut me some slack its easier
        int s1 = 2;
        int s2 = 3;
        int s3 = 4;
        int s4 = 5;
        int[] h1 = hexHelper(s1);
        int[] h2 = hexHelper(s2);
        int[] h3 = hexHelper(s3);
        int[] h4 = hexHelper(s4);
        assertArrayEquals(h1, new int[]{2, 4, 4, 2});
        assertArrayEquals(h2, new int[]{3, 5, 7, 7, 5, 3});
        assertArrayEquals(h3, new int[]{4, 6, 8, 10, 10, 8, 6, 4});
        assertArrayEquals(h4, new int[]{5, 7, 9, 11, 13, 13, 11, 9, 7, 5});
    }

    public static void main(String[] args) {
    }
}
