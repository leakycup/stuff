package me.soubhik;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sb8 on 9/30/18.
 */
public class Snippet {
    private static class Candidate {
        int start;
        int end;

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
        Candidate candidate = getShortestOrdered(words, queryTokens, 0, 0);

        return candidate.toString(words);
    }

    public Candidate getShortestOrdered(List<String> textTokens, List<String> queryTokens, int textIndex, int queryIndex) {
        if ((textIndex >= textTokens.size()) || (queryIndex >= queryTokens.size())) {
            return new Candidate();
        }

        String currentTextToken = textTokens.get(textIndex);
        String currentQueryToken = queryTokens.get(queryIndex);
        if ((queryIndex == (queryTokens.size() - 1)) && currentQueryToken.equals(currentTextToken)) {
            Candidate candidate = new Candidate();
            candidate.start = textIndex;
            candidate.end = textIndex;
            return candidate;
        }

        if (currentQueryToken.equals(currentTextToken)) {
            Candidate candidate1 = getShortestOrdered(textTokens, queryTokens, textIndex+1, queryIndex+1);
            int length1 = Integer.MAX_VALUE;
            if (candidate1.start >= 0) {
                candidate1.start = textIndex;
                length1 = candidate1.end - candidate1.start + 1;
            }
            Candidate candidate2 = getShortestOrdered(textTokens, queryTokens, textIndex+1, queryIndex);
            int length2 = (candidate2.start >= 0) ? (candidate2.end - candidate2.start + 1) : Integer.MAX_VALUE;
            if (length1 < length2) {
                return candidate1;
            } else {
                return candidate2;
            }
        } else {
            return getShortestOrdered(textTokens, queryTokens, textIndex+1, queryIndex);
        }
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

    public static void testShortestSnippets() {
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

    public static void main(String[] args) {
        tokenizerTests();
        testShortestSnippets();
    }
}
