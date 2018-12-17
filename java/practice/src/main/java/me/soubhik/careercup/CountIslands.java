package me.soubhik.careercup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by soubhik on 16-12-2018.
 * from https://www.careercup.com/question?id=5669768827764736
 */
public class CountIslands {
    private static final int M = 2;
    private static final int N = 3;

    //   assume: matrix is not empty (has at least 1 row)
    //   assume: number of islands is < 0xffffffff - 'z'
    //   O(m*n) time. modifies the input matrix. without modification, it takes O(m*n) space.
    public static int countIslandsSmallMatrix(char[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        Map<Integer, Integer> merges = new HashMap<>();
        int numIslands = 0;
        int uniqCounter = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (matrix[i][j] != 'x') {
                    continue;
                }
                int islandAbove = -1;
                if  ((i > 0) && (matrix[i-1][j] != 'o')) {
                    islandAbove = matrix[i-1][j] - 'z';
                }
                int islandLeft = -1;
                if ((j > 0) && (matrix[i][j-1] != 'o')) {
                    islandLeft = matrix[i][j-1] - 'z';
                }
                if ((islandAbove < 0) && (islandLeft < 0)) {
                    //new island
                    numIslands++;
                    uniqCounter++;
                    matrix[i][j] = (char) ('z' + uniqCounter);
                } else if ((islandAbove < 0)) {
                    //extend the island from left
                    matrix[i][j] = (char)('z' + islandLeft);
                } else if ((islandLeft < 0)) {
                    //extend the island from above
                    matrix[i][j] = (char)('z' + islandAbove);
                } else {
                    int island1 = findMergedIsland(merges, islandAbove);
                    int island2 = findMergedIsland(merges, islandLeft);
                    if (island1 == island2) {
                        //merge 2 islands that have already been merged to one
                        matrix[i][j] = (char)('z' + island1);
                    } else {
                        //merge 2 two islands: from above and left
                        numIslands--;
                        int mergeSource = (island1 < island2) ? island2 : island1;
                        int mergeTarget = (island1 < island2) ? island1 : island2;
                        matrix[i][j] = 'z';
                        matrix[i][j] += (char)(mergeTarget);
                        merges.put(mergeSource, mergeTarget);
                    }
                }
                /*
                matrix[i][j] = 'i';
                numIslands++;
                int adjacentIslands = 0;
                if  ((i > 0) && (matrix[i-1][j] == 'i')) {
                    adjacentIslands++;
                }
                if ((j > 0) && (matrix[i][j-1] == 'i')) {
                    adjacentIslands++;
                }
                if (((i+1) < rows) && (matrix[i+1][j] == 'i')) {
                    adjacentIslands++;
                }
                if (((j+1) < columns) && (matrix[i][j+1] == 'i')) {
                    adjacentIslands++;
                }
                numIslands -= adjacentIslands;
                */
            }
        }

