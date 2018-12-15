package me.soubhik.careercup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by soubhik on 15-12-2018.
 */
public class EquivalentStrings {
    //o(n*k*log(k))
    public static int findEquivalents(String[] input) {
        Map<String, Set<String>> keys = new HashMap<>();
        int duplicates = 0;
        for (String s: input) {
            String key1 = oddCharsSorted(s);
            String key2 = evenCharsSorted(s);
            if (keys.containsKey(key1)) {
                if (keys.get(key1).contains(key2)) {
                    duplicates++;
                } else {
                    keys.get(key1).add(key2);
                }
            } else {
                keys.put(key1, new HashSet<String>());
                keys.get(key1).add(key2);
            }
        }

        if (duplicates > 0) {
            duplicates++;
        }

        return duplicates;
    }

    //O(k*log(k))
    private static String oddCharsSorted(String s) {
        char[] chars = new char[s.length()/2];
        for (int i = 1; i < s.length(); i += 2) {
            chars[i/2] = s.charAt(i);
        }
        Arrays.sort(chars);
        return new String(chars);
    }

    //O(klog(k))
    private static String evenCharsSorted(String s) {
        char[] chars = new char[s.length()/2 + s.length()%2];
        for (int i = 0; i < s.length(); i += 2) {
            chars[i/2] = s.charAt(i);
        }
        Arrays.sort(chars);
        return new String(chars);
    }

    private static void test(String[] input, int expected) {
        int actual = findEquivalents(input);
        assert (expected == actual);
    }

    private static void test() {
        test(new String[]{"abcd", "acbd", "bcad"}, 2);
        test(new String[]{"abcd", "cbad", "adbc"}, 2);
        test(new String[]{"abcd", "dbca", "dacb"}, 2);
        test(new String[]{"abcd", "cbad", "cdab"}, 3);
        test(new String[]{"abcd", "bacd", "badc"}, 0);
        test(new String[]{"abcde", "cbade", "cbaed"}, 2);
        test(new String[]{"abcde", "cbae", "cbaed"}, 0);
    }

    public static void main(String[] args) {
        test();
    }
}
