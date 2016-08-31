package me.soubhik;

import java.util.Random;

/**
 * Created by soubhik on 6/27/16.
 */
public class Sorting {
    public static class Pair<T> {
        final T first;
        final T second;

        public Pair(T first, T second) {
            this.first = first;
            this.second = second;
        }
    }

    private static <T> void swap(T[] elements, int i, int j) {
        if (i == j) {
            return;
        }
        T first = elements[i];
        T second = elements[j];
        elements[i] = second;
        elements[j] = first;
    }

    public static <T extends Comparable<T>> Pair<Integer> dutchNationalFlagPartition(T[] elements, T pivot) {
       return dutchNationalFlagPartition_v2(elements, pivot, 0, elements.length);
    }

    /**
     * returns a pair of integers representing the lower (inclusive) and upper (exclusive) indexes
     * of the pivot elements in the given array.
     * @param elements input array of elements where we want to determine the final location of the pivot element
     * @param pivot the element whose final location we seek to obtain in the elements array. the array may contain
     *              0 or more pivot elements.
     * @param low lower index of the array (inclusive)
     * @param high upper index of the array (exclusive)
     * @param <T> type of the elements. should be a Comparable.
     * @return pair of integers (x, y) representing, respectively, the lower (inclusive) and the upper (exclusive)
     * indexes of the pivot elements positions.
     * x == high iff all elements are lower than pivot.
     * y == low iff all elements are higher than pivot.
     * x == y iff pivot is not found between low and high.
     * otherwise, x >= low, x < high, x < y, y <= high.
     */
    public static <T extends Comparable<T>> Pair<Integer> dutchNationalFlagPartition_v2(T[] elements, T pivot,
                                                                                        int low, int high) {
        assert (elements != null);
        assert (low < high);

        int l = low;
        while (l < high) {
            if (elements[l].compareTo(pivot) >= 0) {
                break;
            }
            l++;
        }
        if (l == high) {
            //all elements are less than pivot
            return new Pair<Integer>(high, high);
        }

        int h = high - 1;
        while (h >= l) {
            if (elements[h].compareTo(pivot) <= 0) {
                break;
            }
            h--;
        }

        int p = l;
        while (p <= h) {
            if (elements[p].compareTo(pivot) == 0) {
                p++;
            } else if (elements[p].compareTo(pivot) < 0) {
                swap(elements, l, p);
                l++;
                p++;
            } else {
                swap(elements, p, h);
                h--;
            }
        }

        return new Pair<>(l, (h+1));
    }

    public static <T extends Comparable<T>> Pair<Integer> dutchNationalFlagPartition_v1(T[] elements, T pivot,
                                                                                        int low, int high) {
        assert (elements != null);
        assert (low < high);

        int n = high - 1;
        while (n >= low) {
            if (elements[n].compareTo(pivot) <= 0) {
                break;
            }
            n--;
        }

        if (n < low) {
            return new Pair<Integer>(-1, -1); //pivot does not exist
        }

        int l = 0; //bug: rhs should be low
        int h = 0;
        while (h <= n) {
            if (elements[h].compareTo(pivot) < 0) {
                swap(elements, l, h);
                l++;
                h++;
            } else if (elements[h].compareTo(pivot) == 0) {
                h++;
            } else {
                swap(elements, h, n);
                n--;
            }
        }

        return new Pair<Integer>(l, h);
    }

    public static int randomInt(int i, int j) {
        final Random random = new Random();
        assert (j >= i);

        return (i + random.nextInt(j - i));
    }

    public static <T extends Comparable<T>> void quickSort(T[] elements) {
        quickSort(elements, 0, elements.length);
    }

    public static <T extends Comparable<T>> void quickSort(T[] elements, int low, int high) {
        assert (high >= low);

        if ((high - low) == 0) {  //sort empty array: trivial case
            return;
        }
        if ((high - low) == 1) {  //sort one element: trivial case
            return;
        }

        int pivotIndex = randomInt(low, high);
        T pivot = elements[pivotIndex];
        Pair<Integer> partitionIndex = dutchNationalFlagPartition_v2(elements, pivot, low, high);
        quickSort(elements, low, partitionIndex.first);
        quickSort(elements, partitionIndex.second, high);
    }

    public static void radixSort() {
        //TODO
    }

    private static <T extends Comparable<T>> void validatePartition(T[] elements, int start, int end, T pivot) {
        validatePartition(elements, 0, elements.length, start, end, pivot);
    }

    private static <T extends Comparable<T>> void validatePartition(T[] elements, int low, int high,
                                                                    int start, int end, T pivot) {
        for (int i = low; i < start; i++) {
            T e = elements[i];
            assert (e.compareTo(pivot) < 0);
        }

        for (int i = start; i < end; i++) {
            T e = elements[i];
            assert (e.compareTo(pivot) == 0);
        }

        for (int i = end; i < high; i++) {
            T e = elements[i];
            assert (e.compareTo(pivot) > 0);
        }
    }

    private static <T extends Comparable<T>> void validateSort(T[] elements) {
        assert (elements.length > 0);

        T previous = elements[0];
        for (T e: elements) {
            assert (e.compareTo(previous) >= 0);
            previous = e;
        }
    }

