package me.soubhik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by soubhik on 6/25/16.
 */
public class BinaryTreeTraversals {
    public static class Node<T> {
        T info;
        Node<T> left;
        Node<T> right;

        Node(T info, Node<T> left, Node<T> right) {
            this.info = info;
            this.left = left;
            this.right = right;
        }
    }

    public static class ComparableNode<T extends Comparable<T>> extends Node<T> implements Comparable<ComparableNode<T>> {
        ComparableNode(T info, ComparableNode<T> left, ComparableNode<T> right) {
            super(info, left, right);
        }

        @Override
        public int compareTo(ComparableNode<T> that) {
            return this.info.compareTo(that.info);
        }

        public ComparableNode<T> getLeft() {
            return (ComparableNode<T>)left;
        }

        public ComparableNode<T> getRight() {
            return (ComparableNode<T>)right;
        }
    }

    public static interface Visitor<T> {
        public void visit(T info);
    }

    public static class Stack<T> {
        LinkedList<T> elements = new LinkedList<T>();

        public void push(T element) {
            elements.add(element);
        }

        public T pop() {
            int numElements = elements.size();
            if (numElements == 0) {
                return null;
            }
            return elements.remove(numElements-1);
        }

        public boolean isEmpty() {
            return elements.isEmpty();
        }
    }

    public static class Queue<T> {
        LinkedList<T> elements = new LinkedList<T>();

        public void add(T element) {
            elements.add(element);
        }

        public T delete() {
            int numElements = elements.size();
            if (numElements == 0) {
                return null;
            }
            return elements.remove(0);
        }

        public boolean isEmpty() {
            return elements.isEmpty();
        }
    }

    // see https://en.wikipedia.org/wiki/Binary_heap
    public static class Heap<T extends Comparable<T>> {
        ArrayList<T> elements = new ArrayList<T>();

        public void add(T element) {
            elements.add(element);

            int numElements = elements.size();
            if (numElements == 1) {
                return;
            }

            int currentIndex = numElements - 1;
            T current = element;
            int parantIndex = (currentIndex - 1) / 2;
            T parent = elements.get(parantIndex);
            while (parent.compareTo(current) < 0) {
                elements.set(parantIndex, current);
                elements.set(currentIndex, parent);

                currentIndex = parantIndex;
                current = parent; //bug?
                parantIndex = (currentIndex - 1) / 2;
                if (parantIndex < 0) {
                    break;
                }
                parent = elements.get(parantIndex);
            }
        }

        public T delete() {
            if (isEmpty()) {
                return null;
            }

            int numElements = elements.size();
            if (numElements == 1) {
                return elements.remove(0);
            }

            T element = elements.get(0);
            T current = elements.remove(numElements - 1);
            numElements--;
            elements.set(0, current);
            int currentIndex = 0;

            while ((currentIndex = swapWithLargerChild(currentIndex)) > 0) {
            }

            return element;
        }

        private T getElement(int index) {
            if (index < elements.size()) {
                return elements.get(index);
            }

            return null;
        }

        private int swapWithLargerChild(int index) {
            T parent = getElement(index);
            if (parent == null) {
                return -1;
            }

            T leftChild = getElement(2*index + 1);
            if (leftChild == null) {
                return -1;
            }

            T rightChild = getElement(2*index + 2);
            T largerChild = leftChild;
            int largerChildIndex = 2*index + 1;
            if ((rightChild != null) && leftChild.compareTo(rightChild) < 0) {
                largerChild = rightChild;
                largerChildIndex = 2*index + 2;
            }

            if (parent.compareTo(largerChild) < 0) {
                elements.set(index, largerChild);
                elements.set(largerChildIndex, parent);
                return largerChildIndex;
            }

            return -1;
        }

        public boolean isEmpty() {
            return elements.isEmpty();
        }
    }

    public static <T> void dfs(Node<T> root) {
        dfs(root, new Visitor<T>() {
            @Override
            public void visit(T info) {
               System.out.println(info.toString());
            }
        });
    }

