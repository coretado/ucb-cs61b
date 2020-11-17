package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class WorldGenerator {
    private final TETile[][] grid;
    private final int rows;
    private final int cols;
    private final Deque<Room> rooms;
    private final Random seedGen;
    private int area = 0;

    private enum CorridorType {
        Vertical,
        Horizontal
    }

    public WorldGenerator(int worldWidth, int worldHeight, long seed) {
        // procedural generator
        this.seedGen = new Random(seed);

        // assuming world is square
        this.grid = new TETile[worldHeight][worldWidth];
        this.rows = worldHeight;
        this.cols = worldWidth;
        this.rooms = new ArrayDeque<>();

        // initialize empty row
        for (int col = 0; col < worldWidth; col += 1) {
            for (int row = 0; row < worldHeight; row += 1) {
                this.grid[col][row] = Tileset.NOTHING;
            }
        }
    }

    public TETile[][] getGrid() {
        return this.grid;
    }

    /* seed generator helpers - Random is (0, bound] */
    private boolean corridorIsVertical() {
        return this.seedGen.nextInt(2) == 0;
    }

    private boolean willGenCorridor() {
        return this.seedGen.nextInt(100) < 25;
    }

    private int generateRoomDimension() {
        int MAXIMUM_ROOM_DIMENSION = 5;
        int MINIMUM_ROOM_DIMENSION = 2;
        return Math.max(MINIMUM_ROOM_DIMENSION, this.seedGen.nextInt(MAXIMUM_ROOM_DIMENSION));
    }

    private int generateDoorCoordinate(int value) {
        return this.seedGen.nextInt(value) + 1;
    }
    /* end seed generator helpers */

    /* grid traversal and grid check helpers */
    private boolean coordinateOutOfBounds(int col, int row) {
        return col < 0 || col >= this.cols || row < 0 || row >= this.rows;
    }

    private boolean tileWasAssigned(char tile) {
        return tile != ' ';
    }

    private boolean roomsClearOfEachOther(Room existing, Coordinate candidateCoordinate, Room candidate) {
        int candidateX2 = candidateCoordinate.getCol() + candidate.getIndexWidth();
        int candidateY2 = candidateCoordinate.getRow() + candidate.getIndexHeight();
        int existingX2 = existing.getOriginCol() + existing.getIndexWidth();
        int existingY2 = existing.getOriginRow() + existing.getIndexHeight();
        return (candidateCoordinate.getCol() > existingX2) ||
            (candidateCoordinate.getRow() > existingY2) ||
            (existing.getOriginCol() > candidateX2) ||
            (existing.getOriginRow() > candidateY2);
    }
    /* end grid traversal and grid check helpers */

    /* everything room related */
    private boolean reachedCapacity() {
        return ((float) this.area / (this.cols * this.rows)) >= 0.65;
    }

    private void addArea(int area) {
        this.area += area;
    }

    private void registerRoom(Room room) {
        this.rooms.add(room);
        this.addArea(room.getArea());
    }

    private void createDungeonCore() {
        Room room = new Room(
            this.generateRoomDimension(),
            this.generateRoomDimension()
        );
        boolean placed = false;
        while (!placed) {
            Coordinate origin = new Coordinate(
                    this.seedGen.nextInt(this.cols),
                    this.seedGen.nextInt(this.rows)
            );
            if (
                this.coordinateOutOfBounds(origin.getCol(), origin.getRow()) ||
                this.coordinateOutOfBounds(
                    origin.getCol() + room.getIndexWidth(),
                    origin.getRow() + room.getIndexHeight()
                )
            ) {
                continue;
            }
            placed = true;
            room.setOrigin(origin);
        }
        this.addDoors(room);
        this.registerRoom(room);
    }

    private Room generateNewCandidateRoom() {
        return this.willGenCorridor() ? this.generateCorridor() : this.generateRoom();
    }

    private Room generateRoom() {
        Room candidate = new Room(this.generateRoomDimension(), this.generateRoomDimension());
        this.addDoors(candidate);
        return candidate;
    }

    private Room generateCorridor() {
        Room corridorCandidate;
        if (this.corridorIsVertical()) {
            corridorCandidate = new Room(3, this.generateRoomDimension());
            this.addCorridorDoors(CorridorType.Vertical, corridorCandidate);
        } else {
            corridorCandidate = new Room(this.generateRoomDimension(), 3);
            this.addCorridorDoors(CorridorType.Horizontal, corridorCandidate);
        }
        return corridorCandidate;
    }

    // 0 = down, 1 = right, 2 = top, 3 = left
    private void addDoors(Room room) {
        Coordinate[] doorCoordinates = new Coordinate[4];
        doorCoordinates[0] = new Coordinate(this.generateDoorCoordinate(room.getInnerWidth()), 0);
        doorCoordinates[1] = new Coordinate(room.getIndexWidth(), this.generateDoorCoordinate(room.getInnerHeight()));
        doorCoordinates[2] = new Coordinate(this.generateDoorCoordinate(room.getInnerWidth()), room.getIndexHeight());
        doorCoordinates[3] = new Coordinate(0, this.generateDoorCoordinate(room.getInnerHeight()));
        room.setDoors(doorCoordinates);
    }

    private void addCorridorDoors(CorridorType corridorType, Room room) {
        Coordinate[] doorCoordinates = new Coordinate[4];
        if (corridorType == CorridorType.Horizontal) {
            doorCoordinates[0] = new Coordinate(this.generateDoorCoordinate(room.getInnerWidth()), 0);
            doorCoordinates[1] = new Coordinate(room.getIndexWidth(), 1);
            doorCoordinates[2] = new Coordinate(
                this.generateDoorCoordinate(room.getInnerWidth()), room.getIndexHeight());
            doorCoordinates[3] = new Coordinate(0, 1);
        } else {
            doorCoordinates[0] = new Coordinate(1, 0);
            doorCoordinates[1] = new Coordinate(
                room.getIndexWidth(), this.generateDoorCoordinate(room.getInnerHeight()));
            doorCoordinates[2] = new Coordinate(1, room.getIndexHeight());
            doorCoordinates[3] = new Coordinate(0, this.generateDoorCoordinate(room.getInnerHeight()));
        }
        room.setDoors(doorCoordinates);
    }

    private Coordinate shiftDoorCoordinate(Room existing, int key) {
        switch (key) {
            case 0: {
                Coordinate bottomDoor = existing.getDoorCoordinate(key);
                Coordinate shifted = new Coordinate(
                    existing.getOriginCol() + bottomDoor.getCol(),
                    existing.getOriginRow() + bottomDoor.getRow() - 1
                );
                if (this.coordinateOutOfBounds(shifted.getCol(), shifted.getRow())) {
                    return null;
                }
                return shifted;
            }
            case 1: {
                Coordinate rightDoor = existing.getDoorCoordinate(key);
                Coordinate shifted = new Coordinate(
                    existing.getOriginCol() + rightDoor.getCol() + 1,
                    existing.getOriginRow() + rightDoor.getRow()
                );
                if (this.coordinateOutOfBounds(shifted.getCol(), shifted.getRow())) {
                    return null;
                }
                return shifted;
            }
            case 2: {
                Coordinate topDoor = existing.getDoorCoordinate(key);
                Coordinate shifted = new Coordinate(
                    existing.getOriginCol() + topDoor.getCol(),
                    existing.getOriginRow() + topDoor.getRow() + 1
                );
                if (this.coordinateOutOfBounds(shifted.getCol(), shifted.getRow())) {
                    return null;
                }
                return shifted;
            }
            default: {
                Coordinate leftDoor = existing.getDoorCoordinate(key);
                Coordinate shifted = new Coordinate(
                existing.getOriginCol() + leftDoor.getCol() - 1,
                existing.getOriginRow() + leftDoor.getRow()
                );
                if (this.coordinateOutOfBounds(shifted.getCol(), shifted.getRow())) {
                    return null;
                }
                return shifted;
            }
        }
    }

    private boolean roomIntersectsAnotherRoom(Room room, Coordinate candidateCoordinate) {
        for (Room r : this.rooms) {
            if (!this.roomsClearOfEachOther(r, candidateCoordinate, room)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkExistingRoomDoorConnectionsForCandidacy(Room existing, Room candidate) {
        // can the candidate room be attached to this room?
        boolean candidateOriginFound = false;

        // make an array of four coordinates that are door coordinates relative to the WORLD rather than the local room
        Coordinate[] mapCoordinates = new Coordinate[4];
        for (int i = 0; i < 4; i += 1) {
            mapCoordinates[i] = this.shiftDoorCoordinate(existing, i);
        }

        // check each map coordinate against all four doors of the candidate
        for (int existingKey = 0; existingKey < 4; existingKey += 1) {
            if (candidateOriginFound) {
                break;
            }

            // if the existing room already has a door taken at this index, or the map coordinate was null, continue
            if (existing.getDoorTaken(existingKey) || mapCoordinates[existingKey] == null) {
                continue;
            }

            // grab mapCoordinate for easier access
            Coordinate mapCoordinate = mapCoordinates[existingKey];

            // for each of the doors on the candidate, see if the candidate room can be placed at the map coordinate
            for (int candidateKey = 0; candidateKey < 4; candidateKey += 1) {
                // cdc = candidate door coordinate corresponding to the current iteration key
                Coordinate cdc = candidate.getDoorCoordinate(candidateKey);

                // making a potential origin from the map coordinate and the cdc
                Coordinate potentialOrigin = new Coordinate(
                mapCoordinate.getCol() - cdc.getCol(), mapCoordinate.getRow() - cdc.getRow()
                );

                // check to see that this is a valid placement on the map
                if (
                    this.coordinateOutOfBounds(potentialOrigin.getCol(), potentialOrigin.getRow()) ||
                    this.coordinateOutOfBounds(
                    potentialOrigin.getCol() + candidate.getIndexWidth(),
                        potentialOrigin.getRow() + candidate.getIndexHeight()
                    )
                ) {
                    continue;
                }

                // check to see that this room does not cross any other room
                if (this.roomIntersectsAnotherRoom(candidate, potentialOrigin)) {
                    continue;
                }

                // if you get here, this is a valid room; set the keys for doors; set candidate origin
                candidate.setOrigin(potentialOrigin);
                existing.setDoorTaken(existingKey);
                candidate.setDoorTaken(candidateKey);
                candidateOriginFound = true;
                break;
            }

        }

        return candidateOriginFound;
    }

    private void setTilesInWorld(Room room) {
        int roomCol = room.getOriginCol();
        int roomRow = room.getOriginRow();
        int roomFarRightCol = roomCol + room.getIndexWidth();
        int roomTopRow = roomRow + room.getIndexHeight();

        for (int col = 0; col < room.getWidth(); col += 1) {
            this.grid[roomCol + col][roomRow] = Tileset.WALL;
            this.grid[roomCol + col][roomTopRow] = Tileset.WALL;
        }

        for (int row = 0; row < room.getHeight(); row += 1) {
            this.grid[roomCol][roomRow + row] = Tileset.WALL;
            this.grid[roomFarRightCol][roomRow + row] = Tileset.WALL;
        }

        // set floor tiles at doors
        for (int key = 0; key < 4; key += 1) {
            if (room.getDoorTaken(key)) {
                // get door coordinate
                Coordinate dc = room.getDoorCoordinate(key);
                // set grid tile
                this.grid[roomCol + dc.getCol()][roomRow + dc.getRow()] = Tileset.FLOOR;
            }
        }

        for (int col = 1; col < room.getIndexWidth(); col += 1) {
            for (int row = 1; row < room.getIndexHeight(); row += 1) {
                this.grid[roomCol + col][roomRow + row] = Tileset.FLOOR;
            }
        }
    }

    public void generateWorld() {
        // create first room
        this.createDungeonCore();

        // while world <= 65% capacity, create rooms
        int overflowCounter = 0;
        while (overflowCounter < 100 && !this.reachedCapacity()) {
            // random new room; could be a wide room or a corridor
            Room candidate = this.generateNewCandidateRoom();
            overflowCounter += 1;

            // check candidate against all rooms to see if it can be placed in room
            for (Room existing : this.rooms) {
                // function will check if candidate can attach to any of the four doors of the existing room
                boolean canPlaceCandidate = checkExistingRoomDoorConnectionsForCandidacy(existing, candidate);

                // loop will terminate and register room if there was a successful attachment location
                if (canPlaceCandidate) {
                    this.registerRoom(candidate);
                    break;
                }
            }
        }

        // "paint" in the world
        this.rooms.forEach(this::setTilesInWorld);
    }
}
