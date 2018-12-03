package me.soubhik.careercup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by soubhik on 03-12-2018.
 * from https://www.careercup.com/question?id=5697271283318784
 */
public class MergeIntervalArrays {
    private static class Interval {
        int start;
        int end;

        public Interval(int start, int end) {
            if (start > end) {
                throw new IllegalArgumentException("start should be <= end");
            }

            this.start = start;
            this.end = end;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (this == other) {
                return true;
            }
            Interval that = (Interval) other;
            return ((this.start == that.start) && (this.end == that.end));
        }

        public int compare(Interval that) {
            if (this.end < that.start) {
                return -2;
            }
            if (this.start > that.end) {
                return 2;
            }
            if (this.end < that.end) {
                return -1;
            }
            if (this.end > that.end) {
                return 1;
            }

            return 0;
        }
    }

    public static List<Interval> mergeIntervalArrays(ArrayList<Interval> first, ArrayList<Interval> second) {
        List<Interval> mergedIntervals = new LinkedList<>();

        Interval overlappingInterval = null;
        boolean advancedFirst = false;
        int i, j;
        for (i = 0, j = 0; i < first.size() && j < second.size();) {
            Interval one = first.get(i);
            Interval two = second.get(j);
            if (overlappingInterval == null) {
                int c = one.compare(two);
                if (c == -2) {
                    mergedIntervals.add(one);
                    i++;
                } else if (c == 2) {
                    mergedIntervals.add(two);
                    j++;
                } else if (c == 0) {
                    if (one.start < two.start) {
                        mergedIntervals.add(one);
                    } else {
                        mergedIntervals.add(two);
                    }
                    i++;
                    j++;
                } else if (c == -1) {
                    int start = (one.start < two.start) ? one.start : two.start;
                    int end = two.end;
                    overlappingInterval = new Interval(start, end);
                    i++;
                    advancedFirst = true;
                } else if (c == 1) {
                    int start = (one.start < two.start) ? one.start : two.start;
                    int end = one.end;
                    overlappingInterval = new Interval(start, end);
                    j++;
                    advancedFirst = false;
                } else {
                    throw new RuntimeException("buggy code");
                }
            } else {
                if (advancedFirst) {
                    int c = overlappingInterval.compare(one);
                    if (c == -2) {
                        mergedIntervals.add(overlappingInterval);
                        overlappingInterval = null;
                        j++;
                    } else if (c == -1) {
                        overlappingInterval.end = one.end;
                        j++;
                        advancedFirst = false;
                    } else if (c == 1) {
                        i++;
                        advancedFirst = true;
                    } else if (c == 0) {
                        mergedIntervals.add(overlappingInterval);
                        overlappingInterval = null;
                        i++;
                        j++;
                    } else {
                        throw new RuntimeException("buggy code");
                    }
                } else {
                    int c = overlappingInterval.compare(two);
                    if (c == -2) {
                        mergedIntervals.add(overlappingInterval);
                        overlappingInterval = null;
                        i++;
                    } else if (c == -1) {
                        overlappingInterval.end = two.end;
                        i++;
                        advancedFirst = true;
                    } else if (c == 1) {
                        j++;
                        advancedFirst = false;
                    } else if (c == 0) {
                        mergedIntervals.add(overlappingInterval);
                        overlappingInterval = null;
                        i++;
                        j++;
                    } else {
                        throw new RuntimeException("buggy code");
                    }
                }
            }
        }

        if (overlappingInterval != null) {
            mergedIntervals.add(overlappingInterval);
            if (advancedFirst) {
                j++;
            } else {
                i++;
            }
        }

        for (; i < first.size(); i++) {
            mergedIntervals.add(first.get(i));
        }
        for (; j < second.size(); j++) {
            mergedIntervals.add(second.get(j));
        }

        return mergedIntervals;
    }

    private static ArrayList<Interval> toIntervalArray(int[] points) {
        if ((points.length == 0) || ((points.length % 2) == 1)) {
            throw new IllegalArgumentException("expecting even number of points");
        }

        ArrayList<Interval> intervalArray = new ArrayList<>(points.length/2);

        for (int i = 1; i < points.length; i += 2) {
            intervalArray.add(new Interval(points[i-1], points[i]));
        }

        return intervalArray;
    }

    private static void test1() {
        ArrayList<Interval> first = toIntervalArray(new int[] {2, 4, 7, 7, 10, 15});
        ArrayList<Interval> second = toIntervalArray(new int[] {5, 6, 8, 9, 18, 20});
        List<Interval> expected = toIntervalArray(new int[] {2, 4, 5, 6, 7, 7, 8, 9, 10, 15, 18, 20});
        List<Interval> actual = mergeIntervalArrays(first, second);

        assert (expected.equals(actual));
    }

    private static void test2() {
        ArrayList<Interval> first = toIntervalArray(new int[] {1, 2, 5, 6, 10, 19, 23, 25});
        ArrayList<Interval> second = toIntervalArray(new int[] {8, 11, 15, 15, 17, 24});
        List<Interval> expected = toIntervalArray(new int[] {1, 2, 5, 6, 8, 25});
        List<Interval> actual = mergeIntervalArrays(first, second);

        assert (expected.equals(actual));
    }

    private static void test3() {
        ArrayList<Interval> first = toIntervalArray(new int[] {1, 2, 5, 6, 10, 19, 23, 25, 30, 40});
        ArrayList<Interval> second = toIntervalArray(new int[] {8, 11, 15, 15, 17, 24});
        List<Interval> expected = toIntervalArray(new int[] {1, 2, 5, 6, 8, 25, 30, 40});
        List<Interval> actual = mergeIntervalArrays(first, second);

        assert (expected.equals(actual));
    }

    private static void test4() {
        ArrayList<Interval> first = toIntervalArray(new int[] {1, 2, 5, 6, 10, 19, 23, 25});
        ArrayList<Interval> second = toIntervalArray(new int[] {8, 11, 15, 15, 17, 24, 30, 40});
        List<Interval> expected = toIntervalArray(new int[] {1, 2, 5, 6, 8, 25, 30, 40});
        List<Interval> actual = mergeIntervalArrays(first, second);

        assert (expected.equals(actual));
    }

    private static void test5() {
        ArrayList<Interval> first = toIntervalArray(new int[] {1, 2, 5, 6, 10, 19, 23, 25, 28, 28, 30, 40, 50, 60});
        ArrayList<Interval> second = toIntervalArray(new int[] {8, 11, 15, 15, 17, 24, 30, 40});
        List<Interval> expected = toIntervalArray(new int[] {1, 2, 5, 6, 8, 25, 28, 28, 30, 40, 50, 60});
        List<Interval> actual = mergeIntervalArrays(first, second);

        assert (expected.equals(actual));
    }

    private static void test() {
        test1();
        test2();
        test3();
        test4();
        test5();
    }

    public static void main(String[] args) {
        test();
    }
}
