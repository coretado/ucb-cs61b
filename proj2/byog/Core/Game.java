package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // TODO: Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        StringBuilder playerInput = new StringBuilder();
        StringBuilder seedString = new StringBuilder();
        for (int i = 0; i < input.length(); i += 1) {
            if (Character.isDigit(input.charAt(i))) {
                seedString.append(input.charAt(i));
            } else {
                playerInput.append(input.charAt(i));
            }
        }

        WorldGenerator worldGenerator = new WorldGenerator(WIDTH, HEIGHT, Long.parseLong(seedString.toString()));
        worldGenerator.generateWorld();
        return worldGenerator.getGrid();
    }

    public static void main(String[] args) {
        Game game = new Game();
        TETile[][] world = game.playWithInputString("123456");
        game.ter.initialize(WIDTH, HEIGHT);
//        for (int i = 0; i < 50; i += 1) {
//            for (int y = 0; y < 50; y += 1) {
//                if (world[i][y].character() == '#' || world[i][y].character() == '·') {
//                    System.out.println("at coor (" + i + ", " + y + ") : " + world[i][y].character());
//                }
//            }
//        }
        game.ter.renderFrame(world);
    }
}
