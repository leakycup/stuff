package me.soubhik.careercup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by soubhik on 20-12-2018.
 * from https://www.careercup.com/question?id=5759325447389184
 */
public class IsSorted {
    private static int toIntValue(String word, Map<Character, Integer> charValues, int maxLen) {
        if (word.isEmpty()) {
            return 0;
        }

        int base = charValues.keySet().size() + 1;
        int value = charValues.get(word.charAt(0)); //assume all chars in word are valid
        for (int i = 1; i < word.length(); i++) {
            value *= base;
            value += charValues.get(word.charAt(i));
        }

        //0-pad on the right so that strings can be compared alphabetically
        int multiplier = (int)Math.pow(base, (maxLen - word.length()));
        value *= multiplier;

        return value;
    }

    // sorted == ascending order i.e. input[i] >= input[i-1]
    // "a" vs "aa": which one is greater? "aa"
    // assign a number to each char. lower the char in sorted order, lower the number.
    // c = 1, b = 2, a = 3 O(|charset|)
    // convert string to a modulo (|charset|+1) integer and compare the strings. tie is not possible (since no 0s). O((max length of input string)*|words|)
    public static boolean isSorted(String[] words, char[] chars) {
        if (words.length == 0) {
            return true;
        }

        Map<Character, Integer> charValues = new HashMap<>();
        int value = 0;
        for (char c: chars) {
            value++;
            charValues.put(c, value);
        }

        int maxLen = Integer.MIN_VALUE;
        for (String w: words) {
            if (w.length() > maxLen) {
                maxLen = w.length();
            }
        }

        int prevValue = toIntValue(words[0], charValues, maxLen);
        for (int i = 1; i < words.length; i++) {
            int currValue = toIntValue(words[i], charValues, maxLen);
            if (currValue < prevValue) {
                return false;
            }
            prevValue = currValue;
        }

        return true;
    }

    private static void test() {
        boolean actual = isSorted(new String[]{"cc", "cb", "bb", "ac"}, new char[]{'c', 'b', 'a'});
        boolean expected = true;
        assert (expected == actual);

        actual = isSorted(new String[]{"cc", "cb", "bb", "ac"}, new char[]{'b', 'c', 'a'});
        expected = false;
        assert (expected == actual);

        actual = isSorted(new String[]{"cc", "ccc", "cb", "bb", "ac"}, new char[]{'c', 'b', 'a'});
        expected = true;
        assert (expected == actual);

        actual = isSorted(new String[]{"cc", "cbc", "cb", "bb", "ac"}, new char[]{'c', 'b', 'a'});
        expected = false;
        assert (expected == actual);

        actual = isSorted(new String[]{"", "cc", "ccc", "cb", "bb", "ac"}, new char[]{'c', 'b', 'a'});
        expected = true;
        assert (expected == actual);
    }

    public static void main(String[] args) {
        test();
    }
}
