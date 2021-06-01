import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    private double ROOT_DELTA_X;
    private double ROOT_DELTA_Y;

    public Rasterer() {
        this.ROOT_DELTA_X = MapServer.ROOT_LRLON - MapServer.ROOT_ULLON;
        this.ROOT_DELTA_Y = MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT;
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        System.out.println(params);
        Map<String, Object> results = new HashMap<>();

        // Catching corner cases
        if (params == null) {
            return this.badRequestObject(results);
        }
        if (this.hasBadCoordinates(params)) {
            return this.badRequestObject(results);
        }

        // Making Map to store all res data
        Map<String, Object> data = new HashMap<>();
        // Determining depth
        data.put("depth", this.calculateDepth(params));
        // Determining render grid
        this.calculateRenderGrid(params, data);
        // Assigning data to results
        this.assignParams(results, data, params);

        return results;
    }

    /**
     * Helper function used to create a bad request return object
     *
     * @param results Map object used as a return by the server
     * @return A map of results with dummy values AND a specified FALSE query success output
     */
    private Map<String, Object> badRequestObject(Map<String, Object> results) {
        results.put("render_grid", null);
        results.put("raster_ul_lon", -1);
        results.put("raster_ul_lat", -1);
        results.put("raster_lr_lon", -1);
        results.put("raster_lr_lat", -1);
        results.put("depth", 0);
        results.put("query_success", false);
        return results;
    }

    /**
     * Helper function used to catch edge cases of coordinate inputs. These bad inputs include:
     * - if the lower latitude is somehow higher than the origin latitude
     * - if the rightmost longitude is somehow less than the origin longitude
     * - if the coordinates provided do not generate a rectangular query box that intersects
     *   with the ROOT coordinates
     *
     * @param params Map object from server request containing BearMaps Request parameters
     * @return Boolean specifying whether the BearMaps request violates the above conditions
     */
    private boolean hasBadCoordinates(Map<String, Double> params) {
        Double paramULLON = params.get("ullon");
        Double paramLRLON = params.get("lrlon");
        Double paramULLAT = params.get("ullat");
        Double paramLRLAT = params.get("lrlat");

        if (paramLRLAT > paramULLAT) {
            return true;
        }
        if (paramULLON > paramLRLON) {
            return true;
        }
        return this.hasIntersectionWithRoot(paramULLON, paramLRLON, paramULLAT, paramLRLAT);
    }

    /**
     * Helper function to determine if BearMaps request makes a rectangular
     * query box that is contained within the ROOT coordinates
     *
     * @param paramULLON - upper left longitude of request
     * @param paramLRLON - lower right longitude of request
     * @param paramULLAT - upper left latitude of request
     * @param paramLRLAT - lower left latitude of request
     * @return Boolean specifying whether the BearMaps request does not
     *         intersect the ROOT coordinates
     */
    private boolean hasIntersectionWithRoot(
            Double paramULLON,
            Double paramLRLON,
            Double paramULLAT,
            Double paramLRLAT
    ) {
        // x3 > x2
        if (paramULLON > MapServer.ROOT_LRLON) {
            return false;
        }
        // x4 < x1
        if (paramLRLON < MapServer.ROOT_ULLON) {
            return false;
        }
        // y3 > y2
        if (paramULLAT > MapServer.ROOT_LRLAT) {
            return false;
        }
        // y4 < y1
        return !(paramLRLAT < MapServer.ROOT_ULLAT);
    }

    /**
     * Helper function to assign values to results Map.
     * NOTE: Will automatically put query_success to true!
     *
     * @param results - Map object
     * @param data - Helper object containing calculated Render Grid,
     *               rasterer Longitude and Latitude, and Depth
     */
    private void assignParams(
            Map<String, Object> results,
            Map<String, Object> data,
            Map<String, Double> params
    ) {
        results.put("render_grid", data.get("render_grid"));
        results.put("raster_ul_lon", data.get("raster_ul_lon"));
        results.put("raster_ul_lat", data.get("raster_ul_lat"));
        results.put("raster_lr_lon", data.get("raster_lr_lon"));
        results.put("raster_lr_lat", data.get("raster_lr_lat"));
        if (params.get("width") == null) {
            results.put("width", 256);
        } else {
            results.put("width", params.get("width"));
        }
        if (params.get("height") == null) {
            results.put("height", 256);
        } else {
            results.put("height", params.get("height"));
        }
        results.put("depth", data.get("depth"));
        results.put("query_success", true);
    }

    /**
     * Helper function used to calculate the correct depth level needed for BearMaps image request
     *
     * @param params - BearMaps request object
     *
     * @return Integer specifying the BearMaps image depth level needed
     */
    private int calculateDepth(Map<String, Double> params) {
        double paramLRLON = params.get("lrlon");
        double paramULLON = params.get("ullon");
        double width = params.get("w");
        double paramDPP = (paramLRLON - paramULLON) / width;
        int depth = 0;
        while (true) {
            double divider = Math.pow(2.0, depth);
//            double deltaX = depth == 0 ? this.ROOT_DELTA_X : this.ROOT_DELTA_X / divider;
            double deltaX = this.ROOT_DELTA_X / divider;
            double longDPP =
                    ((MapServer.ROOT_ULLON + deltaX) - MapServer.ROOT_ULLON) / MapServer.TILE_SIZE;
            if (longDPP <= paramDPP) {
                break;
            }
            if (depth == 7) {
                break;
            }
            depth += 1;
        }
        return depth;
    }

    /**
     * Helper function used to generate the String Matrix of filenames needed
     * to render the BearMaps image request
     *
     * @param params - BearMaps request object
     * @param data - Local object for response information
     *
     * @return String Matrix of filenames
     */
    private void calculateRenderGrid(Map<String, Double> params, Map<String, Object> data) {
        this.calculateImageRange(params, data); // THIS FUNCTION WILL MUTATE "data"
        this.generateRenderGrid(data); // THIS FUNCTION WILL MUTATE "data"
    }

    private void calculateImageRange(Map<String, Double> params, Map<String, Object> data) {
        int k = (int) Math.pow(2, (Integer) data.get("depth"));
        double latIncrement = this.ROOT_DELTA_Y / k;
        double longIncrement = this.ROOT_DELTA_X / k;
        double ullon = params.get("ullon");
        double ullat = params.get("ullat");
        double lrlon = params.get("lrlon");
        double lrlat = params.get("lrlat");
        double mutativeULLON = MapServer.ROOT_ULLON;
        double mutativeULLAT = MapServer.ROOT_ULLAT;

        // finding latitude data
        if (ullat > MapServer.ROOT_ULLAT) {
            data.put("raster_ul_lat", MapServer.ROOT_ULLAT);
            data.put("y0", 0);
        }
        if (lrlat < MapServer.ROOT_LRLAT) {
            data.put("raster_lr_lat", MapServer.ROOT_LRLON);
            data.put("y1", k - 1);
        }
        if (!data.containsKey("y0") || !data.containsKey("y1")) {
            for (int i = 0; i < k; i += 1) {
                if (!data.containsKey("y0")
                        && ullat <= mutativeULLAT
                        && ullat >= (mutativeULLAT - latIncrement)
                ) {
                    data.put("raster_ul_lat", mutativeULLAT);
                    data.put("y0", i);
                }
                if (!data.containsKey("y1")
                        && lrlat <= mutativeULLAT
                        && lrlat >= (mutativeULLAT - latIncrement)
                ) {
                    data.put("raster_lr_lat", mutativeULLAT - latIncrement);
                    data.put("y1", i);
                }
                mutativeULLAT -= latIncrement;
                if (data.containsKey("y0") && data.containsKey("y1")) {
                    break;
                }
            }
        }

        // finding longitude data
        if (ullon < MapServer.ROOT_ULLON) {
            data.put("raster_ul_lon", MapServer.ROOT_ULLON);
            data.put("x0", 0);
        }
        if (lrlon > MapServer.ROOT_LRLON) {
            data.put("raster_lr_lon", MapServer.ROOT_LRLON);
            data.put("x1", k - 1);
        }
        if (!data.containsKey("x0") || !data.containsKey("x1")) {
            for (int i = 0; i < k; i += 1) {
                if (!data.containsKey("x0")
                        && ullon >= mutativeULLON
                        && ullon <= (mutativeULLON + longIncrement)
                ) {
                    data.put("raster_ul_lon", mutativeULLON);
                    data.put("x0", i);
                }
                if (!data.containsKey("x1")
                        && lrlon >= mutativeULLON
                        && lrlon <= (mutativeULLON + longIncrement)
                ) {
                    data.put("raster_lr_lon", mutativeULLON + longIncrement);
                    data.put("x1", i);
                }
                mutativeULLON += longIncrement;
                if (data.containsKey("x0") && data.containsKey("x1")) {
                    break;
                }
            }
        }
    }

    private void generateRenderGrid(Map<String, Object> data) {
        int x0 = (int) data.get("x0");
        int x1 = (int) data.get("x1");
        int y0 = (int) data.get("y0");
        int y1 = (int) data.get("y1");
        int depth = (int) data.get("depth");
        int row = (y1 - y0) + 1;
        int col = (x1 - x0) + 1;
        String[][] renderGrid = new String[row][col];

        for (int i = 0; i < col; i += 1) {
            for (int ii = 0; ii < row; ii += 1) {
                renderGrid[ii][i] = "d" + depth + "_x" + (x0 + i) + "_y" + (y0 + ii) + ".png";
            }
        }

        data.put("render_grid", renderGrid);
    }
}
