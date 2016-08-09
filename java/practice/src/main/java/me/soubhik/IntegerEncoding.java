package me.soubhik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by soubhik on 06-08-2016.
 */
public class IntegerEncoding {
    public static interface IntegerCode {
        //index==0 => Most Significant
        public byte[] encode(int x);
        public int decode(byte[] code);
        public byte[] add(byte[] a, byte[] b);
        public byte[] sub(byte[] a, byte[] b);
        public byte[] mult(byte[] a, byte[] b);
        public boolean isValid(byte[] a);

        default public String toBinaryString(byte[] code) {
            StringBuilder builder = new StringBuilder();

            for (byte c: code) {
                String byteAsString = String.format("%8s", Integer.toBinaryString(c & 0xff)).replace(' ', '0');
                builder.append(byteAsString);
                builder.append(' ');
            }

            return builder.toString().trim();
        }
    }

    /*
    public static interface Digitiser<T> extends Iterator<Integer> {
        public default void remove() {
            throw new UnsupportedOperationException("Digitiser does not support remove()");
        }
    }

    public static class IntDigitiser implements Digitiser<Integer> {
        private final int number;
        private int extractRemainder = 128;
        private int extractByte = 1;

        public IntDigitiser(int number) {
            this.number = number;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Integer next() {
            return -1;
        }
    }
    */

    //see https://en.wikipedia.org/wiki/Variable-length_quantity
    //also, Lucene VInt type : http://lucene.apache.org/core/3_6_2/fileformats.html
    //base can be any integer in (1, 128]
    public static class VLQ implements IntegerCode {
        private static final byte MASK = (byte)(1 << 7);
        private static final int DEFAULT_BASE = 128;

        private final int base;

        public VLQ() {
            this(DEFAULT_BASE);
        }

        public VLQ(int base) {
            assert (base > 1);
            assert (base <= DEFAULT_BASE);

            this.base = base;
        }

        @Override
        public byte[] encode(int x) {
            if (x == 0) {
                return new byte[] {0};
            }

            int extractRemainder = base;
            int extractByte = 1;
            ArrayList<Byte> bytes = new ArrayList<Byte>();
            while (x >= extractByte) {
                int remainder = x % extractRemainder;
                byte b = (byte)(remainder / extractByte);
                bytes.add(b);
                extractByte *= base;
                extractRemainder *= base;
            }

            byte[] result = toByteArray(bytes, bytes.size());
            for (int i = 0; i < result.length - 1; i++) {
                result[i] = (byte)(result[i] | MASK);
            }

            return result;
        }

        @Override
        public int decode(byte[] code) {
            int multiplier = 1;
            int result = 0;
            for (int i = code.length-1; i >= 0; i--) {
                int b = code[i] & (~MASK);
                result += b*multiplier;
                multiplier *= base;
            }

            return result;
        }

        @Override
        public boolean isValid(byte[] x) {
            for (int i = 0; i < x.length - 1; i++) {
                byte b = x[i];
                if ((b & MASK) == 0) {
                    return false;
                }

                b &= ~MASK;
                if ((b >= base) || (b < 0)) {
                    return false;
                }
            }

            if ((x[x.length - 1] >= base) || (x[x.length - 1] < 0))  {
                return false;
            }

            return true;
        }

        @Override
        public byte[] add(byte[] x, byte[] y) {
            return add(x, 0, x.length, y, 0, y.length);
        }

        private byte[] add(byte[] x, int xStart, int xEnd, byte[] y, int yStart, int yEnd) {
            assert (xEnd > xStart);
            assert (yEnd > yStart);

            int xLen = xEnd  - xStart;
            int yLen = yEnd - yStart;

            int maxLen, minLen, maxArrayStart;
            byte[] maxArray;
            if (xLen >= yLen) {
                maxLen = xLen;
                minLen = yLen;
                maxArray = x;
                maxArrayStart = xStart;
            } else {
                maxLen = yLen;
                minLen = xLen;
                maxArray = y;
                maxArrayStart = yStart;
            }

            ArrayList<Byte> result = new ArrayList<>(maxLen + 1);
            int carry = 0;
            int idx = 0;
            for (; idx < minLen; idx++) {
                int xIdx = xStart + xLen - 1 - idx;
                int yIdx = yStart + yLen - 1 - idx;
                byte a = (byte)(x[xIdx] & (~MASK));
                byte b = (byte)(y[yIdx] & (~MASK));
                int sum = a + b + carry;
                if (sum >= base) {
                    sum -= base;
                    carry = 1;
                } else {
                    carry = 0;
                }
                result.add((byte)sum);
            }

            for (; idx < maxLen; idx++) {
                int bIdx = maxArrayStart + maxLen - 1 - idx;
                byte b = (byte)(maxArray[bIdx] & (~MASK));
                int sum = b + carry;
                if (sum >= base) {
                    sum -= base;
                    carry = 1;
                } else {
                    carry = 0;
                }
                result.add((byte)sum);
            }

            int resultLen = maxLen;
            if (carry == 1) {
                result.add((byte)carry);
                resultLen++;
            }

            byte[] sum = toByteArray(result, resultLen);
            for (int i = 0; i < sum.length - 1; i++) {
                sum[i] |= MASK;
            }
            sum[sum.length-1] &= ~MASK;

            return sum;
        }

