package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Created by soubhik on 01-12-2018.
 * from https://practice.geeksforgeeks.org/problems/maximum-rectangular-area-in-a-histogram/0
 */
public class MaxRectangleInHistogram {
    private static class Interval implements Comparable<Interval> {
        private final int start;
        private final int end;

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public int compareTo(Interval other) {
            if (other.end < this.start) {
                return -1;
            } else if (other.start > this.end) {
                return 1;
            } else {
                return 0;
            }
        }

        public int compareTo(int i) {
            return compareTo(new Interval(i, i));
        }
    }

    private static class Node {
        private final Interval interval;
        private Node left;
        private Node right;

        public Node(int i, int j) {
            this.interval = new Interval(i, j);
        }
    }

    // only non-overlapping intervals
    private static class IntervalTree {
        private Node root;

        public IntervalTree(int i, int j) {
            this.root = new Node(i, j);
        }

        public Node find(int i) {
            if (root == null) {
                return null;
            }

            Node node = new Node(i, i);
            Node current = root;
            while (true) {
                int position = current.interval.compareTo(node.interval);
                if (position == 0) {
                    // found
                    return current;
                }
                if (position < 0) {
                    if (current.left != null) {
                        current = current.left;
                    } else {
                        return null;
                    }
                } else {
                    if (current.right != null) {
                        current = current.right;
                    } else {
                        return null;
                    }
                }
            }
        }

        public void insert(int i, int j) {
            Node node = new Node(i, j);
            if (root == null) {
                root = node;
                return;
            }

            Node current = root;
            while (true) {
                int position = current.interval.compareTo(node.interval);
                if (position == 0) {
                    //overlap
                    throw new IllegalArgumentException("inserting overlapping interval");
                }
                if (position < 0) {
                    if (current.left != null) {
                        current = current.left;
                    } else {
                        current.left = node;
                        return;
                    }
                } else {
                    if (current.right != null) {
                        current = current.right;
                    } else {
                        current.right = node;
                        return;
                    }
                }
            }
        }

        public void remove(Node node) {
            if (root == null) {
                throw new IllegalArgumentException("removing from empty tree");
            }

            this.root = remove(root, node);
        }

        private static Node remove(Node subtree, Node node) {
            Node current = subtree;
            Node parent = null;
            while (true) {
                int position = current.interval.compareTo(node.interval);
                if (position == 0) {
                    if (current != node) {
                        throw new IllegalArgumentException("node not found");
                    }
                    //found
                    break;
                }
                if (position < 0) {
                    if (current.left != null) {
                        parent = current;
                        current = current.left;
                    } else {
                        throw new IllegalArgumentException("node not found");
                    }
                } else {
                    if (current.right != null) {
                        parent = current;
                        current = current.right;
                    } else {
                        throw new IllegalArgumentException("node not found");
                    }
                }
            }

            Node left = current.left;
            Node right = current.right;
            Node newRoot = null;
            if (left != null) {
                newRoot = findMaximum(left);
                left = remove(left, newRoot);
            } else if (right != null) {
                newRoot = findMinimum(right);
                right = remove(right, newRoot);
            }

            if (newRoot != null) {
                newRoot.left = left;
                newRoot.right = right;
            }

            if (parent != null) {
                if (parent.left == current) {
                    parent.left = newRoot;
                } else {
                    parent.right = newRoot;
                }
            } else {
                subtree = newRoot;
            }

            return subtree;
        }

        private static Node findMaximum(Node node) {
            Node current = node;
            while (current.right != null) {
                current = current.right;
            }

            return current;
        }

        private static Node findMinimum(Node node) {
            Node current = node;
            while (current.left != null) {
                current = current.left;
            }

            return current;
        }
    }

    private static class Bar implements Comparable<Bar> {
        final int height;
        final int index;

        public Bar(int height, int index) {
            this.height = height;
            this.index = index;
        }

        @Override
        public int compareTo(Bar other) {
            return (this.height - other.height);
        }
    }

    public static int maxRectangularArea(int[] histogram) {
        PriorityQueue<Bar> heap = new PriorityQueue<>();
        for (int i = 0; i < histogram.length; i++) {
            heap.add(new Bar(histogram[i], i));
        }

        IntervalTree intervalTree = new IntervalTree(0, histogram.length-1);
        int maxArea = 0;

        while (!heap.isEmpty()) {
            Bar shortest = heap.remove();
            Node node = intervalTree.find(shortest.index);
            int area = (node.interval.end - node.interval.start + 1) * shortest.height;
            if (area > maxArea) {
                maxArea = area;
            }
            intervalTree.remove(node);
            if (shortest.index > node.interval.start) {
                intervalTree.insert(node.interval.start, shortest.index-1);
            }
            if (shortest.index < node.interval.end) {
                intervalTree.insert(shortest.index+1, node.interval.end);
            }
        }

        return maxArea;
    }

    private static void test1() {
        int[] histogram = new int[] {4};
        int expected = 4;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test2() {
        int[] histogram = new int[] {4, 7};
        int expected = 8;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test3() {
        int[] histogram = new int[] {7, 4};
        int expected = 8;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test4() {
        int[] histogram = new int[] {7, 7};
        int expected = 14;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test5() {
        int[] histogram = new int[] {2, 13, 20};
        int expected = 26;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test6() {
        int[] histogram = new int[] {12, 13, 20};
        int expected = 36;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test7() {
        int[] histogram = new int[] {20, 13, 2};
        int expected = 26;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test8() {
        int[] histogram = new int[] {20, 13, 12};
        int expected = 36;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test9() {
        int[] histogram = new int[] {20, 2, 13};
        int expected = 20;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test10() {
        int[] histogram = new int[] {20, 22, 13, 17, 19};
        int expected = 65;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test11() {
        int[] histogram = new int[] {20, 22, 0, 17, 19};
        int expected = 40;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test12() {
        int[] histogram = new int[] {6, 2, 5, 4, 5, 1, 6};
        int expected = 12;
        int actual = maxRectangularArea(histogram);
        assert (expected == actual);
    }

    private static void test() {
        test1();
        test2();
        test3();
        test4();
        test5();
        test6();
        test7();
        test8();
        test9();
        test10();
        test11();
        test12();
    }

    private static class TestCase {
        int[] histogram;

        public TestCase(int[] histogram) {
            this.histogram = histogram;
        }
    }

    public static void main(String[] args) {
        test();
        int numTestCases;
        TestCase[] testCases;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            numTestCases = Integer.parseInt(line);
            testCases = new TestCase[numTestCases];
            for (int i = 0; i < numTestCases; i++) {
                line = reader.readLine();
                int n = Integer.parseInt(line);
                int[] histogram = new int[n];

                line = reader.readLine();
                String[] parts = line.split(" ");
                for (int j = 0; j < n; j++) {
                    histogram[j] = Integer.parseInt(parts[j]);
                }
                testCases[i] = new TestCase(histogram);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            int area = maxRectangularArea(testCase.histogram);
            System.out.println(area);
        }
    }
}
