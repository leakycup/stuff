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
       return dutchNationalFlagPartition(elements, pivot, 0, elements.length);
    }

    public static <T extends Comparable<T>> Pair<Integer> dutchNationalFlagPartition(T[] elements, T pivot,
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
        Pair<Integer> partitionIndex = dutchNationalFlagPartition(elements, pivot, low, high);
        quickSort(elements, low, partitionIndex.first); //bug: partitionIndex.first is inclusive
        quickSort(elements, partitionIndex.second, high); //bug: partitionIndex.second is exclusive
    }

    public static void radixSort() {
        //TODO
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
    }
}
