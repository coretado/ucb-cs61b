package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    private double[] stats;
    private double trials;

    public PercolationStats(int N, int T, PercolationFactory pf) {
        // initialize stats array to collect data
        this.stats = new double[N];
        this.trials = T;

        // Attempt percolation for T number of Percolation objects;
        for (int i = 0; i < T; i += 1) {
            Percolation p = pf.make(N);

            // while system does not percolate, randomly open a row and column
            while (true) {
                p.open(StdRandom.uniform(N), StdRandom.uniform(N));
                if (p.percolates()) {
                    break;
                }
            }
            stats[i] = (double) p.numberOfOpenSites() / (N * N);
        }
    }

    /**
     * Returns a sample mean of the percolation threshold for T trials
     * @return
     */
    public double mean() {
        return StdStats.mean(this.stats);
    }

    /**
     * Returns the sample standard deviation of percolation threshold for T trials
     * @return
     */
    public double stddev() {
        return StdStats.stddev(this.stats);
    }

    /**
     * Returns the low endpoint of 95% confidence interval for T trials
     * @return
     */
    public double confidenceLow() {
        return this.mean() - ((1.96 * this.stddev()) / (Math.sqrt(this.trials)));
    }

    /**
     * Returns the high endpoint of 95% confidence interval for T trials
     * @return
     */
    public double confidenceHigh() {
        return this.mean() + ((1.96 * this.stddev()) / (Math.sqrt(this.trials)));
    }
}
