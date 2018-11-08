package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by soubhik on 08-11-2018.
 * from https://practice.geeksforgeeks.org/problems/print-leaf-nodes-from-preorder-traversal-of-bst/0
 */
public class PreorderToLeaves {
    private static class Subtree {
        final int start;
        final int end;

        public Subtree(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public boolean isLeaf() {
            return (this.start == this.end);
        }
    }

    public static List<Integer> toLeafNodes(int[] preorder) {
        List<Integer> leafNodes = new LinkedList<>();

        if (preorder.length == 0) {
            return leafNodes;
        }

        Subtree completeTree = new Subtree(0, preorder.length-1);
        Deque<Subtree> subtrees = new LinkedList<>();
        subtrees.push(completeTree);
        while (!subtrees.isEmpty()) {
            Subtree subtree = subtrees.pop();
            if (subtree.isLeaf()) {
                leafNodes.add(preorder[subtree.start]);
            } else {
                int leftStart = subtree.start + 1;
                int leftEnd = subtree.end;
                for (int i = leftStart; i <= subtree.end; i++) {
                    if (preorder[i] > preorder[subtree.start]) {
                        leftEnd = i - 1;
                        break;
                    }
                }

                if (leftEnd < leftStart) { // no left subtree
                    Subtree right = new Subtree(subtree.start+1, subtree.end);
                    subtrees.push(right);
                } else if (leftEnd == subtree.end) { // no right subtree
                    Subtree left = new Subtree(leftStart, leftEnd);
                    subtrees.push(left);
                } else { //both left and right subtrees are present
                    Subtree left = new Subtree(leftStart, leftEnd);
                    Subtree right = new Subtree(leftEnd+1, subtree.end);

                    subtrees.push(right);
                    subtrees.push(left);
                }
            }
        }

        return leafNodes;
    }

    private static void test1() {
        int[] preorder = new int[] {890, 325, 290, 530, 965};
        List<Integer> expected = Arrays.asList(290, 530, 965);
        List<Integer> actual = toLeafNodes(preorder);

        assert (expected.equals(actual));
    }

    private static void test2() {
        int[] preorder = new int[] {3, 2, 4};
        List<Integer> expected = Arrays.asList(2, 4);
        List<Integer> actual = toLeafNodes(preorder);

        assert (expected.equals(actual));
    }

    private static void test3() {
        int[] preorder = new int[] {261, 142, 106, 41, 114, 121, 232, 157, 149, 178, 931, 867, 653, 328, 304, 345, 428, 657, 752, 766, 799, 918, 940};
        List<Integer> expected = Arrays.asList(41, 121, 149, 178, 304, 428, 799, 918, 940);
        List<Integer> actual = toLeafNodes(preorder);

        assert (expected.equals(actual));
    }

    private static void test4() {
        int[] preorder = new int[] {419, 371, 126, 63, 357, 324, 222, 901, 898, 617, 555, 549, 547, 831, 764, 622, 891};
        List<Integer> expected = Arrays.asList(63, 222, 547, 622, 891);
        List<Integer> actual = toLeafNodes(preorder);

        assert (expected.equals(actual));
    }

    private static void test() {
        test1();
        test2();
        test3();
        test4();
    }

    private static class TestCase {
        int[] preorder;

        TestCase(int[] preorder) {
            this.preorder = preorder;
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

                int[] preorder = new int[n];

                line = reader.readLine();
                String[] parts = line.split(" ");
                for (int j = 0; j < parts.length; j++) {
                    preorder[j] = Integer.parseInt(parts[j]);
                }
                testCases[i] = new TestCase(preorder);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            List<Integer> leafNodes = toLeafNodes(testCase.preorder);
            for (Integer leaf: leafNodes) {
                System.out.print(leaf + " ");
            }
            System.out.println();
        }
    }
}
