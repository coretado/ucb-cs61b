package byog.Core;

public class WorldGenerator {
    /**
     * General idea:
     * i) generate a stack or queue of rooms that can be iterated; generate a tileset world with input width and height
     * ii) keep track of the fill count of the world
     * iii) in a for loop, generate rooms until the fill count reaches a critical threshold
     * - thought process of Room generation is elaborated upon in Room.java
     * iv) once the threshold has been reached, call upon a helper method that can occupy the tiles in the world
     * v) GenerateWorld method (or some related name) will then return a completed Tileset
     */
}
