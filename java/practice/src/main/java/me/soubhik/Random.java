package me.soubhik;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by soubhik on 7/3/16.
 */
public class Random {
    //TODO: reservoir sampling, entropy
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

        public RandomIntWithoutReplacement(int upper) {
            this(0, upper);
        }

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

    public static class Distribution<T> {
        Map<T, Integer> frequencies;

        public Distribution() {
            frequencies = new HashMap<T, Integer>();
        }

        public void add(T datum) {
            if (!frequencies.containsKey(datum)) {
                frequencies.put(datum, 0);
            }
            int oldFrequency = frequencies.get(datum);
            frequencies.put(datum, oldFrequency+1);
        }

        public Map<T, Double> probabilities() {
            int totalFrequency = 0;
            for (int frequency: frequencies.values()) {
                totalFrequency += frequency;
            }

            Map<T, Double> probabilityDistribution = new HashMap<T, Double>();
            for (Map.Entry<T, Integer> entry: frequencies.entrySet()) {
                T datum = entry.getKey();
                int frequency = entry.getValue();
                double probability = ((double)frequency)/((double)totalFrequency);
                probabilityDistribution.put(datum, probability);
            }

            return probabilityDistribution;
        }

        public void print() {
            Map<T, Double> probabilityDistribution = probabilities();

            for (Map.Entry<T, Integer> entry: frequencies.entrySet()) {
                T datum = entry.getKey();
                int frequency = entry.getValue();
                double probability = probabilityDistribution.get(datum);
                System.out.println(datum + ":" + frequency + " (" + probability + ")");
            }
        }
    }

    public static <T> Collection<T> sample(List<T> population, int sampleSize) {
        assert (sampleSize < population.size());
        assert (sampleSize > 0);

        List<T> sample = new ArrayList<T>(sampleSize);
        RandomIntWithoutReplacement randomGenerator = new RandomIntWithoutReplacement(population.size());
        for (int i = 0; i < sampleSize; i++) {
            int randomIndex = randomGenerator.next();
            sample.add(population.get(randomIndex));
        }

        return sample;
    }

    public static <T> void randomSort(List<T> elements) {
        int[] indices = new int[elements.size()];
        RandomIntWithoutReplacement randomGenerator = new RandomIntWithoutReplacement(elements.size());
        for (int i = 0; i < indices.length; i++) {
            indices[i] = randomGenerator.next();
        }
        //sbh: debug
        /*
        System.out.print("original indices: ");
        for (int index: indices) {
            System.out.print(index + ", ");
        }
        System.out.print("\n");
        */
        //sbh: end debug

        int currentIndex = 0;
        int restartIndex = currentIndex + 1;
        T currentElement = elements.get(currentIndex);
        for (int i = 0; i < indices.length; i++) {
            int newIndex = indices[currentIndex];
            indices[currentIndex] = -1; //mark visited
            T displaced = elements.get(newIndex);
            elements.set(newIndex, currentElement);

            //sbh: debug
            /*
            System.out.println("sbh: currentIndex: " + currentIndex + ", newIndex: " + newIndex +
                    ", currentElement: " + currentElement + ", displaced: " + displaced);
            System.out.print("elements: ");
            for (T e: elements) {
                System.out.print(e + ", ");
            }
            System.out.print("\n");
            System.out.print("indices: ");
            for (int index: indices) {
                System.out.print(index + ", ");
            }
            System.out.print("\n");
            */
            //sbh: end debug

            currentElement = displaced;
            currentIndex = newIndex;
            while ((restartIndex < indices.length) && (indices[currentIndex] < 0)) {
                currentIndex = restartIndex;
                currentElement = elements.get(currentIndex);
                restartIndex++;
            }
            if (currentIndex >= indices.length) {
                break;
            }
        }
    }

    public static <T> double selfInformation(Distribution<T> distribution, T datum) {
        return selfInformation(distribution.probabilities(), datum);
    }

