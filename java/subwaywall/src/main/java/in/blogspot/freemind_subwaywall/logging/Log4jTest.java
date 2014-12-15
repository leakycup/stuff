package in.blogspot.freemind_subwaywall.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public class Log4jTest {
    private static Logger logger = Logger.getLogger(Log4jTest.class);
    static {
        BasicConfigurator.configure();
    }

    public static void main (String[] args) {
        String msg = "This is a\nmutiline message\n...";
        logger.error(msg);
    }
}
