package me.soubhik.GeoDatabase;

import me.soubhik.IntegerEncoding;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by soubhik on 01-09-2016.
 */
public class InMemoryStorage implements Storage {
    private static final float RESOLUTION = 0.5f;

    //a square whose sides are RESOLUTION km long.
    //we store the co-ordinates of the bottom left corner of the square.
    private static class Square {
        final float x;
        final float y;

        Square(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private final Map<String, Float> xCoordinates;
    private final Map<String, Float> yCoordinates;
    private final Map<String, Square> keyToSquare;
    private final MultiValuedMap<Square, String> squareToKeys;
    private final Map<Pair<Integer, Integer>, Square> segmentIndicesToSqaure;
    private final Map<Square, Pair<Integer, Integer>> squareToSegmentIndices;

    public InMemoryStorage() {
        xCoordinates = new HashMap<>();
        yCoordinates = new HashMap<>();
        keyToSquare = new HashMap<>();
        squareToKeys = new HashSetValuedHashMap<>();
        segmentIndicesToSqaure = new HashMap<>();
        squareToSegmentIndices = new HashMap<>();
    }

    @Override
    public boolean insert(String key, LatLong latLong) {
        xCoordinates.put(key, latLong.latitude());
        yCoordinates.put(key, latLong.longitude());
        return true;
    }

    @Override
    public boolean build() {
        float[] xSegments = buildSegments(xCoordinates);
        float[] ySegments = buildSegments(yCoordinates);

        for (String key: xCoordinates.keySet()) {
            float x = xCoordinates.get(key);
            float y = yCoordinates.get(key);
            int xIndex = findSegment(xSegments, x);
            int yIndex = findSegment(ySegments, y);
            Square s = segmentIndicesToSqaure.get(new ImmutablePair<>(xIndex, yIndex));
            keyToSquare.put(key, s);
            squareToKeys.put(s, key);
        }

        return true;
    }

    @Override
    public Set<String> find(String key, int radius) {
        Square s = keyToSquare.get(key);
        Pair<Integer, Integer> center = squareToSegmentIndices.get(s);

        Set<String> results = new HashSet<>();
        int steps = (int)((float)radius / RESOLUTION);
        for (int i = 0; i < steps; i++) {
            int xIndex = center.getLeft() + i;
            int maxYIndex = getMaxYIndex(radius, i);
            for (int j = 0; j < maxYIndex; j++) {
                int yIndex = center.getRight() + j;
                Pair<Integer, Integer> pair = new ImmutablePair<>(xIndex, yIndex);
                if (segmentIndicesToSqaure.containsKey(pair)) {
                    Square square = segmentIndicesToSqaure.get(pair);
                }
            }
        }

        return results;
    }

    private int getMaxYIndex(int radius, int x) {
        //solve y from radius^2 = x^2 + y^2
        //stub for now
        return 0;
    }

    private void buildSegmentIndicesToSquares(float[] xSegments, float[] ySegments) {
        for (int i = 0; i < xSegments.length; i++) {
            for (int j = 0; j < ySegments.length; j++) {
                Pair<Integer, Integer> pair = new ImmutablePair<>(i, j);
                Square s = new Square(xSegments[i], ySegments[j]);
                segmentIndicesToSqaure.put(pair, s);
                squareToSegmentIndices.put(s, pair);
            }
        }
    }

    private float findMax(Map<?, Float> coordinates) {
        return Collections.max(coordinates.values());
    }

    private float findMin(Map<?, Float> coordinates) {
        return Collections.min(coordinates.values());
    }

    //find distance between 2 points on an axis
    private float distanceInKm(float x, float y) {
        return 0; //dummy for now
    }

    private float[] buildSegments(Map<?, Float> coordinates) {
        float max = findMax(coordinates);
        float min = findMin(coordinates);
        float distance = distanceInKm(min, max);
        int numSegments = (int)(distance / RESOLUTION);
        float[] segments = new float[numSegments];
        float s = min;
        for (int i = 0; i < segments.length; i++) {
            segments[i] = s;
            s += RESOLUTION;
        }

        return segments;
    }

    private int findSegment(float[] segments, float point) {
        int index = Arrays.binarySearch(segments, point);
        if (index >= 0) {
            return index;
        }
        int insertionPoint = -index - 1;

        return (insertionPoint - 1);
    }
}
