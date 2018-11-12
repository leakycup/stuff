package me.soubhik.GforG;

/**
 * Created by soubhik on 12-11-2018.
 * from https://practice.geeksforgeeks.org/problems/multiply-two-strings/1
 */
public class StringMultiply {
    private static final int BLOCK_SIZE = 8;

    private static int addDigitsToArray(int number, int[] array, int offset, int digits) {
        for (int i = 0; i < digits; i++) {
            array[offset + i] = number % 10;
            number /= 10;
        }
        return number % 10;
    }

    // both first and second are large decimal numbers represented as strings.
    // this function multiplies first with the digit at the given offset of second
    // returns the result as an array of int[] (lower the index, less is the significance.
    // index 0 is least significant).
    private static int[] multiplyWithSingleDigit(String first, String second, int offset) {
        int[] product = new int[first.length() + 1 + offset]; //automatically 0 initialized by Java

        int multiplier = Character.digit(second.charAt(second.length() - offset - 1), 10);
        int productIndex = offset;
        int carry = 0;
        for (int i = first.length(); i > 0; i -= BLOCK_SIZE) {
            int j = i - BLOCK_SIZE;
            if (j < 0) {
                j = 0;
            }
            int multiplicand = Integer.parseInt(first.substring(j, i));
            int result = multiplicand * multiplier + carry;
            carry = addDigitsToArray(result, product, productIndex, i-j);
            productIndex += BLOCK_SIZE;
        }

        product[product.length - 1] = carry;

        return product;
    }

    private static int[] add(int[][] numbers, int m, int n) {
        int[] sum = new int[m+n+1];  // 0 initialized by Java

        int carry = 0;
        for (int i = 0; i < m+n; i++) {
            sum[i] = carry;
            for (int j = 0; j < n; j++) {
                sum[i] += (i < numbers[j].length) ? numbers[j][i] : 0;
            }
            carry = sum[i] / 10;
            sum[i] %= 10;
        }

        sum[m+n] = carry;
        return sum;
    }

    private static String arrayToString(int[] number, boolean negative) {
        int i;
        for (i = number.length - 1; i >= 0; i--) {
            if (number[i] > 0) {
                break;
            }
        }

        if (i < 0) {
            return "0";
        }

        StringBuilder builder = new StringBuilder();
        if (negative) {
            builder.append("-");
        }
        for (; i >= 0; i--) {
            builder.append(number[i]);
        }

        return builder.toString();
    }

    private static String stringMultiply(String multiplicand, String multiplier, boolean negative) {
        int[][] products = new int[multiplier.length()][];
        for (int i = 0; i < multiplier.length(); i++) {
            products[i] = multiplyWithSingleDigit(multiplicand, multiplier, i);
        }

        int[] result = add(products, multiplicand.length(), multiplier.length());

        return arrayToString(result, negative);
    }

    public static String stringMultiply(String multiplicand, String multiplier) {
        boolean negative = false;

        if (multiplicand.startsWith("-")) {
            multiplicand = multiplicand.substring(1);
            negative = !negative;
        } else if (multiplicand.startsWith("+")) {
            multiplicand = multiplicand.substring(1);
        }

        if (multiplier.startsWith("-")) {
            multiplier = multiplier.substring(1);
            negative = !negative;
        } else if (multiplier.startsWith("+")) {
            multiplier = multiplier.substring(1);
        }

        return stringMultiply(multiplicand, multiplier, negative);
    }

    private static void test1() {
        String actual = stringMultiply("2", "3");
        String expected = "6";

        assert (expected.equals(actual));
    }

    private static void test2() {
        String actual = stringMultiply("3", "2");
        String expected = "6";

        assert (expected.equals(actual));
    }

    private static void test3() {
        String actual = stringMultiply("2", "0");
        String expected = "0";

        assert (expected.equals(actual));
    }

    private static void test4() {
        String actual = stringMultiply("0", "2");
        String expected = "0";

        assert (expected.equals(actual));
    }

    private static void test5() {
        String actual = stringMultiply("0", "0");
        String expected = "0";

        assert (expected.equals(actual));
    }

    private static void test6() {
        String actual = stringMultiply("12", "4");
        String expected = "48";

        assert (expected.equals(actual));
    }

    private static void test7() {
        String actual = stringMultiply("4", "12");
        String expected = "48";

        assert (expected.equals(actual));
    }

