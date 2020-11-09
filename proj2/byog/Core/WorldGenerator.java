package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayDeque;
import java.util.Deque;

public class WorldGenerator {
    public enum RectType {
        ROOM,
        CORRIDOR
    }
    
    private final TETile[][] grid;
    private final Deque<Room> rooms;
    private int roomArea;

    public WorldGenerator(int worldWidth, int worldHeight) {
        this.grid = new TETile[worldWidth][worldHeight];

        // generate empty world
        for (int x = 0; x < worldWidth; x += 1) {
            for (int y = 0; y < worldHeight; y += 1) {
                grid[x][y] = Tileset.NOTHING;
            }
        }

        // generate empty deque
        this.rooms = new ArrayDeque<>();
    }

    private int height() {
        return this.grid[0].length;
    }

    private int width() {
        return this.grid.length;
    }

    private int worldSize() {
        return this.width() * this.height();
    }

    private boolean hitCapacity() {
        return Math.floorDiv(this.roomArea, this.worldSize()) >= 80;
    }

    private Room generateRoom() {
        RectType rectType = SeedGenerator.generateCorridor() ? RectType.CORRIDOR : RectType.ROOM;
        if (rectType == RectType.CORRIDOR) {
            return new Room(1, SeedGenerator.genDimension(this.height()), rectType);
        }
        return new Room(
            SeedGenerator.genDimension(this.width()),
            SeedGenerator.genDimension(this.height()),
            rectType
        );
    }

    private void generateWorld() {
        // generate room tiles
        while (!hitCapacity()) {
            // new room
            Room newRoom = this.generateRoom();

            // loop conditions - while placement is not valid, try 10 times
            boolean placement = false;
            int tryCount = 0;

            // searching where to place room...
            while (!placement && tryCount < 10) {
                Coordinate randomOrigin = this.randomRoomOrigin();
                placement = this.checkPlacement(randomOrigin, newRoom);
                if (placement) {
                    newRoom.setOrigin(randomOrigin);
                }
                tryCount += 1;
            }

            // could not place room, make a new room and try again
            if (!placement) {
                continue;
            }

            // could place room, register rooms tiles, add it to rooms queue, increase roomArea
            this.addRoomArea(newRoom);
            this.registerRoom(newRoom);
            rooms.add(newRoom);
        }

        // surround rooms from rooms queue with wall tiles
        this.rooms.forEach(room -> {

        });
    }

    private void addRoomArea(Room room) {
        this.roomArea += room.roomSize();
    }

    private boolean checkPlacement(Coordinate origin, Room room) {
        boolean valid = true;
        for (int x = origin.getX(); x < room.getWidth(); x += 1) {
            for (int y = origin.getY(); y < room.getHeight(); y += 1) {
                if (this.grid[x][y].character() == '#' || this.grid[x][y].character() == '·') {
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }

    private void registerRoom(Room room) {
        for (int x = room.getOrigin().getX(); x < room.getWidth(); x += 1) {
            for (int y = room.getOrigin().getY(); y < room.getHeight(); y += 1) {
                this.grid[x][y] = Tileset.FLOOR;
            }
        }
    }

    private Coordinate randomRoomOrigin() {
        return new Coordinate(
            SeedGenerator.genOrigin(this.width()),
            SeedGenerator.genOrigin(this.height())
        );
    }
}
