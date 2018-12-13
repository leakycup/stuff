package me.soubhik;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sb8 on 9/30/18.
 */
public class Snippet {
    private static class Candidate {
        int start; //inclusive
        int end; //inclusive

        public Candidate() {
            this.start = -1;
            this.end = -1;
        }

        public String toString(List<String> tokens) {
            if (start < 0) {
                return "";
            }

            StringBuilder builder = new StringBuilder();
            for (int i=start; i <= end; i++) {
                builder.append(tokens.get(i));
                builder.append(" ");
            }

            return builder.toString().trim();
        }
    }

    private final List<String> words;

    private static List<String> tokenize(String text) {
        String normalizedText = text.replaceAll("\\W", " ");
        List<String> tokens = Arrays.asList(normalizedText.split("[\\s]+"));
        return tokens;
    }

    public Snippet(String text) {
        this.words = tokenize(text);
    }

    public String getShortestOrdered(String query) {
        List<String> queryTokens = tokenize(query);
        Candidate[][] memoizationArray = new Candidate[words.size()][queryTokens.size()];
        Candidate candidate = getShortestOrdered(words, queryTokens, 0, 0, memoizationArray);

        return candidate.toString(words);
    }

    //due to memoization, the main body of the function is executed at most once for each (textIndex, queryIndex) pair.
    // 0 <= textIndex < words.size(); 0 <= queryIndex < queryTokens.size(). so this is O(words.size() * queryTokens.size())
    public Candidate getShortestOrdered(List<String> textTokens, List<String> queryTokens, int textIndex, int queryIndex,
                                        Candidate[][] memoizationArray) {
        if ((textIndex >= textTokens.size()) || (queryIndex >= queryTokens.size())) {
            return new Candidate();
        }

        if (memoizationArray[textIndex][queryIndex] != null) {
            return memoizationArray[textIndex][queryIndex];
        }

        String currentTextToken = textTokens.get(textIndex);
        String currentQueryToken = queryTokens.get(queryIndex);
        if ((queryIndex == (queryTokens.size() - 1)) && currentQueryToken.equals(currentTextToken)) {
            Candidate candidate = new Candidate();
            candidate.start = textIndex;
            candidate.end = textIndex;
            memoizationArray[textIndex][queryIndex] = candidate;
            return candidate;
        }

        if (currentQueryToken.equals(currentTextToken)) {
            Candidate candidate1 = getShortestOrdered(textTokens, queryTokens, textIndex+1, queryIndex+1, memoizationArray);
            int length1 = Integer.MAX_VALUE;
            if (candidate1.start >= 0) {
                candidate1.start = textIndex;
                length1 = candidate1.end - candidate1.start + 1;
            }
            Candidate candidate2 = getShortestOrdered(textTokens, queryTokens, textIndex+1, queryIndex, memoizationArray);
            int length2 = (candidate2.start >= 0) ? (candidate2.end - candidate2.start + 1) : Integer.MAX_VALUE;
            if (length1 < length2) {
                memoizationArray[textIndex][queryIndex] = candidate1;
                return candidate1;
            } else {
                memoizationArray[textIndex][queryIndex] = candidate2;
                return candidate2;
            }
        } else {
            Candidate candidate = getShortestOrdered(textTokens, queryTokens, textIndex+1, queryIndex, memoizationArray);
            memoizationArray[textIndex][queryIndex] = candidate;
            return candidate;
        }
    }

    public String getShortestUnordered(String query) {
        List<String> queryTokens = tokenize(query);
        if (queryTokens.isEmpty()) {
            return "";
        }

        Map<String, Integer> tokenCount = new HashMap<>();
        for (String token: queryTokens) {
            tokenCount.put(token, 0);
        }

        //find the initial candidate snippet that starts at index 0
        Candidate candidate = null;
        int numUniqTokens = 0;
        for (int i = 0; i < words.size(); i++) { //O(n)
            String token = words.get(i);
            if (!tokenCount.containsKey(token)) {
                continue;
            }
            int count = tokenCount.get(token);
            if (count == 0) {
                numUniqTokens++;
            }
            count++;
            tokenCount.put(token, count);
            if (numUniqTokens == tokenCount.size()) {
                candidate = new Candidate();
                candidate.start = 0;
                candidate.end = i;
                break;
            }
        }

        if (candidate == null) {
            return "";
        }

        //try to find a shorter snippet
        int start = candidate.start;
        int end = candidate.end;
        int minLength = end - start + 1;
        boolean expanding = false;
        while (end < words.size()) {
            //each iteration of this loop increments either start or end
            //range of values for start is [0, (n-m)] i.e. (n-m+1) distinct values.
            //range of values for end is [(m-1), (n-1)] i.e. (n-m+1) distinct values.
            //even if we iterate through all possible values of start and end, the number of iterations is
            //going to be 2*(n-m+1). thus, this loop is O(n).
            if (expanding) {
                String endToken = words.get(end);
                if (!tokenCount.containsKey(endToken)) {
                    end++;
                    continue;
                }
                int endTokenCount = tokenCount.get(endToken);
                endTokenCount++;
                tokenCount.put(endToken, endTokenCount);
                expanding = false;
            }
            String token = words.get(start);
            if (!tokenCount.containsKey(token)) {
                start++;
                continue;
            }
            int count = tokenCount.get(token);
            if (count > 1) {
                //shrink the snippet
                count--;
                tokenCount.put(token, count);
                start++;
            } else {
                //expand the snippet
                int length = end - start + 1;
                if (length < minLength) {
                    minLength = length;
                    candidate.start = start;
                    candidate.end = end;
                }
                end++;
                expanding = true;
            }
        }

        return candidate.toString(words);
    }

