package byog.Core;

public class PlayerLocation implements CoordinateBase {
    private int row;
    private int col;

    public PlayerLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
