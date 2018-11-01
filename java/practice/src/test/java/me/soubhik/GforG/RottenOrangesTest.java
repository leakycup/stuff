package me.soubhik.GforG;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

public class RottenOrangesTest extends TestCase {
    @Test
    public void test1() {
        int[][] oranges = new int[][] {{2, 1, 0, 2, 1}, {1, 0, 1, 2, 1}, {1, 0, 0, 2, 1}};
        int r = 3;
        int c = 5;
        int expected = 2;
        int actual = RottenOranges.findMinTime(r, c, oranges);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        int[][] oranges = new int[][] {{2, 1, 0, 2, 1}, {0, 0, 1, 2, 1}, {1, 0, 0, 2, 1}};
        int r = 3;
        int c = 5;
        int expected = -1;
        int actual = RottenOranges.findMinTime(r, c, oranges);

        Assert.assertEquals(expected, actual);
    }
}