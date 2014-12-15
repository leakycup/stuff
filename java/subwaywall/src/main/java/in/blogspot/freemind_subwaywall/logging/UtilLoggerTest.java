package in.blogspot.freemind_subwaywall.logging;

import java.util.logging.Logger;
import java.util.logging.Level;

public class UtilLoggerTest {
    private static Logger logger = Logger.getLogger(UtilLoggerTest.class.getName());

    public static void main (String[] args) {
        String msg = "This is a\nmutiline message\n...";
        logger.log(Level.SEVERE, msg);
    }
}