        return numIslands;
    }

    private static int findMergedIsland(Map<Integer, Integer> merges, int island) {
        while (merges.containsKey(island)) {
            island = merges.get(island);
        }

        return island;
    }

    //   assume: matrix is not empty (has at least 1 row)
    //   O(m*n)
    public static int countIslandsLargeMatrix(char[][] matrix) {
        /*
        int rows = matrix.length;
        int columns = matrix[0].length;
        int numIslands = 0;
        for (int i = 0; i < rows; i += M) {
            int remainingRows = rows - i;
            int r = (remainingRows > M) ? M : remainingRows;
            for (int j = 0; j < columns; j += N) {
                int remainingColumns = columns - j;
                int c = (remainingColumns > N) ? N : remainingColumns;
                char[] prevRowSegment = null;
                if (i > 0) {
                    prevRowSegment = new char[c];
                    System.arraycopy(matrix[i-1], j, prevRowSegment, 0, c);
                }
                char[] prevColumnSegment = null;
                if (j > 0) {
                    prevColumnSegment = new char[r];
                    for (int k = i; k < i + r; k++) {
                        prevColumnSegment[k-i] = matrix[k][j-1];
                    }
                }
                numIslands += countIslands(matrix, i, j, r, c, prevRowSegment, prevColumnSegment);
            }
        }
        return numIslands;
        */
        //TODO: fix the algo
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] != 'o') {
                    matrix[i][j] = 'x';
                }
            }
        }
        return countIslandsSmallMatrix(matrix);
    }

    private static int countIslandsSquareMatrix(char[][] matrix, int firstRow, int firstColumn, int length,
                                                char[] prevRowSegment, char[] prevColumnSegment) {
        int numIslands = 0;
        if ((matrix[firstRow][firstColumn] == 'x') &&
                ((prevRowSegment == null) || (prevRowSegment[0] != 'x')) &&
                ((prevColumnSegment == null) || (prevColumnSegment[0] != 'x'))) {
            numIslands++;
        }

        for (int k = 2; k <= length; k++) {
            int i = firstRow + k - 1;
            for (int j = firstColumn; j < (firstColumn+k-1); j++) {
                if (matrix[i][j] != 'x') {
                    continue;
                }
                if ((j != firstColumn) && (matrix[i][j-1] == 'x')) {
                    continue;
                } else if ((j == firstColumn) && (prevColumnSegment != null) && (prevColumnSegment[i-firstRow] == 'x')) {
                    continue;
                }
                numIslands++;
            }

            int j = firstColumn + k - 1;
            for (i = firstRow; i < (firstRow + k - 1); i++) {
                if (matrix[i][j] != 'x') {
                    continue;
                }
                if ((i != firstRow) && (matrix[i-1][j] == 'x')) {
                    continue;
                } else if ((i == firstRow) && (prevRowSegment != null) && (prevRowSegment[j - firstColumn] == 'x')) {
                    continue;
                }
                numIslands++;
            }

            i = firstRow + k - 1;
            j = firstColumn + k - 1;
            if ((matrix[i][j] == 'x') && (matrix[i-1][j] != 'x') && (matrix[i][j-1] != 'x')) {
                numIslands++;
            }
        }

        return  numIslands;
    }

    private static int countIslands(char[][] matrix, int firstRow, int firstColumn, int rows, int columns,
                                    char[] prevRowSegment, char[] prevColumnSegment) {
        int numIslands = 0;
        for (int i = firstRow; i < (firstRow + rows); i++) {
            for (int j = firstColumn; j < (firstColumn + columns); j++) {
                if (matrix[i][j] != 'x') {
                    continue;
                }
                if  ((i > firstRow) && (matrix[i-1][j] == 'x')) {
                    continue;
                } else if ((i == firstRow) && (prevRowSegment != null)) {
                    if (prevRowSegment[j-firstColumn] == 'x') {
                        continue;
                    }
                }

                if ((j > firstColumn) && (matrix[i][j-1] == 'x')) {
                    continue;
                } else if ((j == firstColumn) && (prevColumnSegment != null)) {
                    if (prevColumnSegment[i-firstRow] == 'x') {
                        continue;
                    }
                }

                numIslands++;
            }
        }

        return numIslands;
    }

    private static void test1() {
        char[][] matrix = new char[][] {
                {'o', 'x', 'x', 'o'},
                {'x', 'o', 'x', 'o'},
                {'x', 'o', 'x', 'o'}
        };
        int expected = 2;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test2() {
        char[][] matrix = new char[][] {
                {'o', 'x', 'o'},
                {'x', 'o', 'o'}
        };
        int expected = 2;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test3() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'o'},
                {'o', 'o', 'x'}
        };
        int expected = 2;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test4() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x'},
                {'o', 'o', 'x'}
        };
        int expected = 2;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test5() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'o'},
                {'o', 'x', 'x'}
        };
        int expected = 2;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test6() {
        char[][] matrix = new char[][] {
                {'x', 'x', 'o'},
                {'o', 'o', 'x'}
        };
        int expected = 2;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test7() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'o'},
                {'x', 'o', 'x'}
        };
        int expected = 2;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test8() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'o'},
                {'x', 'x', 'x'}
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test9() {
        char[][] matrix = new char[][] {
                {'x', 'x', 'o'},
                {'o', 'x', 'x'}
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test10() {
        char[][] matrix = new char[][] {
                {'x', 'x', 'o'},
                {'x', 'x', 'x'}
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test11() {
        char[][] matrix = new char[][] {
                {'x', 'x', 'x'},
                {'x', 'x', 'x'}
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test12() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x'},
                {'o', 'x', 'o'}
        };
        int expected = 3;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test16() {
        char[][] matrix = new char[][] {
                {'o', 'o', 'x'},
                {'o', 'x', 'x'}
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test17() {
        char[][] matrix = new char[][] {
                {'o', 'o', 'x'},
                {'x', 'x', 'x'}
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test13() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x', 'x', 'o', 'x'},
                {'o', 'x', 'o', 'o', 'x', 'o'}
        };
        int expected = 5;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test14() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x', 'x', 'o', 'x', 'o'},
                {'o', 'x', 'o', 'o', 'x', 'o', 'x'}
        };
        int expected = 6;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test15() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x', 'x', 'o', 'x', 'o'},
                {'o', 'x', 'o', 'o', 'x', 'x', 'x'}
        };
        int expected = 4;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test18() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x'},
                {'o', 'x', 'o'},
                {'x', 'o', 'x'},
        };
        int expected = 5;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test19() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x'},
                {'x', 'x', 'o'},
                {'x', 'o', 'x'},
        };
        int expected = 3;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test20() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x'},
                {'x', 'x', 'x'},
                {'x', 'o', 'x'},
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test21() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x'},
                {'x', 'o', 'x'},
                {'x', 'o', 'x'},
        };
        int expected = 2;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test22() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x'},
                {'o', 'o', 'o'},
                {'x', 'o', 'x'},
        };
        int expected = 4;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test23() {
        char[][] matrix = new char[][] {
                {'x', 'x', 'x'},
                {'x', 'o', 'x'},
                {'x', 'o', 'x'},
                {'x', 'x', 'x'},
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test24() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x'},
                {'x', 'x', 'x'},
                {'x', 'o', 'x'},
                {'x', 'x', 'x'},
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test25() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x', 'x', 'x'},
                {'x', 'x', 'x', 'o', 'x'},
                {'x', 'o', 'o', 'o', 'x'},
                {'x', 'x', 'x', 'x', 'x'},
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test26() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x', 'x', 'x'},
                {'x', 'x', 'x', 'o', 'x'},
                {'x', 'o', 'o', 'x', 'x'},
                {'x', 'x', 'x', 'x', 'x'},
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test27() {
        char[][] matrix = new char[][] {
                {'x', 'o', 'x', 'x', 'x'},
                {'x', 'x', 'x', 'o', 'x'},
                {'x', 'o', 'o', 'x', 'o'},
                {'x', 'x', 'x', 'x', 'x'},
        };
        int expected = 1;
        int actual;
        actual = countIslandsSmallMatrix(matrix);
        assert (expected == actual);
        actual = countIslandsLargeMatrix(matrix);
        assert (expected == actual);
    }

    private static void test() {
        test1();
        test2();
        test3();
        test4();
        test5();
        test6();
        test7();
        test8();
        test9();
        test10();
        test11();
        test12();
        test16();
        test17();
        test13();
        test14();
        test15();
        test18();
        test19();
        test20();
        test21();
        test22();
        test23();
        test24();
        test25();
        test26();
        test27();
    }

    public static void main(String[] args) {
        test();
    }
}
