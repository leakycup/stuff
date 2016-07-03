package me.soubhik;

import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Created by soubhik on 6/27/16.
 */
public class EditDistance {
    public static enum OPS {
        INSERT,
        DELETE,
        REPLACE,
        TRANSPOSE,
        COPY,
        IMPOSSIBLE
    }

    public static interface CostFunction {
        public ImmutablePair<Integer, OPS> cost(String from, String to, OPS op);
    }

    public static class BaseCostFunction implements CostFunction {
        private final int insert, delete, replace, transpose, copy, impossible;

        public BaseCostFunction(int insert, int delete, int replace, int transpose, int copy, int impossible) {
            this.insert = insert;
            this.delete = delete;
            this.replace = replace;
            this.transpose = transpose;
            this.copy = copy;
            this.impossible = impossible;
        }

        @Override
        public ImmutablePair<Integer, OPS> cost(String from, String to, OPS op) {
            assert (from != null);
            assert (to != null);

            if (((op == OPS.COPY) || (op == OPS.REPLACE)) && from.equals(to)) {
                return new ImmutablePair<Integer, OPS>(copy, OPS.COPY);
            }

            int fromLen = from.length();
            int toLen = to.length();

            if ((op == OPS.INSERT) && ((fromLen + 1) == toLen) && to.startsWith(from)) {
                return new ImmutablePair<Integer, OPS>(insert, OPS.INSERT);
            }

            if ((op == OPS.DELETE) && ((toLen + 1) == fromLen) && from.startsWith(to)) {
                return new ImmutablePair<Integer, OPS>(delete, OPS.DELETE);
            }

            if ((op == OPS.TRANSPOSE) &&
                    (fromLen >= 2) &&
                    (toLen >= 2) &&
                    (from.charAt(fromLen - 2) == to.charAt(toLen - 1)) &&
                    (from.charAt(fromLen - 1) == to.charAt(toLen - 2))) {
                return new ImmutablePair<Integer, OPS>(transpose, OPS.TRANSPOSE);
            }

            if ((op == OPS.REPLACE) &&
                    (fromLen >= 1) &&
                    (toLen >= 1) &&
                    (from.charAt(fromLen - 1) != to.charAt(toLen - 1))) {
                return new ImmutablePair<Integer, OPS>(replace, OPS.REPLACE);
            }

            return new ImmutablePair<Integer, OPS>(impossible, OPS.IMPOSSIBLE);
        }
    }

    public static class UniformCostFunction extends BaseCostFunction {
       public UniformCostFunction() {
           super(1, 1, 1, 1, 0, Integer.MAX_VALUE);
       }
    }

    private static ImmutablePair<Integer, String> min(ImmutablePair<Integer, String>... edits) {
        ImmutablePair<Integer, String> minEdit = null;
        for (ImmutablePair<Integer, String> edit: edits) {
            if (edit == null) {
                continue;
            }
            if (minEdit == null) {
                minEdit = edit;
            }
            if (minEdit.left > edit.left) {
                minEdit = edit;
            }
        }

        return minEdit;
    }

    private static int positiveSum(int x, int y) {
        int sum = x + y;
        if (sum < 0) {
            return Integer.MAX_VALUE;
        }

        return sum;
    }

    //damerau levenshtein edit distance: https://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance
    public static ImmutablePair<Integer, String> editDistance(String from, String to, CostFunction costFunction) {
        assert (from != null);
        assert (to != null);

        ImmutablePair<Integer, String>[][] memoizationArray = new ImmutablePair[from.length()+1][to.length()+1];
        return editDistance(from, to, from.length(), to.length(), costFunction, memoizationArray);
    }

