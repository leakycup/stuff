package me.soubhik;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created by sb8 on 9/29/18.
 */
public class GraphAlgorithms {
    public static class Node {
        private int id;
        private Map<Integer, Integer> adjacency;

        public Node(int id) {
            this.id = id;
            this.adjacency = new HashMap<>();
        }

        public void addNeighbor(int id, int weight) {
            adjacency.put(id, weight);
        }

        public Set<Integer> neighbors() {
            return adjacency.keySet();
        }

        public int getWeight(int id) {
            if (adjacency.containsKey(id)) {
                return adjacency.get(id);
            }

            return -1;
        }

        public int getId() {
            return id;
        }
    }

    public static class Graph {
        private List<Node> nodes;

        public Graph(int numNodes, Triple<Integer, Integer, Integer>[] edges) {
            this.nodes = new ArrayList<>(numNodes);
            for (int nodeId=0; nodeId < numNodes; nodeId++) {
                Node node = new Node(nodeId);
                nodes.add(nodeId, node);
            }

            for (Triple<Integer, Integer, Integer> edge: edges) {
                int left = edge.getLeft();
                int right = edge.getMiddle();
                int weight = edge.getRight();
                nodes.get(left).addNeighbor(right, weight);
                nodes.get(right).addNeighbor(left, weight);
            }
        }

        public Node getNode(int nodeId) {
            return nodes.get(nodeId);
        }

        public List<Integer> preorderWalk(int source) {
            List<Integer> nodeList = new ArrayList<>(nodes.size());

            Deque<Integer> nodeStack = new LinkedList<>();
            nodeStack.push(source);
            Set<Integer> visited = new HashSet<>();
            while (!nodeStack.isEmpty()) {
                int nodeId = nodeStack.pop();
                if (visited.contains(nodeId)) {
                    continue;
                }
                nodeList.add(nodeId);
                visited.add(nodeId);
                Node node = nodes.get(nodeId);
                for (int neighborId: node.neighbors()) {
                    nodeStack.push(neighborId);
                }
            }

            return nodeList;
        }

        public Iterable<Node> getAllNodes() {
            return Collections.unmodifiableCollection(nodes);
        }
    }

    public static class NodeInfo implements Comparable<NodeInfo> {
        private Node node;
        private int distance;
        private int predecessor;
        private boolean visited;

        public NodeInfo(Node node) {
            this.node = node;
            this.distance = Integer.MAX_VALUE;
            this.predecessor = -1;
            this.visited = false;
        }

        public boolean update(int distance, int predecessor) {
            if (distance < this.distance) {
                this.distance = distance;
                this.predecessor = predecessor;
                return true;
            }

            return false;
        }

        public void visit() {
            this.visited = true;
        }

        @Override
        public int compareTo(NodeInfo that) {
            return (this.distance - that.distance);
        }
    }

    public static class Dijkstra {
        PriorityQueue<NodeInfo> nodeInfoHeap;
        List<NodeInfo> nodeInfoList;

        public Dijkstra(Graph graph, int source) {
            this.nodeInfoHeap = new PriorityQueue<>();
            this.nodeInfoList = new ArrayList<>();
            for (Node node: graph.getAllNodes()) {
                NodeInfo nodeInfo = new NodeInfo(node);
                int id = node.getId();
                if (id == source) {
                    nodeInfo.update(0, -1);
                }
                nodeInfoHeap.add(nodeInfo);
                nodeInfoList.add(id, nodeInfo);
            }
        }