        @Override
        public byte[] sub(byte[] x, byte[] y) {
            return sub(x, 0, x.length, y, 0, y.length);
        }

        public byte[] sub(byte[] x, int xStart, int xEnd, byte[] y, int yStart, int yEnd) {
            assert (xEnd > xStart);

            byte paddingByte = MASK;
            int newYStart = yStart;
            for (; newYStart < yEnd - 1; newYStart++) {
                if (y[newYStart] != paddingByte) {
                    break;
                }
            }
            yStart = newYStart;
            assert (yEnd > yStart);

            int xLen = xEnd  - xStart;
            int yLen = yEnd - yStart;
            assert (xLen >= yLen);

            int maxLength = xLen;
            int minLength = yLen;
            byte[] maxArray = x;

            ArrayList<Byte> result = new ArrayList<>(maxLength);
            int resultLen = 1;
            int borrow = 0;
            int idx = 0;
            for (; idx < minLength; idx++) {
                int xIdx = xStart + xLen - 1 - idx;
                int yIdx = yStart + yLen - 1 - idx;
                byte xValue = (byte)(x[xIdx] & (~MASK));
                byte yValue = (byte)(y[yIdx] & (~MASK));
                int nextBorrow = 0;
                if (xValue < (yValue + borrow)) {
                    xValue += base;
                    nextBorrow = 1;
                }
                byte s = (byte)(xValue - yValue - borrow);
                result.add(s);
                if (s != 0) {
                    resultLen = idx + 1;
                }
                borrow = nextBorrow;
            }

            for (; idx < maxLength; idx++) {
                int xIdx = xStart + xLen - 1 - idx;
                byte xValue = (byte)(maxArray[xIdx] & (~MASK));
                int nextBorrow = 0;
                if (xValue < borrow) {
                    xValue += base;
                    nextBorrow = 1;
                }
                byte s = (byte)(xValue - borrow);
                result.add(s);
                if (s != 0) {
                    resultLen = idx + 1;
                }
                borrow = nextBorrow;
            }

            byte[] resultBytes = toByteArray(result, resultLen);
            for (int i = 0; i < resultBytes.length - 1; i++) {
                resultBytes[i] |= MASK;
            }

            return resultBytes;
        }

        @Override
        public byte[] mult(byte[] x, byte[] y) {
            if (x.length > y.length) {
                y = paddLeft(y, x.length - y.length);
            } else if (x.length < y.length) {
                x = paddLeft(x, y.length - x.length);
            }
            return unpaddLeft(multKaratsuba(x, 0, x.length, y, 0, y.length));
        }

        //fast multiplication using Karatsuba algorithm: https://en.wikipedia.org/wiki/Karatsuba_algorithm
        private byte[] multKaratsuba(byte[] x, int xStart, int xEnd, byte[] y, int yStart, int yEnd) {
            assert (xEnd > xStart);
            assert (yEnd > yStart);

            int xLen = xEnd - xStart;
            int yLen = yEnd - yStart;
            assert (xLen == yLen);

            if (xLen == 1) {
                return multByte(x[xStart], y[yStart]);
            }

            byte[] z2 = multKaratsuba(x, xStart, xStart + xLen/2, y, yStart, yStart + yLen/2);
            byte[] z0 = multKaratsuba(x, xStart + xLen/2, xEnd, y, yStart + yLen/2, yEnd);

            byte[] x0plusx1 = add(x, xStart, xStart + xLen / 2, x, xStart + xLen / 2, xEnd);
            byte[] y0plusy1 = add(y, yStart, yStart + yLen/2, y, yStart + yLen/2, yEnd);
            if (x0plusx1.length > y0plusy1.length) {
                y0plusy1 = paddLeft(y0plusy1, x0plusx1.length - y0plusy1.length);
            } else if (x0plusx1.length < y0plusy1.length) {
                x0plusx1 = paddLeft(x0plusx1, y0plusy1.length - x0plusx1.length);
            }
            byte[] z1 = multKaratsuba(x0plusx1, 0, x0plusx1.length, y0plusy1, 0, y0plusy1.length);
            z1 = sub(z1, z2);
            z1 = sub(z1, z0);

            int ceilingOfLenBy2 = (xLen + 1)/2;
            z2 = multByBasePowerN(z2, 2*ceilingOfLenBy2);
            z1 = multByBasePowerN(z1, ceilingOfLenBy2);
            byte[] result = add(z1, z2);
            result = add(result, z0);

            return result;
        }

