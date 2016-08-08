package me.soubhik;

import java.util.ArrayList;
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
    //base can be any integer in (0, 128]
    public static class VLQ implements IntegerCode {
        private static final byte MASK = (byte)(1 << 7);
        private static final int DEFAULT_BASE = 128;

        private final int base;

        public VLQ() {
            this(DEFAULT_BASE);
        }

        public VLQ(int base) {
            assert (base > 0);
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
        public byte[] add(byte[] x, byte[] y) {
            int maxLen, minLen;
            byte[] maxArray;
            if (x.length >= y.length) {
                maxLen = x.length;
                minLen = y.length;
                maxArray = x;
            } else {
                maxLen = y.length;
                minLen = x.length;
                maxArray = y;
            }

            ArrayList<Byte> result = new ArrayList<>(maxLen + 1);
            int carry = 0;
            int idx = 0;
            for (; idx < minLen; idx++) {
                int xIdx = x.length - 1 - idx;
                int yIdx = y.length - 1 - idx;
                byte a = (byte)(x[xIdx] & (~MASK));
                byte b = (byte)(y[yIdx] & (~MASK));
                byte sum = (byte)(a + b + carry);
                carry = (sum < 0) ? 1: 0;
                result.add(sum);
            }

            for (; idx < maxArray.length; idx++) {
                int bIdx = maxArray.length - 1 - idx;
                byte b = (byte)(maxArray[bIdx] & (~MASK));
                byte sum = (byte)(b + carry);
                carry = (sum < 0) ? 1: 0;
                result.add(sum);
            }

            int resultLen = maxLen;
            if (carry == 1) {
                result.add((byte)carry);
                resultLen++;
            }

            byte[] sum = toByteArray(result, resultLen);
            sum[0] = (byte)(sum[0] | MASK);
            sum[sum.length-1] = (byte)(sum[sum.length-1] & (~MASK));

            return sum;
        }

        @Override
        public byte[] sub(byte[] x, byte[] y) {
            int maxLength = x.length;
            int minLength = y.length;
            byte[] maxArray = x;

            ArrayList<Byte> result = new ArrayList<>(maxLength);
            int resultLen = 1;
            int borrow = 0;
            int idx = 0;
            for (; idx < minLength; idx++) {
                int xIdx = x.length - 1 - idx;
                int yIdx = y.length - 1 - idx;
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
                int xIdx = x.length - 1 - idx;
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
                resultBytes[i] = (byte)(resultBytes[i] | MASK);
            }

            return resultBytes;
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
        assert (sub == actualSub);
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
    }

    public static void main(String[] args) {
        IntegerCode vlqCoder = new VLQ();
        test1(vlqCoder);
    }
}
