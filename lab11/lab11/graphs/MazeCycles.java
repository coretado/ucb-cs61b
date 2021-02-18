package lab11.graphs;

/**
 *  @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private final Maze maze;
    private boolean cycleFound = false;

    public MazeCycles(Maze m) {
        super(m);
        this.maze = m;
        this.edgeTo[0] = 0;
    }

    @Override
    public void solve() {
        // TODO: Your code here!
        this.DFSCycleDetector(0);
    }

    // Helper methods go here
    private void DFSCycleDetector(int v) {
        // v is 0; default starting at bottom left for cycle detection; leaving var name for readability
        this.marked[v] = true;
        announce();

        if (this.cycleFound) {
            return;
        }

        for (int u : this.maze.adj(v)) {
            if (!marked[u]) {
                this.edgeTo[u] = v;
                announce();
                this.DFSCycleDetector(u);
                if (this.cycleFound) {
                    return;
                }
            } else if (this.marked[u] && this.edgeTo[v] != u) {
                this.cycleFound = true;
                this.markCycle(u, v);
                return;
            }
        }
    }

    private void markCycle(int parent, int offender) {
        int size = this.edgeTo.length;
        // resetting edgeTo
        for (int i = 0; i < size; i += 1) {
            this.edgeTo[i] = Integer.MAX_VALUE;
        }
        this.edgeTo[parent] = offender;
        announce();
    }
}

