import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        long sid = g.closest(stlon, stlat);
        long eid = g.closest(destlon, destlat);
        PriorityQueue<RouteSearchNode> pq = new PriorityQueue<>();

        pq.add(new RouteSearchNode(sid, null, 0.0, 0.0, 0.0));
        // "Best" is really these two maps
        Map<Long, Double> best = new HashMap<>();
        Map<Long, Long> pathed = new HashMap<>();
        Long state = eid;
        boolean noPath = false;

        best.put(sid, 0.0);
        pathed.put(sid, sid);

        while (true) {
            if (pq.size() == 0) {
                noPath = true;
                break;
            }
            RouteSearchNode prev = pq.remove();

            if (prev.id == eid) {
                break;
            }

            Iterable<Long> adj = g.adjacent(prev.id);
            if (prev.prevId == null) {
                for (Long id : adj) {
                    double E = g.distance(prev.id, id); // d(v, w)
                    double H = g.distance(id, eid); // d(w, end) == h(w)
                    RouteSearchNode RSN = new RouteSearchNode(id, prev.id, prev.DSW, E, H);
                    best.put(id, prev.DSW + E); // d(s, w)
                    pathed.put(id, prev.id);
                    pq.add(RSN);
                }
            } else {
                for (Long id : adj) {
                    // if id doesn't exist, simply add it to best
                    if (!best.containsKey(id)) {
                        double E = g.distance(prev.id, id); // d(v, w)
                        double H = g.distance(id, eid); // d(w, end) == h(w)
                        RouteSearchNode RSN = new RouteSearchNode(id, prev.id, prev.DSW, E, H);
                        best.put(id, prev.DSW + E); // d(s, w)
                        pathed.put(id, prev.id);
                        pq.add(RSN);
                        continue;
                    }

                    double E = g.distance(prev.id, id); // d(v, w)
                    double DSW = prev.DSW + E; // d(s, w)
                    // if id exists, AND the traversed from source plus euclidean beats it
                    if (best.containsKey(id) && best.get(id) > DSW) {
                        double H = g.distance(id, eid); // d(w, end) == h(w)
                        RouteSearchNode RSN = new RouteSearchNode(id, prev.id, prev.DSW, E, H);
                        best.put(id, DSW); // d(s, w)
                        pathed.put(id, prev.id);
                        pq.add(RSN);
                    }
                }
            }
        }

        if (noPath) {
            return new ArrayList<>();
        }

        List<Long> res = new ArrayList<>();
        while (state != sid) {
            res.add(state);
            state = pathed.get(state);
        }
        res.add(sid);
        Collections.reverse(res);
        return res;
    }

    private static class RouteSearchNode implements Comparable<RouteSearchNode> {
        private final long id;
        private final double DSW;
        private final double P;
        private final Long prevId;

        public RouteSearchNode(long id, Long prevId, double dsv, double edvw, double hw) {
            this.id = id;
            this.prevId = prevId;
            this.DSW = dsv + edvw;
            this.P = dsv + edvw + hw;
        }

        @Override
        public int compareTo(RouteSearchNode o) {
            return Double.compare(this.P, o.P);
        }
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigationDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        List<NavigationDirection> nav = new ArrayList<>();
        Long state = route.get(0);
        double path = 0.0;
        Long pathId = route.get(0);

        NavigationDirection start = new NavigationDirection();
        start.direction = 0;
        start.way = g.fetchWayName(route.get(0));
        start.distance = 0.0;
        nav.add(start);

        Iterator<Long> iter = route.iterator();
        iter.next();

        while (iter.hasNext()) {
            Long id = iter.next();
            path += g.distance(pathId, id);
            pathId = id;
            if (!g.fetchWayName(state).equals(g.fetchWayName(id))) {
                nav.get(nav.size() - 1).distance = path;
                NavigationDirection step = new NavigationDirection();
                step.way = g.fetchWayName(id);
                step.direction = bearingHelper(state, id, g);
                step.distance = 0.0;
                nav.add(step);
                state = id;
            }
        }

        return nav; // FIXME
    }

    private static int bearingHelper(Long from, Long to, GraphDB g) {
        double bearing = g.bearing(from, to);

        if (bearing >= -15.0 && bearing <= 15.0) { // straight
            return 1;
        }
        if (bearing < -15.0 && bearing >= -30.0) { // slight left
            return 2;
        }
        if (bearing > 15.0 && bearing <= 30.0) { // slight right
            return 3;
        }
        if (bearing < -30.0 && bearing >= -100.0) { // left
            return 5;
        }
        if (bearing > 30.0 && bearing <= 100.0) { // right
            return 4;
        }
        if (bearing < -100.0) { // sharp left
            return 6;
        }
        return 7; // sharp right
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
