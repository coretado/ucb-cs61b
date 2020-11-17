package byog.Core;

public class Room {
    private final int width;
    private final int height;
    private Coordinate origin;
    private Coordinate[] doors;
    private final boolean[] doorTaken = new boolean[]{false, false, false, false};

    public Room(int width, int height) {
        this.width = width + 2;
        this.height = height + 2;
    }

    public void setOrigin(Coordinate origin) {
        this.origin = origin;
    }

    public int getOriginCol() {
        return this.origin.getCol();
    }

    public int getOriginRow() {
        return this.origin.getRow();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getIndexWidth() {
        return this.width - 1;
    }

    public int getIndexHeight() {
        return this.height - 1;
    }

    public int getInnerWidth() {
        return this.width - 2;
    }

    public int getInnerHeight() {
        return this.height - 2;
    }

    public int getArea() {
        return this.width * this.height;
    }

    public void setDoors(Coordinate[] doors) {
        this.doors = doors;
    }

    public Coordinate getDoorCoordinate(int key) {
        return this.doors[key];
    }

    public void setDoorTaken(int key) {
        this.doorTaken[key] = true;
    }

    public boolean getDoorTaken(int key) {
        return this.doorTaken[key];
    }
}
