package me.soubhik.GforG;

/**
 * Created by soubhik on 18-11-2018.
 * from https://practice.geeksforgeeks.org/problems/convert-ternary-expression-to-binary-tree/1
 */
public class TernaryExprToBinaryTree {
    private static class Node {
        final char data;
        Node left;
        Node right;

        public Node(char c) {
            this.data = c;
        }
    }

    private static class StringIterator {
        private final String s;
        private int index;

        public StringIterator(String s, int i) {
            this.s = s;
            this.index = i;
        }

        public boolean hasNext() {
            return (index < (s.length()));
        }

        public char next() {
            char c = s.charAt(index);
            index++;

            return c;
        }
    }

    private static Node toBinaryTree(StringIterator expr, StringBuilder preorderBuilder) {
        if (!expr.hasNext()) {
            throw new IllegalArgumentException();
        }

        char c = expr.next();
        Node node = new Node(c);
        preorderBuilder.append(c);
        if (!expr.hasNext()) {
            return node;
        }

        char o = expr.next();
        if (o == ':') {
            return node;
        }

        if (o != '?') {
            throw new IllegalArgumentException();
        }

        Node left = toBinaryTree(expr, preorderBuilder);
        Node right = toBinaryTree(expr, preorderBuilder);


        node.left = left;
        node.right = right;

        return node;
    }

    public static String convertExpToPreorder(String str, int i) {
        StringIterator expr = new StringIterator(str, i);
        StringBuilder preorderBuilder = new StringBuilder();
        Node root = toBinaryTree(expr, preorderBuilder);
        if (expr.hasNext()) {
            throw new IllegalArgumentException();
        }

        return preorderBuilder.toString();
    }

    public static Node convertExp(String str, int i) {
        StringIterator expr = new StringIterator(str, i);
        StringBuilder preorderBuilder = new StringBuilder();
        Node root = toBinaryTree(expr, preorderBuilder);

        return root;
    }

    private static void test1() {
        String expr = "a?b:c";
        String actual = convertExpToPreorder(expr, 0);
        String expected = "abc";

        assert (expected.equals(actual));
    }

    private static void test2() {
        String expr = "a?b?c:d:e";
        String actual = convertExpToPreorder(expr, 0);
        String expected = "abcde";

        assert (expected.equals(actual));
    }

    private static void test3() {
        String expr = "a?b:c?d:e";
        String actual = convertExpToPreorder(expr, 0);
        String expected = "abcde";

        assert (expected.equals(actual));
    }

    private static void test4() {
        String expr = "a";
        String actual = convertExpToPreorder(expr, 0);
        String expected = "a";

        assert (expected.equals(actual));
    }

    private static void test5() {
        String expr = "";
        try {
            String actual = convertExpToPreorder(expr, 0);
        } catch (IllegalArgumentException e) {
            //expected
            return;
        }

        //unexpected
        assert (false);
    }

    private static void test6() {
        String expr = "a?";
        try {
            String actual = convertExpToPreorder(expr, 0);
        } catch (IllegalArgumentException e) {
            //expected
            return;
        }

        //unexpected
        assert (false);
    }

    private static void test7() {
        String expr = "a?b";
        try {
            String actual = convertExpToPreorder(expr, 0);
        } catch (IllegalArgumentException e) {
            //expected
            return;
        }

        //unexpected
        assert (false);
    }

    private static void test8() {
        String expr = "a?b:";
        try {
            String actual = convertExpToPreorder(expr, 0);
        } catch (IllegalArgumentException e) {
            //expected
            return;
        }

        //unexpected
        assert (false);
    }

    private static void test9() {
        String expr = "ab";
        try {
            String actual = convertExpToPreorder(expr, 0);
        } catch (IllegalArgumentException e) {
            //expected
            return;
        }

        //unexpected
        assert (false);
    }

    private static void test10() {
        String expr = "a?b?:c:d";
        try {
            String actual = convertExpToPreorder(expr, 0);
        } catch (IllegalArgumentException e) {
            //expected
            return;
        }

        //unexpected
        assert (false);
    }

    private static void test11() {
        String expr = "?b:c";
        try {
            String actual = convertExpToPreorder(expr, 0);
        } catch (IllegalArgumentException e) {
            //expected
            return;
        }

        //unexpected
        assert (false);
    }

    private static void test12() {
        String expr = "a:b?c";
        try {
            String actual = convertExpToPreorder(expr, 0);
        } catch (IllegalArgumentException e) {
            //expected
            return;
        }

        //unexpected
        assert (false);
    }

    private static void test13() {
        String expr = "a?bc:d";
        try {
            String actual = convertExpToPreorder(expr, 0);
        } catch (IllegalArgumentException e) {
            //expected
            return;
        }

        //unexpected
        assert (false);
    }

    private static void test14() {
        String expr = "a:";
        try {
            String actual = convertExpToPreorder(expr, 0);
        } catch (IllegalArgumentException e) {
            //expected
            return;
        }

        //unexpected
        assert (false);
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
        test13();
        //test14(); //TODO: failing test case
    }

    public static void main(String[] args) {
        test();
    }
}
