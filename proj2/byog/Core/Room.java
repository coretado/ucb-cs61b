package byog.Core;
import byog.Core.WorldGenerator.RectType;

/**
 * This class will either return a rectangular room or a single width hallway,
 * The values used for the random generation of the dimensions of a Room can be found
 * in SeedGenerator.java.
 */
public class Room {
    private final int width;
    private final int height;
    private int doorKey;
    private final RectType rectType;
    public Coordinate origin;
    // 0 = up, 1 = right, 2 = down, 3 = left
    private Coordinate[] doors;

    public Room(int width, int height, RectType rectType) {
        this.width = width + 2;
        this.height = height + 2;
        this.rectType = rectType;
    }

    public void setOrigin(Coordinate origin) {
        this.origin = origin;
    }

    public Coordinate getOrigin() {
        return this.origin;
    }

    public RectType getRectType() {
        return this.rectType;
    }

    public int roomSize() {
        return width * height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setDoors(Coordinate[] doors) {
        this.doors = doors;
    }

    public Coordinate[] getDoors() {
        return this.doors;
    }

    public void setDoorKey(int key) {
        this.doorKey = key;
    }

    public Coordinate getDoorCoordinate() {
        return this.doors[this.doorKey];
    }
}
