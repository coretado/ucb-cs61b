package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 35;
    public static final int HEIGHT = 35;
    /* game env variables */
    private final int gameClockCycle = 17;
    private StringBuilder gameState = new StringBuilder();
    private boolean playing = false;
    private boolean quitgame = false;
    private boolean loadgame = false;
    private int internalWidth;
    private int internalHeight;
    private final Font standardFont = new Font("Monaco", Font.BOLD, 16);
    private final Font titleFont = new Font("Monaco", Font.BOLD, 30);
    private TETile[][] world;
    private WorldGenerator worldGenerator;
    private PlayerLocation playerLocation;
    private final String QUIT_COMMAND = ":Q";

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        // initialize internal env variables
        this.internalHeight = HEIGHT + 10;
        this.internalWidth = WIDTH + 10;

        // initialize world
        this.ter.initialize(this.internalWidth, this.internalHeight, 5, 5);

        // start game
        while (!this.quitgame) {
            // start menu loop
            if (!this.playing) {
                // display menu options
                this.drawMenuOptionsFrame();
                StdDraw.pause(gameClockCycle);

                // receive player option
                char option = this.solicitMenuOption();

                // enqueue player move
                this.gameState.append(option);

                // user quit
                if (option == 'Q') {
                    this.quitgame = true;
                }

                // user loading saved game
                if (option == 'L') {
                    // TODO: Decide if load game cycle is done here or in playing loop
                    this.loadgame = true;
                }

                // user starting new game, collect seed input
                if (option == 'N') {
                    // gather user input
                    String gatherSeed = this.solicitNewGameSeedInput();
                    // strip N and S characters
                    String seed = gatherSeed.substring(1, gatherSeed.length() - 1);
                    // start game
                    this.worldGenerator = new WorldGenerator(WIDTH, HEIGHT, Long.parseLong(seed));
                    this.worldGenerator.generateWorld();
                    this.world = this.worldGenerator.getGrid();
                    // randomly place character in a room
                    this.playerLocation = this.worldGenerator.randomlyPlacePlayerModel();
                    System.out.println("player location at start: (" + this.playerLocation.getCol() + ", " + this.playerLocation.getRow() + ")");
                    // start the game
                    this.playing = true;
                }
            }

            // start game loop
            while(this.playing) {
                this.solicitGameInput();
            }
        }
        
        System.out.println("I'm out");
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

    private void drawMenuOptionsFrame() {
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

    // separated so game UI and game traversal are separate
    private void drawGameGui() {
        // prepare the "pen"
        StdDraw.setFont(this.standardFont);
        StdDraw.setPenColor(Color.white);

        // -5 to account for padding
        int mouseCol = ((int) StdDraw.mouseX()) - 5;
        int mouseRow = ((int) StdDraw.mouseY()) - 5;

        // only display a description if not "out of bounds"
        if (mouseCol > -1 && mouseCol < WIDTH && mouseRow > -1 && mouseRow < HEIGHT) {
            TETile hoverTile = this.world[mouseCol][mouseRow];
            StdDraw.textLeft(1, this.internalHeight - 1, hoverTile.description());
        }

        StdDraw.show();
    }

    private void drawGameFrame() {
        ter.renderFrame(this.world);
        this.drawGameGui();
    }

    private char solicitMenuOption() {
        char userInput = 0;

        while (true) {
            // pause for game tick
            StdDraw.pause(gameClockCycle);

            // continue if no input detected
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            userInput = StdDraw.nextKeyTyped();

            if (userInput == 'Q' || userInput == 'L' || userInput == 'N') {
                break;
            }
        }

        return userInput;
    }

    private String solicitNewGameSeedInput() {
        // IO ends when character is "S"
        boolean endIO = false;
        StringBuilder sb = new StringBuilder();
        this.drawSeedInputFrame(sb.toString());

        while (true) {
            // pause for game tick
            StdDraw.pause(gameClockCycle);

            // continue if no input detected
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            // assuming in good faith every input is either an integer or "S"
            char input = StdDraw.nextKeyTyped();
            this.gameState.append(input);
            sb.append(input);

            // continue is used to not show integers with stop command
            if (input == 'S') {
                break;
            }

            // draw input frame
            this.drawSeedInputFrame(sb.toString());
        }

        return sb.toString();
    }

    private void moveDirectionHelper(char dir) {
        if (dir != 'W' && dir != 'A' && dir != 'S' && dir != 'D') {
            return;
        }

        PlayerLocation move = new PlayerLocation();

        if (dir == 'W') {
            move.setRow(this.playerLocation.getRow() + 1);
            move.setCol(this.playerLocation.getCol());
        }
        if (dir == 'A') {
            move.setRow(this.playerLocation.getRow());
            move.setCol(this.playerLocation.getCol() - 1);
        }
        if (dir == 'S') {
            move.setRow(this.playerLocation.getRow() - 1);
            move.setCol(this.playerLocation.getCol());
        }
        if (dir == 'D') {
            move.setRow(this.playerLocation.getRow());
            move.setCol(this.playerLocation.getCol() + 1);
        }
        // if player attempts to move into a wall, don't have to update their position
        if (this.world[move.getCol()][move.getRow()].character() == '#') {
            return;
        }

        // swap tiles
        this.world[this.playerLocation.getCol()][this.playerLocation.getRow()] = Tileset.FLOOR;
        this.world[move.getCol()][move.getRow()] = Tileset.PLAYER;

        this.playerLocation = move;
    }

    private void solicitGameInput() {
        // draw the initial game frame
        this.drawGameFrame();

        // gather user input
        while (true) {
            // pause for game tick
            StdDraw.pause(gameClockCycle);

            // detect mouse movement
            this.drawGameFrame();

            // continue if no input detected
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            // only unicode characters are valid; i.e. arrows keys would not work
            char input = StdDraw.nextKeyTyped();
            this.gameState.append(input);

            // detect if game needs to end
            if (this.gameState.substring(this.gameState.length() - 2).equals(QUIT_COMMAND)) {
                break;
            }

            // move character model
            this.moveDirectionHelper(input);
        }

        this.playing = false;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.playWithKeyboard();
        // TETile[][] world = game.playWithInputString("123456");
        // game.ter.initialize(WIDTH, HEIGHT);
        // game.ter.renderFrame(world);
    }
}
