import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    private final Picture picture;
    private int width;
    private int height;
    private boolean transposed;
    private Color[][] colors;

    public SeamCarver(Picture picture) {
        this.picture = picture;
        this.width = picture.width();
        this.height = picture.height();
        this.colors = new Color[this.height][this.width];
        this.transposed = false;
        for (int row = 0; row < this.height; row += 1) {
            for (int col = 0; col < this.width; col += 1) {
                colors[row][col] = picture.get(col, row);
            }
        }
    }

    public Picture picture() {
        Picture res = new Picture(this.width, this.height);
        for (int row = 0; row < this.height; row += 1) {
            for (int col = 0; col < this.width; col += 1) {
                res.set(col, row, colors[row][col]);
            }
        }
        return res;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public double energy(int x, int y) {
        // following values assume origin is upper left as 0,0
        Color left = this.fetchColor(x - 1, y);
        Color right = this.fetchColor(x + 1, y);
        Color up = this.fetchColor(x, y - 1);
        Color down = this.fetchColor(x, y + 1);
        double redX = Math.pow(left.getRed() - right.getRed(), 2);
        double greenX = Math.pow(left.getGreen() - right.getGreen(), 2);
        double blueX = Math.pow(left.getBlue() - right.getBlue(), 2);
        double redY = Math.pow(up.getRed() - down.getRed(), 2);
        double greenY = Math.pow(up.getGreen() - down.getGreen(), 2);
        double blueY = Math.pow(up.getBlue() - down.getBlue(), 2);
        return redX + greenX + blueX + redY + blueY + greenY;
    }

    private Color fetchColor(int x, int y) {
        int col = x >= this.width ? x % this.width : x < 0 ? this.width - 1 : x;
        int row = y >= this.height ? y % this.height : y < 0 ? this.height - 1 : y;
        return this.colors[row][col];
    }

    public int[] findHorizontalSeam() {
        this.transposeColors();
        int[] seam = this.findVerticalSeam();
        this.transposeColors();
        return seam;
    }

    private void transposeColors() {
        if (!transposed) {
            this.swapDimensions();
            Color[][] flip = new Color[this.height][this.width];
            for (int row = 0; row < this.height; row += 1) {
                for (int col = 0; col < this.width; col += 1) {
                    flip[row][col] = this.colors[col][row];
                }
            }
            this.setColors(flip);
            this.flipTransposed();
        } else {
            this.swapDimensions();
            Color[][] flip = new Color[this.height][this.width];
            for (int row = 0; row < this.height; row += 1) {
                for (int col = 0; col < this.width; col += 1) {
                    flip[row][col] = this.colors[col][row];
                }
            }
            this.setColors(flip);
            this.flipTransposed();
        }
    }

    private void setColors(Color[][] colours) {
        this.colors = colours;
    }

    private void swapDimensions() {
        int hold = this.height;
        this.height = this.width;
        this.width = hold;
    }

    private void flipTransposed() {
        this.transposed = !this.transposed;
    }

    public int[] findVerticalSeam() {
        double[][] energies = new double[this.height][this.width];

        for (int row = 0; row < this.height; row += 1) {
            for (int col = 0; col < this.width; col += 1) {
                energies[row][col] = this.energy(col, row);
            }
        }

        double[][] relaxed = this.relaxEdges(energies);

        int[] res = new int[this.height];
        double resEnergy = Double.POSITIVE_INFINITY;

        // bottom-up
        for (int col = 0; col < this.width; col += 1) {
            // no need to check path if end energy is worse than what's found
            double pathFinalEnergy = relaxed[this.height - 1][col];

            if (pathFinalEnergy < resEnergy) {
                resEnergy = pathFinalEnergy;
                int colPointer = col;

                for (int row = this.height - 1; row >= 0; row -= 1) {
                    if (row == this.height - 1) {
                        res[row] = colPointer;
                        continue;
                    }

                    int delta = 0;
                    double tri = Double.POSITIVE_INFINITY;
                    if (colPointer - 1 > -1) {
                        if (relaxed[row][colPointer - 1] < tri) {
                            tri = relaxed[row][colPointer - 1];
                            res[row] = colPointer - 1;
                            delta = -1;
                        }
                    }
                    if (colPointer + 1 < this.width) {
                        if (relaxed[row][colPointer + 1] < tri) {
                            tri = relaxed[row][colPointer + 1];
                            res[row] = colPointer + 1;
                            delta = 1;
                        }
                    }
                    if (relaxed[row][colPointer] < tri) {
                        res[row] = colPointer;
                        delta = 0;
                    }
                    colPointer += delta;
                }
            }
        }

        return res;
    }

    private double[][] relaxEdges(double[][] energies) {
        double[][] relaxed = new double[this.height][this.width];

        for (int row = 0; row < this.height; row += 1) {
            for (int col = 0; col < this.width; col += 1) {
                relaxed[row][col] = energies[row][col];
                if (row == 0) {
                    relaxed[row][col] = energies[row][col];
                } else {
                    relaxed[row][col] = Double.POSITIVE_INFINITY;
                }
            }
        }

        for (int row = 1; row < this.height; row += 1) {
            for (int col = 0; col < this.width; col += 1) {

                if (col - 1 >= 0) {
                    double energy = relaxed[row - 1][col - 1] + energies[row][col];
                    if (energy < relaxed[row][col]) {
                        relaxed[row][col] = energy;
                    }
                }

                if (col + 1 < this.width) {
                    double energy = relaxed[row - 1][col + 1] + energies[row][col];
                    if (energy < relaxed[row][col]) {
                        relaxed[row][col] = energy;
                    }
                }

                double energy = relaxed[row - 1][col] + energies[row][col];
                if (energy < relaxed[row][col]) {
                    relaxed[row][col] = energy;
                }
            }
        }

        return relaxed;
    }

    private void checkContiguousPath(int[] seam) {
        for (int i = 1; i < seam.length; i += 1) {
            int store = Math.abs(seam[i - 1] - seam[i]);
            if (store < -1 || store > 1) {
                throw new IllegalArgumentException("Seam is not contiguous");
            }
        }
    }

    public void removeHorizontalSeam(int[] seam) {
        if (seam.length != this.width) {
            throw new IllegalArgumentException("Seam is not correct length");
        }
        this.checkContiguousPath(seam);
        SeamRemover.removeHorizontalSeam(this.picture, seam);
    }

    public void removeVerticalSeam(int[] seam) {
        if (seam.length != this.height) {
            throw new IllegalArgumentException("Seam is not correct length");
        }
        this.checkContiguousPath(seam);
        SeamRemover.removeVerticalSeam(this.picture, seam);
    }
}
