package me.soubhik.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by soubhik on 02-10-2017.
 */
public class RedundantConnectionII {
    static class Solution {
        public static class Pair<T> {
            final T first;
            final T second;

            Pair(T first, T second) {
                this.first = first;
                this.second = second;
            }

            @Override
            public boolean equals(Object that) {
                if (this == that) {
                    return true;
                }

                if (that == null) {
                    return false;
                }

                if (!(that instanceof Pair)) {
                    return false;
                }

                Pair other = (Pair)that;
                return (Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second));
            }

            @Override
            public int hashCode() {
                return Objects.hash(first, second);
            }
        }

        Map<Integer, List<Pair<Integer>>> incomingEdges = new HashMap<>();
        Map<Integer, List<Pair<Integer>>> outgoingEdges = new HashMap<>();
        Set<Pair<Integer>> cycle;
        int nodeWith2Parents = 0;

        public int[] findRedundantDirectedConnection(int[][] edges) {
            return pairToArray(findRedundantEdge(edges));
        }

        public Pair<Integer> findRedundantEdge(int[][] edges) {
            preprocessDigraph(edges);
            detectCycle(edges);

            System.out.println("nodeWith2Parents: " + nodeWith2Parents);
            System.out.println("cycle: ");
            if (cycle != null) {
                for (Pair<Integer> edge: cycle) {
                    System.out.println(edge.first + "-->" + edge.second);
                }
            }

            if ((nodeWith2Parents == 0) && (cycle == null)) {
                throw new RuntimeException("no node with 2 parents, no cycle in digraph");
            }

            if ((nodeWith2Parents > 0) && (cycle != null)) {
                List<Pair<Integer>> parents = incomingEdges.get(nodeWith2Parents);
                for (Pair<Integer> parent: parents) {
                    System.out.println("parent: " + parent.first + "," + parent.second);
                    if (cycle.contains(parent)) {
                        return parent;
                    }
                }
                System.out.println("digraph has a node with 2 parents and a cycle, yet, can't find redundant edge");
                throw new RuntimeException("digraph has a node with 2 parents and a cycle, yet, can't find redundant edge");
            }

            if (cycle != null) {
                for (int i = edges.length - 1; i >= 0; i--) {
                    Pair<Integer> edge = new Pair<>(edges[i][0], edges[i][1]);
                    if (cycle.contains(edge)) {
                        return edge;
                    }
                }
                System.out.println("digraph has a cycle, yet, can't find redundant edge");
                throw new RuntimeException("digraph has a cycle, yet, can't find redundant edge");
            }

            List<Pair<Integer>> parents = incomingEdges.get(nodeWith2Parents);
            return (parents.get(parents.size() - 1));
        }

        private int[] pairToArray(Pair<Integer> pair) {
            int[] array = new int[2];
            array[0] = pair.first;
            array[1] = pair.second;

            return array;
        }

        private void preprocessDigraph(int[][] edges) {
            int numEdges = edges.length;
            for (int i = 0; i < numEdges; i++) {
                int first = edges[i][0];
                int second = edges[i][1];
                Pair<Integer> edge = new Pair<>(first, second);

                int parents = addToMultimap(incomingEdges, second, edge);
                if ((parents == 2) && (nodeWith2Parents == 0)) {
                    nodeWith2Parents = second;
                } else if ((parents == 2) && (nodeWith2Parents > 0)) {
                    throw new RuntimeException("unexpected: more than one nodes with multiple parents found.");
                } else if (parents > 2) {
                    throw new RuntimeException("unexpected: more than 2 parents found.");
                }

                addToMultimap(outgoingEdges, first, edge);
            }
        }

        private int addToMultimap(Map<Integer, List<Pair<Integer>>> map, Integer key, Pair<Integer> value) {
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(value);

            return map.get(key).size();
        }

        private void detectCycle(int[][] edges) {
            Set<Pair<Integer>> edgesInCycle = new HashSet<>();
            for (int i = 0; i < edges.length; i++) {
                Pair<Integer> edge = new Pair<>(edges[i][0], edges[i][1]);
                Set<Pair<Integer>> visitedEdges = new HashSet<>();
                findEdgesInCycle(edge.first, edge, edgesInCycle, visitedEdges);
                if (!edgesInCycle.isEmpty()) {
                    cycle = edgesInCycle;
                    return;
                }
            }
        }

        private Set<Pair<Integer>> findEdgesInCycle(int origin, Pair<Integer> edge, Set<Pair<Integer>> edgesInCycle,
                                                    Set<Pair<Integer>> visitedEdges) {
            if (visitedEdges.contains(edge)) {
                return edgesInCycle;
            }
            visitedEdges.add(edge);

            if (origin == edge.second) {
                edgesInCycle.add(edge);
                return edgesInCycle;
            }

            if (!outgoingEdges.containsKey(edge.second)) {
                return edgesInCycle;
            }
            List<Pair<Integer>> nextEdges = outgoingEdges.get(edge.second);
            for (Pair<Integer> nextEdge: nextEdges) {
                findEdgesInCycle(origin, nextEdge, edgesInCycle, visitedEdges);
                if (!edgesInCycle.isEmpty()) {
                    edgesInCycle.add(edge);
                    return edgesInCycle;
                }
            }

            return edgesInCycle;
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        int[][] edges = {{2,1},{3,1},{4,2},{1,4}};
        solution.findRedundantDirectedConnection(edges);
    }
}
