package me.soubhik.totp;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Implementation of time-based OTP algorithm
 * (please see https://en.wikipedia.org/wiki/Time-based_One-time_Password_Algorithm).
 *
 * Important differences from Google Authenticator (https://github.com/google/google-authenticator-android):
 * 1. secret is base64 encoded (base64 is more space efficient than base32).
 * 2. uses SHA2 (SHA-256) (SHA2 is more secure than SHA1).
 * 3. OTP is a 32 bit positive int, not truncated to a predefined number of digits. this number can be transmitted
 * to an authentication server as is. if a human needs to type the OTP, the caller can trivially extract the
 * required number of digits by a modulo operation
 * (e.g. see https://github.com/google/google-authenticator-android/blob/master/AuthenticatorApp/src/main/java/com/google/android/apps/authenticator/PasscodeGenerator.java#L159).
 *
 * Created by soubhik on 04-10-2016.
 */
public class Totp {
    private static final int MAX_CODE_LENGTH = 9;
    private static final String ALGORITHM = "HmacSHA256";

    private final long epoch;
    private final long step;
    private final Mac mac;

    public Totp(String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        this(secret, 0L, 30000L);
    }

    public Totp(String secret, long epoch, long step) throws NoSuchAlgorithmException, InvalidKeyException {
        this.epoch = epoch;
        this.step = step;

        byte[] secretBytes = new Base64().decode(secret);
        this.mac = Mac.getInstance(ALGORITHM);
        Key key = new SecretKeySpec(secretBytes, ALGORITHM);
        mac.init(key);
    }

    public int code() {
        long now = System.currentTimeMillis();
        long count = (now - epoch) / step;
        byte[] codeBytes = mac.doFinal(longToBytes(count));
        int offset = codeBytes[codeBytes.length - 1] & 0x1f; //sha-256 code is 32 bytes
        int codeInt = bytesToPositiveInt(codeBytes, offset);

        return codeInt;
    }

    private static int bytesToPositiveInt(byte[] bytes, int offset) {
        int result = bytesToInt(bytes, offset);
        return (result & 0x7fffffff);
    }

    private static int bytesToInt(byte[] bytes, int offset) {
        int result = 0;
        byte shift = 24;
        for (int i = offset; i < offset + 4; i++) {
            int b = bytes[i%bytes.length];
            b &= 0xff;
            result |= (b << shift);
            shift -= 8;
        }

        return result;
    }

    private static byte[] longToBytes(long l) {
        final long mask = 0xffL;

        byte[] bytes = new byte[8];
        for (int i = 0;  i < 8; i++) {
            bytes[bytes.length - 1 - i] = (byte)(l & mask);
            l >>>= 8;
        }

        return bytes;
    }

    private static void testBytesToInt(byte[] bytes, int offset, int expected) {
        int actual = bytesToInt(bytes, offset);
        assert (actual == expected);
    }

    private static void testBytesToPositiveInt(byte[] bytes, int offset, int expected) {
        int actual = bytesToPositiveInt(bytes, offset);
        assert (actual == expected);
    }

    private static void testBytesDecoders() {
        int expected1 = 0x01020304;
        byte bytes1[] = new byte[] {1, 2, 3, 4};
        testBytesToInt(bytes1, 0, expected1);
        testBytesToPositiveInt(bytes1, 0, expected1);

        byte[] bytes = new byte[] {(byte)0xba, (byte)0xba, (byte)0xb1, (byte)0xac,
                                    (byte)0x44, (byte)0x22, (byte)0x88, (byte)0x11};

        //offset 0
        int expected = 0xbabab1ac;
        testBytesToInt(bytes, 0, expected);
        expected = 0x3abab1ac;
        testBytesToPositiveInt(bytes, 0, expected);

        //offset 2
        expected = 0xb1ac4422;
        testBytesToInt(bytes, 2, expected);
        expected = 0x31ac4422;
        testBytesToPositiveInt(bytes, 2, expected);

        //offset 5
        expected = 0x228811ba;
        testBytesToInt(bytes, 5, expected);
        expected = 0x228811ba;
        testBytesToPositiveInt(bytes, 5, expected);
    }

    private static void testLongToBytes(long l, byte[] expected) {
        byte[] actual = longToBytes(l);
        assert Arrays.equals(expected, actual);
    }

    private static void testByteEncoders() {
        long test = 0x01020304L;
        byte[] expected = new byte[] {0, 0, 0, 0, 1, 2, 3, 4};
        testLongToBytes(test, expected);

        test = 0xf1abcdefL;
        expected = new byte[] {0, 0, 0, 0, (byte)0xf1, (byte)0xab, (byte)0xcd, (byte)0xef};
        testLongToBytes(test, expected);

        test = 0x661234569a93L;
        expected = new byte[] {0, 0, (byte)0x66, (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x9a, (byte)0x93};
        testLongToBytes(test, expected);

        test = 0xffeeddbbeeddeeddL;
        expected = new byte[] {(byte)0xff, (byte)0xee, (byte)0xdd, (byte)0xbb, (byte)0xee, (byte)0xdd, (byte)0xee, (byte)0xdd};
        testLongToBytes(test, expected);

        test = 0x3feeddbbeeddeeddL;
        expected = new byte[] {(byte)0x3f, (byte)0xee, (byte)0xdd, (byte)0xbb, (byte)0xee, (byte)0xdd, (byte)0xee, (byte)0xdd};
        testLongToBytes(test, expected);
    }

    private static void testSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            System.err.print("Unexpected exception: " + e);
            e.printStackTrace();
        }
    }

    private static void testTotp(Totp totp, long smallDelay, long longDelay) {
        int code = totp.code();
        testSleep(smallDelay);
        int newCode = totp.code();
        assert (code == newCode);
        testSleep(longDelay);
        newCode = totp.code();
        assert (code != newCode);
    }

    public static void main(String[] args) {
        testBytesDecoders();
        testByteEncoders();

        String testSecret = "MzI4dTkzMnVmbm9ma2QgMTItPWAzMCAtMDFyIHBva3cgbHNkbXZsZHNtdmxzZG12a2xkZm52amtu";

        Totp totp;
        try {
            totp = new Totp(testSecret);
        } catch (Exception e){
            System.err.println(e);
            e.printStackTrace();
            return;
        }
        testTotp(totp, 1, 40000);

        try {
            totp = new Totp(testSecret, 100000, 5000);
        } catch (Exception e){
            System.err.println(e);
            e.printStackTrace();
            return;
        }
        testTotp(totp, 1, 10000);
    }
}