        public Map<Integer, List<Integer>> singleSourceShortestPath() {
            NodeInfo current;
            while ((current = nodeInfoHeap.poll()) != null) {
                /*
                if (current.visited) {
                    continue;
                }
                current.visited = true;
                */
                int currentId = current.node.getId();
                int distance = current.distance;
                for (Integer neighborId: current.node.neighbors()) {
                    int newDistance = distance + current.node.getWeight(neighborId);
                    NodeInfo neighborNodeInfo = nodeInfoList.get(neighborId);
                    boolean updated = neighborNodeInfo.update(newDistance, currentId);
                    if (updated) {
                        nodeInfoHeap.remove(neighborNodeInfo);
                        nodeInfoHeap.add(neighborNodeInfo);
                    }
                }
            }

            Map<Integer, List<Integer>> shortestPathMap = new HashMap<>();
            for (NodeInfo nodeInfo: nodeInfoList) {
                List<Integer> path = new ArrayList<>();
                int id = nodeInfo.node.getId();
                path.add(id);
                int predecessor = nodeInfo.predecessor;
                while (predecessor != -1) {
                    if (shortestPathMap.containsKey(predecessor)) {
                        path.addAll(shortestPathMap.get(predecessor));
                        break;
                    }
                    path.add(predecessor);
                    NodeInfo predecessorNodeInfo = nodeInfoList.get(predecessor);
                    predecessor = predecessorNodeInfo.predecessor;
                }
                shortestPathMap.put(id, path);
            }
            return shortestPathMap;
        }
    }

    public static class Prim {
        PriorityQueue<NodeInfo> nodeInfoHeap;
        List<NodeInfo> nodeInfoList;

        public Prim(Graph graph, int source) {
            this.nodeInfoHeap = new PriorityQueue<>();
            this.nodeInfoList = new ArrayList<>();
            for (Node node: graph.getAllNodes()) {
                NodeInfo nodeInfo = new NodeInfo(node);
                int id = node.getId();
                if (id == source) {
                    nodeInfo.update(0, -1);
                }
                nodeInfoHeap.add(nodeInfo);
                nodeInfoList.add(id, nodeInfo);
            }
        }

        public Triple<Integer, Integer, Integer>[] mst() {
            NodeInfo current;
            while ((current = nodeInfoHeap.poll()) != null) {
                current.visit();
                int currentId = current.node.getId();
                for (Integer neighborId: current.node.neighbors()) {
                    NodeInfo neighborNodeInfo = nodeInfoList.get(neighborId);
                    if (neighborNodeInfo.visited) {
                        continue;
                    }
                    int newDistance = current.node.getWeight(neighborId);
                    boolean updated = neighborNodeInfo.update(newDistance, currentId);
                    if (updated) {
                        nodeInfoHeap.remove(neighborNodeInfo);
                        nodeInfoHeap.add(neighborNodeInfo);
                    }
                }
            }

            Triple<Integer, Integer, Integer>[] edges = new Triple[nodeInfoList.size() - 1];
            int i = 0;
            for (NodeInfo nodeInfo: nodeInfoList) {
                int left = nodeInfo.predecessor;
                if (left < 0) {
                    continue;
                }
                int right = nodeInfo.node.getId();
                int weight = nodeInfo.node.getWeight(left);
                Triple<Integer, Integer, Integer> edge = new ImmutableTriple<>(left, right, weight);
                edges[i] = edge;
                i++;
            }

            return edges;
        }
    }

    public static void printEdges(Triple<Integer, Integer, Integer>[] edges) {
        System.out.println("=====================Graph edges=======================");
        for (Triple<Integer, Integer, Integer> edge: edges) {
            System.out.println("(" + edge.getLeft() + ", " + edge.getMiddle() + "): " + edge.getRight());
        }
    }

    public static void testDijkstra(int numNodes, Triple<Integer, Integer, Integer>[] edges) {
        printEdges(edges);
        Graph graph = new Graph(numNodes, edges);
        for (int source=0; source < numNodes; source++) {
            System.out.println("=====testDijkstra(): source=" + source + "=============");
            Dijkstra dijkstra = new Dijkstra(graph, source);
            Map<Integer, List<Integer>> shortestPathMap = dijkstra.singleSourceShortestPath();
            for (Integer nodeId: shortestPathMap.keySet()) {
                System.out.print("(" + source + ", " + nodeId + "): ");
                List<Integer> path = shortestPathMap.get(nodeId);
                for (int i=path.size() - 1; i >= 0; i--) {
                    System.out.print(path.get(i) + " ");
                }
                System.out.print("\n");
            }
        }
        System.out.println("=====testDijkstra(): DONE =============");
    }

