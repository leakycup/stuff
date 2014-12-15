package in.blogspot.freemind_subwaywall.character_encoding;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class PrintDanda {
    //private static String dandaAsString = "।";
    private static String dandaAsString = "।";
    private static String dandaCodeAsString = "\u0964";
    //private static char dandaAsChar = '।'; //compilation error: unclosed character literal on javac/Java HotSpot VM 1.6.0_26 on Mac OSX (Snow Leopard). compiles ok on javac/Java HotSpot VM 1.6.0_24 on RedHat Linux 5.4.
    private static char dandaCodeAsChar = '\u0964';
    private static int dandaCodeAsInt = 2404;
    private static char questionMarkAsChar = '?';
    private static char questionMarkCodeAsChar = 63;
    private static PrintStream out;

    public static void main(String[] args) {
        try {
            out = new PrintStream(System.out, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("failed to open out stream", e);
        }
        out.println("dandaAsString: " + dandaAsString);
        out.println("dandaCodeAsString: " + dandaCodeAsString);
        out.println("dandaCodeAsChar: " + dandaCodeAsChar);

        int idx;
        idx = dandaAsString.indexOf(dandaCodeAsChar); //-1 on Snow Leopard, 0 on RHEL 5.4
        out.println("index of danda (char) in dandaAsString: " + idx);
        idx = dandaCodeAsString.indexOf(dandaCodeAsChar); //0 on both Snow Leopard, RHEL 5.4
        out.println("index of danda (char) in dandaCodeAsString: " + idx);
        idx = dandaAsString.indexOf(dandaCodeAsInt); //-1 on Snow Leopard, 0 on RHEL 5.4
        out.println("index of danda (int) in dandaAsString: " + idx);
        idx = dandaCodeAsString.indexOf(dandaCodeAsInt); //0 on both Snow Leopard, RHEL 5.4
        out.println("index of danda (int) in dandaCodeAsString: " + idx);

        idx = dandaAsString.indexOf(questionMarkAsChar);
        out.println("index of question-mark (char) in dandaAsString: " + idx);
        idx = dandaCodeAsString.indexOf(questionMarkAsChar);
        out.println("index of question-mark (char) in dandaCodeAsString: " + idx);
        idx = dandaAsString.indexOf(questionMarkCodeAsChar);
        out.println("index of question-mark code (char) in dandaAsString: " + idx);
        idx = dandaCodeAsString.indexOf(questionMarkCodeAsChar);
        out.println("index of question-mark code (char) in dandaCodeAsString: " + idx);

        out.println("codepoints for dandaAsString: ");
        printCodePoints(dandaAsString);
        out.println("codepoints for dandaCodeAsString: ");
        printCodePoints(dandaCodeAsString);

        char dandaAsCharArray[] = Character.toChars(dandaCodeAsInt);
        out.println("codepoints for dandaAsCharArray: ");
        printCodePoints(new String(dandaAsCharArray));
        for (int i=0; i< dandaAsCharArray.length; i++) {
            if (dandaAsCharArray[i] == dandaCodeAsInt) {
                out.println("found danda in dandaAsCharArray");
                break;
            }
        }

        char eosCharacters[] = new char[] {'.', '!', '?', (char)dandaCodeAsInt};
        out.println("dandaAsString contains eos character? " +
                           containsEos(dandaAsString, eosCharacters));
        out.println("dandaCodeAsString contains eos character? " +
                           containsEos(dandaCodeAsString, eosCharacters));

    }

    private static void printCodePoints(String str) {
        for (int i=0; i < str.length();) {
            int codepoint = str.codePointAt(i);
            out.print(codepoint + ", ");
            i += Character.charCount(codepoint);
        }
        out.print("\n");
    }

    private static boolean containsEos(String str, char[] eosCharacters) {
        for (int i=0; i<str.length(); i++) {
            for (int j=0; j<eosCharacters.length; j++) {
                if (str.charAt(i) == eosCharacters[j]) {
                    return (true);
                }
            }
        }

        return (false);
    }
}
