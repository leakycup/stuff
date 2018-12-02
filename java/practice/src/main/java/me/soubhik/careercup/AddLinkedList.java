package me.soubhik.careercup;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by soubhik on 02-12-2018.
 * from https://www.careercup.com/question?id=5723406763294720
 */
public class AddLinkedList {
    //Java LinkedList is a doubly linked list. it provides a ListIterator that allows iteration in both
    // directions. we can solve it using a ListIterator or without using a ListIterator.
    // following solution uses Iterator rather than ListIterator.
    public static LinkedList<Integer> sumList(LinkedList<Integer> first, LinkedList<Integer> second) {
        Iterator<Integer> longer;
        Iterator<Integer> shorter;
        int diff;
        if (first.size() > second.size()) {
            longer = first.iterator();
            shorter = second.iterator();
            diff = first.size() - second.size();
        } else {
            longer = second.iterator();
            shorter = first.iterator();
            diff = second.size() - first.size();
        }

        LinkedList<Integer> intermediate = new LinkedList<>();
        for (int i = 0; i < diff; i++) {
            intermediate.addFirst(longer.next());
        }
        while (longer.hasNext()) {
            int sum = longer.next() + shorter.next();
            intermediate.addFirst(sum);
        }

        LinkedList<Integer> result = new LinkedList<>();
        int carry = 0;
        for (Integer i: intermediate) {
            int sum = i + carry;
            result.addFirst(sum%10);
            carry = sum / 10;
        }
        if (carry != 0) {
            result.addFirst(carry);
        }

        return result;
    }

    private static void test1() {
        LinkedList<Integer> first = new LinkedList<>(Arrays.asList(1, 2, 3));
        LinkedList<Integer> second = new LinkedList<>(Arrays.asList(4, 5, 6));
        LinkedList<Integer> expected = new LinkedList<>(Arrays.asList(5, 7, 9));
        LinkedList<Integer> actual = sumList(first, second);
        assert (expected.equals(actual));
    }

    private static void test2() {
        LinkedList<Integer> first = new LinkedList<>(Arrays.asList(1, 2, 3, 4));
        LinkedList<Integer> second = new LinkedList<>(Arrays.asList(4, 5, 3));
        LinkedList<Integer> expected = new LinkedList<>(Arrays.asList(1, 6, 8, 7));
        LinkedList<Integer> actual = sumList(first, second);
        assert (expected.equals(actual));
    }

    private static void test3() {
        LinkedList<Integer> first = new LinkedList<>(Arrays.asList(4, 5, 3));
        LinkedList<Integer> second = new LinkedList<>(Arrays.asList(1, 2, 3, 4));
        LinkedList<Integer> expected = new LinkedList<>(Arrays.asList(1, 6, 8, 7));
        LinkedList<Integer> actual = sumList(first, second);
        assert (expected.equals(actual));
    }

    private static void test4() {
        LinkedList<Integer> first = new LinkedList<>(Arrays.asList(1, 3, 2, 3, 4));
        LinkedList<Integer> second = new LinkedList<>(Arrays.asList(4, 5, 3));
        LinkedList<Integer> expected = new LinkedList<>(Arrays.asList(1, 3, 6, 8, 7));
        LinkedList<Integer> actual = sumList(first, second);
        assert (expected.equals(actual));
    }

    private static void test5() {
        LinkedList<Integer> first = new LinkedList<>(Arrays.asList(4, 5, 3));
        LinkedList<Integer> second = new LinkedList<>(Arrays.asList(1, 3, 2, 3, 4));
        LinkedList<Integer> expected = new LinkedList<>(Arrays.asList(1, 3, 6, 8, 7));
        LinkedList<Integer> actual = sumList(first, second);
        assert (expected.equals(actual));
    }

    private static void test6() {
        LinkedList<Integer> first = new LinkedList<>(Arrays.asList(7, 2));
        LinkedList<Integer> second = new LinkedList<>(Arrays.asList(1, 9));
        LinkedList<Integer> expected = new LinkedList<>(Arrays.asList(9, 1));
        LinkedList<Integer> actual = sumList(first, second);
        assert (expected.equals(actual));
    }

    private static void test7() {
        LinkedList<Integer> first = new LinkedList<>(Arrays.asList(8, 2));
        LinkedList<Integer> second = new LinkedList<>(Arrays.asList(1, 9));
        LinkedList<Integer> expected = new LinkedList<>(Arrays.asList(1, 0, 1));
        LinkedList<Integer> actual = sumList(first, second);
        assert (expected.equals(actual));
    }

    private static void test8() {
        LinkedList<Integer> first = new LinkedList<>(Arrays.asList(1, 8, 2));
        LinkedList<Integer> second = new LinkedList<>(Arrays.asList(1, 9));
        LinkedList<Integer> expected = new LinkedList<>(Arrays.asList(2, 0, 1));
        LinkedList<Integer> actual = sumList(first, second);
        assert (expected.equals(actual));
    }

    private static void test9() {
        LinkedList<Integer> first = new LinkedList<>(Arrays.asList(3));
        LinkedList<Integer> second = new LinkedList<>(Arrays.asList(0));
        LinkedList<Integer> expected = new LinkedList<>(Arrays.asList(3));
        LinkedList<Integer> actual = sumList(first, second);
        assert (expected.equals(actual));
    }

    private static void test10() {
        LinkedList<Integer> first = new LinkedList<>(Arrays.asList(3));
        LinkedList<Integer> second = new LinkedList<>(Arrays.asList(2));
        LinkedList<Integer> expected = new LinkedList<>(Arrays.asList(5));
        LinkedList<Integer> actual = sumList(first, second);
        assert (expected.equals(actual));
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
    }

    public static void main(String[] args) {
        test();
    }
}
