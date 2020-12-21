package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final WeightedQuickUnionUF model;
    private final boolean[] spaces;
    private final int dimension;
    private int openSites = 0;

    public Percolation(int N) {
        // check for illegal constructor parameter
        this.checkIfInputOutOfBounds(N);

        // making a square number for grid
        int squaredInput = N * N;

        // helper variable for mapping
        this.dimension = N;

        // create the WQUUF object
        this.model = new WeightedQuickUnionUF(squaredInput + 2);

        // create internal representation
        this.spaces = new boolean[squaredInput + 2];

        // create source and sink
        this.spaces[0] = true;
        this.spaces[squaredInput + 1] = true;

        // connect top row and bottom row to sink
        for (int i = 0; i < N; i += 1) {
            this.model.union(0, i);
            this.model.union(squaredInput - 1, this.mapRowAndCol(N - 1, i));
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

        // set space to true - "full"
        this.spaces[this.mapRowAndCol(row, col)] = true;

        // check surrounding sites if their open; connecting them if so
        this.checkForAdjacentConnection(row, col, row - 1, col);
        this.checkForAdjacentConnection(row, col, row, col + 1);
        this.checkForAdjacentConnection(row, col, row + 1, col);
        this.checkForAdjacentConnection(row, col, row, col - 1);
        
        // increment number of sites opened up
        this.openSites += 1;
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

        return !this.spaces[this.mapRowAndCol(row, col)];
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
        return this.model.connected(0, (this.dimension * this.dimension) + 1);
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
        if (this.isFull(targetRow, targetCol)) {
            this.model.union(
                this.mapRowAndCol(originRow, originCol),
                    this.mapRowAndCol(targetRow, targetCol)
            );
        }
    }
}
