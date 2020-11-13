package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class WorldGenerator {
    private final int ROOM_WIDTH = 7;
    private final int ROOM_HEIGHT = 7;

    public enum RectType {
        ROOM,
        CORRIDOR
    }

    private final Random seedGen;
    private final TETile[][] grid;
    private final Deque<Room> rooms;
    private int roomArea;

    public WorldGenerator(int rows, int columns, long seed) {
        // row-major order
        this.grid = new TETile[rows][columns];

        // Collection of rooms and Procedural Generator for world map
        this.rooms = new ArrayDeque<>();
        this.seedGen = new Random(seed);

        // generate empty world
        for (int col = 0; col < columns; col += 1) {
            for (int row = 0; row < rows; row += 1) {
                this.grid[col][row] = Tileset.NOTHING;
            }
        }
    }

    /** returns a grid of TETiles representing an ASCII world */
    public TETile[][] getGrid() {
        return this.grid;
    }

    /** ASSUMPTION: world map is generated using a square map e.g. 50 x 50 */
    private int rows() {
        return this.grid.length;
    }

    /** ASSUMPTION: world map is generated using a square map e.g. 50 x 50 */
    private int cols() {
        return this.grid[0].length;
    }

    /** returns a float representation of number of rows multiplied by number of columns in grid */
    private float worldSize() {
        return this.cols() * this.rows();
    }

    /** determines if the total room area generated on the map exceeds some percentage. DEFAULT: 65% */
    private boolean hitCapacity() {
        return (this.roomArea / this.worldSize()) >= 0.65;
    }

    /** Begin Procedural Generation Helpers */

    /**
     * Generate a random dimension number for a rectangular room between a hard coded lower bound and a provided
     * upper bound (should be either the randomly generated width or height of a room)
     */
    private int genDimension(int value) {
        return Math.max(2, this.seedGen.nextInt(value));
    }

    /**
     * Generate a random dimension number for a door placement between 0 and a provided upper bound.
     */
    private int genDoorDimension(int value) {
        return this.seedGen.nextInt(value);
    }

    /**
     * Determine if a corridor will be generated instead of a room based upon a hard coded value.
     * DEFAULT: 30% chance probability.
     */
    private boolean generateCorridor() {
        return this.seedGen.nextInt(100) < 30;
    }

    /**
     * A 50/50 coinflip. Used to determine if a corridor will be horizontal or vertical.
     */
    private boolean generateVerticalColumn() {
        return this.seedGen.nextInt(1) == 0;
    }

    /** End Procedural Generation Helpers */

    /** Begin Room Generation Callers */

    /** Generates a room with no chance of returning a corridor. This is abstracted away so the generate room logic
     *  can be read as declarative statements as much as possible. 4 doors are then randomly generated and snapped
     *  onto the room.
     */
    private Room generateCore() {
        Room core = new Room(this.genDimension(ROOM_WIDTH), this.genDimension(ROOM_HEIGHT), RectType.ROOM);
        this.generateDoors(core);
        return core;
    }

    /** Generates a room with a 30% chance of making a corridor. If a corridor is made, it further has a 50/50
     * chance of being either a vertical or horizontal corridor. 4 doors are then randomly generated and snapped
     * onto the room.
     */
    private Room generateRoom() {
        // determine room type
        RectType rectType = this.generateCorridor() ? RectType.CORRIDOR : RectType.ROOM;

        Room newlyGeneratedRoom;

        if (rectType == RectType.CORRIDOR) {
            newlyGeneratedRoom = this.generateVerticalColumn()
                ? new Room(1, this.genDimension(ROOM_HEIGHT), rectType)
                : new Room(this.genDimension(ROOM_WIDTH), 1, rectType);
        } else {
            newlyGeneratedRoom = new Room(
                    this.genDimension(ROOM_WIDTH),
                    this.genDimension(ROOM_HEIGHT),
                    rectType
            );
        }

        // generate doors
        this.generateDoors(newlyGeneratedRoom);

        return newlyGeneratedRoom;
    }

    public void generateWorld() {
        // generate dungeon core
        Room first = this.generateCore();

        boolean dungeonCorePlaced = false;

        while (!dungeonCorePlaced) {
            Coordinate randomOrigin = new Coordinate(
                this.seedGen.nextInt(this.cols()),
                this.seedGen.nextInt(this.rows()));
            dungeonCorePlaced = this.coreRoomCanBePlacedOntoGrid(randomOrigin, first);
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

    /** End Room Generation Callers */

    /** Begin Room Generation Helpers */

    /** 0 = top, 1 = right, 2 = down, 3 = left */
    private void generateDoors(Room room) {
        Coordinate[] doors = new Coordinate[4];
        doors[0] = new Coordinate(this.genDoorDimension(room.getCols()), room.getAdjustedHeight());
        doors[1] = new Coordinate(room.getAdjustedWidth(), this.genDimension(room.getRows()));
        doors[2] = new Coordinate(this.genDoorDimension(room.getCols()), 0);
        doors[3] = new Coordinate(0, this.genDimension(room.getRows()));
        room.setDoors(doors);
    }

    /** only used when placing the first room (because subsequent rooms are only concerned
     * with attaching to each other) - will check to see if any of the rooms coordinates are out of bounds
     */
    private boolean coreRoomCanBePlacedOntoGrid(Coordinate origin, Room room) {
        int mappedWidth = origin.getX() + room.getCols();
        int mappedHeight = origin.getY() + room.getRows();
        for (int x = origin.getX(); x < mappedWidth; x += 1) {
            for (int y = origin.getY(); y < mappedHeight; y += 1) {
                if (this.coordinateOutOfBounds(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Adds room's area to counted area, paints in floor tiles, and adds room to room queue.
     *  The assumption is that if a room makes it to this stage, it has passed placement checks.
     */
    private void registerRoom(Room room) {
        this.roomArea += room.roomSize();
        this.addFloorTilesToRoom(room);
        this.rooms.add(room);
    }

    /**
     *  Paints in floor tiles in the interior of a room (minus the wall padding)
     */
    private void addFloorTilesToRoom(Room room) {
        int innerRows = room.getInnerRows();
        int innerCols = room.getInnerCols();
        int floorStartCol = room.getOrigin().getX() + 1;
        int floorStartRow = room.getOrigin().getY() + 1;

        for (int col = 0; col < innerCols; col += 1) {
            for (int row = 0; row < innerRows; row += 1) {
                this.grid[floorStartCol + col][floorStartRow + row] = Tileset.FLOOR;
            }
        }
    }

    /** End Room Generation Helpers */

    /** will check all of the doors of some registeredRoom room and return the doorKey of a valid connection if it exists */
    private int checkConnectRoom(Room newRoom, Room registeredRoom) {
        int key = -1;
        for (int i = 0; i < 4; i += 1) {
            if (!this.invalidConnection(i, newRoom, registeredRoom)) {
                key = i;
                break;
            }
        }
        return key;
    }

    /** helper function for checkConnectRoom() that checks whether or not two rooms have a valid connection */
    private boolean invalidConnection(int doorKey, Room newRoom, Room registeredRoom) {
        Coordinate potentialNewRoomOrigin = this.mapOriginFromPotentialDoor(doorKey, newRoom, registeredRoom);
        if (potentialNewRoomOrigin == null) {
            return true;
        }
        return this.invalidPlacement(registeredRoom, newRoom, potentialNewRoomOrigin);
    }

    /** will take the coordinates of a shifted door and subtract  */
    private Coordinate mapOriginFromPotentialDoor(int doorKey, Room newRoom, Room registeredRoom) {
        Coordinate base = registeredRoom.getDoor(doorKey);
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
        Coordinate actualDoorCoordinate = newRoom.getDoor(doorKey);
        Coordinate potentialNewRoomOrigin = new Coordinate(
        shifted.getX() - actualDoorCoordinate.getX(), shifted.getY() - actualDoorCoordinate.getY());
        if (this.coordinateOutOfBounds(potentialNewRoomOrigin.getX(), potentialNewRoomOrigin.getY())) {
            return null;
        }
        return potentialNewRoomOrigin;
    }

    /** checks if some coordinate (c0) : roomBottomLeft <= c0 <= roomTopRight */
    private boolean invalidPlacement(Room originRoom, Room newRoom, Coordinate potentialPlacement) {
        if (this.coordinateOutOfBounds(potentialPlacement.getX(), potentialPlacement.getY())) {
            return true;
        }
        if (
                this.twoPointsHasIntersection(
                    potentialPlacement.getX(),
                    potentialPlacement.getY(),
                    originRoom.getOrigin().getX(),
                    originRoom.getOrigin().getX() + originRoom.getWidth(),
                    originRoom.getOrigin().getY(),
                    originRoom.getOrigin().getY() + originRoom.getHeight()
                )
        ) {
            return true;
        }
        for (int x = potentialPlacement.getX(); x < potentialPlacement.getX() + newRoom.getWidth(); x += 1) {
            for (int y = potentialPlacement.getY(); y < potentialPlacement.getY() + newRoom.getHeight(); y += 1) {
                if (this.tileAlreadySet(this.grid[x][y].character())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean twoPointsHasIntersection(int originX, int originY, int x0, int x1, int y0, int y1) {
        return (originX >= x0 && originX <= x1) && (originY >= y0 && originY <= y1);
    }

    /** add tiles to the border of the room */
    private void addWallToRoom(Room room) {
        int xp = room.getOrigin().getX();
        int rightColumn = xp + room.getWidth() - 1;
        int yp = room.getOrigin().getY();
        int topRow = yp + room.getHeight() - 1;
        int actualWidth = xp + room.getWidth();
        int actualHeight = yp + room.getHeight();

        Coordinate door = room.getAssignedDoor();
        this.grid[xp + door.getX()][yp + door.getY()] = Tileset.FLOOR;

        for (int y = yp; y < actualHeight; y += 1) {
            checkAndAssignWall(xp, y);
            checkAndAssignWall(rightColumn, y);
        }
        for (int x = xp; x < actualWidth; x += 1) {
            checkAndAssignWall(x, yp);
            checkAndAssignWall(x, topRow);
        }
    }

    private void checkAndAssignWall(int x, int y) {
        if (this.coordinateOutOfBounds(x, y) || this.tileAlreadySet(this.grid[x][y].character())) {
            return;
        }
        this.grid[x][y] = Tileset.WALL;
    }



    private boolean coordinateOutOfBounds(int x, int y) {
        return (x < 0 || x >= this.width() || y < 0 || y >= this.height());
    }

    private boolean tileAlreadySet(char tileChar) {
        return tileChar == '·' || tileChar == '#';
    }
}
