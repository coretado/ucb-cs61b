package hw4.puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Solver {
    private int moves;
    private PriorityQueue<WorldState> searchNodes;
    private List<WorldState> solution;

    public Solver(WorldState initial) {
        this.searchNodes = new PriorityQueue<>();
        this.searchNodes.add(initial);
        this.solution = new ArrayList<>();
        this.moves = 0;
        while (true) {
            WorldState ws = this.searchNodes.remove();
            this.moves += 1;
            if (ws.estimatedDistanceToGoal() == 0) {
                break;
            }
            for (WorldState wsprime : ws.neighbors()) {
                if (!wsprime.equals(ws)) {
                    this.searchNodes.add(wsprime);
                }
            }
            this.solution.add(ws);
        }
    }

    public int moves() {
        return this.moves;
    }

    public Iterable<WorldState> solution() {
        return this.solution;
    }
}
