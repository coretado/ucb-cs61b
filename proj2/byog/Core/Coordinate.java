package byog.Core;

public class Coordinate implements CoordinateBase {
    private final int col;
    private final int row;

    public Coordinate(int col, int row) {
        this.col = col;
        this.row = row;
    }

    @Override
    public int getCol() {
        return this.col;
    }

    @Override
    public int getRow() {
        return this.row;
    }
}
