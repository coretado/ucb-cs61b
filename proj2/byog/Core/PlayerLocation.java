package byog.Core;

public class PlayerLocation implements CoordinateBase {
    private int col;
    private int row;

    public PlayerLocation() {
    }

    public PlayerLocation(int col, int row) {
        this.col = col;
        this.row = row;
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
