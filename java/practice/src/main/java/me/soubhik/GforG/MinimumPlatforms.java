package me.soubhik.GforG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by sb8 on 10/9/18.
 */
public class MinimumPlatforms {
    public static class TestCase
    {
        private final int numTrains;
        private final int[] arrivals;
        private final int[] departures;

        public TestCase(int numTrains, int[] arrivals, int[] departures) {
            this.numTrains = numTrains;
            this.arrivals = arrivals;
            this.departures = departures;
        }
    }

    private static class PointInTime implements Comparable<PointInTime> {
        private final int time;
        private final boolean arrival;

        public PointInTime(int time, boolean arrival) {
            this.time = time;
            this.arrival = arrival;
        }

        @Override
        public int compareTo(PointInTime that) {
            int ret = this.time - that.time;
            /*
            if (ret == 0) {
                if (this.arrival != that.arrival) {
                    ret = (!this.arrival) ? -1 : 1;
                } else {
                    ret = 0;
                }
            }
            */

            return ret;
        }
    }

    public static int minPlatforms(int numTrains, int[] arrivals, int[] departures) {
        PointInTime[] points = new PointInTime[2*numTrains];

        int i = 0;
        for (int arrival: arrivals) {
            PointInTime pointInTime = new PointInTime(arrival, true);
            points[i] = pointInTime;
            i++;
        }
        for (int departure: departures) {
            PointInTime pointInTime = new PointInTime(departure, false);
            points[i] = pointInTime;
            i++;
        }

        Arrays.sort(points);
        int numTrainsInStation = 0;
        int maxNumTrainsInStation = numTrainsInStation;
        int previousTime = -1;
        for (PointInTime pointInTime: points) {
            int currentTime = pointInTime.time;
            //update the max after processing all events that occurred in the same time.
            if (currentTime != previousTime) {
                maxNumTrainsInStation = (numTrainsInStation > maxNumTrainsInStation) ? numTrainsInStation : maxNumTrainsInStation;
            }
            if (pointInTime.arrival) {
                numTrainsInStation++;
                //maxNumTrainsInStation = (numTrainsInStation > maxNumTrainsInStation) ? numTrainsInStation : maxNumTrainsInStation;
            } else {
                numTrainsInStation--;
            }
        }
        maxNumTrainsInStation = (numTrainsInStation > maxNumTrainsInStation) ? numTrainsInStation : maxNumTrainsInStation;

        return maxNumTrainsInStation;
    }

    private static void test() {
        int[] arrivals = new int[] {900, 1000, 1100};
        int[] departures = new int[] {910, 1130, 1110};
        assert (2 == minPlatforms(3, arrivals, departures));

        arrivals = new int[] {900, 915, 925, 1100};
        departures = new int[] {920, 940, 1130, 1110};
        assert (2 == minPlatforms(4, arrivals, departures));

        arrivals = new int[] {900, 940, 950, 1100, 1500, 1800};
        departures = new int[] {910, 1200, 1120, 1130, 1900, 2000};
        assert (3 == minPlatforms(6, arrivals, departures));
    }

    public static void main (String[] args)
    {
        test();

        int numTestCases;
        TestCase[] testCases;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            numTestCases = Integer.parseInt(line.trim());
            testCases = new TestCase[numTestCases];
            for (int i = 0; i < numTestCases; i++) {
                String line1 = reader.readLine();
                int numTrains = Integer.parseInt(line1.trim());

                String line2 = reader.readLine();
                int[] arrivals = new int[numTrains];
                String[] parts2 = line2.split(" ");
                for (int j = 0; j < numTrains; j++) {
                    if (parts2[j].trim().isEmpty()) {
                        continue;
                    }
                    arrivals[j] = Integer.parseInt(parts2[j].trim());
                }

                String line3 = reader.readLine();
                int[] departures = new int[numTrains];
                String[] parts3 = line3.split(" ");
                for (int j = 0; j < numTrains; j++) {
                    if (parts3[j].trim().isEmpty()) {
                        continue;
                    }
                    departures[j] = Integer.parseInt(parts3[j].trim());
                }

                testCases[i] = new TestCase(numTrains, arrivals, departures);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            int minPlatforms = minPlatforms(testCase.numTrains, testCase.arrivals, testCase.departures);
            System.out.println(minPlatforms);
        }
    }
}
