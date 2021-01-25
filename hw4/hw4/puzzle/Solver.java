package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;

public class Solver {
    private final Queue<WorldState> solution;

    public Solver(WorldState initial) {
        MinPQ<SearchNode> searchNodes = new MinPQ<>();
        this.solution = new Queue<>();
        searchNodes.insert(new SearchNode(initial, 0, null));

        SearchNode state;

        while (true) {
            SearchNode min = searchNodes.delMin();
            if (min.worldState.isGoal()) {
                state = min;
                break;
            }
            if (min.previousNode == null) {
                for (WorldState sp : min.worldState.neighbors()) {
                    searchNodes.insert(new SearchNode(sp, min.moves + 1, min));
                }
            } else {
                for (WorldState sp : min.worldState.neighbors()) {
                    WorldState minWorldState = min.previousNode.worldState;
                    if (!sp.equals(minWorldState)) {
                        searchNodes.insert(new SearchNode(sp, min.moves + 1, min));
                    }
                }
            }
        }

        for ( ; state != null; state = state.previousNode) {
            this.solution.enqueue(state.worldState);
        }
    }

    private static class SearchNode implements Comparable<SearchNode> {
        private final WorldState worldState;
        private final int moves;
        private final SearchNode previousNode;
        private final int priority;

        public SearchNode(
            WorldState worldState,
            int moves,
            SearchNode previousNode
        ) {
            this.worldState = worldState;
            this.moves = moves;
            this.previousNode = previousNode;
            this.priority = worldState.estimatedDistanceToGoal() + moves;
        }

        public int compareTo(SearchNode O) {
            return this.priority - O.priority;
        }
    }

    public int moves() {
        return this.solution.size() - 1;
    }

    public Iterable<WorldState> solution() {
        return this.solution;
    }
}
