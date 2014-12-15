package in.blogspot.freemind_subwaywall.everything_else;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class NestedTryCatch {
    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                                                                    System.in));
            String input = in.readLine();
            try {
                if (input.equals("one")) {
                    throw new MyExceptionOne();
                } else {
                    throw new MyExceptionTwo();
                }
            } catch (MyExceptionTwo e) {
                System.err.println("Caught MyExceptionTwo\n");
            }
        } catch (MyExceptionOne e) {
            System.err.println("Caught MyExceptionOne\n");
        } catch (IOException e) {
            System.err.println("Caught IOException\n");
        }
    }

    private static class MyExceptionOne extends Exception {
    }

    private static class MyExceptionTwo extends Exception {
    }
}
