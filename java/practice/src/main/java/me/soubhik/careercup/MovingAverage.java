package me.soubhik.careercup;

/**
 * Created by soubhik on 20-12-2018.
 * from https://www.careercup.com/question?id=6313643925831680
 */
public class MovingAverage {
    private final int[] sumArray;

    // assume: input: N, output: moving average, preprocess an array of int values
    // assume: N must be less than the size of input array
    // algo:
    // preprocess(values[]):
    // sum[i]: stores sum of all elements values[0:i], i in [0, values.length-1]
    // movingAverage(n):
    // sum = sum[values.length-1] - sum[values.length - 1 - n], n < values.length
    // avg = sum/n
    public MovingAverage(int[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException();
        }

        sumArray = new int[values.length];
        sumArray[0] = values[0];
        for (int i=1; i < values.length; i++) {
            sumArray[i] = sumArray[i-1] + values[i];
        }
    }

    public float movingAverage(int n) {
        if ((n > sumArray.length) || (n <= 0)) {
            throw new IllegalArgumentException();
        }

        if (n == sumArray.length) {
            float avg = ((float)sumArray[n-1])/n;
            return avg;
        }

        int sum = sumArray[sumArray.length-1] - sumArray[sumArray.length - 1 - n];
        return ((float)sum)/n;
    }

    private static void test() {
        MovingAverage movingAverage = new MovingAverage(new int[] {4, 2, 8, 0, 5});

        float actual = movingAverage.movingAverage(4);
        float expected = (float)15 / 4;
        assert (actual == expected);

        actual = movingAverage.movingAverage(2);
        expected = (float)5 / 2;
        assert (expected == actual);

        actual = movingAverage.movingAverage(5);
        expected = (float)19 / 5;
        assert (expected == actual);

        actual = movingAverage.movingAverage(1);
        expected = (float)5 / 1;
        assert (expected == actual);

        boolean pass = false;
        try {
            actual = movingAverage.movingAverage(0);
        } catch (IllegalArgumentException e) {
            //expected
            pass = true;
        }
        assert (pass);

        pass = false;
        try {
            actual = movingAverage.movingAverage(10);
        } catch (IllegalArgumentException e) {
            //expected
            pass = true;
        }
        assert (pass);
    }

    public static void main(String[]  args) {
        test();
    }
}
