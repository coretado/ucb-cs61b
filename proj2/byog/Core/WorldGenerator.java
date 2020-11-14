package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class WorldGenerator {
    private final int ROOM_WIDTH = 6;
    private final int ROOM_HEIGHT = 6;

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
        for (int row = 0; row < rows; row += 1) {
            for (int col = 0; col < columns; col += 1) {
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
        while (!this.hitCapacity()) {
            // new room
            Room candidate = this.generateRoom();

            // check rooms for a valid connection
            for (Room existing : this.rooms) {
                System.out.println("AH INFINITE LOOP");
                boolean wasAbleToConnectToDoorInToCheckRoom =
                        this.checkAndSetOriginToPotentialRoomIfValid(candidate, existing);
                if (wasAbleToConnectToDoorInToCheckRoom) {
                    this.registerRoom(candidate);
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
        doors[0] = new Coordinate(this.genDoorDimension(room.getCols()), room.getRows());
        doors[1] = new Coordinate(room.getCols(), this.genDimension(room.getRows()));
        doors[2] = new Coordinate(this.genDoorDimension(room.getCols()), 0);
        doors[3] = new Coordinate(0, this.genDimension(room.getRows()));
        room.setDoors(doors);
    }

    /** only used when placing the first room (because subsequent rooms are only concerned
     * with attaching to each other) - will check to see if any of the rooms coordinates are out of bounds
     */
    private boolean coreRoomCanBePlacedOntoGrid(Coordinate origin, Room room) {
        int mappedCols = origin.getCol() + room.getCols();
        int mappedRows = origin.getRow() + room.getRows();
        for (int row = origin.getRow(); row < mappedRows; row += 1) {
            for (int col = origin.getCol(); col < mappedCols; col += 1) {
                if (this.coordinateOutOfBounds(col, row)) {
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
        int col = room.getOrigin().getCol() + 1;
        int row = room.getOrigin().getRow() + 1;
        int colLength = col + room.getInnerCols();
        int rowLength = row + room.getInnerRows();

        for ( ; row < rowLength; row += 1) {
            for ( ; col < colLength; col += 1) {
                this.grid[col][row] = Tileset.FLOOR;
            }
        }
    }

    /** End Room Generation Helpers */

    /** will check all of the doors of some registeredRoom room and return the doorKey of a valid connection if it exists */
    private boolean checkAndSetOriginToPotentialRoomIfValid(Room candidate, Room existing) {
        Coordinate potentialCandidateOrigin = null;
        for (int i = 0; i < 4; i += 1) {
            // if this room already has another room assigned to this door, continue
            if (existing.doorPositionAssigned(i)) {
                continue;
            }

            // this room does not have another room assigned to this door
            potentialCandidateOrigin = mapCandidateOriginFromExistingDoor(i, candidate, existing);

            // if there was no potentialCandidateOrigin, this door is not a connection candidate
            if (potentialCandidateOrigin != null) {
                existing.assignDoorPositionWithRoom(i);
                break;
            }
        }

        if (potentialCandidateOrigin == null) {
            return false;
        } else {
            candidate.setOrigin(potentialCandidateOrigin);
            return true;
        }
    }

    /** will take the coordinates of a shifted door and subtract  */
    private Coordinate mapCandidateOriginFromExistingDoor(int doorKey, Room candidate, Room existing) {
        Coordinate doorCoordinateFromExisting = existing.getDoor(doorKey);
        Coordinate mappedCoordinate;

        switch (doorKey) {
            case 0:
                mappedCoordinate = new Coordinate(
                        doorCoordinateFromExisting.getCol() + existing.getOrigin().getCol(),
                        doorCoordinateFromExisting.getRow() + existing.getOrigin().getRow() + 1
                );
                break;
            case 1:
                mappedCoordinate = new Coordinate(
                        doorCoordinateFromExisting.getCol() + existing.getOrigin().getCol() + 1,
                        doorCoordinateFromExisting.getRow() + existing.getOrigin().getRow()
                );
                break;
            case 2:
                mappedCoordinate = new Coordinate(
                        doorCoordinateFromExisting.getCol() + existing.getOrigin().getCol(),
                        doorCoordinateFromExisting.getRow() + existing.getOrigin().getRow() - 1
                );
                break;
            default:
                mappedCoordinate = new Coordinate(
                        doorCoordinateFromExisting.getCol() + existing.getOrigin().getCol() - 1,
                        doorCoordinateFromExisting.getRow() + existing.getOrigin().getRow()
                );
                break;
        }

        Coordinate candidateDoorCoordinate = null;

        for (int i = 0; i < 4; i += 1) {
            Coordinate candidateConnectionCoordinate = candidate.getDoor(i);

            // logic here is that if you take one additional step in the doorKey direction, that is where
            // the candidate room door coordinate is going to lie, which means if you then subtract the
            // candidate room door coordinate by the door coordinates you should get it's origin relative to the parent
            Coordinate potentialNewRoomOrigin = new Coordinate(
                    mappedCoordinate.getCol() - candidateConnectionCoordinate.getCol(),
                    mappedCoordinate.getRow() - candidateConnectionCoordinate.getRow()
            );

            if (!this.invalidPlacement(candidate, existing, potentialNewRoomOrigin)) {
                candidateDoorCoordinate = potentialNewRoomOrigin;
                candidate.assignDoorPositionWithRoom(i);
                break;
            }
        }

        return candidateDoorCoordinate;
    }

    /** checks if some coordinate (c0) : roomBottomLeft <= c0 <= roomTopRight */
    private boolean invalidPlacement(Room candidate, Room existing, Coordinate potentialPlacement) {
        if (this.coordinateOutOfBounds(potentialPlacement.getCol(), potentialPlacement.getRow())) {
            return true;
        }

        if (
                this.roomHasIntersectionWithPoint(
                    potentialPlacement.getCol(),
                    potentialPlacement.getRow(),
                    existing.getOrigin().getCol(),
                    existing.getOrigin().getCol() + existing.getAdjustedWidth(),
                    existing.getOrigin().getRow(),
                    existing.getOrigin().getRow() + existing.getAdjustedHeight()
                )
        ) {
            return true;
        }

        for (int row = potentialPlacement.getRow(); row < potentialPlacement.getRow() + candidate.getRows(); row += 1) {
            for (int col = potentialPlacement.getCol(); col < potentialPlacement.getCol() + candidate.getCols(); col += 1) {
                if (this.coordinateOutOfBounds(col, row) || this.tileAlreadySet(this.grid[col][row].character())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean roomHasIntersectionWithPoint(int originCol, int originRow, int col0, int col1, int row0, int row1) {
        return (originCol >= col0 && originCol <= col1) && (originRow >= row0 && originRow <= row1);
    }

    /** add tiles to the border of the room */
    private void addWallToRoom(Room room) {
        int leftColumn = room.getOrigin().getCol();
        int rightColumn = leftColumn + room.getAdjustedWidth();
        int bottomRow = room.getOrigin().getRow();
        int topRow = bottomRow + room.getAdjustedHeight();
        int actualWidth = leftColumn + room.getCols();
        int actualHeight = bottomRow + room.getRows();

        for (int i = 0; i < 4; i += 1) {
            if (room.doorPositionAssigned(i)) {
                Coordinate door = room.getDoor(i);
                this.grid[leftColumn + door.getCol()][bottomRow + door.getRow()] = Tileset.FLOOR;
            }
        }

        for (int row = bottomRow; row < actualHeight; row += 1) {
            checkAndAssignWall(leftColumn, row);
            checkAndAssignWall(rightColumn, row);
        }
        for (int col = leftColumn; col < actualWidth; col += 1) {
            checkAndAssignWall(col, bottomRow);
            checkAndAssignWall(col, topRow);
        }
    }

    private void checkAndAssignWall(int x, int y) {
        if (this.coordinateOutOfBounds(x, y) || this.tileAlreadySet(this.grid[x][y].character())) {
            return;
        }
        this.grid[x][y] = Tileset.WALL;
    }



    private boolean coordinateOutOfBounds(int col, int row) {
        return (col < 0 || col >= this.cols() || row < 0 || row >= this.rows());
    }

    private boolean tileAlreadySet(char tileChar) {
        return tileChar == '·' || tileChar == '#';
    }
}