    public static <T> void dfs(Node<T> root, Visitor<T> visitor) {
        if (root == null) {
            return;
        }
        if (visitor == null) {
            return;
        }

        Stack<Node<T>> stack = new Stack<Node<T>>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node<T> node = stack.pop();
            visitor.visit(node.info);
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }
    }

    public static <T> void bfs(Node<T> root, Visitor<T> visitor) {
        if (root == null) {
            return;
        }
        if (visitor == null) {
            return;
        }

        Queue<Node<T>> queue = new Queue<Node<T>>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node<T> node = queue.delete();
            visitor.visit(node.info);
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        }
    }

    public static <T extends Comparable<T>> void bestFirstSearch(ComparableNode<T> root, Visitor<T> visitor) {
        if (root == null) {
            return;
        }
        if (visitor == null) {
            return;
        }

        Heap<ComparableNode<T>> queue = new Heap<ComparableNode<T>>();
        queue.add(root);
        while (!queue.isEmpty()) {
            ComparableNode<T> node = queue.delete();
            visitor.visit(node.info);
            if (node.left != null) {
                queue.add(node.getLeft());
            }
            if (node.right != null) {
                queue.add(node.getRight());
            }
        }
    }

    public static <T> void preOrderRecursive(Node<T> root, Visitor<T> visitor) {
        if (root == null) {
            return;
        }
        if (visitor == null) {
            return;
        }

        visitor.visit(root.info);
        preOrderRecursive(root.left, visitor);
        preOrderRecursive(root.right, visitor);
    }

    public static <T> void inOrderRecursive(Node<T> root, Visitor<T> visitor) {
        if (root == null) {
            return;
        }
        if (visitor == null) {
            return;
        }

        inOrderRecursive(root.left, visitor);
        visitor.visit(root.info);
        inOrderRecursive(root.right, visitor);
    }

    public static <T> void postOrderRecursive(Node<T> root, Visitor<T> visitor) {
        if (root == null) {
            return;
        }
        if (visitor == null) {
            return;
        }

        postOrderRecursive(root.left, visitor);
        postOrderRecursive(root.right, visitor);
        visitor.visit(root.info);
    }

    private static class NodeContext<T> {
        Node<T> node;
        int verticalTab;
        int horizontalTab;

        NodeContext(Node<T> node, int verticalTab, int horizontalTab) {
            this.node = node;
            this.verticalTab = verticalTab;
            this.horizontalTab = horizontalTab;
        }
    }

    public static <T> void bfsWithNodeContext(Node<T> root, Visitor<NodeContext<T>> visitor) {
        if (root == null) {
            return;
        }
        if (visitor == null) {
            return;
        }

        NodeContext<T> rootContext = new NodeContext<T>(root, 0, 0);
        bfsWithNodeContext(rootContext, visitor);
    }

    public static <T> void bfsWithNodeContext(NodeContext<T> root, Visitor<NodeContext<T>> visitor) {
        if ((root == null) || (root.node == null)) {
            return;
        }
        if (visitor == null) {
            return;
        }

        Queue<NodeContext<T>> queue = new Queue<NodeContext<T>>();
        queue.add(root);
        while (!queue.isEmpty()) {
            NodeContext<T> nodeContext = queue.delete();
            visitor.visit(nodeContext);
            Node<T> node = nodeContext.node;
            int vTab = nodeContext.verticalTab + 1;
            if (node.left != null) {
                int hTab = nodeContext.horizontalTab - 1;
                NodeContext<T> childContext = new NodeContext<T>(node.left, vTab, hTab);
                queue.add(childContext);
            }
            if (node.right != null) {
                int hTab = nodeContext.horizontalTab + 1;
                NodeContext<T> childContext = new NodeContext<T>(node.right, vTab, hTab);
                queue.add(childContext);
            }
        }
    }

    public static <T> void enumaratePaths(Node<T> root) {
        if (root == null) {
            System.out.println("<empty>");
            return;
        }
        enumaratePaths(root, new Stack<T>());
    }

    public static <T> void enumaratePaths(Node<T> root, Stack<T> pathNodes) {
        pathNodes.push(root.info);
        if ((root.left == null) && (root.right == null)) {
            printStackNodes(pathNodes);
            pathNodes.pop();
            return;
        }

        if (root.left != null) {
            enumaratePaths(root.left, pathNodes);
        }
        if (root.right != null) {
            enumaratePaths(root.right, pathNodes);
        }

        pathNodes.pop();
    }

    private static <T> void printStackNodes(Stack<T> pathNodes) {
        System.out.println("from root to a leaf:");
        for (T element: pathNodes.elements) {
            System.out.println(element);
        }
    }

    public static class ParentedNode<T> extends Node<T> {
        ParentedNode<T> parent;

        ParentedNode(T info, ParentedNode<T> left, ParentedNode<T> right, ParentedNode<T> parent) {
            super(info, left, right);
            this.parent = parent;
        }

        ParentedNode(Node<T> node, ParentedNode<T> parent) {
            super(node.info, node.left, node.right);
            this.parent = parent;
        }
    }

    public static <T> void lca(ParentedNode<T> u, ParentedNode<T> v) {
        if ((u == null) || (v == null)) {
            System.err.println("Invalid argument for lca: can't be null");
            return;
        }

        List<Node<T>> uToRoot = findPathToRoot(u);
        List<Node<T>> vToRoot = findPathToRoot(v);
        List<Node<T>> longestSuffix = findLongestSuffix(uToRoot, vToRoot);
        if (longestSuffix.isEmpty()) {
            System.err.println("Invalid argument for lca: none found");
            return;
        }
        System.out.println("LCA of " + u.info + " and " + v.info + " is: " +
                longestSuffix.get(longestSuffix.size() - 1).info);
    }

    private static <T> List<Node<T>> findPathToRoot(ParentedNode<T> node) {
        List<Node<T>> toRoot = new LinkedList<Node<T>>();
        toRoot.add(node);
        ParentedNode<T> current = node.parent;
        while (current != null) {
            toRoot.add(current);
            current = current.parent;
        }

        return toRoot;
    }

    private static <T> List<T> findLongestSuffix(List<T> l1, List<T> l2) {
        List<T> commonSuffix = new LinkedList<T>();

        for (int i = l1.size()-1, j = l2.size()-1; (i >= 0) && (j >= 0); i--, j--) {
            T item1 = l1.get(i);
            T item2 = l2.get(j);
            if (item1 == item2) {
                commonSuffix.add(item1);
            } else {
                break;
            }
        }

        return commonSuffix;
    }

    private static Visitor<String> printNode = new Visitor<String>() {
        @Override
        public void visit(String info) {
            System.out.println(info);
        }
    };

    private static interface VisitorAggregator<T> extends Visitor<T> {
        public void aggregate();
    }

    private static class PrettyPrinter<T> implements VisitorAggregator<NodeContext<T>> {
        ArrayList<NodeContext<T>> nodes = new ArrayList<NodeContext<T>>();

        @Override
        public void visit(NodeContext<T> nodeContext) {
            nodes.add(nodeContext);
        }

        @Override
        public void aggregate() {
            int minLeftTab = 0;
            for (NodeContext<T> nodeContext: nodes) {
                int leftTab = nodeContext.horizontalTab;
                if (leftTab < minLeftTab) {
                    minLeftTab = leftTab;
                }
            }

            if (minLeftTab < 0) {
                minLeftTab = -minLeftTab;
                for (NodeContext<T> nodeContext: nodes) {
                    nodeContext.horizontalTab += minLeftTab;
                }
            }

            int previousVTab = 0;
            int previousHTab = 0;
            for (NodeContext<T> nodeContext: nodes) {
                int vTab = nodeContext.verticalTab;
                if (vTab > previousVTab) {
                    System.out.print("\n");
                    previousHTab = 0;
                }
                previousVTab = vTab;

                int hTab = nodeContext.horizontalTab;
                for (int i = previousHTab; i < hTab; i++) {
                    System.out.print("    ");
                }
                previousHTab = hTab;

                System.out.print(nodeContext.node.info + "," + vTab + "," + hTab);
            }
            System.out.print("\n");
        }
    }

    private static VisitorAggregator<NodeContext<Integer>> summer = new VisitorAggregator<NodeContext<Integer>>() {
        ArrayList<NodeContext<Integer>> nodes = new ArrayList<NodeContext<Integer>>();

        @Override
        public void aggregate() {
            Map<Integer, Integer> horizontalSum = new HashMap<Integer, Integer>();
            Map<Integer, Integer> verticalSum = new HashMap<Integer, Integer>();

            for (NodeContext<Integer> nodeContext: nodes) {
                Integer value = nodeContext.node.info;
                int vTab = nodeContext.verticalTab;
                int hTab = nodeContext.horizontalTab;

                if (!horizontalSum.containsKey(vTab)) {
                    horizontalSum.put(vTab, 0);
                }
                Integer hSum = horizontalSum.get(vTab);
                hSum += value;
                horizontalSum.put(vTab, hSum);

                if (!verticalSum.containsKey(hTab)) {
                    verticalSum.put(hTab, 0);
                }
                Integer vSum = verticalSum.get(hTab);
                vSum += value;
                verticalSum.put(hTab, vSum);
            }

            System.out.println("Horizontal sum");
            System.out.println("===========================");
            for (Integer key: horizontalSum.keySet()) {
                Integer value = horizontalSum.get(key);
                System.out.println(key + ":" + value);
            }

            System.out.println("Vertical sum");
            System.out.println("===========================");
            for (Integer key: verticalSum.keySet()) {
                Integer value = verticalSum.get(key);
                System.out.println(key + ":" + value);
            }
        }

        @Override
        public void visit(NodeContext<Integer> nodeContext) {
            nodes.add(nodeContext);
        }
    };

    public static void main(String[] args) {
        Node<String> a = new Node<String>("A", null, null);
        Node<String> b = new Node<String>("B", null, null);
        Node<String> c = new Node<String>("C", a, b);
        Node<String> d = new Node<String>("D", null, null);
        Node<String> e = new Node<String>("E", null, d);
        Node<String> f = new Node<String>("F", c, e);

        Node<String> root = f;

        System.out.println("DFS (pre-order): ");
        System.out.println("==============");
        dfs(root);

        System.out.println("BFS: ");
        System.out.println("==============");
        bfs(root, printNode);

        System.out.println("Pre-order (recursive): ");
        System.out.println("==============");
        preOrderRecursive(root, printNode);

        System.out.println("Post-order (recursive): ");
        System.out.println("==============");
        postOrderRecursive(root, printNode);

        System.out.println("In-order (recursive): ");
        System.out.println("==============");
        inOrderRecursive(root, printNode);

        PrettyPrinter<String> prettyPrint = new PrettyPrinter();
        System.out.println("Pretty print");
        System.out.println("==============");
        bfsWithNodeContext(root, prettyPrint);
        prettyPrint.aggregate();

        Node<Integer> six = new Node<Integer>(6, null, null);
        Node<Integer> eight = new Node<Integer>(8, null, six);
        Node<Integer> fourL = new Node<Integer>(4, null, null);
        Node<Integer> fourR = new Node<Integer>(4, null, null);
        Node<Integer> two = new Node<Integer>(2, fourL, fourR);
        Node<Integer> five = new Node<Integer>(5, eight, two);

        Node<Integer> root2 = five;

        PrettyPrinter<Integer> prettyPrint2 = new PrettyPrinter();
        System.out.println("Pretty print2");
        System.out.println("==============");
        bfsWithNodeContext(root2, prettyPrint2);
        prettyPrint2.aggregate();

        System.out.println("Sums");
        System.out.println("==============");
        bfsWithNodeContext(root2, summer);
        summer.aggregate();

        System.out.println("All paths");
        System.out.println("==============");
        enumaratePaths(root);

        System.out.println("All paths 2");
        System.out.println("==============");
        enumaratePaths(root2);

        ParentedNode<String> pf = new ParentedNode<String>(f, null);
        ParentedNode<String> pc = new ParentedNode<String>(c, pf);
        ParentedNode<String> pe = new ParentedNode<String>(e, pf);
        ParentedNode<String> pa = new ParentedNode<String>(a, pc);
        ParentedNode<String> pb = new ParentedNode<String>(b, pc);
        ParentedNode<String> pd = new ParentedNode<String>(d, pe);
        lca(pa, pb);
        lca(pa, pc);
        lca(pa, pa);
        lca(pb, pd);
        lca(pc, pd);
        lca(pf, pb);
        lca(pe, null);
        lca(pc, new ParentedNode<String>(d, null));

        Heap<Integer> heap = new Heap<Integer>();
        int[] numbers = new int[] {8, 4, 3, 2, 4, 6};
        System.out.println("Heap test");
        System.out.println("==============");
        for (int n: numbers) {
            System.out.print(n + " ");
            heap.add(n);
        }
        System.out.print("\n");
        Integer n;
        while ((n = heap.delete()) != null) {
            System.out.print(n + " ");
        }
        System.out.print("\n");

        ComparableNode<String> ca = new ComparableNode<String>(a.info, null, null);
        ComparableNode<String> cb = new ComparableNode<String>(b.info, null, null);
        ComparableNode<String> cd = new ComparableNode<String>(d.info, null, null);
        ComparableNode<String> cc = new ComparableNode<String>(c.info, ca, cb);
        //ComparableNode<String> ce = new ComparableNode<String>(e.info, null, cd);
        ComparableNode<String> ce = new ComparableNode<String>("G", null, cd);
        ComparableNode<String> cf = new ComparableNode<String>(f.info, cc, ce);

        ComparableNode<String> root3 = cf;

        System.out.println("Best-first search");
        System.out.println("==============");
        bestFirstSearch(root3, printNode);
    }
}