    public static <T> double selfInformation(Map<T, Double> probabilities, T datum) {
        assert probabilities.containsKey(datum);

        double p = probabilities.get(datum);
        return -log2(p);
    }

    public static double entropy(Distribution<?> distribution) {
        return entropy(distribution.probabilities());
    }

    public static double entropy(Map<?, Double> probabilities) {
        double e = 0d;
        for (double p: probabilities.values()) {
            e += p * log2(p);
        }

        return -e;
    }

    public static <T> double pmi(Distribution<T> xDistribution,
                                 Distribution<T> yDistribution,
                                 Distribution<ImmutablePair<T, T>> xyDistribution,
                                 T x, T y) {
        return pmi(xDistribution.probabilities(), yDistribution.probabilities(), xyDistribution.probabilities(), x, y);
    }

    public static <T> double pmi(Map<T, Double> xProbabilities,
                                 Map<T, Double> yProbabilities,
                                 Map<ImmutablePair<T, T>, Double> xyProbabilities,
                                 T x, T y) {
        double selfX = selfInformation(xProbabilities, x);
        double selfY = selfInformation(yProbabilities, y);
        double selfXY = selfInformation(xyProbabilities, new ImmutablePair<T, T>(x, y));

        return (selfX + selfY - selfXY);
    }

    public static <T> double normalizedPMI(Distribution<T> xDistribution,
                                           Distribution<T> yDistribution,
                                           Distribution<ImmutablePair<T, T>> xyDistribution,
                                           T x, T y) {
        return normalizedPMI(xDistribution.probabilities(),
                yDistribution.probabilities(),
                xyDistribution.probabilities(), x, y);
    }

    public static <T> double normalizedPMI(Map<T, Double> xProbabilities,
                                           Map<T, Double> yProbabilities,
                                           Map<ImmutablePair<T, T>, Double> xyProbabilities,
                                           T x, T y) {
        double pxy = xyProbabilities.get(new ImmutablePair<T, T>(x, y));

        return (-pmi(xProbabilities, yProbabilities, xyProbabilities, x, y) / log2(pxy));
    }

    public static <T> double mutualInformation(Distribution<T> xDistribution,
                                               Distribution<T> yDistribution,
                                               Distribution<ImmutablePair<T, T>> xyDistribution) {
        return mutualInformation(xDistribution.probabilities(),
                yDistribution.probabilities(),
                xyDistribution.probabilities());
    }

    public static <T> double mutualInformation(Map<T, Double> xProbabilities,
                                               Map<T, Double> yProbabilities,
                                               Map<ImmutablePair<T, T>, Double> xyProbabilities) {
        double mi = 0d;

        for (T x: xProbabilities.keySet()) {
            for (T y: yProbabilities.keySet()) {
                double pmiXY = pmi(xProbabilities, yProbabilities, xyProbabilities, x, y);
                double pXY = xyProbabilities.get(new ImmutablePair<T, T>(x, y));
                mi += pmiXY * pXY;
            }
        }

        return mi;
    }

    public static <T> double crossEntropy(Distribution<T> xDistribution, Distribution<T> yDistribution) {
        return crossEntropy(xDistribution.probabilities(), yDistribution.probabilities());
    }

    public static <T> double crossEntropy(Map<T, Double> xProbabilities, Map<T, Double> yProbabilities) {
        double ce = 0d;

        for (T x: xProbabilities.keySet()) {
            double px = xProbabilities.get(x);
            double py;
            if (yProbabilities.containsKey(x)) {
                py = yProbabilities.get(x);
                ce += px * log2(py);
            } else {
                py = 0d;
                assert (px == 0);
                ce += 0d; //as x-->0, x*log(x)-->0
            }
        }

        return -ce;
    }

    public static <T> double klDivergence(Distribution<T> xDistribution, Distribution<T> yDistribution) {
        return klDivergence(xDistribution.probabilities(), yDistribution.probabilities());
    }

