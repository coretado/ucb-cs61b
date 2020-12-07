package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ArrayBlockingQueue;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 35;
    public static final int HEIGHT = 35;
    /* game env variables */
    private final int gameClockCycle = 17;
    private StringBuilder playerMoves = new StringBuilder();
    private ArrayBlockingQueue<Character> lastEight = new ArrayBlockingQueue<>(8);
    private boolean playing = false;
    private boolean quitgame = false;
    private boolean loadgame = false;
    private int internalWidth;
    private int internalHeight;
    private final Font standardFont = new Font("Monaco", Font.BOLD, 16);
    private final Font titleFont = new Font("Monaco", Font.BOLD, 30);

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        // initialize internal env variables
        this.internalHeight = HEIGHT + 10;
        this.internalWidth = WIDTH + 5;

        // initialize world
        this.ter.initialize(this.internalWidth, this.internalHeight, 5, 0);

        // start game
        while (!this.quitgame) {
            // start menu loop
            if (!this.playing) {
                // display menu options
                this.paintMenuOptions();
                StdDraw.pause(gameClockCycle);

                // receive player option
                char option = this.solicitMenuOption();

                // enqueue player move
                this.playerMoves.append(option);

                // user quit
                if (option == 'Q') {
                    this.quitgame = true;
                }

                // user loading saved game
                if (option == 'L') {
                    this.loadgame = true;
                }

                // user starting new game, collect seed input
                if (option == 'N') {
                    // gather user input
                    String gatherSeed = this.solicitNewGameSeedInput();
                    // strip N and S characters
                    String seed = gatherSeed.substring(1, gatherSeed.length() - 1);
                    // start game

                }
            }

            // start game loop
            while(this.playing) {

            }
        }
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

    private void resetScreen() {
        StdDraw.clear();
        StdDraw.clear(Color.black);
    }

    private void paintMenuOptions() {
        // grab positions
        int midWidth = this.internalWidth / 2;
        int titleHeight = (int) (this.internalHeight * 0.8);
        int newGameTextHeight = (int) (this.internalHeight * 0.65);
        int loadGameTextHeight = (int) (this.internalHeight * 0.6);
        int quitGameTextHeight = (int) (this.internalHeight * 0.55);

        this.resetScreen();

        // Set Title Text
        StdDraw.setFont(this.titleFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(midWidth, titleHeight, "CS61B Roguelike");

        // Set Options Text
        StdDraw.setFont(this.standardFont);
        StdDraw.text(midWidth, newGameTextHeight, "New Game (N)");
        StdDraw.text(midWidth, loadGameTextHeight, "Load Game (L)");
        StdDraw.text(midWidth, quitGameTextHeight, "Quit (Q)");

        // Show the world the menu. hello, world.
        StdDraw.show();
    }

    private void drawSeedInputFrame(String seed) {
        // grab positions
        int midWidth = this.internalWidth / 2;
        int instructionHeight = (int) (this.internalHeight * 0.8);
        int inputHeight = (int) (this.internalHeight * 0.55);

        this.resetScreen();

        // set instruction text
        StdDraw.setFont(this.titleFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(midWidth, instructionHeight, "Enter seed:");

        // set user input
        StdDraw.setFont(this.standardFont);
        StdDraw.text(midWidth, inputHeight, seed);

        // hello, world
        StdDraw.show();
    }

    private char solicitMenuOption() {
        char userInput = 0;
        boolean validInput = false;

        while (!validInput) {
            // pause for game tick
            StdDraw.pause(gameClockCycle);

            // continue if no input detected
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            userInput = StdDraw.nextKeyTyped();

            if (userInput == 'Q' || userInput == 'L' || userInput == 'N') {
                validInput = true;
            }
        }

        return userInput;
    }

    private String solicitNewGameSeedInput() {
        // IO ends when character is "S"
        boolean endIO = false;
        StringBuilder sb = new StringBuilder();
        this.drawSeedInputFrame(sb.toString());

        while (!endIO) {
            // pause for game tick
            StdDraw.pause(gameClockCycle);

            // continue if no input detected
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            // assuming in good faith every input is either an integer or "S"
            char input = StdDraw.nextKeyTyped();
            this.playerMoves.append(input);
            sb.append(input);

            // continue is used to not show integers with stop command
            if (input == 'S') {
                endIO = true;
                continue;
            }

            // draw input frame
            this.drawSeedInputFrame(sb.toString());
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        Game game = new Game();
        TETile[][] world = game.playWithInputString("123456");
        game.ter.initialize(WIDTH, HEIGHT);
        game.ter.renderFrame(world);
    }
}
