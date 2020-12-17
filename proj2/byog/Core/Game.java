package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Game {
    /* static game environment_variables */
    TERenderer ter = new TERenderer();
    private static final int WIDTH = 35;
    private static final int HEIGHT = 35;
    private static final int INTERNAL_WIDTH = WIDTH + 10;
    private static final int INTERNAL_HEIGHT = HEIGHT + 10;
    private static final int MID_WIDTH = INTERNAL_WIDTH / 2;
    private final Font standardFont = new Font("Monaco", Font.BOLD, 16);
    private final Font titleFont = new Font("Monaco", Font.BOLD, 30);
    private final String QUIT_COMMAND = ":Q";

    /* dynamic game environment_variables */
    private final int gameClockCycle = 17;
    private StringBuilder gameState;
    private PlayerLocation playerLocation;
    private boolean playing = false;
    private boolean quitgame = false;
    private TETile[][] world;
    private WorldGenerator worldGenerator;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        // initialize world
        this.ter.initialize(INTERNAL_WIDTH, INTERNAL_HEIGHT, 5, 5);

        // start game
        while (!this.quitgame) {
            // start menu loop
            if (!this.playing) {
                // display menu options
                this.drawMenuOptionsFrame();
                StdDraw.pause(gameClockCycle);

                // receive player option
                char option = this.solicitMenuOption();

                // user quit
                if (option == 'Q') {
                    this.quitgame = true;
                }

                // user loading saved game
                if (option == 'L') {
                    this.loadGameFromSaveState(this.loadGame());
                    this.playing = true;
                }

                // user starting new game, collect seed input
                if (option == 'N') {
                    // start game
                    this.instantiateGameWorldForPlay(
                        Long.parseLong(this.solicitNewGameSeedInput()));
                    // start the game
                    this.playing = true;
                }
            }

            // start game loop
            while (this.playing) {
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
        // the detection of a new game or a load game are under the assumption an auto grader will
        // only input valid strings starting with 'N' or 'L' - that is to say this method
        // would not be robust against a user in the real world
        if (input.charAt(0) == 'N') {
            this.playGameFromKeyboardInput(input);
            return this.world;
        }

        this.loadThenPlayGameFromKeyboardInput(input);
        return this.world;
    }

    private void resetScreen() {
        StdDraw.clear();
        StdDraw.clear(Color.black);
    }

    private void drawMenuOptionsFrame() {
        // grab positions
        int titleHeight = (int) (INTERNAL_HEIGHT * 0.8);
        int newGameTextHeight = (int) (INTERNAL_HEIGHT * 0.65);
        int loadGameTextHeight = (int) (INTERNAL_HEIGHT * 0.6);
        int quitGameTextHeight = (int) (INTERNAL_HEIGHT * 0.55);

        this.resetScreen();

        // Set Title Text
        StdDraw.setFont(this.titleFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(MID_WIDTH, titleHeight, "CS61B Roguelike");

        // Set Options Text
        StdDraw.setFont(this.standardFont);
        StdDraw.text(MID_WIDTH, newGameTextHeight, "New Game (N)");
        StdDraw.text(MID_WIDTH, loadGameTextHeight, "Load Game (L)");
        StdDraw.text(MID_WIDTH, quitGameTextHeight, "Quit (Q)");

        // Show the world the menu. hello, world.
        StdDraw.show();
    }

    private void drawSeedInputFrame(String seed) {
        // grab positions
        int instructionHeight = (int) (INTERNAL_HEIGHT * 0.8);
        int inputHeight = (int) (INTERNAL_WIDTH * 0.55);

        this.resetScreen();

        // set instruction text
        StdDraw.setFont(this.titleFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(MID_WIDTH, instructionHeight, "Enter seed:");

        // set user input
        StdDraw.setFont(this.standardFont);
        StdDraw.text(MID_WIDTH, inputHeight, seed);

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
            StdDraw.textLeft(1, INTERNAL_HEIGHT - 1, hoverTile.description());
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
        StringBuilder sb = new StringBuilder();
        this.drawSeedInputFrame(sb.toString());

        while (true) {
            // pause for game tick
            StdDraw.pause(gameClockCycle);

            // continue if no input detected
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            // grab number inputs
            char input = StdDraw.nextKeyTyped();
            if (input >= '0' && input <= '9') {
                sb.append(input);
            }

            // stop seed input
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
                this.saveGame(new GameState(this.gameState.toString()));
                break;
            }

            // move character model
            this.moveDirectionHelper(input);
        }

        this.playing = false;
    }

    private String loadGame() {
        File file = new File("./save.txt");

        if (file.exists()) {
            try {
                FileInputStream fs = new FileInputStream(file);
                ObjectInputStream os = new ObjectInputStream(fs);
                GameState gs = (GameState) os.readObject();
                os.close();
                return gs.getGameState();
            } catch (FileNotFoundException e) {
                System.out.println("There is no save.txt file");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("Class GameState not found");
            }
        }

        return "";
    }

    private void saveGame(GameState gs) {
        File file = new File("./save.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(gs);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("File save.txt not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private void instantiateGameWorldForPlay(Long seed) {
        this.worldGenerator = new WorldGenerator(WIDTH, HEIGHT, seed);
        this.worldGenerator.generateWorld();
        this.world = this.worldGenerator.getGrid();
        this.playerLocation = this.worldGenerator.randomlyPlacePlayerModel();
        this.gameState = new StringBuilder("N" + seed + "S");
    }

    private boolean scanPlayerActionForQuitCommand(int counter, String input) {
        return (
            input.charAt(counter) == ':'
            && counter + 1 < input.length()
            && input.charAt(counter + 1) == 'Q');
    }

    private void playGameFromKeyboardInput(String input) {
        // starting variables for parsing seed
        StringBuilder sb = new StringBuilder();
        int stringCounter = 1;

        // parse seed
        while (input.charAt(stringCounter) != 'S') {
            sb.append(input.charAt(stringCounter));
            stringCounter += 1;
        }

        // move counter past the 'S' character
        stringCounter += 1;

        this.instantiateGameWorldForPlay(Long.parseLong(sb.toString()));

        // run through input actions
        while (stringCounter < input.length()) {
            // check for the quit action
            if (this.scanPlayerActionForQuitCommand(stringCounter, input)) {
                this.gameState.append(QUIT_COMMAND);
                break;
            }
            // move player
            this.moveDirectionHelper(input.charAt(stringCounter));
            // mutate game state
            this.gameState.append(input.charAt(stringCounter));
            // increment position of input scan
            stringCounter += 1;
        }

        // if user had a save command, make sure to save the game
        if (this.gameState.substring(this.gameState.length() - 2).equals(QUIT_COMMAND)) {
            this.saveGame(new GameState(this.gameState.toString()));
        }
    }

    private void loadThenPlayGameFromKeyboardInput(String input) {
        // fetch previous game state
        String previousGameState = this.loadGame();

        //starting variables for parsing seed
        StringBuilder sb = new StringBuilder();
        int previousStringCounter = 1;

        // parse seed
        while (previousGameState.charAt(previousStringCounter) != 'S') {
            sb.append(previousGameState.charAt(previousStringCounter));
            previousStringCounter += 1;
        }

        // move counter past the 'S' character
        previousStringCounter += 1;

        this.instantiateGameWorldForPlay(Long.parseLong(sb.toString()));

        // run through input actions
        while (previousStringCounter < previousGameState.length()) {
            // check for quit command; because this is previous game state,
            // we just break loop and 'present' game
            if (this.scanPlayerActionForQuitCommand(previousStringCounter, previousGameState)) {
                break;
            }
            // move player
            this.moveDirectionHelper(previousGameState.charAt(previousStringCounter));
            // mutate game state
            this.gameState.append(previousGameState.charAt(previousStringCounter));
            // increment position of input scan
            previousStringCounter += 1;
        }

        // string counter starts at 1 to skip 'L'
        int stringCounter = 1;

        // run through new input actions
        while (stringCounter < input.length()) {
            // check for quit command
            if (this.scanPlayerActionForQuitCommand(stringCounter, input)) {
                this.gameState.append(QUIT_COMMAND);
                break;
            }
            // move player
            this.moveDirectionHelper(input.charAt(stringCounter));
            // mutate game state
            this.gameState.append(input.charAt(stringCounter));
            // increment position of input scan
            stringCounter += 1;
        }

        // if user had a save command, make sure to save the game
        if (this.gameState.length() > 2
            && this.gameState.substring(this.gameState.length() - 2).equals(":Q")
        ) {
            this.saveGame(new GameState(this.gameState.toString()));
        }
    }

    private void loadGameFromSaveState(String input) {
        // create seed, start after 'N' character
        StringBuilder gameStateSeed = new StringBuilder();
        int stringCounter = 1;

        // parse seed from string since we save game state as a string
        while (input.charAt(stringCounter) != 'S') {
            gameStateSeed.append(input.charAt(stringCounter));
            stringCounter += 1;
        }

        // skip the first 'S' character;
        stringCounter += 1;

        // recreate base world using fetched seed
        this.instantiateGameWorldForPlay(Long.parseLong(gameStateSeed.toString()));

        // re-run player actions
        while (stringCounter < input.length()) {
            // check for the quit action
            if (this.scanPlayerActionForQuitCommand(stringCounter, input)) {
                break;
            }
            // fetch action
            this.gameState.append(input.charAt(stringCounter));
            // perform action
            this.moveDirectionHelper(input.charAt(stringCounter));
            // increment counter
            stringCounter += 1;
        }
    }

    public static void main(String[] args) {
        // Game game = new Game();
        // game.playWithKeyboard();
        // game.ter.initialize(WIDTH, HEIGHT);
        // TETile[][] world = game.playWithInputString("N999SDDDWWWDDD");

        // group one
        // TETile[][] world = game.playWithInputString("N999SDDD:Q");
        // TETile[][] world = game.playWithInputString("LWWWDDD");

        // group two
        // TETile[][] world = game.playWithInputString("N999SDDD:Q");
        // TETile[][] world = game.playWithInputString("LWWW:Q");
        // TETile[][] world = game.playWithInputString("LDDD:Q");

        // group three
        // TETile[][] world = game.playWithInputString("N999SDDD:Q");
        // TETile[][] world = game.playWithInputString("L:Q");
        // TETile[][] world = game.playWithInputString("L:Q");
        // TETile[][] world = game.playWithInputString("LWWWDDD");

        // game.ter.renderFrame(world);
    }
}