    public static <T> double klDivergence(Map<T, Double> xProbabilities, Map<T, Double> yProbabilities) {
        double ceXY = crossEntropy(xProbabilities, yProbabilities);
        double eX = entropy(xProbabilities);

        return (ceXY - eX);
    }

    public static <T> double jsDivergence(Distribution<T> xDistribution, Distribution<T> yDistribution) {
        return jsDivergence(xDistribution.probabilities(), yDistribution.probabilities());
    }

    public static <T> double jsDivergence(Map<T, Double> xProbabilities, Map<T, Double> yProbabilities) {
        Map<T, Double> zProbabilities = new HashMap<T, Double>(xProbabilities);

        for (T y: yProbabilities.keySet()) {
            if (!zProbabilities.containsKey(y)) {
                zProbabilities.put(y, 0d);
            }
            double pz = zProbabilities.get(y);
            pz += yProbabilities.get(y);
            zProbabilities.put(y, pz);
        }

        for (T z: zProbabilities.keySet()) {
            double pz = zProbabilities.get(z);
            pz /= 2d;
            zProbabilities.put(z, pz);
        }

        double klXZ = klDivergence(xProbabilities, zProbabilities);
        double klYZ = klDivergence(yProbabilities, zProbabilities);

        return (klXZ + klYZ)/2d;
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

    private static double log2(double number) {
        return Math.log(number) / Math.log(2.0d);
    }

    private static void randomTest(int lower, int upper) {
        RandomInt randomInt = new RandomInt(lower, upper);
        Distribution distribution = new Distribution<Integer>();
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
        Distribution distribution = new Distribution<Integer>();
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
        Distribution distribution2 = new Distribution<Integer>();
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
        Distribution distribution = new Distribution<Integer>();
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

    private static void randomTest4(List<String> population, int size) {
        Collection<String> sample = sample(population, size);

        Distribution populationDistribution = new Distribution<String>();
        System.out.print("Population: ");
        for (String s: population) {
            System.out.print(s + " ");
            populationDistribution.add(s);
        }
        System.out.print("\n");
        System.out.println("Population distribution: ");
        populationDistribution.print();

        Distribution sampleDistribution = new Distribution<String>();
        System.out.print("Sample: ");
        for (String s: sample) {
            System.out.print(s + " ");
            sampleDistribution.add(s);
        }
        System.out.print("\n");
        System.out.println("Sample distribution: ");
        sampleDistribution.print();
    }

    private static void randomTest5(List<Integer> elements) {
        Distribution<Integer> distribution = new Distribution<Integer>();
        System.out.print("Original array: ");
        for (Integer e: elements) {
            System.out.print(e + ", ");
            distribution.add(e);
        }
        System.out.print("\n");
        System.out.println("original distribution:");
        distribution.print();

        randomSort(elements);
        distribution = new Distribution<Integer>();
        System.out.print("Random sorted array: ");
        for (Integer e: elements) {
            System.out.print(e + ", ");
            distribution.add(e);
        }
        System.out.print("\n");
        System.out.println("distribution after sort:");
        distribution.print();
    }

    private static void randomTest6(Distribution<Integer> d1, Distribution<Integer> d2) {
        //double mi = mutualInformation(d1, d2);
        double crossEntropy12 = crossEntropy(d1, d2);
        double crossEntropy21 = crossEntropy(d2, d1);
        double kld12 = klDivergence(d1, d2);
        double kld21 = klDivergence(d2, d1);
        double jsd = jsDivergence(d1, d2);

        System.out.println("Distribution similarity:");
        System.out.println("=======================================");
        System.out.println("distribution#1: entropy: " + entropy(d1));
        d1.print();
        System.out.println("distribution#2: entropy: " + entropy(d2));
        d2.print();
        //System.out.println("mutual information: " + mi + ", jsd: " + jsd + ", kld: " + kld);
        System.out.println("jsd: " + jsd +
                ", kld12: " + kld12 + ", kld21: " + kld21 +
                ", crossEntropy12: " + crossEntropy12 + ", crossEntropy21: " + crossEntropy21);
    }

    private static Distribution<Integer> buildDistribution(int[] data, int[] frequencies) {
        Distribution<Integer> d = new Distribution<Integer>();

        for (int i=0; i < data.length; i++) {
            int frequency = frequencies[i];
            for (int j=0; j < frequency; j++) {
                d.add(data[i]);
            }
        }

        return d;
    }

    private static Distribution<ImmutablePair<Integer, Integer>> buildJointDistribution(int[] data,
                                                                                        int[] frequencies1,
                                                                                        int[] frequencies2) {
        Distribution<ImmutablePair<Integer, Integer>> d = new Distribution<ImmutablePair<Integer, Integer>>();

        for (int i=0; i < data.length; i++) {
            for (int j=0; j < data.length; j++) {
                ImmutablePair<Integer, Integer> datum = new ImmutablePair<Integer, Integer>(data[i], data[j]);
                int f1 = frequencies1[i];
                int f2 = frequencies2[j];
                int minFrequency = Math.min(f1, f2);
                int f = new RandomInt(minFrequency+1).next();
                for (int k=0; k < f; k++) {
                    d.add(datum);
                }
            }
        }

        return d;
    }

    public static void main(String[] args) {
        int k = 2;
        RandomInt randomInt = new RandomInt(k);
        Distribution distribution = new Distribution<Integer>();
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

        System.out.println("Random sampling test");
        System.out.println("===========================================");
        List<String> population =
                Arrays.asList("horse dog bat elephant dog dog cat dog tiger deer fox dog bat cat cow".split(" "));
        randomTest4(population, 5);
        randomTest4(population, 5);
        randomTest4(population, 5);

        System.out.println("Random sort test");
        System.out.println("===========================================");
        List<Integer> elements =
                Arrays.asList(new Integer[] {1, 3, 4, 6, 8, 10, 13, 10, 10, 14, 19, 6, 22, 6, 3, 3, 3, 3, 28, 6});
        randomTest5(elements);
        randomTest5(elements);
        randomTest5(elements);

        elements = Arrays.asList(new Integer[] {1, 2, 3, 4});
        randomTest5(elements);
        randomTest5(elements);
        randomTest5(elements);

        int[] data = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        int[] f1 = new int[] {2, 4, 8, 3, 2, 1, 2, 2, 3, 2};
        Distribution<Integer> d1 = buildDistribution(data, f1);
        randomTest6(d1, d1);

        int[] f2 = new int[] {2, 3, 2, 2, 1, 2, 3, 8, 4, 2};
        Distribution<Integer> d2 = buildDistribution(data, f2);
        randomTest6(d1, d2);

        int[] f3 = new int[] {2, 3, 6, 3, 2, 1, 2, 2, 2, 2};
        Distribution<Integer> d3 = buildDistribution(data, f3);
        randomTest6(d1, d3);

        int[] f4 = new int[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        Distribution<Integer> d4 = buildDistribution(data, f4);
        randomTest6(d1, d4);

        int[] f5 = new int[] {2, 3, 2, 2, 1, 2, 3, 16, 4, 2};
        Distribution<Integer> d5 = buildDistribution(data, f5);
        randomTest6(d1, d5);

        int[] f6 = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 100};
        Distribution<Integer> d6 = buildDistribution(data, f6);

        int[] f7 = new int[]{100, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        Distribution<Integer> d7 = buildDistribution(data, f7);

        randomTest6(d6, d7);

        int[] f8 = new int[]{1, 1, 1, 1, 1, 100, 100, 100, 100, 100};
        Distribution<Integer> d8 = buildDistribution(data, f8);

        int[] f9 = new int[]{100, 100, 100, 100, 100, 1, 1, 1, 1, 1};
        Distribution<Integer> d9 = buildDistribution(data, f9);

        Distribution<ImmutablePair<Integer, Integer>> d12 = buildJointDistribution(data, f1, f2);

        randomTest6(d8, d9);
    }
}
