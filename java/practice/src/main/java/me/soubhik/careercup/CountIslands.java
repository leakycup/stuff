package me.soubhik.careercup;

/**
 * Created by soubhik on 16-12-2018.
 * from https://www.careercup.com/question?id=5669768827764736
 */
public class CountIslands {
    private static final int M = 2;
    private static final int N = 3;

    //   assume: matrix is not empty (has at least 1 row)
    //   O(m*n)
    public static int countIslandsSmallMatrix(char[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        int numIslands = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (matrix[i][j] != 'x') {
                    continue;
                }
                if  ((i > 0) && (matrix[i-1][j] == 'x')) {
                    continue;
                }
                if ((j > 0) && (matrix[i][j-1] == 'x')) {
                    continue;
                }
                if ((i > 0) && (j < (columns-1)) && (matrix[i-1][j+1] == 'x') && (matrix[i][j+1] == 'x')) {
                    continue;
                }
                numIslands++;
            }
        }

        return numIslands;
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
        return countIslandsSmallMatrix(matrix);
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
        test13();
        test14();
        test15();
    }

    public static void main(String[] args) {
        test();
    }
}
