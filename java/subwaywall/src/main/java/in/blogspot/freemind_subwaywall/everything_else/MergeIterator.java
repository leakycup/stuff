package in.blogspot.freemind_subwaywall.everything_else;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Created by sb8 on 10/25/18.
 */
public class MergeIterator<T> implements Iterator<T> {
    public static enum  MOVE {
        MOVE_FIRST,
        MOVE_SECOND
    }

    private final Iterator<T> firstIterator;
    private final Iterator<T> secondIterator;
    private final BiFunction<T, T, MOVE> mergeFunction;

    private T firstElement;
    private T secondElement;

    public MergeIterator(Iterable<T> firstIterable, Iterable<T> secondIterable, BiFunction<T, T, MOVE> mergeFunction) {
        this(firstIterable.iterator(), secondIterable.iterator(), mergeFunction);
    }

    public MergeIterator(Iterator<T> firstIterator, Iterator<T> secondIterator, BiFunction<T, T, MOVE> mergeFunction) {
        this.firstIterator = firstIterator;
        this.secondIterator = secondIterator;
        this.mergeFunction = mergeFunction;

        firstElement = getNext(firstIterator);
        secondElement = getNext(secondIterator);
    }

    private T getNext(Iterator<T> iterator) {
        if (iterator.hasNext()) {
            return iterator.next();
        }

        return null;
    }

    private T makeMove(MOVE move) {
        T ret;
        if (move == MOVE.MOVE_FIRST) {
            ret = firstElement;
            firstElement = getNext(firstIterator);
        } else {
            ret = secondElement;
            secondElement = getNext(secondIterator);
        }

        return ret;
    }

    @Override
    public boolean hasNext() {
        return ((firstElement != null) || (secondElement != null));
    }

    @Override
    public T next() {
        T ret;

        if (firstElement == null) {
            ret = makeMove(MOVE.MOVE_SECOND);
        } else if (secondElement == null) {
            ret = makeMove(MOVE.MOVE_FIRST);
        } else {
            MOVE move = mergeFunction.apply(firstElement, secondElement);
            ret = makeMove(move);
        }

        return ret;
    }

    private static void testSortMerge1() {
        List<Integer> firstList = Arrays.asList(1, 2, 3, 4);
        List<Integer> secondList = Arrays.asList(5, 6, 7, 8);
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

        MergeIterator<Integer> iterator = new MergeIterator<>(firstList, secondList,
                ((i, j) -> ((int)i <= (int)j) ? MOVE.MOVE_FIRST : MOVE.MOVE_SECOND));
        List<Integer> actual = new ArrayList<>();
        while (iterator.hasNext()) {
            actual.add(iterator.next());
        }

        assert (expected.equals(actual));
    }

    private static void testSortMerge2() {
        List<Integer> firstList = Arrays.asList(1, 4, 8);
        List<Integer> secondList = Arrays.asList(2, 3, 5, 6, 7);
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

        MergeIterator<Integer> iterator = new MergeIterator<>(firstList, secondList,
                ((i, j) -> ((int)i <= (int)j) ? MOVE.MOVE_FIRST : MOVE.MOVE_SECOND));
        List<Integer> actual = new ArrayList<>();
        while (iterator.hasNext()) {
            actual.add(iterator.next());
        }

        assert (expected.equals(actual));
    }

    private static void testSortMerge3() {
        List<Integer> firstList = Arrays.asList(1, 3);
        List<Integer> secondList = Arrays.asList(2, 4, 5, 6, 7, 8);
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

        MergeIterator<Integer> iterator = new MergeIterator<>(firstList, secondList,
                ((i, j) -> ((int)i <= (int)j) ? MOVE.MOVE_FIRST : MOVE.MOVE_SECOND));
        List<Integer> actual = new ArrayList<>();
        while (iterator.hasNext()) {
            actual.add(iterator.next());
        }

        assert (expected.equals(actual));
    }

    private static void testSortMerge4() {
        List<Integer> firstList = Arrays.asList(2, 4, 5, 6, 7, 8);
        List<Integer> secondList = Arrays.asList(1, 3);
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

        MergeIterator<Integer> iterator = new MergeIterator<>(firstList, secondList,
                ((i, j) -> ((int)i <= (int)j) ? MOVE.MOVE_FIRST : MOVE.MOVE_SECOND));
        List<Integer> actual = new ArrayList<>();
        while (iterator.hasNext()) {
            actual.add(iterator.next());
        }

        assert (expected.equals(actual));
    }

