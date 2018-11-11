package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by soubhik on 11-11-2018.
 * from https://practice.geeksforgeeks.org/problems/word-boggle/0
 */
public class WordBoggle {
    private static class Node {
        Character letter;
        Map<Character, Node> nextLetters;
        boolean leaf;
        String word;

        public Node() {
            this(null);
        }

        public Node(Character c) {
            this.letter = c;
            this.nextLetters = new HashMap<>();
            this.leaf = false;
        }

        public boolean hasNextLetter(Character c) {
            return nextLetters.keySet().contains(c);
        }

        public Node getNextLetter(Character c) {
            return nextLetters.get(c);
        }

        public Node addNextLetter(Character c) {
            if (hasNextLetter(c)) {
                throw new IllegalArgumentException("trie node already has next letter c=" + c);
            }

            Node nextLetter = new Node(c);
            nextLetters.put(c, nextLetter);

            return nextLetter;
        }

        public boolean isLeaf() {
            return leaf;
        }

        public String toWord() {
            if (!isLeaf()) {
                throw new IllegalArgumentException("node is not a leaf");
            }

            return word;
        }

        public void markLeaf(String word) {
            this.leaf = true;
            this.word = word;
        }
    }

    private static class Trie {
        final Node root;
        private int maxLength;

        private Trie() {
            this.root = new Node();
            this.maxLength = 0;
        }

        public static Trie construct(String[] words) {
            Trie trie = new Trie();

            for (String word: words) {
                addWord(trie, word);
                if (word.length() > trie.maxLength) {
                    trie.maxLength = word.length();
                }
            }

            return trie;
        }

        private static void addWord(Trie trie, String word) {
            Node current = trie.root;
            for (int i = 0; i < word.length(); i++) {
                Character letter = word.charAt(i);
                Node next = current.getNextLetter(letter);
                if (next == null) {
                    next = current.addNextLetter(letter);
                }

                current = next;
            }

            current.markLeaf(word);
        }

        public int getMaxLength() {
            return maxLength;
        }
    }

    private static class Cell {
        final int i;
        final int j;
        final Character c;

        public Cell(int i, int j, Character c) {
            this.i = i;
            this.j = j;
            this.c = c;
        }
    }

    private static class Sequence {
        private final Cell start;
        private final Cell end;
        private final Set<Cell> cells;
        private final Node trieNode;

        private Sequence(Cell start, Cell end, Set<Cell> cells, Node trieNode) {
            this.start = start;
            this.end = end;
            this.cells = cells;
            this.trieNode = trieNode;
        }

        public static Sequence newUnitSequence(Cell start, Trie trie) {
            Node root = trie.root;
            if (!root.hasNextLetter(start.c)) {
                return null;
            }

            Set<Cell> newCells = new HashSet<>();
            newCells.add(start);
            Sequence sequence = new Sequence(start, start, newCells, root.getNextLetter(start.c));

            return sequence;
        }

        public boolean canGrow(Cell cell) {
            if (this.cells.contains(cell)) {
                return false;
            }

            return trieNode.hasNextLetter(cell.c);
        }

        public Sequence grow(Cell cell) {
            if (!this.canGrow(cell)) {
                throw new IllegalArgumentException("the new sequence can not be grown");
            }

            Cell newStart = this.start;
            Cell newEnd = cell;
            Set<Cell> newCells = new HashSet<>(this.cells);
            newCells.add(newEnd);
            Node newTrieNode = trieNode.getNextLetter(cell.c);

            return new Sequence(newStart, newEnd, newCells, newTrieNode);
        }

        public int length() {
            return cells.size();
        }

        public boolean isWord() {
            return trieNode.isLeaf();
        }

        public String toWord() {
            if (!isWord()) {
                throw new IllegalArgumentException("sequence is not a word");
            }

            return trieNode.toWord();
        }
    }

    private static class Boggle {
        private final Cell[][] cells;

