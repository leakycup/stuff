package me.soubhik;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by soubhik on 7/3/16.
 */
public class Random {
    //TODO: random number generator, reservoir sampling, entropy
    public static class Interval {
        double start; //inclusive
        double end; //exclusive
        double probability; //>= 0 and < 1
    }

    public static class RandomInt {
        private final ArrayList<Double> intervals;
        private final int lower;

        public RandomInt(int n) {
            this(0, n);
        }

        public RandomInt(int lower, int upper) {
            assert (upper > lower);

            int range = upper - lower;
            this.intervals = new ArrayList<Double>(range);
            this.lower = lower;

            double length = ((double)1)/((double)range);
            double start = 0;
            for (int i = 0; i < range; i++) {
                intervals.add(start);
                start += length;
            }
        }

        public int next() {
            double random = Math.random();
            return (int)findInterval(intervals, random) + lower;
        }
    }

    public static class DiscreteRandom {
        private final ArrayList<Double> intervals;
        private final Map<Integer, Integer> intervalToDatum;

        public DiscreteRandom(Distribution distribution) {
            Map<Integer, Double> probabilities = distribution.probabilities();
            int numIntervals = probabilities.keySet().size();

            this.intervals = new ArrayList<Double>(numIntervals);
            this.intervalToDatum = new HashMap<Integer, Integer>(numIntervals);

            int idx = 0;
            double intervalStart = 0.0d;
            for (Map.Entry<Integer, Double> entry: probabilities.entrySet()) {
                int datum = entry.getKey();
                intervalToDatum.put(idx, datum);
                idx++;

                intervals.add(intervalStart);
                double probability = entry.getValue();
                intervalStart += probability;
            }
        }

        public int next() {
            double random = Math.random();
            int interval = findInterval(intervals, random);
            return intervalToDatum.get(interval);
        }
    }

    public static class RandomIntWithoutReplacement {
        private final int lower, upper;
        private final int[] numbers;
        private int numbersAvailable;

        public RandomIntWithoutReplacement(int lower, int upper) {
            int range = upper - lower;
            this.lower = lower;
            this.upper = upper;
            this.numbers = new int[range];
            for (int i = 0; i < range; i++) {
                numbers[i] = i;
            }
            this.numbersAvailable = range;
        }

        public int next() {
            if (numbersAvailable <= 0) {
                return (upper);
            }

            RandomInt randomInt = new RandomInt(numbersAvailable); //O(numbersAvailable)
            int nextRandomIndex = randomInt.next(); //O(log(numbersAvailable))
            int nextRandom = numbers[nextRandomIndex];
            if (nextRandomIndex < (numbersAvailable - 1)) {
                numbers[nextRandomIndex] = numbers[numbersAvailable - 1];
            }
            numbersAvailable--;

            return nextRandom + lower;
        }
    }

    public static class Distribution {
        Map<Integer, Integer> frequencies;

        public Distribution() {
            frequencies = new HashMap<Integer, Integer>();
        }

        public void add(Integer datum) {
            if (!frequencies.containsKey(datum)) {
                frequencies.put(datum, 0);
            }
            int oldFrequency = frequencies.get(datum);
            frequencies.put(datum, oldFrequency+1);
        }

        public Map<Integer, Double> probabilities() {
            int totalFrequency = 0;
            for (int frequency: frequencies.values()) {
                totalFrequency += frequency;
            }

            Map<Integer, Double> probabilityDistribution = new HashMap<Integer, Double>();
            for (Map.Entry<Integer, Integer> entry: frequencies.entrySet()) {
                int datum = entry.getKey();
                int frequency = entry.getValue();
                double probability = ((double)frequency)/((double)totalFrequency);
                probabilityDistribution.put(datum, probability);
            }

            return probabilityDistribution;
        }

        public void print() {
            Map<Integer, Double> probabilityDistribution = probabilities();

            for (Map.Entry<Integer, Integer> entry: frequencies.entrySet()) {
                int datum = entry.getKey();
                int frequency = entry.getValue();
                double probability = probabilityDistribution.get(datum);
                System.out.println(datum + ":" + frequency + " (" + probability + ")");
            }
        }
    }

    private static <T> int findInterval(ArrayList<? extends Comparable<? super T>> intervals, T key) {
        int index = Collections.binarySearch(intervals, key);

        if (index >= 0) { //found
            return index;
        }

        //not found. index == -(insertionPoint + 1)
        int insertionPoint = -(index + 1);

        return (insertionPoint - 1);
    }

    private static void randomTest(int lower, int upper) {
        RandomInt randomInt = new RandomInt(lower, upper);
        Distribution distribution = new Distribution();
        System.out.println("Random integers from " + lower + " to " + upper);
        System.out.println("===============================================");
        for (int i = 0; i < 20; i++) {
            int datum = randomInt.next();
            distribution.add(datum);
            System.out.print(datum + " ");
        }
        System.out.print("\n");
        distribution.print();
    }

    private static void randomTest2(int lower, int upper) {
        RandomInt randomInt = new RandomInt(lower, upper);
        Distribution distribution = new Distribution();
        System.out.println("Random integers from " + lower + " to " + upper + " using RandomInt");
        System.out.println("===============================================");
        for (int i = 0; i < 20; i++) {
            int datum = randomInt.next();
            distribution.add(datum);
            System.out.print(datum + " ");
        }
        System.out.print("\n");
        distribution.print();

        System.out.println("Random integers from " + lower + " to " + upper + " using DiscreteRandom");
        System.out.println("===============================================");
        DiscreteRandom discreteRandom = new DiscreteRandom(distribution);
        Distribution distribution2 = new Distribution();
        for (int i = 0; i < 20; i++) {
            int datum = discreteRandom.next();
            distribution2.add(datum);
            System.out.print(datum + " ");
        }
        System.out.print("\n");
        distribution2.print();
    }

    private static void randomTest3(int lower, int upper) {
        RandomIntWithoutReplacement randomInt = new RandomIntWithoutReplacement(lower, upper);
        Distribution distribution = new Distribution();
        System.out.println("Random integers from " + lower + " to " + upper + " using RandomIntWithoutReplacement");
        System.out.println("===============================================");
        for (int i = 0; i < 20; i++) {
            int datum = randomInt.next();
            System.out.print(datum + " ");
            if (datum < upper) {
                distribution.add(datum);
            }
        }
        System.out.print("\n");
        distribution.print();
    }

    public static void main(String[] args) {
        int k = 2;
        RandomInt randomInt = new RandomInt(k);
        Distribution distribution = new Distribution();
        System.out.println("Random integers from 0 to " + k);
        System.out.println("===========================================");
        for (int i = 0; i < 20; i++) {
            int datum = randomInt.next();
            distribution.add(datum);
            System.out.print(datum + " ");
        }
        System.out.print("\n");
        distribution.print();

        randomTest(0, 3);
        randomTest(4, 12);

        randomTest2(0, 2);
        randomTest2(6, 10);

        randomTest3(0, 2);
        randomTest3(6, 10);
    }
}