    public static void testPrim(int numNodes, Triple<Integer, Integer, Integer>[] edges) {
        printEdges(edges);
        Graph graph = new Graph(numNodes, edges);
        for (int source=0; source < numNodes; source++) {
            System.out.println("=====testPrim(): source=" + source + "=============");
            Prim prim = new Prim(graph, source);
            Triple<Integer, Integer, Integer>[] mstEdges = prim.mst();
            printEdges(mstEdges);
        }
        System.out.println("=====testPrim(): DONE =============");
    }

    public static void testPreorderWalk(int numNodes, Triple<Integer, Integer, Integer>[] edges) {
        printEdges(edges);
        Graph graph = new Graph(numNodes, edges);
        for (int source=0; source < numNodes; source++) {
            System.out.println("=====testPreorderWalk(): source=" + source + "=============");
            List<Integer> preorder = graph.preorderWalk(source);
            for (int nodeId: preorder) {
                System.out.print(nodeId + " ");
            }
            System.out.print("\n");
        }
        System.out.println("=====testPreorderWalk(): DONE =============");
    }

    public static void main(String[] args) {
        Triple<Integer, Integer, Integer>[] edges1 = new Triple[3];
        edges1[0] = new ImmutableTriple<>(0, 1, 10);
        edges1[1] = new ImmutableTriple<>(0, 2, 20);
        edges1[2] = new ImmutableTriple<>(1, 2, 5);
        testDijkstra(3, edges1);

        Triple<Integer, Integer, Integer>[] edges2 = new Triple[0];
        testDijkstra(1, edges2);

        Triple<Integer, Integer, Integer>[] edges3 = new Triple[1];
        edges3[0] = new ImmutableTriple<>(0, 1, 10);
        testDijkstra(2, edges3);

        Triple<Integer, Integer, Integer>[] edges4 = new Triple[3];
        edges4[0] = new ImmutableTriple<>(0, 1, 10);
        edges4[1] = new ImmutableTriple<>(1, 2, 20);
        edges4[2] = new ImmutableTriple<>(1, 3, 15);
        testDijkstra(4, edges4);

        Triple<Integer, Integer, Integer>[] edges5 = new Triple[4];
        edges5[0] = new ImmutableTriple<>(0, 1, 10);
        edges5[1] = new ImmutableTriple<>(1, 2, 20);
        edges5[2] = new ImmutableTriple<>(1, 3, 15);
        edges5[3] = new ImmutableTriple<>(3, 2, 6);
        testDijkstra(4, edges5);

        Triple<Integer, Integer, Integer>[] edges6 = new Triple[4];
        edges6[0] = new ImmutableTriple<>(0, 1, 10);
        edges6[1] = new ImmutableTriple<>(1, 2, 20);
        edges6[2] = new ImmutableTriple<>(1, 3, 15);
        edges6[3] = new ImmutableTriple<>(3, 2, 2);
        testDijkstra(4, edges6);

        Triple<Integer, Integer, Integer>[] edges7 = new Triple[5];
        edges7[0] = new ImmutableTriple<>(0, 1, 10);
        edges7[1] = new ImmutableTriple<>(1, 2, 20);
        edges7[2] = new ImmutableTriple<>(1, 3, 15);
        edges7[3] = new ImmutableTriple<>(3, 2, 6);
        edges7[4] = new ImmutableTriple<>(0, 3, 5);
        testDijkstra(4, edges7);

        Triple<Integer, Integer, Integer>[] edges8 = new Triple[6];
        edges8[0] = new ImmutableTriple<>(0, 1, 10);
        edges8[1] = new ImmutableTriple<>(1, 2, 20);
        edges8[2] = new ImmutableTriple<>(1, 3, 15);
        edges8[3] = new ImmutableTriple<>(3, 2, 6);
        edges8[4] = new ImmutableTriple<>(0, 3, 5);
        edges8[5] = new ImmutableTriple<>(2, 0, 1);
        testDijkstra(4, edges8);

        Triple<Integer, Integer, Integer>[] edges9 = new Triple[3];
        edges9[0] = new ImmutableTriple<>(0, 1, 10);
        edges9[1] = new ImmutableTriple<>(0, 2, 20);
        edges9[2] = new ImmutableTriple<>(1, 2, 5);
        testPrim(3, edges9);

        Triple<Integer, Integer, Integer>[] edges10 = new Triple[6];
        edges10[0] = new ImmutableTriple<>(0, 1, 20);
        edges10[1] = new ImmutableTriple<>(0, 2, 10);
        edges10[2] = new ImmutableTriple<>(0, 3, 25);
        edges10[3] = new ImmutableTriple<>(1, 2, 15);
        edges10[4] = new ImmutableTriple<>(1, 3, 5);
        edges10[5] = new ImmutableTriple<>(2, 3, 20);
        testPrim(4, edges10);

        Triple<Integer, Integer, Integer>[] edges11 = new Triple[6];
        edges11[0] = new ImmutableTriple<>(0, 1, 20);
        edges11[1] = new ImmutableTriple<>(0, 2, 10);
        edges11[2] = new ImmutableTriple<>(1, 2, 15);
        edges11[3] = new ImmutableTriple<>(0, 3, 25);
        edges11[4] = new ImmutableTriple<>(2, 3, 20);
        edges11[5] = new ImmutableTriple<>(1, 3, 5);
        testPrim(4, edges11);

        Triple<Integer, Integer, Integer>[] edges12 = new Triple[6];
        edges12[0] = new ImmutableTriple<>(0, 1, 12);
        edges12[1] = new ImmutableTriple<>(0, 2, 10);
        edges12[2] = new ImmutableTriple<>(0, 3, 25);
        edges12[3] = new ImmutableTriple<>(1, 2, 15);
        edges12[4] = new ImmutableTriple<>(1, 3, 12);
        edges12[5] = new ImmutableTriple<>(2, 3, 20);
        testPrim(4, edges12);

        Triple<Integer, Integer, Integer>[] edges13 = new Triple[6];
        edges13[0] = new ImmutableTriple<>(0, 1, 12);
        edges13[1] = new ImmutableTriple<>(0, 2, 10);
        edges13[2] = new ImmutableTriple<>(1, 2, 15);
        edges13[3] = new ImmutableTriple<>(0, 3, 25);
        edges13[4] = new ImmutableTriple<>(2, 3, 20);
        edges13[5] = new ImmutableTriple<>(1, 3, 12);
        testPrim(4, edges13);

        Triple<Integer, Integer, Integer>[] edges14 = new Triple[0];
        testPreorderWalk(1, edges14);

        Triple<Integer, Integer, Integer>[] edges15 = new Triple[1];
        edges15[0] = new ImmutableTriple<>(0, 1, 12);
        testPreorderWalk(2, edges15);

        Triple<Integer, Integer, Integer>[] edges16 = new Triple[2];
        edges16[0] = new ImmutableTriple<>(0, 1, 12);
        edges16[1] = new ImmutableTriple<>(1, 2, 14);
        testPreorderWalk(3, edges16);

        Triple<Integer, Integer, Integer>[] edges17 = new Triple[2];
        edges17[0] = new ImmutableTriple<>(0, 1, 12);
        edges17[1] = new ImmutableTriple<>(0, 2, 14);
        testPreorderWalk(3, edges17);

        Triple<Integer, Integer, Integer>[] edges18 = new Triple[3];
        edges18[0] = new ImmutableTriple<>(0, 1, 12);
        edges18[1] = new ImmutableTriple<>(0, 2, 14);
        edges18[2] = new ImmutableTriple<>(1, 2, 10);
        testPreorderWalk(3, edges18);

        Triple<Integer, Integer, Integer>[] edges19 = new Triple[3];
        edges19[0] = new ImmutableTriple<>(0, 1, 12);
        edges19[1] = new ImmutableTriple<>(0, 2, 14);
        edges19[2] = new ImmutableTriple<>(0, 3, 10);
        testPreorderWalk(4, edges19);

        Triple<Integer, Integer, Integer>[] edges20 = new Triple[4];
        edges20[0] = new ImmutableTriple<>(0, 1, 12);
        edges20[1] = new ImmutableTriple<>(0, 2, 14);
        edges20[2] = new ImmutableTriple<>(0, 3, 10);
        edges20[3] = new ImmutableTriple<>(1, 3, 12);
        testPreorderWalk(4, edges20);
    }
}