        //shift and add multiplication: https://en.wikipedia.org/wiki/Multiplication_algorithm
        private byte[] multByte(byte b1, byte b2) {
            b1 &= ~MASK;
            b2 &= ~MASK;
            byte mask = 1;
            byte shift = 0;
            int tempResult = 0;
            for (int i = 0; i < 7; i++) {
                if ((b2 & mask) > 0) {
                    tempResult += b1 << shift;
                }
                shift++;
                mask <<= 1;
            }

            return encode(tempResult);
        }

        private byte[] multByBasePowerN(byte[] x, int n) {
            assert (n >= 0);

            if (n == 0) {
                return x;
            }

            byte[] result = new byte[x.length + n];
            System.arraycopy(x, 0, result, 0, x.length);
            byte paddingByte = MASK;
            Arrays.fill(result, x.length, result.length, paddingByte);
            result[x.length - 1] |= MASK;
            result[result.length - 1] &= ~MASK;

            return result;
        }

        private byte[] paddLeft(byte[] original, int n) {
            byte[] padded = new byte[original.length + n];
            byte paddingByte = MASK;
            Arrays.fill(padded, 0, n, paddingByte);
            System.arraycopy(original, 0, padded, n, original.length);

            return padded;
        }

        private byte[] unpaddLeft(byte[] original) {
            int numPaddingBytes = 0;
            byte padddingByte = MASK;
            for (int i = 0; i < original.length - 1; i++) {
                if (original[i] != padddingByte) {
                    break;
                }
                numPaddingBytes++;
            }

            if (numPaddingBytes == 0) {
                return original;
            }

            int unpaddedLength = original.length - numPaddingBytes;
            byte[] unpadded = new byte[unpaddedLength];
            System.arraycopy(original, numPaddingBytes, unpadded, 0, unpaddedLength);

            return unpadded;
        }
    }

    private static byte[] toByteArray(Iterable<Byte> bytes, int length) {
        byte[] result = new byte[length];

        Iterator<Byte> bytesIterator = bytes.iterator();
        for (int i = length - 1; i >= 0; i--) {
            assert (bytesIterator.hasNext());

            byte b = bytesIterator.next();
            result[i] = b;
        }

        return result;
    }

    private static void encodeDecodeTest(int[] values, IntegerCode coder) {
        for (int v: values) {
            byte[] code = coder.encode(v);
            int actual = coder.decode(code);
            System.out.println("expected: " + v + ", actual: " + actual + ", code: " + coder.toBinaryString(code));
            assert (v == actual);
        }
    }

    private static void additionTest(int a, int b, IntegerCode coder) {
        assert (a >= 0);
        assert (b >= 0);
        int sum = a + b;
        assert (sum >= a);
        assert (sum >= b);

        byte[] aCode = coder.encode(a);
        byte[] bCode = coder.encode(b);
        byte[] sumCode = coder.add(aCode, bCode);
        int actualSum = coder.decode(sumCode);

        System.out.println("a: " + a + ", b: " + b + ", expected sum: " + sum + ", actual sum: " + actualSum +
                        ", code: " + coder.toBinaryString(sumCode));
        assert (coder.isValid(sumCode));
        assert (sum == actualSum);
    }

    private static void subtractionTest(int a, int b, IntegerCode coder) {
        assert (a >= 0);
        assert (b <= a);

        int sub = a - b;
        byte[] aCode = coder.encode(a);
        byte[] bCode = coder.encode(b);
        byte[] subCode = coder.sub(aCode, bCode);
        int actualSub = coder.decode(subCode);

        System.out.println("a: " + a + ", b: " + b + ", expected sub: " + sub + ", actual sub: " + actualSub +
                ", code: " + coder.toBinaryString(subCode));
        assert (coder.isValid(subCode));
        assert (sub == actualSub);
    }

    private static void multiplicationTest(int a, int b, IntegerCode coder) {
        assert (a >= 0);
        assert (b >= 0);

        int expected = a*b;
        byte[] aBytes = coder.encode(a);
        byte[] bBytes = coder.encode(b);
        byte[] actualBytes = coder.mult(aBytes, bBytes);
        int actual = coder.decode(actualBytes);

        System.out.println("a: " + a + ", b: " + b + ", expected mult: " + expected + ", actual mult: " + actual +
                ", code: " + coder.toBinaryString(actualBytes));
        assert (coder.isValid(actualBytes));
        assert (expected == actual);
    }

