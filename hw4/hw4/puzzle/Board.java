package hw4.puzzle;

public class Board {
    private int[][] tiles;
    private int size;
    private int blankRow;
    private int blankCol;

    public Board(int[][] tiles) {
        this.tiles = tiles;
        this.size = tiles.length;
        int blank = this.findBlank(tiles);
        this.blankCol = transformCol(blank);
        this.blankRow = transformRow(blank);
    }

    public int tileAt(int i, int j) {
        this.checkCoorOutOfBounds(i, j);
        return this.tiles[i][j];
    }

    public int size() {
        return this.size;
    }

    public Iterable<WorldState> neighbors() {

    }

    public int hamming(int[][] potential) {
        int total = 0;
        for (int row = 0; row < this.size; row += 1) {
            for (int col = 0; col < this.size; col += 1) {
                int expected = transformCoor(col, row);
                int actual = potential[row][col];
                if (expected != actual) {
                    total += 1;
                }
            }
        }
        return total;
    }

    public int manhattan(int[][] potential) {
        int total = 0;
        for (int row = 0; row < this.size; row += 1) {
            for (int col = 0; col < this.size; col += 1) {
                int coordinate = potential[row][col];
                int targetCol = this.transformCol(coordinate);
                int targetRow = this.transformRow(coordinate);
                total = total + Math.abs(col - targetCol);
                total = total + Math.abs(row - targetRow);
            }
        }
        return total;
    }

    private int findBlank(int[][] incoming) {
        for (int i = 0; i < this.size; i += 1) {
            for (int ii = 0; ii < this.size; ii += 1) {
                if (this.tiles[i][ii] == 0) {
                    return this.transformCoor(i, ii);
                }
            }
        }
    }

    private int transformRow(int row) {
        return row % this.size;
    }

    private int transformCol(int col) {
        return col / this.size;
    }

    private int transformCoor(int col, int row) {
        return col + (row * this.size);
    }

    public int estimatedDistanceToGoal() {
    }

    public boolean equals(Object y) {
    }

    /** Returns the string representation of the board. 
      * Uncomment this method. */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i,j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

    private void checkCoorOutOfBounds(int i, int j) {
        if (i < 0 || i >= this.size || j < 0 || j >= this.size) {
            throw new IndexOutOfBoundsException(
                "Coordinate violates parameters; either i or j is less than 0 or greater than max size."
            );
        }
    }
}
