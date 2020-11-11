package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class WorldGenerator {
    private final int ROOM_WIDTH = 8;
    private final int ROOM_HEIGHT = 8;

    public enum RectType {
        ROOM,
        CORRIDOR
    }

    private final Random seedGen;
    private final TETile[][] grid;
    private final Deque<Room> rooms;
    private int roomArea;

    public WorldGenerator(int worldWidth, int worldHeight, long seed) {
        this.grid = new TETile[worldWidth][worldHeight];
        this.seedGen = new Random(seed);

        // generate empty world
        for (int x = 0; x < worldWidth; x += 1) {
            for (int y = 0; y < worldHeight; y += 1) {
                grid[x][y] = Tileset.NOTHING;
            }
        }

        // generate empty deque
        this.rooms = new ArrayDeque<>();
    }

    public TETile[][] getGrid() {
        return this.grid;
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

    private int genDimension(int value) {
        return Math.max(3, this.seedGen.nextInt(value));
    }

    private int genOrigin(int value) {
        return this.seedGen.nextInt(value);
    }

    private boolean generateCorridor() {
        return this.seedGen.nextInt(100) < 30;
    }

    private boolean coinflip() {
        return this.seedGen.nextInt(1) == 0;
    }

    private int boundByOneAndInput(int input) {
        return 1 + this.seedGen.nextInt(input);
    }

    /** returns a room with no chance of it being a corridor */
    private Room generateCore() {
        Room core = new Room(
            this.genDimension(ROOM_WIDTH),
            this.genDimension(ROOM_HEIGHT),
            RectType.ROOM
        );
        this.generateDoors(core);
        return core;
    }

    private Room generateRoom() {
        RectType rectType = this.generateCorridor() ? RectType.CORRIDOR : RectType.ROOM;
        if (rectType == RectType.CORRIDOR) {
            return this.coinflip()
                ? new Room(1, this.genDimension(ROOM_HEIGHT), rectType)
                : new Room(this.genDimension(ROOM_WIDTH), 1, rectType);
        }

        Room construct = new Room(
            this.genDimension(ROOM_WIDTH),
            this.genDimension(ROOM_HEIGHT),
            rectType
        );
        this.generateDoors(construct);

        return construct;
    }

    public void generateWorld() {
        // generate dungeon core
        Room first = this.generateCore();

        boolean dungeonCorePlaced = false;

        while (!dungeonCorePlaced) {
            Coordinate randomOrigin = this.randomRoomOrigin();
            dungeonCorePlaced = this.checkPlacement(randomOrigin, first);
            if (dungeonCorePlaced) {
                first.setOrigin(randomOrigin);
            }
        }

        this.registerRoom(first);

        // generate add on rooms and corridors
        while (!hitCapacity()) {
            // new room
            Room potentialRoom = this.generateRoom();

            // iterate through all rooms and see if you can place the new room

            // will naturally terminate the loop if there is no valid room connection
            for (Room toCheck : this.rooms) {
                int doorKey = this.checkConnectRoom(potentialRoom, toCheck);
                if (doorKey != -1) {
                    potentialRoom.setDoorKey(doorKey);
                    potentialRoom.setOrigin(mapOriginFromPotentialDoor(doorKey, potentialRoom, toCheck));
                    this.registerRoom(potentialRoom);
                    break;
                }
            }
        }

        // surround rooms from rooms queue with wall tiles
        this.rooms.forEach(this::addWallToRoom);
    }

    /** will check all of the doors of some attachee room and return the doorKey of a valid connection if it exists */
    private int checkConnectRoom(Room attacher, Room attachee) {
        int key = -1;
        for (int i = 0; i < 4; i += 1) {
            if (!this.invalidConnection(i, attacher, attachee)) {
                key = i;
                break;
            }
        }
        return key;
    }

    /** helper function for checkConnectRoom() that checks whether or not two rooms have a valid connection */
    private boolean invalidConnection(int doorKey, Room attacher, Room attachee) {
        Coordinate mappedOrigin = this.mapOriginFromPotentialDoor(doorKey, attacher, attachee);
        if (mappedOrigin == null) {
            return true;
        }
        return this.invalidPlacement(attachee, mappedOrigin);
    }

    /** will take the coordinates of a shifted door and subtract  */
    private Coordinate mapOriginFromPotentialDoor(int doorKey, Room attacher, Room attachee) {
        Coordinate base = attachee.getDoor(doorKey);
        Coordinate shifted;
        switch (doorKey) {
            case 0:
                shifted = new Coordinate(base.getX(), base.getY() + 1);
                break;
            case 1:
                shifted = new Coordinate(base.getX() + 1, base.getY());
                break;
            case 2:
                shifted = new Coordinate(base.getX(), base.getY() - 1);
                break;
            default:
                shifted = new Coordinate(base.getX() - 1, base.getY());
                break;
        }
        Coordinate actualDoorCoordinate = attacher.getDoor(doorKey);
        Coordinate mappedOrigin = new Coordinate(
        shifted.getX() - actualDoorCoordinate.getX(), shifted.getY() - actualDoorCoordinate.getY());
        if (this.coordinateOutOfBounds(mappedOrigin.getX(), mappedOrigin.getY())) {
            return null;
        }
        return mappedOrigin;
    }

    /** checks if some coordinate (c0) : roomBottomLeft <= c0 <= roomTopRight */
    private boolean invalidPlacement(Room originRoom, Coordinate potentialPlacement) {
        if (this.coordinateOutOfBounds(potentialPlacement.getX(), potentialPlacement.getY())) {
            return true;
        }
        int x0 = originRoom.getOrigin().getX();
        int x1 = x0 + originRoom.getWidth();
        int y0 = originRoom.getOrigin().getY();
        int y1 = y0 + originRoom.getHeight();
        return (potentialPlacement.getX() >= x0 && potentialPlacement.getX() <= x1) &&
            (potentialPlacement.getY() >= y0 && potentialPlacement.getY() <= y1);
    }

    /** only used when placing the first room (because subsequent rooms are only concerned
     * with attaching to each other) - will check to see if any of the rooms coordinates are out of bounds
     */
    private boolean checkPlacement(Coordinate origin, Room room) {
        boolean valid = true;
        for (int x = origin.getX(); x < room.getWidth(); x += 1) {
            for (int y = origin.getY(); y < room.getHeight(); y += 1) {
                if (this.coordinateOutOfBounds(x, y)) {
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }

    private void registerRoom(Room room) {
        this.roomArea += room.roomSize();
        this.addFloorTilesToRoom(room);
        this.rooms.add(room);
    }

    private void addFloorTilesToRoom(Room room) {
        int actualWidth = room.getWidth() - 2;
        int actualHeight = room.getHeight() - 2;
        for (int x = room.getOrigin().getX() + 1; x < actualWidth; x += 1) {
            for (int y = room.getOrigin().getY() + 1; y < actualHeight; y += 1) {
                this.grid[x][y] = Tileset.FLOOR;
            }
        }
    }

    /** add tiles to the border of the room */
    private void addWallToRoom(Room room) {
        int leftColumn = room.getOrigin().getX();
        int rightColumn = leftColumn + room.getWidth() - 1;
        int bottomRow = room.getOrigin().getY();
        int topRow = bottomRow + room.getHeight() - 1;

        Coordinate door = room.getAssignedDoor();
        this.grid[door.getX()][door.getY()] = Tileset.FLOOR;

        for (int y = room.getOrigin().getY(); y < room.getHeight(); y += 1) {
            checkAndAssignWall(leftColumn, y);
            checkAndAssignWall(rightColumn, y);
        }
        for (int x = room.getOrigin().getX(); x < room.getWidth(); x += 1) {
            checkAndAssignWall(x, bottomRow);
            checkAndAssignWall(x, topRow);
        }
    }

    private void checkAndAssignWall(int x, int y) {
        if (this.coordinateOutOfBounds(x, y) || this.tileAlreadySet(this.grid[x][y].character())) {
            return;
        }
        this.grid[x][y] = Tileset.WALL;
    }

    private void generateDoors(Room room) {
        Coordinate[] doors = new Coordinate[4];
        doors[0] = new Coordinate(this.boundByOneAndInput(room.getWidth() - 2), room.getHeight());
        doors[1] = new Coordinate(room.getWidth() - 1, this.boundByOneAndInput(room.getHeight() - 2));
        doors[2] = new Coordinate(this.boundByOneAndInput(room.getWidth() - 2), 0);
        doors[3] = new Coordinate(0, this.boundByOneAndInput(room.getHeight() - 2));
        room.setDoors(doors);
    }

    private Coordinate randomRoomOrigin() {
        return new Coordinate(
            this.genOrigin(this.width()),
            this.genOrigin(this.height())
        );
    }

    private boolean coordinateOutOfBounds(int x, int y) {
        return (x < 0 || x >= this.width() || y < 0 || y >= this.height());
    }

    private boolean tileAlreadySet(char tileChar) {
        return tileChar == '·' || tileChar == '#';
    }
}
