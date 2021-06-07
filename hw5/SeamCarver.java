import edu.princeton.cs.algs4.Picture;

import java.awt.*;

public class SeamCarver {
    private Picture picture;
    private int width;
    private int height;

    public SeamCarver(Picture picture) {
        this.picture = picture;
    }

    public Picture picture() {
        return this.picture;
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
        int col = x > this.width ? x % this.width : x < 0 ? this.width - 1 : x;
        int row = y > this.height ? y % this.height : y < 0 ? this.height - 1 : y;
        return this.picture.get(col, row);
    }

    public int[] findHorizontalSeam() {
        int[] todo = new int[1];
        return todo;
    }

    public int[] findVerticalSeam() {
        int[] todo = new int[1];
        return todo;
    }

    public void removeHorizontalSeam(int[] seam) {
        SeamRemover.removeHorizontalSeam(this.picture, seam);
    }

    public void removeVerticalSeam(int[] seam) {
        SeamRemover.removeVerticalSeam(this.picture, seam);
    }
}
