package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    private final double[] stats;
    private final double trials;

    public PercolationStats(int N, int T, PercolationFactory pf) {
        // initialize stats array to collect data
        this.stats = new double[N];
        this.trials = T;

        // Attempt percolation for T number of Percolation objects;
        for (int i = 0; i < T; i += 1) {
            Percolation p = pf.make(N);

            // while system does not percolate, randomly open a row and column
            do {
                int row = StdRandom.uniform(N);
                int col = StdRandom.uniform(N);
                if (!p.isOpen(row, col)) {
                    p.open(row, col);
                }
            } while (!p.percolates());
            stats[i] = (double) p.numberOfOpenSites() / (N * N);
        }
    }

    /**
     * Returns a sample mean of the percolation threshold for T trials
     * @return - sample mean for T trials
     */
    public double mean() {
        return StdStats.mean(this.stats);
    }

    /**
     * Returns the sample standard deviation of percolation threshold for T trials
     * @return - sample standard deviation for T trials
     */
    public double stddev() {
        return StdStats.stddev(this.stats);
    }

    /**
     * Returns the low endpoint of 95% confidence interval for T trials
     * @return - lower 95% confidence interval value for T trials
     */
    public double confidenceLow() {
        return this.mean() - ((1.96 * this.stddev()) / (Math.sqrt(this.trials)));
    }

    /**
     * Returns the high endpoint of 95% confidence interval for T trials
     * @return - higher 95% confidence interval for T trials
     */
    public double confidenceHigh() {
        return this.mean() + ((1.96 * this.stddev()) / (Math.sqrt(this.trials)));
    }
}
