package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;

import java.util.*;

public class Solver {
    private int moves;
    private final Deque<WorldState> solution;
    // private int enqueuecounter = 1;

    public Solver(WorldState initial) {
        // setup
        MinPQ<SearchNode> searchNodes = new MinPQ<>();
        this.solution = new ArrayDeque<>();
        this.moves = 0;
        searchNodes.insert(new SearchNode(initial, this.moves, null));
        // System.out.println("enqueue: " + this.enqueuecounter);

        // first node
        SearchNode state = searchNodes.delMin();
        if (state.worldState.estimatedDistanceToGoal() == 0) {
            // System.out.println("enqueue: " + (++this.enqueuecounter));
            this.solution.add(state.worldState);
        } else {
            for (WorldState statePrime : state.worldState.neighbors()) {
                searchNodes.insert(new SearchNode(statePrime, 1, state));
            }
            // continuing search if initial node isn't goal
            while (true) {
                state = searchNodes.delMin();
                this.moves += 1;
                if (state.worldState.estimatedDistanceToGoal() == 0) {
                    this.solution.add(state.worldState);
                    break;
                }
                for (WorldState statePrime : state.worldState.neighbors()) {
                    if (!statePrime.equals(state.previousNode.worldState)) {
                        // System.out.println("enqueue: " + (++this.enqueuecounter));
                        searchNodes.insert(new SearchNode(statePrime, this.moves, state));
                    }
                }
                this.solution.add(state.worldState);
            }
        }
    }

    private static class SearchNode implements Comparable<SearchNode> {
        private final WorldState worldState;
        private final int moves;
        private final SearchNode previousNode;

        public SearchNode(
            WorldState worldState,
            int moves,
            SearchNode searchNode
        ) {
            this.worldState = worldState;
            this.moves = moves;
            this.previousNode = searchNode;
        }

        public int compareTo(SearchNode O) {
            int tPrio = this.worldState.estimatedDistanceToGoal() + this.moves;
            int oPrio = O.worldState.estimatedDistanceToGoal() + O.moves;
            return Integer.compare(tPrio, oPrio);
        }
    }

    public int moves() {
        return this.moves;
    }

    public Iterable<WorldState> solution() {
        return this.solution;
    }
}
