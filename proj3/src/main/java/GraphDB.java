import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    private final Map<Long, Node> vertices;

    /* Helper Classes Start */

    // Representation of a Vertex
    private static class Node {
        private long id;
        private double lat;
        private double lon;
        private final List<Long> adj = new ArrayList<>();
    }

    // Representation of an Edge
    public static class Edge {
        private final long O; // origin
        private final long D; // destination

        public Edge(long O, long D) {
            this.O = O;
            this.D = D;
        }
    }

    /* Helper Classes End */

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        // initialize Hashmap for easy Vertex ID lookup
        this.vertices = new HashMap<>();

        // XML parsing
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        // prune marooned vertices
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        // TODO: Your code here.
        // This function is called after the SAX parser is run, so I'm assuming that all edges
        // will have been added by this point, and I can just do an iteration over the keyset
        // of the Vertices and prune Nodes with empty adjacency lists
        Iterable<Long> ids = this.vertices();
        for (Long id : ids) {
            if (this.maroonedNode(id)) {
                this.removeNode(id);
            }
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        return this.vertices.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        Node found = this.vertices.get(v);
        if (found == null) {
            throw new IllegalArgumentException("The id: " + v + ", could not be found in Vertex map!");
        }
        return new ArrayList<>(found.adj); // return defensively copied array
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        double currentDelta = Double.MAX_VALUE;
        Node C = null;
        Iterable<Long> ids = this.vertices();

        for (Long id : ids) {
            Node N = this.vertices.get(id);
            double calcDistance = distance(lon, lat, N.lon, N.lat);
            if (calcDistance < currentDelta) {
                currentDelta = calcDistance;
                C = N;
            }
        }

        if (C == null) {
            return 0;
        }

        return C.id;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        Node found = this.vertices.get(v);
        if (found == null) {
            throw new IllegalArgumentException("Id of: " + v + ", could not be found in Vertex map!");
        }
        return found.lon;
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        Node found = this.vertices.get(v);
        if (found == null) {
            throw new IllegalArgumentException("Id of: " + v + ", could not be found in Vertex map!");
        }
        return found.lat;
    }

    /**
     * Adds a new Node to the Vertices HashMap to allow for Vertex lookup by id
     * Note: Vertex is equivalent to a Node from the dataset
     * @param id The id of the Vertex
     * @param lat The latitude of the Vertex
     * @param lon The longitude of the Vertex
     */
    public void addNode(long id, double lat, double lon) {
        if (this.vertices.containsKey(id)) {
            return;
        }
        Node N = new Node();
        N.id = id;
        N.lat = lat;
        N.lon = lon;
        this.vertices.put(id, N);
    }

    /**
     * Adds a new bi-directional edge between the Origin Node id and Destination Node id
     * @param edge
     */
    public void addEdge(Edge edge) {
        Node N = this.vertices.get(edge.O);
        Node D = this.vertices.get(edge.D);
        if (N == null || D == null) {
            throw new IllegalArgumentException("Edge id's: <" + edge.O + ", " + edge.D + ">, could not be found in Vertex map!");
        }
        // bi-directional edge connection
        N.adj.add(edge.D);
        D.adj.add(edge.O);
    }

    /**
     * Checks to see if the Vertex with the corresponding id has an empty adjacency list
     * Note: Vertex is equivalent to a Node from the dataset
     * @param id The id of the Vertex to check
     * @return A boolean indicating whether or not this Vertex has no connections
     */
    private boolean maroonedNode(long id) {
        Node N = this.vertices.get(id);
        if (N == null) {
            throw new IllegalArgumentException("Id of: " + id + ", could not be found in Vertex map!");
        }
        return N.adj.isEmpty();
    }

    /**
     * Removes a Vertex from the Vertices HashMap with the corresponding id
     * @param id The id of the Vertex to prune
     */
    private void removeNode(long id) {
        this.vertices.remove(id);
    }
}
