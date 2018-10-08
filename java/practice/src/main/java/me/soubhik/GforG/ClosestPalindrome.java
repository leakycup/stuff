package me.soubhik.GforG;

/**
 * Created by sb8 on 10/8/18.
 */
public class ClosestPalindrome {
    public static class TestCase {
        long n;
        public TestCase(long n) {
            this.n = n;
        }
    }

    private int numDigits(long n) {
        int numDigits = 0;
        int divisor = 10;
        for (int i = 1; i <= 14; i++) {
            n /= divisor;
            if (n == 0) {
                numDigits = i;
                break;
            }
        }

        return numDigits;
    }

    private long reverse(long n, int numDigits) {
        long reversed = 0;
        int divisor = 10;
        for (int i = 1; i <= numDigits; i++) {
            long digit = n % divisor;
            n /= divisor;
            reversed *= 10;
            reversed += digit;
        }

        return reversed;
    }

    private long extractUpper(long n, int numDigits) {
        int divisor = 1;
        for (int i = 1; i <= numDigits/2; i++) {
            divisor *= 10;
        }
        if ((numDigits % 2) == 1) {
            divisor *= 10;
        }

        long upper = n / divisor;

        return upper;
    }

    private long findCandidatePalindrome(long base) {
        int numDigits = numDigits(base);

        long upper = extractUpper(base, numDigits);
        int numDigitsInUpper = numDigits/2;
        long reversedUpper = reverse(upper, numDigitsInUpper);
        long palindrome = base + reversedUpper;

        return palindrome;
    }

    private long findClosest(long n, long... candidates) {
        long closest = candidates[0];
        long closestDistance = Math.abs(n - closest);

        for (int i = 1; i < candidates.length; i++) {
            long candidate = candidates[i];
            long distance = Math.abs(n - candidate);
            if (distance < closestDistance) {
                closest = candidate;
                closestDistance = distance;
            } else if (distance == closestDistance) {
                closest = (closest > candidate) ? candidate : closest;
            }
        }

        return closest;
    }

    private long closestPalindrome(long n) {
        int numDigits = numDigits(n);
        if (numDigits < 2) {
            return n;
        }

        int divisor = 1;
        for (int i = 1; i <= numDigits/2; i++) {
            divisor *= 10;
        }
        long lower = n % divisor;
        long base = n - lower;

        long base1 = base + divisor;
        long base2 = base - divisor;
        base2 -= base2 % divisor;

        if ((numDigits % 2) == 1) {
            divisor *= 10;
        }

        long palindrome1 = findCandidatePalindrome(base);
        long palindrome2 = findCandidatePalindrome(base1);
        long palindrome3 = findCandidatePalindrome(base2);
        long palindrome = findClosest(n, palindrome1, palindrome2, palindrome3);

        return palindrome;
    }

    private static void test(ClosestPalindrome closestPalindrome) {
        assert (0 == closestPalindrome.closestPalindrome(0));
        assert (4 == closestPalindrome.closestPalindrome(4));
        assert (9 == closestPalindrome.closestPalindrome(9));
        assert (11 == closestPalindrome.closestPalindrome(10));
        assert (11 == closestPalindrome.closestPalindrome(11));
        assert (11 == closestPalindrome.closestPalindrome(16));
        assert (22 == closestPalindrome.closestPalindrome(19));
        assert (88 == closestPalindrome.closestPalindrome(91));
        assert (191 == closestPalindrome.closestPalindrome(188));
        assert (191 == closestPalindrome.closestPalindrome(190));
        assert (191 == closestPalindrome.closestPalindrome(191));
        assert (191 == closestPalindrome.closestPalindrome(192));
        assert (202 == closestPalindrome.closestPalindrome(199));
        assert (979 == closestPalindrome.closestPalindrome(984));
    }

    public static void main(String[] args)
    {
        ClosestPalindrome closestPalindrome = new ClosestPalindrome();
        test(closestPalindrome);

        /*
        int numTestCases;
        TestCase[] testCases;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            numTestCases = Integer.parseInt(line);
            testCases = new TestCase[numTestCases];
            for (int i = 0; i < numTestCases; i++) {
                line = reader.readLine();
                long n = Long.parseLong(line);
                testCases[i] = new TestCase(n);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (TestCase testCase: testCases) {
            long palindrome = closestPalindrome.closestPalindrome(testCase.n);
            System.out.println(palindrome);
        }
        */
    }
}