    public static ImmutablePair<Integer, String> editDistance(String from, String to, int fromLen, int toLen,
                                                              CostFunction costFunction,
                                                              ImmutablePair<Integer, String>[][] memoizationArray) {
        assert (fromLen >= 0);
        assert (toLen >= 0);

        if (memoizationArray[fromLen][toLen] != null) {
            return memoizationArray[fromLen][toLen];
        }

        if ((fromLen == 0) && (toLen == 0)) {
            memoizationArray[fromLen][toLen] = new ImmutablePair<Integer, String>(0, "");
            return memoizationArray[fromLen][toLen];
        }

        ImmutablePair<Integer, String> delete = null;
        if (fromLen > 0) {
            ImmutablePair<Integer, String> e = editDistance(from, to, fromLen-1, toLen, costFunction,
                    memoizationArray);
            ImmutablePair<Integer, OPS> c = costFunction.cost(from.substring(fromLen-1, fromLen), "", OPS.DELETE);
            delete = new ImmutablePair<Integer, String>(positiveSum(e.left, c.left),
                    e.right + "|" + c.right.name() + "(" +from.substring(fromLen-1, fromLen) + ")");
        }

        ImmutablePair<Integer, String> insert = null;
        if (toLen > 0) {
            ImmutablePair<Integer, String> e = editDistance(from, to, fromLen, toLen-1, costFunction,
                    memoizationArray);
            ImmutablePair<Integer, OPS> c = costFunction.cost("", to.substring(toLen-1, toLen), OPS.INSERT);
            insert = new ImmutablePair<Integer, String>(positiveSum(e.left, c.left),
                    e.right + "|" + c.right.name() + "(" + to.substring(toLen-1, toLen) + ")");
        }

        ImmutablePair<Integer, String> replace = null;
        if ((fromLen > 0) && (toLen > 0)) {
            ImmutablePair<Integer, String> e = editDistance(from, to, fromLen-1, toLen-1, costFunction,
                    memoizationArray);
            ImmutablePair<Integer, OPS> c =
                    costFunction.cost(from.substring(fromLen-1, fromLen), to.substring(toLen-1, toLen), OPS.REPLACE);
            replace = new ImmutablePair<Integer, String>(positiveSum(e.left, c.left),
                    e.right + "|" + c.right.name() +
                            "(" + from.substring(fromLen-1, fromLen) + "," + to.substring(toLen-1, toLen) + ")");
        }

        ImmutablePair<Integer, String> transpose = null;
        if ((fromLen >= 2) & (toLen >= 2)) {
            ImmutablePair<Integer, String> e = editDistance(from, to, fromLen-2, toLen-2, costFunction,
                    memoizationArray);
            ImmutablePair<Integer, OPS> c =
                    costFunction.cost(from.substring(fromLen-2, fromLen), to.substring(toLen-2, toLen), OPS.TRANSPOSE);
            transpose = new ImmutablePair<Integer, String>(positiveSum(e.left, c.left),
                    e.right + "|" + c.right.name() +
                            "(" + from.substring(fromLen-2, fromLen) + "," + to.substring(toLen-2, toLen) + ")");
        }

        memoizationArray[fromLen][toLen] = min(delete, insert, replace, transpose);
        return memoizationArray[fromLen][toLen];
    }

    private static void testEditDistance(String from, String to, CostFunction costFunction) {
        ImmutablePair<Integer, String> edit = editDistance(from, to, costFunction);
        System.out.println(from + " --> " + to + ": cost: " + edit.left + ", edits: " + edit.right);
    }

    private static void testEditDistance(String from, String to) {
        testEditDistance(from, to, new UniformCostFunction());
    }

    public static void main(String[] args) {
        testEditDistance("papar", "paper");
        testEditDistance("papier", "paper");
        testEditDistance("papr", "paper");
        testEditDistance("paepr", "paper");
        testEditDistance("highlighlers", "highlighters");
        testEditDistance("78 x 4", "78 x4");
        testEditDistance("nexera", "extra");
        testEditDistance("tabs", "ittabs");
        testEditDistance("bat", "cat");
        testEditDistance("bat", "bet");
        testEditDistance("bat", "bad");
        testEditDistance("bat", "bats");
        testEditDistance("bat", "at");
        testEditDistance("magnetse", "magnets");
        testEditDistance("apple", "potatoes");

        CostFunction costFunction = new BaseCostFunction(2, 2, 1, 1, 0, 500000);
        testEditDistance("nexera", "extra", costFunction);

        costFunction = new BaseCostFunction(1, 1, 1, 5, 0, 500000);
        testEditDistance("paepr", "paper", costFunction);
    }
}