    private static void test8() {
        String actual = stringMultiply("14", "12");
        String expected = "168";

        assert (expected.equals(actual));
    }

    private static void test9() {
        String actual = stringMultiply("14", "10");
        String expected = "140";

        assert (expected.equals(actual));
    }

    private static void test10() {
        String actual = stringMultiply("11", "23");
        String expected = "253";

        assert (expected.equals(actual));
    }

    private static void test11() {
        String actual = stringMultiply("2345678912", "1");
        String expected = "2345678912";

        assert (expected.equals(actual));
    }

    private static void test12() {
        String actual = stringMultiply("2345678912", "10");
        String expected = "23456789120";

        assert (expected.equals(actual));
    }

    private static void test13() {
        String actual = stringMultiply("2345678912", "123");
        String expected = "288518506176";

        assert (expected.equals(actual));
    }

    private static void test14() {
        String actual = stringMultiply("2345678912", "123057982");
        String expected = "288654513330675584";

        assert (expected.equals(actual));
    }

    private static void test15() {
        String actual = stringMultiply("675356291270936062618792023759228973612931947845036106320615547656937452547443078688", "3149206892664950487172722610615949091771159776736563948129390885096385611598481030444");
        String expected = "2126836687475069782586215495445641271967259409903170474345710011389771081014284572613170622096969671986161361328038381810405364204628173817225082912914044898630015577472";

        assert (expected.equals(actual));
    }

    private static void test16() {
        int[] firstDigits = new int[] {2, 4, 6, 8};
        StringBuilder first = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            first.append(firstDigits[i%4]);
        }

        int[] secondDigits = new int[] {1, 3, 6, 9};
        StringBuilder second = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            second.append(secondDigits[i%4]);
        }

        String actual = stringMultiply(first.toString(), second.toString());
        String expected = "3379367839774276457548745173547257716070636966686967726675657864816384628761906093599658995802570556085511541453175220512350264929483247354638454144444347425041534056395938623765366835713474337732803183308629892892279526982601250424072310221321162019192218251728163115341437134012431146104909520855075806610564046703700273017600789981988497879690959394969399930292059108901189148817872086238526842983328235813880417944784777507653755674597362726571687071697468776780668365866489639262956198610160045907581057135616551954225325522851315034493748404743464645494452435542584161406439673870377336763579348233853288319130942997290028032706260925122415231822212124202719301833173616391542144513481251115410570960086307660669057204750378028101840086998998929795969896019504940793109213911690198922882587288631853484378340824381468049795278557758766175647467737072737176707969826885678866916594649764006303620661096012591558185721562455275430533352365139504249454848475146544557446043634266416940723975387837137810790780048101819882958392848985868683878088778974907191689265936294599556965397509847994500420139023603330430052706240721081809151012110912061303140014971594169117881885198220792176227323702467256426612758285529523049314632433340343735343631372838253922401941164213431044074504460146984795489249895086518352805377547455715668576558625959605661536250634764446541663867356832692970267123722073177414751176087705780278997996809381908287838484818578867587728869896690639160925793549451954896459742983999370034013102280325042205190616071308100907100411011198129513921489158616831780187719742071216822652362245925562653275028472944304131383235333234293526362337203817391440114108420543024399449645934690478748844981507851755272536954665563566057575854595160486145624263396436653366306727682469217018711572127309740675037600769777947891798880858182827983768473857086678764886189589055915292499346944395409637973498319929002601230220031704140511060807050802089909961093119012871384148115781675177218692";

        assert (expected.equals(actual));
        //System.out.println(first);
        //System.out.println(second);
    }

    private static void test17() {
        StringBuilder first = new StringBuilder();
        first.append(1);
        for (int i = 1; i < 1000; i++) {
            first.append(0);
        }

        StringBuilder second = new StringBuilder();
        second.append(1);
        for (int i = 1; i < 1000; i++) {
            second.append(0);
        }

        StringBuilder expectedBuilder = new StringBuilder();
        expectedBuilder.append(1);
        for (int i = 1; i <= 999*2; i++) {
            expectedBuilder.append(0);
        }

        String actual = stringMultiply(first.toString(), second.toString());
        String expected = expectedBuilder.toString();

        assert (expected.equals(actual));
        //System.out.println(first);
        //System.out.println(second);
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
        test13();
        test14();
        test15();
        test16();
        test17();
    }

    public static void main(String[] args) {
        test();
    }
}
