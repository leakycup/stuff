package me.soubhik.careercup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by soubhik on 19-12-2018.
 * from https://www.careercup.com/question?id=5634222671790080
 */
public class AllPossiblePatterns {
    //can same character repeat in a list? yes.
    //if same string is produced multiple times, (e.g. 1: ['a'], 2:['b'], 12:['ab'] then do we repeat? or output only unique patterns? unique patterns.
    //can the input string contain characters not present in the dictionary? no.
    //can the input string contain same dictionary key multiple times? yes.
    //algo:
    //step1 (tokenize): determine all possible ways to break the input string into combination of dictionary keys
    //123 -> [1, 2, 3], [12, 3]
    //step2 (generate): for each combination of keys, iterate over the keys. for each key, iterate over the values and keep appending to the output builder.
    //step3: convert the output builder to string, add it in output set.
    public static Set<String> generatePatterns(Map<String, List<String>> dictionary, String input) {
        Set<List<String>> tokenLists = tokenize(dictionary, input);
        Set<String> patterns = generate(dictionary, tokenLists);

        return patterns;
    }

    private static Set<List<String>> tokenize(Map<String, List<String>> dictionary, String input) {
        if (input.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<String> keys = dictionary.keySet();
        int maxLen = Integer.MIN_VALUE;
        int minLen = Integer.MAX_VALUE;
        for (String key: keys) {
            if (key.length() > maxLen) {
                maxLen = key.length();
            }
            if (key.length() < minLen) {
                minLen = key.length();
            }
        }
        Set<List<String>>[] memoizationArray = new Set[input.length()]; //null initialized by Java
        tokenize(input, 0, keys, minLen, maxLen, memoizationArray);

        return memoizationArray[0];
    }

    private static void tokenize(String input, int index, Set<String> keys, int minLen, int maxLen, Set<List<String>>[] memoizationArray) {
        if (memoizationArray[index] != null) {
            return;
        }

        memoizationArray[index] = new HashSet<>();

        for (int i = index; i < input.length(); i++) {
            if ((i - index + 1) > maxLen) {
                break;
            }
            String token = input.substring(index, i+1);
            if (!keys.contains(token)) {
                continue;
            }
            int charsRemaining = input.length() - i - 1;
            if (charsRemaining == 0) {
                List<String> tokens = new ArrayList<>();
                tokens.add(token);
                memoizationArray[index].add(tokens);
            } else if (charsRemaining >= minLen) {
                tokenize(input, i+1, keys, minLen, maxLen, memoizationArray);
                for (List<String> tokens: memoizationArray[i+1]) {
                    List<String> expandedTokens = new ArrayList<>();
                    expandedTokens.add(token);
                    expandedTokens.addAll(tokens);
                    memoizationArray[index].add(expandedTokens);
                }
            }
        }

        return;
    }

    private static Set<String> generate(Map<String, List<String>> dictionary, Set<List<String>> tokenLists) {
        Set<String> patterns = new HashSet<>();

        for (List<String> tokenList: tokenLists) {
            LinkedList<String> pattern = new LinkedList<>();
            generateRecursive(tokenList, 0, dictionary, patterns, pattern);
        }

        return patterns;
    }

    private static void generateRecursive(List<String> tokenList, int index, Map<String, List<String>> dictionary,
                                          Set<String> patterns, LinkedList<String> pattern) {
        String token = tokenList.get(index);
        if (index == (tokenList.size() -1)) {
            for (String patternPart: dictionary.get(token)) {
                pattern.addLast(patternPart);
                patterns.add(toString(pattern));
                pattern.removeLast();
            }

            return;
        }

        for (String patternPart: dictionary.get(token)) {
            pattern.addLast(patternPart);
            generateRecursive(tokenList, index+1, dictionary, patterns, pattern);
            pattern.removeLast();
        }
    }

    private static String toString(List<String> pattern) {
        StringBuilder builder = new StringBuilder();
        for (String patternPart: pattern) {
            builder.append(patternPart);
        }

        return builder.toString();
    }

    private static void testTokenize(Map<String, List<String>> dictionary, String input, Set<List<String>> expected) {
        Set<List<String>> actual = tokenize(dictionary, input);
        assert (expected.equals(actual));
    }

    private static void testTokenize() {
        Map<String, List<String>> tokens = new HashMap<>();
        tokens.put("1", Collections.EMPTY_LIST);
        tokens.put("2", Collections.EMPTY_LIST);
        tokens.put("3", Collections.EMPTY_LIST);
        tokens.put("4", Collections.EMPTY_LIST);
        tokens.put("12", Collections.EMPTY_LIST);
        tokens.put("123", Collections.EMPTY_LIST);
        tokens.put("56", Collections.EMPTY_LIST);

        Set<List<String>> expected = new HashSet<>();

        expected.clear();
        expected.add(Arrays.asList("1"));
        testTokenize(tokens, "1", expected);

        expected.clear();
        expected.add(Arrays.asList("1", "4"));
        testTokenize(tokens, "14", expected);

        expected.clear();
        expected.add(Arrays.asList("1", "2"));
        expected.add(Arrays.asList("12"));
        testTokenize(tokens, "12", expected);

        expected.clear();
        expected.add(Arrays.asList("1", "2", "3"));
        expected.add(Arrays.asList("12", "3"));
        expected.add(Arrays.asList("123"));
        testTokenize(tokens, "123", expected);

        expected.clear();
        expected.add(Arrays.asList("4", "1", "2"));
        expected.add(Arrays.asList("4", "12"));
        testTokenize(tokens, "412", expected);

        expected.clear();
        expected.add(Arrays.asList("1", "2", "4"));
        expected.add(Arrays.asList("12", "4"));
        testTokenize(tokens, "124", expected);

        expected.clear();
        expected.add(Arrays.asList("1", "2", "4", "3"));
        expected.add(Arrays.asList("12", "4", "3"));
        testTokenize(tokens, "1243", expected);

        expected.clear();
        expected.add(Arrays.asList("1", "2", "2", "3"));
        expected.add(Arrays.asList("12", "2", "3"));
        testTokenize(tokens, "1223", expected);

        expected.clear();
        expected.add(Arrays.asList("1", "1", "2", "3", "4"));
        expected.add(Arrays.asList("1", "12", "3", "4"));
        expected.add(Arrays.asList("1", "123", "4"));
        testTokenize(tokens, "11234", expected);

        expected.clear();
        expected.add(Arrays.asList("56"));
        testTokenize(tokens, "56", expected);

        expected.clear();
        expected.add(Arrays.asList("1", "2", "56"));
        expected.add(Arrays.asList("12","56"));
        testTokenize(tokens, "1256", expected);

        expected.clear();
        expected.add(Arrays.asList("1", "2", "4", "56"));
        expected.add(Arrays.asList("12","4", "56"));
        testTokenize(tokens, "12456", expected);

        expected.clear();
        expected.add(Arrays.asList("1", "2", "4", "1", "2"));
        expected.add(Arrays.asList("1", "2", "4", "12"));
        expected.add(Arrays.asList("12", "4", "1", "2"));
        expected.add(Arrays.asList("12", "4", "12"));
        testTokenize(tokens, "12412", expected);

        expected.clear();
        testTokenize(tokens, "", expected);

        expected.clear();
        testTokenize(tokens, "1254", expected);
    }

    private static void testGeneratePatterns(Map<String, List<String>> dictionary, String input, Set<String> expected) {
        Set<String> actual = generatePatterns(dictionary, input);
        assert (expected.equals(actual));
    }

    private static void testGeneratePatterns() {
        Map<String, List<String>> tokens = new HashMap<>();
        tokens.put("1", Arrays.asList("a", "b", "c"));
        tokens.put("2", Arrays.asList("d", "e"));
        tokens.put("3", Arrays.asList("f", "g"));
        tokens.put("4", Collections.EMPTY_LIST);
        tokens.put("12", Arrays.asList("ae", "j"));
        tokens.put("123", Arrays.asList("h"));
        tokens.put("56", Arrays.asList("i"));

        Set<String> expected = new HashSet<>();

        expected.clear();

        testGeneratePatterns(tokens, "1", new HashSet<>(Arrays.asList("a", "b", "c")));
        testGeneratePatterns(tokens, "12", new HashSet<>(Arrays.asList("ad", "ae", "bd", "be", "cd", "ce", "j")));
        testGeneratePatterns(tokens, "14", new HashSet<>(Arrays.asList()));
    }

    public static void main(String[] args) {
        testTokenize();
        testGeneratePatterns();
    }
}
