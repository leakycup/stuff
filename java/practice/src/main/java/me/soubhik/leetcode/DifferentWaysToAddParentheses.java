package me.soubhik.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import org.testng.Assert;

/**
 * Created by soubhik on 21-10-2017.
 */
public class DifferentWaysToAddParentheses {
    static class Solution {
        private static final Set<String> supportedOperators;
        private static final Map<String, BinaryOperator<Integer>> supportedOperations = new HashMap<>();
        static {
            supportedOperations.put("+", (x, y) -> (x+y));
            supportedOperations.put("-", (x, y) -> (x-y));
            supportedOperations.put("*", (x, y) -> (x*y));

            supportedOperators = supportedOperations.keySet();
        }

        Map<String, List<Integer>> memoizer = new HashMap<>();
        //Map<String, List<Integer>> memoizer2 = new HashMap<>();

        public List<Integer> diffWaysToCompute(String input) {
            List<Integer> results = groupAndCalculate(input);
            Collections.sort(results);
            return results;
        }

        private List<Integer> groupAndCalculate(String expression) {
            if (memoizer.containsKey(expression)) {
                return memoizer.get(expression);
            }

            List<Integer> results = new ArrayList<>();
            List<Integer> operators = new ArrayList<>();

            for (int i = 0; i < expression.length(); i++) {
                String character = expression.substring(i, i+1);
                if (supportedOperators.contains(character)) {
                    operators.add(i);
                }
            }

            if (operators.isEmpty()) {
                results.add(Integer.parseInt(expression));
                memoizer.put(expression, results);
                return results;
            }

            for (Integer i: operators) {
                String left = expression.substring(0, i);
                String right = expression.substring(i+1);
                String operator = expression.substring(i, i+1);
                List<Integer> leftResults = groupAndCalculate(left);
                List<Integer> rightResults = groupAndCalculate(right);
                List<Integer> combined = combine(leftResults, rightResults, operator);
                results.addAll(combined);
            }

            memoizer.put(expression, results);
            return results;
        }

        List<Integer> combine(List<Integer> results1, List<Integer> results2, String operator) {
            //String key = new StringBuilder().append(results1).append(operator).append(results2).toString();
            //if (memoizer2.containsKey(key)) {
            //    return memoizer2.get(key);
            //}

            List<Integer> results = new ArrayList<>(results1.size() * results2.size());
            BinaryOperator<Integer> op = supportedOperations.get(operator);
            for (Integer r1: results1) {
                for (Integer r2: results2) {
                    results.add(op.apply(r1, r2));
                }
            }

            //memoizer2.put(key, results);
            return results;
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        List<Integer> result = solution.diffWaysToCompute("3+1*2");
        List<Integer> expected = Arrays.asList(5, 8);
        Assert.assertEquals(result, expected);
    }
}
