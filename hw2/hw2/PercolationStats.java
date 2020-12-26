package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    private final double calculcatedMean;
    private final double calculatedStdDev;
    private final double calculatedConfidenceLow;
    private final double calculatedConfidenceHigh;

    public PercolationStats(int N, int T, PercolationFactory pf) {
        // check for out of bounds
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException("Invalid inputs N or T");
        }

        // initialize stats array to collect data
        double[] stats = new double[T];

        // Attempt percolation for T number of Percolation objects;
        for (int i = 0; i < T; i += 1) {
            Percolation p = pf.make(N);

            // while system does not percolate, randomly open a row and column
            while (!p.percolates()) {
                int row = StdRandom.uniform(N);
                int col = StdRandom.uniform(N);
                p.open(row, col);
            }

            stats[i] = (double) p.numberOfOpenSites() / (N * N);
        }

        this.calculcatedMean = StdStats.mean(stats);
        this.calculatedStdDev = StdStats.stddev(stats);

        double stdDevPart = 1.96 * this.calculatedStdDev;
        double sqrT = Math.sqrt(T);
        this.calculatedConfidenceLow = this.calculcatedMean - (stdDevPart / sqrT);
        this.calculatedConfidenceHigh = this.calculcatedMean + (stdDevPart / sqrT);
    }

    /**
     * Returns a sample mean of the percolation threshold for T trials
     * @return - sample mean for T trials
     */
    public double mean() {
        return this.calculcatedMean;
    }

    /**
     * Returns the sample standard deviation of percolation threshold for T trials
     * @return - sample standard deviation for T trials
     */
    public double stddev() {
        return this.calculatedStdDev;
    }

    /**
     * Returns the low endpoint of 95% confidence interval for T trials
     * @return - lower 95% confidence interval value for T trials
     */
    public double confidenceLow() {
        return this.calculatedConfidenceLow;
    }

    /**
     * Returns the high endpoint of 95% confidence interval for T trials
     * @return - higher 95% confidence interval for T trials
     */
    public double confidenceHigh() {
        return this.calculatedConfidenceHigh;
    }
}