    public static void main(String[] args) {
        Integer[] inputs1 = new Integer[] {2, 8, 4, 3, 4, 3, 6, 9, 0};

        System.out.println("Dutch national flag partitioning problem");
        System.out.println("=================================================");
        System.out.println("Dutch national flag partition with pivot: " + 3);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        Pair<Integer> indices = dutchNationalFlagPartition(inputs1, 3);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs1, indices.first, indices.second, 3);

        System.out.println("Dutch national flag partition with pivot: " + 3);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs1, 3);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs1, indices.first, indices.second, 3);

        System.out.println("Dutch national flag partition with pivot: " + 6);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs1, 6);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs1, indices.first, indices.second, 6);

        System.out.println("Dutch national flag partition with pivot: " + 2);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs1, 2);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs1, indices.first, indices.second, 2);

        System.out.println("Dutch national flag partition with pivot: " + 8);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs1, 8);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs1, indices.first, indices.second, 8);

        System.out.println("Dutch national flag partition with pivot: " + 20);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs1, 20);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs1, indices.first, indices.second, 20);
        assert (indices.first == indices.second);

        System.out.println("Dutch national flag partition with pivot: " + -5);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs1, -5);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs1, indices.first, indices.second, -5);
        assert (indices.first == indices.second);

        System.out.println("Dutch national flag partition with pivot: " + 7);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs1, 7);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs1, indices.first, indices.second, 7);
        assert (indices.first == indices.second);

        System.out.println("Dutch national flag partition with pivot: " + 0);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs1, 0);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs1, indices.first, indices.second, 0);

        System.out.println("Dutch national flag partition with pivot: " + 9);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs1, 9);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs1, indices.first, indices.second, 9);

        Integer[] inputs2 = new Integer[] {4, 4, 4, 4, 4};
        System.out.println("Dutch national flag partition with pivot: " + 4);
        for (int i: inputs2) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs2, 4);
        for (int i: inputs2) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs2, indices.first, indices.second, 4);

        System.out.println("Dutch national flag partition with pivot: " + 2);
        for (int i: inputs2) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs2, 2);
        for (int i: inputs2) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs2, indices.first, indices.second, 2);
        assert (indices.first == indices.second);

        System.out.println("Dutch national flag partition with pivot: " + 8);
        for (int i: inputs2) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs2, 8);
        for (int i: inputs2) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs2, indices.first, indices.second, 8);
        assert (indices.first == indices.second);

        String[] inputs3 = new String[] {"red", "blue", "blue", "red", "white", "blue"};
        System.out.println("Dutch national flag partition with pivot: " + "blue");
        for (String input: inputs3) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition(inputs3, "blue");
        for (String input: inputs3) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(inputs3, indices.first, indices.second, "blue");

        Integer[] inputs = new Integer[] {28, 58, 3, 89, 33, 89, 68, 80, 42, 10};
        Integer[] testData = new Integer[inputs.length];

        System.arraycopy(inputs, 0, testData, 0, inputs.length);
        System.out.println("Dutch national flag partition with pivot: " + 33);
        for (Integer input: testData) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition_v2(testData, 33, 2, 7);
        for (Integer input: testData) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(testData, 2, 7, indices.first, indices.second, 33);

        System.arraycopy(inputs, 0, testData, 0, inputs.length);
        System.out.println("Dutch national flag partition with pivot: " + 89);
        for (Integer input: testData) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition_v2(testData, 89, 2, 7);
        for (Integer input: testData) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(testData, 2, 7, indices.first, indices.second, 89);

        System.arraycopy(inputs, 0, testData, 0, inputs.length);
        System.out.println("Dutch national flag partition with pivot: " + 58);
        for (Integer input: testData) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition_v2(testData, 58, 2, 7);
        for (Integer input: testData) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(testData, 2, 7, indices.first, indices.second, 58);
        assert (indices.first == indices.second);

        System.arraycopy(inputs, 0, testData, 0, inputs.length);
        System.out.println("Dutch national flag partition with pivot: " + 42);
        for (Integer input: testData) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        indices = dutchNationalFlagPartition_v2(testData, 42, 2, 7);
        for (Integer input: testData) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        System.out.println("[" + indices.first + "," + indices.second + ")");
        validatePartition(testData, 2, 7, indices.first, indices.second, 42);
        assert (indices.first == indices.second);

        System.out.println("quicksort");
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        quickSort(inputs1);
        for (int i: inputs1) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        validateSort(inputs1);

        System.out.println("quicksort");
        for (int i: inputs2) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        quickSort(inputs2);
        for (int i: inputs2) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        validateSort(inputs2);

        System.out.println("quicksort");
        for (String input: inputs3) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        quickSort(inputs3);
        for (String input: inputs3) {
            System.out.print(input + " ");
        }
        System.out.print("\n");
        validateSort(inputs3);

        Integer[] inputs4 = new Integer[] {1, 8, 5, 6, 2, 2, 7};
        System.out.println("quicksort");
        for (int i: inputs4) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        quickSort(inputs4);
        for (int i: inputs4) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        validateSort(inputs4);

        Integer[] inputs5 = new Integer[] {2, 2, 2, 8, 2, 4, 2};
        System.out.println("quicksort");
        for (int i: inputs5) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        quickSort(inputs5);
        for (int i: inputs5) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
        validateSort(inputs5);
    }
}
