package me.soubhik;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
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
    //TODO: reservoir sampling

    /*
     * generate a random number between lower and (upper-1) with uniform probability
     */
    public static class UniformRandomInt {
        private final ArrayList<Double> intervals;
        private final int lower;

        public UniformRandomInt(int n) {
            this(0, n);
        }

        public UniformRandomInt(int lower, int upper) {
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

    /*
     * generate discrete random values (of type T) from the given distribution
     */
    public static class DiscreteRandom<T> {
        private final ArrayList<Double> intervals;
        private final Map<Integer, T> intervalToDatum;

        public DiscreteRandom(Distribution distribution) {
            Map<T, Double> probabilities = distribution.probabilitiesDoublePrecision();
            int numIntervals = probabilities.keySet().size();

            this.intervals = new ArrayList<Double>(numIntervals);
            this.intervalToDatum = new HashMap<Integer, T>(numIntervals);

            int idx = 0;
            double intervalStart = 0.0d;
            for (Map.Entry<T, Double> entry: probabilities.entrySet()) {
                T datum = entry.getKey();
                intervalToDatum.put(idx, datum);
                idx++;

                intervals.add(intervalStart);
                double probability = entry.getValue();
                intervalStart += probability;
            }
        }

        public T next() {
            double random = Math.random();
            int interval = findInterval(intervals, random);
            return intervalToDatum.get(interval);
        }
    }

    /*
     * generate distinct random integers between lower and (upper-1) such that the probability
     * of each number is same.
     */
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

            UniformRandomInt randomInt = new UniformRandomInt(numbersAvailable); //O(numbersAvailable)
            int nextRandomIndex = randomInt.next(); //O(log(numbersAvailable))
            int nextRandom = numbers[nextRandomIndex];
            if (nextRandomIndex < (numbersAvailable - 1)) {
                numbers[nextRandomIndex] = numbers[numbersAvailable - 1];
            }
            numbersAvailable--;

            return nextRandom + lower;
        }
    }

    public static class RandomIntWithoutReplacementV2 {
        private final int lower, upper;
        private final int[] numbers;
        private int numbersAvailable;
        UniformRandomInt randomInt;

        public RandomIntWithoutReplacementV2(int upper) {
            this(0, upper);
        }

        public RandomIntWithoutReplacementV2(int lower, int upper) {
            int range = upper - lower;
            this.lower = lower;
            this.upper = upper;
            this.numbers = new int[range];
            for (int i = 0; i < range; i++) {
                numbers[i] = i;
            }
            this.numbersAvailable = range;
            this.randomInt = new UniformRandomInt(range); //O(range)
        }

        public int next() {
            if (numbersAvailable <= 0) {
                return (upper);
            }

            int nextRandomIndex = randomInt.next(); //O(log(range))
            //bug: modulo does not correctly generate a uniform random number generator
            // for [0, numbersAvailable) if range is not a factor of numbersAvailable.
            // e.g. range == 10, numbersAvailable == 3. then modulo maps Random(range) to Random(numbersAvailable)
            // as follows:
            // 0, 3, 6, 9 => 0
            // 1, 4, 7 => 1
            // 2, 5, 8 => 2
            // thus, probability of 0 is > that of 1 or 2.
            nextRandomIndex %= numbersAvailable;
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
            add(datum, 1);
        }

        public void add(T datum, int frequency) {
            if (!frequencies.containsKey(datum)) {
                frequencies.put(datum, 0);
            }
            int oldFrequency = frequencies.get(datum);
            frequencies.put(datum, oldFrequency+frequency);
        }

        public Map<T, BigDecimal> probabilities() {
            return probabilitiesBigDecimal();
        }

        public Map<T, BigDecimal> probabilitiesBigDecimal() {
            return null;
        }

        public Map<T, Double> probabilitiesDoublePrecision() {
            int totalFrequency = 0;
            for (int frequency: frequencies.values()) {
                totalFrequency += frequency;
            }

            Map<T, Double> probabilityDistribution = new HashMap<T, Double>();
            double cumulativeProbabilities = 0.0d;
            for (Map.Entry<T, Integer> entry: frequencies.entrySet()) {
                T datum = entry.getKey();
                int frequency = entry.getValue();
                double probability = ((double)frequency)/((double)totalFrequency);
                cumulativeProbabilities += probability;
                probabilityDistribution.put(datum, probability);
            }

            return probabilityDistribution;
        }

        public void print() {
            Map<T, Double> probabilityDistribution = probabilitiesDoublePrecision();

            for (Map.Entry<T, Integer> entry: frequencies.entrySet()) {
                T datum = entry.getKey();
                int frequency = entry.getValue();
                double probability = probabilityDistribution.get(datum);
                System.out.println(datum + ":" + frequency + " (" + probability + ")");
            }
        }
    }

    public static class RandomString {
        private static final String[] characters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789".split("");
        private final UniformRandomInt randomInt;

        public RandomString() {
            this.randomInt = new UniformRandomInt(1, characters.length); //1st char is "". ignore.
        }

        String next(int len) {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < len; i++) {
                int index = randomInt.next();
                builder.append(characters[index]);
            }

            return builder.toString();
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
        return selfInformation(distribution.probabilitiesDoublePrecision(), datum);
    }

    public static <T> double selfInformation(Map<T, Double> probabilities, T datum) {
        assert probabilities.containsKey(datum);

        double p = probabilities.get(datum);
        return -log2(p);
    }

    public static double entropy(Distribution<?> distribution) {
        return entropy(distribution.probabilitiesDoublePrecision());
    }

    public static double entropy(Map<?, Double> probabilities) {
        double e = 0d;
        for (double p: probabilities.values()) {
            e += p * log2(p);
        }

        return -e;
    }

    public static <U, V> double pmi(Distribution<U> xDistribution,
                                 Distribution<V> yDistribution,
                                 Distribution<ImmutablePair<U, V>> xyDistribution,
                                 U x, V y) {
        return pmi(xDistribution.probabilitiesDoublePrecision(), yDistribution.probabilitiesDoublePrecision(), xyDistribution.probabilitiesDoublePrecision(), x, y);
    }

    public static <U, V> double pmi(Map<U, Double> xProbabilities,
                                 Map<V, Double> yProbabilities,
                                 Map<ImmutablePair<U, V>, Double> xyProbabilities,
                                 U x, V y) {
        double selfX = selfInformation(xProbabilities, x);
        double selfY = selfInformation(yProbabilities, y);
        double selfXY = selfInformation(xyProbabilities, new ImmutablePair<U, V>(x, y));

        return (selfX + selfY - selfXY);
    }

    public static <U, V> double normalizedPMI(Distribution<U> xDistribution,
                                           Distribution<V> yDistribution,
                                           Distribution<ImmutablePair<U, V>> xyDistribution,
                                           U x, V y) {
        return normalizedPMI(xDistribution.probabilitiesDoublePrecision(),
                yDistribution.probabilitiesDoublePrecision(),
                xyDistribution.probabilitiesDoublePrecision(), x, y);
    }

    public static <U, V> double normalizedPMI(Map<U, Double> xProbabilities,
                                           Map<V, Double> yProbabilities,
                                           Map<ImmutablePair<U, V>, Double> xyProbabilities,
                                           U x, V y) {
        double pxy = xyProbabilities.get(new ImmutablePair<U, V>(x, y));

        return (-pmi(xProbabilities, yProbabilities, xyProbabilities, x, y) / log2(pxy));
    }

    public static <U, V> double mutualInformation(Distribution<U> xDistribution,
                                               Distribution<V> yDistribution,
                                               Distribution<ImmutablePair<U, V>> xyDistribution) {
        return mutualInformation(xDistribution.probabilitiesDoublePrecision(),
                yDistribution.probabilitiesDoublePrecision(),
                xyDistribution.probabilitiesDoublePrecision());
    }

    public static <U, V> double mutualInformation(Map<U, Double> xProbabilities,
                                               Map<V, Double> yProbabilities,
                                               Map<ImmutablePair<U, V>, Double> xyProbabilities) {
        double mi = 0d;

        for (U x: xProbabilities.keySet()) {
            for (V y: yProbabilities.keySet()) {
                double pmiXY = pmi(xProbabilities, yProbabilities, xyProbabilities, x, y);
                double pXY = xyProbabilities.get(new ImmutablePair<U, V>(x, y));
                mi += pmiXY * pXY;
            }
        }

        return mi;
    }

    public static <T> double crossEntropy(Distribution<T> xDistribution, Distribution<T> yDistribution) {
        return crossEntropy(xDistribution.probabilitiesDoublePrecision(), yDistribution.probabilitiesDoublePrecision());
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
        return klDivergence(xDistribution.probabilitiesDoublePrecision(), yDistribution.probabilitiesDoublePrecision());
    }

    public static <T> double klDivergence(Map<T, Double> xProbabilities, Map<T, Double> yProbabilities) {
        double ceXY = crossEntropy(xProbabilities, yProbabilities);
        double eX = entropy(xProbabilities);

        return (ceXY - eX);
    }

    public static <T> double jsDivergence(Distribution<T> xDistribution, Distribution<T> yDistribution) {
        return jsDivergence(xDistribution.probabilitiesDoublePrecision(), yDistribution.probabilitiesDoublePrecision());
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
        UniformRandomInt randomInt = new UniformRandomInt(lower, upper);
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
        UniformRandomInt randomInt = new UniformRandomInt(lower, upper);
        Distribution distribution = new Distribution<Integer>();
        System.out.println("Random integers from " + lower + " to " + upper + " using UniformRandomInt");
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
        DiscreteRandom<Integer> discreteRandom = new DiscreteRandom<Integer>(distribution);
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
        System.out.println("jsd: " + jsd +
                ", kld12: " + kld12 + ", kld21: " + kld21 +
                ", crossEntropy12: " + crossEntropy12 + ", crossEntropy21: " + crossEntropy21);
    }

    private static <T> void randomTest7(ImmutablePair<T, T>[] data, int[] frequencies) {
        Distribution<ImmutablePair<T, T>> jointDistribution = buildDistribution(data, frequencies);
        Distribution<ImmutablePair<T, T>> productDistribution = buildProductDistribution(data, frequencies);
        Pair<Distribution<T>, Distribution<T>> distributionPair = marginalize(data, frequencies);
        Distribution<T> d1 = distributionPair.getLeft();
        Distribution<T> d2 = distributionPair.getRight();

        double jsd = jsDivergence(d1, d2);
        double mi = mutualInformation(d1, d2, jointDistribution);
        double kldJointToProduct = klDivergence(jointDistribution, productDistribution);

        System.out.println("Distribution similarity:");
        System.out.println("=======================================");
        System.out.println("distribution#1: entropy: " + entropy(d1));
        d1.print();
        System.out.println("--------------------");
        System.out.println("distribution#2: entropy: " + entropy(d2));
        d2.print();
        System.out.println("--------------------");
        System.out.println("joint distribution: entropy: " + entropy(jointDistribution));
        jointDistribution.print();
        System.out.println("--------------------");
        System.out.println("product distribution: entropy: " + entropy(productDistribution));
        productDistribution.print();
        System.out.println("--------------------");

        System.out.println("jsd: " + jsd + ", mutual information: " + mi + ", kldJointToProduct: " + kldJointToProduct);

        System.out.println("PMI");
        System.out.println("====================");
        for (ImmutablePair<T, T> pair: data) {
            T left = pair.left;
            T right = pair.right;
            double pmi = pmi(d1, d2, jointDistribution, left, right);
            System.out.println("(" + left + ", " + right + "): " + pmi);
        }
    }

    private static void randomTest8(String[] data, int[] counts, int n) {
        assert (data.length == counts.length);

        Distribution<String> inputDistribution = buildDistribution(data, counts);
        DiscreteRandom<String> randomStringer = new DiscreteRandom<String>(inputDistribution);
        System.out.println("Input string distribution");
        System.out.println("=======================");
        inputDistribution.print();
        Distribution<String> outputDistribution = new Distribution<String>();
        System.out.println("Drawing " + n + " strings from the given distribution");
        System.out.println("--------------------");
        for (int i = 0; i < n; i++) {
            String s = randomStringer.next();
            outputDistribution.add(s);
            System.out.println(s);
        }
        System.out.println("Output string distribution");
        System.out.println("--------------------");
        outputDistribution.print();

        double jsd = jsDivergence(inputDistribution, outputDistribution);
        double kld12 = klDivergence(inputDistribution, outputDistribution);
        double kld21 = klDivergence(outputDistribution, inputDistribution);
        double ce12 = crossEntropy(inputDistribution, outputDistribution);
        double ce21 = crossEntropy(outputDistribution, inputDistribution);

        System.out.println("Input vs output distributions: jsd: " + jsd + ", kld12: " + kld12 + ", kld21: " + kld21 +
                            ", cross-entropy12: " + ce12 + ", cross-entropy21: " + ce21);
    }

    private static void randomTest9() {
        System.out.println("Random strings test");
        System.out.println("====================================");

        RandomString randomString = new RandomString();
        for (int i = 0; i < 10; i++) {
            String s = randomString.next(5);
            assert (s.length() == 5);
            System.out.println(s);
        }

        String s = randomString.next(8);
        assert (s.length() == 8);
        System.out.println(s);

        s = randomString.next(50);
        assert (s.length() == 50);
        System.out.println(s);
    }

    public static <T> Distribution<T> buildDistribution(T[] data, int[] frequencies) {
        Distribution<T> d = new Distribution<T>();

        for (int i=0; i < data.length; i++) {
            int frequency = frequencies[i];
            d.add(data[i], frequency);
        }

        return d;
    }

    public static <T> Distribution<T> buildDistribution(Map<T, Integer> frequencies) {
        Object[] data = new Object[frequencies.keySet().size()];
        int[] frequenciesArray = new int[frequencies.values().size()];
        int i = 0;
        for (Map.Entry<T, Integer> entry: frequencies.entrySet()) {
            data[i] = entry.getKey();
            frequenciesArray[i] = entry.getValue();
            i++;
        }
        Distribution<T> distribution = buildDistribution((T[])data, frequenciesArray);

        return distribution;
    }

    private static <U, V> Pair<Distribution<U>, Distribution<V>> marginalize(ImmutablePair<U, V>[] data,
                                                                             int[] frequencies) {
        Pair<Map<U, Integer>, Map<V, Integer>> frequenciesPair = marginalizeFrequencies(data, frequencies);
        Map<U, Integer> leftFrequencies = frequenciesPair.getLeft();
        Map<V, Integer> rightFrequencies = frequenciesPair.getRight();

        Distribution<U> leftDistribution = buildDistribution(leftFrequencies);
        Distribution<V> rightDistribution = buildDistribution(rightFrequencies);

        return new ImmutablePair<Distribution<U>, Distribution<V>>(leftDistribution, rightDistribution);
    }

    private static <U, V> Pair<Map<U, Integer>, Map<V, Integer>> marginalizeFrequencies(ImmutablePair<U, V>[] data,
                                                                                        int[] frequencies) {
        assert (data.length == frequencies.length);

        Map<U, Integer> leftFrequencies = new HashMap<U, Integer>();
        Map<V, Integer> rightFrequencies = new HashMap<V, Integer>();

        for (int i = 0; i < data.length; i++) {
            U left = data[i].left;
            V right = data[i].right;
            int f = frequencies[i];

            if (!leftFrequencies.containsKey(left)) {
                leftFrequencies.put(left, 0);
            }
            leftFrequencies.put(left, leftFrequencies.get(left) + f);

            if (!rightFrequencies.containsKey(right)) {
                rightFrequencies.put(right, 0);
            }
            rightFrequencies.put(right, rightFrequencies.get(right) + f);
        }

        return new ImmutablePair<Map<U, Integer>, Map<V, Integer>>(leftFrequencies, rightFrequencies);
    }

    private static <U, V> Distribution<ImmutablePair<U, V>> buildProductDistribution(ImmutablePair<U, V>[] data,
                                                                                     int[] frequencies) {
        assert (data.length == frequencies.length);

        Pair<Map<U, Integer>, Map<V, Integer>> frequenciesPair = marginalizeFrequencies(data, frequencies);
        Map<U, Integer> leftFrequencies = frequenciesPair.getLeft();
        Map<V, Integer> rightFrequencies = frequenciesPair.getRight();

        int[] productFrequencies = new int[frequencies.length];
        for (int i=0; i<productFrequencies.length; i++) {
            ImmutablePair<U, V> pair = data[i];
            U left = pair.left;
            V right = pair.right;
            int leftFrequency = leftFrequencies.get(left);
            int rightFrequency = rightFrequencies.get(right);
            int productFrequency = leftFrequency * rightFrequency;
            productFrequencies[i] = productFrequency;
        }

        return buildDistribution(data, productFrequencies);
    }

    public static void main(String[] args) {
        int k = 2;
        UniformRandomInt randomInt = new UniformRandomInt(k);
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

        Integer[] data = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

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

        randomTest6(d8, d9);

        ImmutablePair<String, String>[] pairs = new ImmutablePair[10];
        pairs[0] = new ImmutablePair<String, String>("elephant", "pig");
        pairs[1] = new ImmutablePair<String, String>("elephant", "hadoop");
        pairs[2] = new ImmutablePair<String, String>("elephant", "hive");
        pairs[3] = new ImmutablePair<String, String>("elephant", "mahout");
        pairs[4] = new ImmutablePair<String, String>("elephant", "python");
        pairs[5] = new ImmutablePair<String, String>("bee", "pig");
        pairs[6] = new ImmutablePair<String, String>("bee", "hadoop");
        pairs[7] = new ImmutablePair<String, String>("bee", "hive");
        pairs[8] = new ImmutablePair<String, String>("bee", "mahout");
        pairs[9] = new ImmutablePair<String, String>("bee", "python");
        int[] pairCounts = new int[] {20, 15, 3, 10, 5, 18, 2, 16, 1, 1};

        randomTest7(pairs, pairCounts);

        String[] stringData = new String[] {"pink horse", "red dolphin", "black", "white coral", "purple boot",
                                            "yellow", "mauve", "crimson leaves", "blue fish", "silver"};
        int[] stringCounts = new int[] {32, 5, 6, 66, 5, 40, 44, 20, 6, 10};
        randomTest8(stringData, stringCounts, 10000);

        randomTest9();
    }
}