    private static void testSortMerge5() {
        List<Integer> firstList = Arrays.asList();
        List<Integer> secondList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

        MergeIterator<Integer> iterator = new MergeIterator<>(firstList, secondList,
                ((i, j) -> ((int)i <= (int)j) ? MOVE.MOVE_FIRST : MOVE.MOVE_SECOND));
        List<Integer> actual = new ArrayList<>();
        while (iterator.hasNext()) {
            actual.add(iterator.next());
        }

        assert (expected.equals(actual));
    }

    private static void testSortMerge6() {
        List<Integer> firstList = Arrays.asList();
        List<Integer> secondList = Arrays.asList();
        List<Integer> expected = Arrays.asList();

        MergeIterator<Integer> iterator = new MergeIterator<>(firstList, secondList,
                ((i, j) -> ((int)i <= (int)j) ? MOVE.MOVE_FIRST : MOVE.MOVE_SECOND));
        List<Integer> actual = new ArrayList<>();
        while (iterator.hasNext()) {
            actual.add(iterator.next());
        }

        assert (expected.equals(actual));
    }

    private static void testCustomIteration() {
        String word = "abcdef";

        List<String> words1 = Arrays.asList("abx", "pdef", "abde", "bdcefxyz");
        List<String> words2 = Arrays.asList("bcd", "dxy", "cdef");

        Comparator<String> wordComparator = new Comparator<String>() {
            private Set<Character> stringToChars(String s) {
                char[] chars = new char[s.length()];
                s.getChars(0, s.length(), chars, 0);
                Set<Character> charsSet = new HashSet<>();
                for (Character c: chars) {
                    charsSet.add(c);
                }

                return charsSet;
            }

            private int diff(Set<Character> s1, Set<Character> s2) {
                int diff = 0;
                for (Character c: s2) {
                    if (!s1.contains(c)) {
                        diff++;
                    }
                }

                return diff;
            }

            @Override
            public int compare(String o1, String o2) {
                Set<Character> wSet = stringToChars(word);
                Set<Character> o1Set = stringToChars(o1);
                Set<Character> o2Set = stringToChars(o2);

                int diff1 = diff(wSet, o1Set);
                int diff2 = diff(wSet, o2Set);

                return (diff1 - diff2);
            }
        };

        Collections.sort(words1, wordComparator);
        Collections.sort(words2, wordComparator);

        MergeIterator<String> iterator = new MergeIterator<String>(words1, words2,
                ((w1, w2) -> (wordComparator.compare(w1, w2) <= 0 ? MOVE.MOVE_FIRST : MOVE.MOVE_SECOND)));

        List<String> expected = Arrays.asList("abde", "bcd", "cdef", "abx", "pdef", "dxy", "bdcefxyz");
        List<String> actual = new ArrayList<>();
        while (iterator.hasNext()) {
            actual.add(iterator.next());
        }

        assert (expected.equals(actual));
    }

    private static void testTripleIterator() {
        List<Integer> firstList = Arrays.asList(2, 4, 6, 7, 8);
        List<Integer> secondList = Arrays.asList(1, 3);
        List<Integer> thirdList = Arrays.asList(5, 9);
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

        MergeIterator<Integer> iterator1 = new MergeIterator<Integer>(firstList, secondList,
                ((i, j) -> ((int)i <= (int)j) ? MOVE.MOVE_FIRST : MOVE.MOVE_SECOND));
        MergeIterator<Integer> iterator2 = new MergeIterator<Integer>(iterator1, thirdList.iterator(),
                ((i, j) -> ((int)i <= (int)j) ? MOVE.MOVE_FIRST : MOVE.MOVE_SECOND));

        List<Integer> actual = new ArrayList<>();
        while (iterator2.hasNext()) {
            actual.add(iterator2.next());
        }

        assert (expected.equals(actual));
    }

    public static void main(String[] args) {
        testSortMerge1();
        testSortMerge2();
        testSortMerge3();
        testSortMerge4();
        testSortMerge5();
        testSortMerge6();
        testCustomIteration();
        testTripleIterator();
    }
}
