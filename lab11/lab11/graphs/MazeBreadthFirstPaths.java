package lab11.graphs;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 *  @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private int s;
    private int t;
    private final Maze maze;
    private boolean targetFound = false;

    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        this.maze = m;
        this.s = maze.xyTo1D(sourceX, sourceY);
        this.t = maze.xyTo1D(targetX, targetY);
        this.distTo[s] = 0;
        this.edgeTo[s] = s;
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs() {
        // TODO: Your code here. Don't forget to update distTo, edgeTo, and marked, as well as call announce()
        Queue<Integer> fringe = new ArrayDeque<>();
        fringe.add(this.s);
        this.marked[s] = true;
        while (!fringe.isEmpty()) {
            int v = fringe.remove();
            for (int w : this.maze.adj(v)) {
                if (!marked[w]) {
                    this.marked[w] = true;
                    this.edgeTo[w] = v;
                    this.distTo[w] = this.distTo[v] + 1;
                    this.announce();
                    if (w == t) {
                        this.targetFound = true;
                    }
                    if (this.targetFound) {
                        break;
                    }
                    fringe.add(w);
                }
            }
            if (this.targetFound) {
                break;
            }
        }
    }


    @Override
    public void solve() {
        bfs();
    }
}

