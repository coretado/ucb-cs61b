package byog.lab6;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private final int width;
    private final int height;
    private int round;
    private final Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        int seed = Integer.parseInt(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, int seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        this.rand = new Random(seed);
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public String generateRandomString(int n) {
        if (n == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i += 1) {
            sb.append(CHARACTERS[this.rand.nextInt(CHARACTERS.length)]);
        }

        return sb.toString();
    }

    public void drawFrame(String s) {
        StdDraw.clear();
        StdDraw.clear(Color.black);

        if (!this.gameOver) {
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
            StdDraw.textLeft(1, this.height - 1, "Round " + this.round);
            StdDraw.text(this.width / 2.0, height - 1, this.playerTurn ? "Type" : "Watch");
            StdDraw.textRight(
            this.width - 1, this.height - 1, ENCOURAGEMENT[ENCOURAGEMENT.length % this.round]
            );
            StdDraw.line(0, this.height - 2, this.width, this.height - 2);
        }

        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.setPenColor(Color.white);
        StdDraw.text(width / 2.0, height / 2.0, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        for (int i = 0; i < letters.length(); i += 1) {
            this.drawFrame(letters.substring(i, i + 1));
            StdDraw.pause(1000);
            this.drawFrame(" ");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        StringBuilder sb = new StringBuilder();
        this.drawFrame(sb.toString());

        while (sb.length() < n) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            sb.append(StdDraw.nextKeyTyped());
            this.drawFrame(sb.toString());
        }
        StdDraw.pause(500);
        return sb.toString();
    }

    public void startGame() {
        this.round = 1;
        this.gameOver = false;

        while (!this.gameOver) {
            this.drawFrame("Round: " + this.round);
            StdDraw.pause(1000);
            String target = this.generateRandomString(this.round);
            this.flashSequence(target);
            String userInput = this.solicitNCharsInput(this.round);
            if (!target.equals(userInput)) {
                this.gameOver = true;
            }
            this.round += 1;
        }

        this.drawFrame("Game Over! You made it to round: " + this.round);
    }

}