        public Boggle(int n, int m, char[] chars) {
            this.cells = new Cell[n][m];
            int k = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    cells[i][j] = new Cell(i, j, chars[k]);
                    k++;
                }
            }
        }

        private boolean isValid(int i, int j) {
            return ((i >= 0) && (i < cells.length) && (j >= 0) && (j < cells[i].length));
        }

        public Set<Cell> adjacentCells(Cell cell) {
            Set<Cell> adjacentCells = new HashSet<>();

            for (int i = cell.i-1; i <= cell.i+1; i++) {
                for (int j = cell.j-1; j <= cell.j+1; j++) {
                    if (isValid(i, j)) {
                        adjacentCells.add(cells[i][j]);
                    }
                }
            }
            adjacentCells.remove(cell);

            return adjacentCells;
        }

        public Set<String> findWords(Trie dictionary) {
            Set<String> words = new HashSet<>();

            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell cell = cells[i][j];
                    Sequence unitSequence = Sequence.newUnitSequence(cell, dictionary);
                    if (unitSequence == null) {
                        continue;
                    }
                    List<Sequence> sequences = new LinkedList<>();
                    sequences.add(unitSequence);

                    while (!sequences.isEmpty()) {
                        Sequence sequence = sequences.remove(0);
                        if (sequence.isWord()) {
                            words.add(sequence.toWord());
                        }

                        if (sequence.length() == dictionary.maxLength) {
                            continue;
                        }

                        //try to grow the sequence
                        Cell end = sequence.end;
                        Set<Cell> adjacentCells = adjacentCells(end);
                        for (Cell adjacent: adjacentCells) {
                            if (sequence.canGrow(adjacent)) {
                                sequences.add(sequence.grow(adjacent));
                            }
                        }
                    }
                }
            }

            return words;
        }
    }

    public static Set<String> findWordsFromBoggle(String[] words, char[] chars, int n, int m) {
        Trie dictionary = Trie.construct(words);
        Boggle boggle = new Boggle(n, m, chars);

        return boggle.findWords(dictionary);
    }

    private static void test1() {
        String[] words = new String[] {"GEEKS", "FOR", "QUIZ", "GO"};
        char[] chars = new char[] {'G', 'I', 'Z', 'U', 'E', 'K', 'Q', 'S', 'E'};
        int n = 3;
        int m = 3;

        Set<String> expected = new HashSet<>();
        expected.add("GEEKS");
        expected.add("QUIZ");

        Set<String> actual = findWordsFromBoggle(words, chars, n, m);

        assert (expected.equals(actual));
    }

    private static void test2() {
        String[] words = new String[] {"db", "bcd"};
        char[] chars = new char[] {'d', 'd', 'b', 'f', 'e', 'c', 'b', 'c', 'd', 'c'};
        int n = 5;
        int m = 2;

        Set<String> expected = new HashSet<>();
        expected.add("bcd");
        expected.add("db");

        Set<String> actual = findWordsFromBoggle(words, chars, n, m);

        assert (expected.equals(actual));
    }

    private static void test3() {
        String[] words = new String[] {"dfd", "ded", "fd", "e", "dec", "df"};
        char[] chars = new char[] {'f', 'f', 'd', 'e', 'f', 'b', 'b', 'e'};
        int n = 4;
        int m = 2;

        Set<String> expected = new HashSet<>();
        expected.add("df");
        expected.add("fd");
        expected.add("e");

        Set<String> actual = findWordsFromBoggle(words, chars, n, m);

        assert (expected.equals(actual));
    }

    private static void test() {
        test1();
        test2();
        test3();
    }

    private static class TestCase {
        int n;
        int m;
        String[] words;
        char[] chars;

        public TestCase(int n, int m, String[] words, char[] chars) {
            this.n = n;
            this.m = m;
            this.words = words;
            this.chars = chars;
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
                int numWords = Integer.parseInt(line);
                line = reader.readLine();
                String[] words = line.split(" ");

                line = reader.readLine();
                String[] parts = line.split(" ");
                int n = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);

                char[] chars = new char[n*m];
                line = reader.readLine();
                parts = line.split(" ");
                for (int j = 0; j < parts.length; j++) {
                    chars[j] = parts[j].charAt(0);
                }

                testCases[i] = new TestCase(n, m, words, chars);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            Set<String> words = findWordsFromBoggle(testCase.words, testCase.chars, testCase.n, testCase.m);
            if (words.isEmpty()) {
                System.out.println("-1");
            } else {
                ArrayList<String> sortedWords = new ArrayList<>(words);
                Collections.sort(sortedWords);
                for (String word: sortedWords) {
                    System.out.print(word + " ");
                }
                System.out.println();
            }
        }
    }
}
