package me.soubhik.GforG;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by sb8 on 10/28/18.
 * from https://practice.geeksforgeeks.org/problems/construct-tree-from-preorder-traversal/1
 */
public class PreorderToBinaryTree {
    public static class Node {
        int data;
        Node left,right;

        Node(int d) {
            data=d;
            left=right=null;
        }
    }

    public static Node constructTree(int n, int pre[], char preLN[]) {
        if (pre.length == 0) {
            return null;
        }

        Node root = new Node(pre[0]);
        Deque<Node> stack = new LinkedList<>();
        if (preLN[0] == 'N') {
            stack.push(root);
        }

        for (int i = 1; i < pre.length; i++) {
            Node node = new Node(pre[i]);

            Node parent = stack.peekFirst();
            if (parent.left == null) {
                parent.left = node;
            } else {
                parent.right = node;
                stack.pop();
            }

            if (preLN[i] == 'N') {
                stack.push(node);
            }
        }

        return root;
    }

    private static int preorderDfs(Node root, int[] preorderArray, int i) {
        assert (i < preorderArray.length);

        preorderArray[i] = root.data;
        i++;

        if (root.left != null) {
            i = preorderDfs(root.left, preorderArray, i);
        }

        if (root.right != null) {
            i = preorderDfs(root.right, preorderArray, i);
        }

        return i;
    }

    private static void test(int n, int[] pre, char[] preLN) {
        Node actual = constructTree(n, pre, preLN);

        assert (actual != null);
        int[] actualPre = new int[n];
        preorderDfs(actual, actualPre, 0);

        assert (Arrays.equals(pre, actualPre));
    }

    public static void test1() {
        int n = 5;
        int[] pre = new int[] {10, 30, 20, 5, 15};
        char[] preLN = new char[] {'N', 'N', 'L', 'L', 'L'};

        test(n, pre, preLN);
    }

    public static void test2() {
        int n = 7;
        int[] pre = new int[] {1, 2, 4, 5, 3, 6, 7};
        char[] preLN = new char[] {'N', 'N', 'L', 'L', 'N', 'L', 'L'};

        test(n, pre, preLN);
    }

    public static void test3() {
        int n = 1;
        int[] pre = new int[] {5};
        char[] preLN = new char[] {'L'};

        test(n, pre, preLN);
    }

    public static void test4() {
        int n = 2;
        int[] pre = new int[] {8, 3};
        char[] preLN = new char[] {'N', 'L'};

        test(n, pre, preLN);
    }

    public static void test5() {
        int n = 3;
        int[] pre = new int[] {8, 6, 2};
        char[] preLN = new char[] {'N', 'L', 'L'};

        test(n, pre, preLN);
    }

    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
        test5();
    }
}