    private static void test1(IntegerCode coder) {
        //encode and int, then decode
        int[] values = new int[] {0, 1, 2, 8, 127, 128, 137, 146, 160, 288, 306, 56, 250,
                                    16383, 16385, 2097152, 6789989, 80000000};
        System.out.println("encode decode test");
        System.out.println("================================");
        encodeDecodeTest(values, coder);

        //add two integers
        System.out.println("addition test");
        System.out.println("================================");
        additionTest(0, 0, coder);
        additionTest(0, 1, coder);
        additionTest(1, 0, coder);
        additionTest(1, 1, coder);
        additionTest(4, 3, coder);
        additionTest(6, 9, coder);
        additionTest(12, 56, coder);
        additionTest(120, 10, coder);
        additionTest(10, 120, coder);
        additionTest(120, 0, coder);
        additionTest(120, 130, coder);
        additionTest(127, 1, coder);

        //subtract two integers
        System.out.println("subtraction test");
        System.out.println("================================");
        subtractionTest(0, 0, coder);
        subtractionTest(1, 0, coder);
        subtractionTest(1, 1, coder);
        subtractionTest(12, 0, coder);
        subtractionTest(12, 1, coder);
        subtractionTest(12, 12, coder);
        subtractionTest(12, 8, coder);
        subtractionTest(2097152, 0, coder);
        subtractionTest(2097152, 1, coder);
        subtractionTest(2097152, 2097152, coder);
        subtractionTest(2097152, 2097102, coder);
        subtractionTest(306, 160, coder);
        subtractionTest(306, 288, coder);
        subtractionTest(306, 56, coder);

        //multiply two integers
        System.out.println("multiplication test");
        System.out.println("================================");
        multiplicationTest(1, 1, coder);
        multiplicationTest(1, 0, coder);
        multiplicationTest(0, 1, coder);
        multiplicationTest(0, 0, coder);
        multiplicationTest(27, 0, coder);
        multiplicationTest(27, 1, coder);
        multiplicationTest(27, 2, coder);
        multiplicationTest(27, 3, coder);
        multiplicationTest(27, 5, coder);
        multiplicationTest(27, 127, coder);
        multiplicationTest(27, 128, coder);
        multiplicationTest(136, 3, coder);
        multiplicationTest(136, 129, coder);
    }

    private static void test2(IntegerCode coder) {
        //encode and int, then decode
        int[] values = new int[] {0, 1, 2, 8, 127, 128, 137};
        System.out.println("encode decode test");
        System.out.println("================================");
        encodeDecodeTest(values, coder);

        //add two integers
        System.out.println("addition test");
        System.out.println("================================");
        additionTest(0, 0, coder);
        additionTest(0, 1, coder);
        additionTest(1, 0, coder);
        additionTest(1, 1, coder);
        additionTest(4, 3, coder);
        additionTest(6, 9, coder);
        additionTest(12, 56, coder);
        additionTest(120, 10, coder);
        additionTest(10, 120, coder);
        additionTest(120, 0, coder);
        additionTest(120, 130, coder);
        additionTest(127, 1, coder);

        //subtract two integers
        System.out.println("subtraction test");
        System.out.println("================================");
        subtractionTest(0, 0, coder);
        subtractionTest(1, 0, coder);
        subtractionTest(1, 1, coder);
        subtractionTest(12, 0, coder);
        subtractionTest(12, 1, coder);
        subtractionTest(12, 12, coder);
        subtractionTest(12, 8, coder);
        subtractionTest(2097152, 2097152, coder);
        subtractionTest(2097152, 2097102, coder);
        subtractionTest(306, 160, coder);
        subtractionTest(306, 288, coder);
        subtractionTest(306, 56, coder);

        //multiply two integers
        System.out.println("multiplication test");
        System.out.println("================================");
        multiplicationTest(1, 1, coder);
        multiplicationTest(1, 0, coder);
        multiplicationTest(0, 1, coder);
        multiplicationTest(0, 0, coder);
        multiplicationTest(27, 0, coder);
        multiplicationTest(27, 1, coder);
        multiplicationTest(27, 2, coder);
        multiplicationTest(27, 3, coder);
        multiplicationTest(27, 5, coder);
    }

    public static void main(String[] args) {
        IntegerCode vlqCoder = new VLQ();
        System.out.println("Testing VLQ base 128 (default base)");
        System.out.println("================================================");
        test1(vlqCoder);

        System.out.println("Testing VLQ base 10");
        System.out.println("================================================");
        IntegerCode vlqCoderBase10 = new VLQ(10);
        test1(vlqCoderBase10);

        System.out.println("Testing VLQ base 2");
        System.out.println("================================================");
        IntegerCode vlqCoderBase2 = new VLQ(2);
        test2(vlqCoderBase2);
    }
}
