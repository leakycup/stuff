package me.soubhik;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by soubhik on 6/25/16.
 */
public class StringPermutations {
    public static Set<String> permuteString(String original) {
       Set<String> permutations = new HashSet<String>();

        if (original.isEmpty()) {
            permutations.add("");
            return permutations;
        }

        int n = original.length();
        int[] indices = new int[n];
        Arrays.fill(indices, -1);
        permuteString(original, n, indices, permutations);

        return permutations;
    }

    private static void permuteString(String original, int n, int[] indices, Set<String> permutations) {
        assert (n >= 1);

        if (n == 1) {
            for (int i = 0; i < indices.length; i++) {
                if (indices[i] < 0) {
                    indices[i] = n-1;
                    String permutation = indicesToString(original, indices);
                    permutations.add(permutation);
                    indices[i] = -1;
                    return;
                }
            }

        }

        for (int i = 0; i < indices.length; i++) {
            if (indices[i] >= 0) {
                continue;
            }
            indices[i] = n-1;
            permuteString(original, n-1, indices, permutations);
            indices[i] = -1;
        }
    }

    private static String indicesToString(String original, int[] indices) {
        StringBuilder builder = new StringBuilder();
        for (int i: indices) {
            builder.append(original.charAt(i));
        }

        return builder.toString();
    }

    public static void main(String[] args) {
        String input1 = "abc";
        Set<String> results1 = permuteString(input1);
        for (String r: results1) {
            System.out.println(r);
        }
        System.out.println("Total: " + results1.size());

        String input2 = "aba";
        Set<String> results2 = permuteString(input2);
        for (String r: results2) {
            System.out.println(r);
        }
        System.out.println("Total: " + results2.size());
    }
}
