package byog.Core;
import byog.Core.WorldGenerator.RectType;

public class Room {
    private final int cols;
    private final int rows;
    private int doorKey;
    public Coordinate origin;
    // for debugging purposes
    private final RectType rectType;
    // 0 = top, 1 = right, 2 = down, 3 = left
    private Coordinate[] doors;
    private final boolean[] doorWasAssignedRoom;

    public Room(int cols, int rows, RectType rectType) {
        this.cols = cols + 2;
        this.rows = rows + 2;
        this.rectType = rectType;
        this.doorWasAssignedRoom = new boolean[]{false, false, false, false};
    }

    public void setOrigin(Coordinate origin) {
        this.origin = origin;
    }

    public Coordinate getOrigin() {
        return this.origin;
    }

    public int roomSize() {
        return cols * rows;
    }

    public int getCols() {
        return this.cols;
    }

    public int getAdjustedWidth() {
        return this.cols - 1;
    }

    public int getInnerCols() {
        return this.cols - 2;
    }

    public int getRows() {
        return this.rows;
    }

    public int getAdjustedHeight() {
        return this.rows - 1;
    }

    public int getInnerRows() {
        return this.rows - 2;
    }

    public void setDoors(Coordinate[] doors) {
        this.doors = doors;
    }

    public Coordinate getDoor(int doorKey) {
        return this.doors[doorKey];
    }

    public void assignDoorPositionWithRoom(int i) {
        this.doorWasAssignedRoom[i] = true;
    }

    public boolean doorPositionAssigned(int i) {
        return this.doorWasAssignedRoom[i];
    }

}
