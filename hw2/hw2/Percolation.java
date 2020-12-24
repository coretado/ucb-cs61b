package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final WeightedQuickUnionUF model;
    private final WeightedQuickUnionUF backtrack;
    private final boolean[] spaces;
    private final int dimension;
    private final int squared;
    private int openSites = 0;

    public Percolation(int N) {
        // check for illegal constructor parameter
        this.checkIfInputOutOfBounds(N);

        // making a square number for grid
        this.squared = N * N;

        // helper variable for mapping
        this.dimension = N;

        // create the WQUUF objects
        this.model = new WeightedQuickUnionUF(squared + 2);
        this.backtrack = new WeightedQuickUnionUF(squared + 1);

        // create internal representation
        this.spaces = new boolean[squared + 2];

        // create source and sink
        this.spaces[squared] = true;
        this.spaces[squared + 1] = true;

        // connect top row and bottom row to sink
        for (int i = 0; i < N; i += 1) {
            this.model.union(i, squared);
            this.backtrack.union(i, squared);
            this.model.union(this.mapRowAndCol(N - 1, i), squared + 1);
        }
    }

    /**
     * Open the site (row, col) if it is not open
     * @param row - the row coordinate for a 2d grid
     * @param col - the col coordinate for a 2d grid
     */
    public void open(int row, int col) {
        // check for illegal function parameters
        this.checkIfInputOutOfBounds(row, col);

        // if we have not yet opened this site, mark it as open and increment open sites
        if (!this.isOpen(row, col)) {
            this.spaces[this.mapRowAndCol(row, col)] = true;
            this.openSites += 1;

            this.checkForAdjacentConnection(row, col, row - 1, col);
            this.checkForAdjacentConnection(row, col, row, col + 1);
            this.checkForAdjacentConnection(row, col, row + 1, col);
            this.checkForAdjacentConnection(row, col, row, col - 1);
        }
    }

    /**
     * Is the site (row, col) open?
     * @param row - the row coordinate for a 2d grid
     * @param col - the col coordinate for a 2d grid
     * @return indexIsTrue
     */
    public boolean isOpen(int row, int col) {
        this.checkIfInputOutOfBounds(row, col);

        return this.spaces[this.mapRowAndCol(row, col)];
    }

    /**
     * Is the site (row, col) full?
     * @param row - the row coordinate for a 2d grid
     * @param col - the col coordinate for a 2d grid
     * @return indexIsConnected
     */
    public boolean isFull(int row, int col) {
        this.checkIfInputOutOfBounds(row, col);
        int coor = this.mapRowAndCol(row, col);

        return this.spaces[this.mapRowAndCol(row, col)]
            && this.backtrack.connected(this.squared, coor)
            && this.model.connected(this.squared, coor);
    }

    /**
     * Number of open sites
     * @return numOpenSites
     */
    public int numberOfOpenSites() {
        return this.openSites;
    }

    /**
     * Returns whether or not there is a valid connection from the "source" to the "sink"
     * @return systemPercolates
     */
    public boolean percolates() {
        return this.model.connected(this.squared, this.squared + 1);
    }

    private int mapRowAndCol(int row, int col) {
        return (row * this.dimension) + col;
    }
    
    private void checkIfInputOutOfBounds(int N) {
        if (N <= 0) {
            throw new IndexOutOfBoundsException("Can not have size N be less than or equal to 0");
        }
    }

    private void checkIfInputOutOfBounds(int row, int col) {
        if (row < 0 || row >= this.dimension || col < 0 || col >= this.dimension) {
            throw new IndexOutOfBoundsException(
                "Can not have row or column less than 0 or greater than input dimension"
            );
        }
    }

    private void checkForAdjacentConnection(
            int originRow,
            int originCol,
            int targetRow,
            int targetCol
    ) {
        if (
            targetRow < 0
                || targetRow >= this.dimension
                || targetCol < 0
                || targetCol >= this.dimension
        ) {
            return;
        }

        if (this.isOpen(targetRow, targetCol)) {
            int coorOne = this.mapRowAndCol(originRow, originCol);
            int coorTwo = this.mapRowAndCol(targetRow, targetCol);
            this.model.union(coorOne, coorTwo);
            this.backtrack.union(coorOne, coorTwo);
        }
    }

    public static void main(String[] args) {
    }
}