    private static void testTokenize(String input, List<String> expected) {
        List<String> actual = tokenize(input);
        assert (expected.size() == actual.size());
        for (int i=0; i < expected.size(); i++) {
            assert (expected.get(i).equals(actual.get(i)));
        }
    }

    private static void tokenizerTests() {
        String input1 = "quick brown fox";
        List<String> expected1 = Arrays.asList("quick", "brown", "fox");
        testTokenize(input1, expected1);

        String input2 = "quick brown fox.";
        List<String> expected2 = Arrays.asList("quick", "brown", "fox");
        testTokenize(input2, expected2);

        String input3 = "Hello! how are you?";
        List<String> expected3 = Arrays.asList("Hello", "how", "are", "you");
        testTokenize(input3, expected3);

        String input4 = "Agent 007 calling";
        List<String> expected4 = Arrays.asList("Agent", "007", "calling");
        testTokenize(input4, expected4);

        String input5 = "Agent007 calling";
        List<String> expected5 = Arrays.asList("Agent007", "calling");
        testTokenize(input5, expected5);

        String input6 = "AB00cd-ef gh!!ij   ... kl$# pqrs  uv\nwx\tyz.";
        List<String> expected6 = Arrays.asList("AB00cd", "ef", "gh", "ij", "kl", "pqrs", "uv", "wx", "yz");
        testTokenize(input6, expected6);
    }

    public static void testShortestOrderedSnippets() {
        String text = "Made a flexible trip plan, booked a hotel at Hassan and set off. Hassan makes for the best 'base camp' because of its strategic central location to most of the temples and large choice of hotels. If you prefer staying in resorts or homestays instead, Chikmagalur (as a base camp) is a good choice too - but you probably need to cover a few extra kilometers.";
        Snippet snippet = new Snippet(text);

        String query1 = "Hassan";
        String expected1 = "Hassan";
        String actual1 = snippet.getShortestOrdered(query1);
        assert (expected1.equals(actual1));

        String query2 = "hotel Hassan";
        String expected2 = "hotel at Hassan";
        String actual2 = snippet.getShortestOrdered(query2);
        assert (expected2.equals(actual2));

        String query3 = "Hassan hotel";
        String expected3 = "";
        String actual3 = snippet.getShortestOrdered(query3);
        assert (expected3.equals(actual3));

        String query4 = "base camp";
        String expected4 = "base camp";
        String actual4 = snippet.getShortestOrdered(query4);
        assert (expected4.equals(actual4));

        String query5 = "strategic central location";
        String expected5 = "strategic central location";
        String actual5 = snippet.getShortestOrdered(query5);
        assert (expected5.equals(actual5));

        String query6 = "Hassan temples";
        String expected6 = "Hassan makes for the best base camp because of its strategic central location to most of the temples";
        String actual6 = snippet.getShortestOrdered(query6);
        assert (expected6.equals(actual6));

        String query7 = "Made flexible plan";
        String expected7 = "Made a flexible trip plan";
        String actual7 = snippet.getShortestOrdered(query7);
        assert (expected7.equals(actual7));

        String query8 = "extra kilometers.";
        String expected8 = "extra kilometers";
        String actual8 = snippet.getShortestOrdered(query8);
        assert (expected8.equals(actual8));

        String query9 = "base camp choice";
        String expected9 = "base camp is a good choice";
        String actual9 = snippet.getShortestOrdered(query9);
        assert (expected9.equals(actual9));

        String query10 = "Chikmagalur base camp";
        String expected10 = "Chikmagalur as a base camp";
        String actual10 = snippet.getShortestOrdered(query10);
        assert (expected10.equals(actual10));
    }

    private static void testShortestUnorderedSnippets(Snippet snippet, String query, String expected) {
        String actual = snippet.getShortestUnordered(query);
        assert (expected.equals(actual));
    }

    private static void testShortestUnorderedSnippets() {
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "a", "a");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "a b", "a b");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "a b d", "a b c d");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "d a b", "a b c d");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "d b a", "a b c d");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "d a a b", "a b c d");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "d a b b", "a b c d");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "a b e", "a b c d e");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "a c", "a b c");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "c a", "a b c");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "c", "c");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "c c", "c");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "c d", "c d");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "c e", "c d e");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "e", "e");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "e b", "b c d e");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "f", "");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "b f", "");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "b f c", "");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "c b f", "");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "f b c", "");
        testShortestUnorderedSnippets(new Snippet("a a b c d e"), "a b d", "a b c d");
        testShortestUnorderedSnippets(new Snippet("a b a b c d e"), "a b d", "a b c d");
        testShortestUnorderedSnippets(new Snippet("a b b a c d e"), "a b d", "b a c d");
        testShortestUnorderedSnippets(new Snippet("a b b a c d e d b a"), "b d", "d b");
        testShortestUnorderedSnippets(new Snippet("a b b a c d e d c b a"), "b d", "d c b");
        testShortestUnorderedSnippets(new Snippet("a b b a c d e b c d a"), "b d", "d e b");
        testShortestUnorderedSnippets(new Snippet("a b b a c d e e b c d a"), "b d", "b c d");
        testShortestUnorderedSnippets(new Snippet("a b c d e"), "", "");
        testShortestUnorderedSnippets(new Snippet(""), "a b", "");
        testShortestUnorderedSnippets(new Snippet(""), "", "");
    }

    public static void main(String[] args) {
        tokenizerTests();
        testShortestOrderedSnippets();
        testShortestUnorderedSnippets();
    }
}
