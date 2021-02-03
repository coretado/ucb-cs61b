package hw4.puzzle;

import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;

public class Board implements WorldState {
    private final int[][] tiles;
    private final int size;
    private final int blankRow;
    private final int blankCol;
    private final int manhattan;
    private final int hamming;
    private int hashCode;

    public Board(int[][] tiles) {
        this.size = tiles.length;
        this.tiles = new int[this.size][this.size];
        for (int row = 0; row < this.size; row += 1) {
            for (int col = 0; col < this.size; col += 1) {
                this.tiles[row][col] = tiles[row][col];
            }
        }
        int blank = this.findBlank();
        this.blankCol = transformCol(blank);
        this.blankRow = transformRow(blank);
        this.manhattan = this.findManhattan();
        this.hamming = this.findHamming();
    }

    public int tileAt(int i, int j) {
        this.checkCoorOutOfBounds(i, j);
        return this.tiles[i][j];
    }

    public int size() {
        return this.size;
    }

    public Iterable<WorldState> neighbors() {
        Queue<WorldState> worldStateNeighbors = new Queue<>();
        int up = this.isOutOfBounds(
            this.blankRow - 1, this.blankCol)
                ? -1
                : this.transformCoor(this.blankRow - 1, this.blankCol);
        int right = this.isOutOfBounds(
                    this.blankRow, this.blankCol + 1)
                    ? -1
                    : this.transformCoor(this.blankRow, this.blankCol + 1);
        int down = this.isOutOfBounds(
                this.blankRow + 1, this.blankCol)
                    ? -1
                    : this.transformCoor(this.blankRow + 1, this.blankCol);
        int left = this.isOutOfBounds(
                    this.blankRow, this.blankCol - 1)
                    ? -1
                    : this.transformCoor(this.blankRow, this.blankCol - 1);
        if (up != -1) {
            worldStateNeighbors.enqueue(new Board(this.createNeighbor(up)));
        }
        if (right != -1) {
            worldStateNeighbors.enqueue(new Board(this.createNeighbor(right)));
        }
        if (down != -1) {
            worldStateNeighbors.enqueue(new Board(this.createNeighbor(down)));
        }
        if (left != -1) {
            worldStateNeighbors.enqueue(new Board(this.createNeighbor(left)));
        }
        return worldStateNeighbors;
    }

    public int hamming() {
        return this.hamming;
    }

    public int findHamming() {
        int total = 0;
        for (int row = 0; row < this.size; row += 1) {
            for (int col = 0; col < this.size; col += 1) {
                int expected = transformCoor(row, col);
                int actual = this.tiles[row][col];
                if (expected != actual) {
                    total += 1;
                }
            }
        }
        return total;
    }

    public int manhattan() {
        return this.manhattan;
    }

    public int findManhattan() {
        int total = 0;
        for (int row = 0; row < this.size; row += 1) {
            for (int col = 0; col < this.size; col += 1) {
                int coordinate = this.tiles[row][col] - 1;
                if (coordinate == -1) {
                    continue;
                }
                int targetCol = this.transformCol(coordinate);
                int targetRow = this.transformRow(coordinate);
                total += Math.abs(col - targetCol);
                total += Math.abs(row - targetRow);
            }
        }
        return total;
    }

    private int findBlank() {
        int blank = 0;
        for (int row = 0; row < this.size; row += 1) {
            for (int col = 0; col < this.size; col += 1) {
                if (this.tiles[row][col] == 0) {
                    blank = this.transformCoor(row, col);
                    break;
                }
            }
        }
        return blank;
    }

    private int transformRow(int row) {
        return row / this.size;
    }

    private int transformCol(int col) {
        return col % this.size;
    }

    private int transformCoor(int row, int col) {
        return col + (row * this.size);
    }

    private void swap(int[][] O, int swapRow, int swapCol) {
        int oldValue = O[swapRow][swapCol];
        O[this.blankRow][this.blankCol] = oldValue;
        O[swapRow][swapCol] = 0;
    }

    private int[][] createNeighbor(int coor) {
        int swapBlankRow = this.transformRow(coor);
        int swapBlankCol = this.transformCol(coor);
        int[][] neighbor = new int[this.size][this.size];
        for (int row = 0; row < this.size; row += 1) {
            System.arraycopy(this.tiles[row], 0, neighbor[row], 0, this.size);
        }
        this.swap(neighbor, swapBlankRow, swapBlankCol);
        return neighbor;
    }

    public int estimatedDistanceToGoal() {
        return this.manhattan();
    }

    @Override
    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }
        if (!(y instanceof Board)) {
            return false;
        }
        Board that = (Board) y;
        return this.size == that.size
            && this.tilesMatch(this.tiles, that.tiles)
            && this.blankRow == that.blankRow
            && this.blankCol == that.blankCol;
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = Arrays.deepHashCode(this.tiles);
            result = 31 * result + Integer.hashCode(this.size);
            result = 31 * result + Integer.hashCode(this.blankRow);
            result = 31 * result + Integer.hashCode(this.blankCol);
            result = 31 * result + Integer.hashCode(this.manhattan);
            result = 31 * result + Integer.hashCode(this.hamming);
            this.hashCode = result;
        }
        return result;
    }

    /** Returns the string representation of the board. 
      * Uncomment this method. */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

    private void checkCoorOutOfBounds(int i, int j) {
        if (i < 0 || i >= this.size || j < 0 || j >= this.size) {
            throw new IndexOutOfBoundsException(
                "Coordinate violates parameters; i or j is less than 0 or greater than max size."
            );
        }
    }

    private boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= this.size || col < 0 || col >= this.size;
    }

    private boolean tilesMatch(int[][] me, int[][] you) {
        for (int row = 0; row < this.size; row += 1) {
            for (int col = 0; col < this.size; col += 1) {
                if (me[row][col] != you[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }
}
