package org.apache.log4j;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LogConfigurator adapter for log4j to java logger.
 * It provides log4j BasicConfigurator methods used in JAS
 * @author Heinz Kredel.
 */
public class BasicConfigurator {

    /**
     * configure logging.
     */
    public static void configure() {
        try {
            java.util.logging.LogManager.getLogManager().readConfiguration();

            Handler[] handlers =
                    Logger.getLogger("").getHandlers();
            for (int index = 0; index < handlers.length; index++) {
                handlers[index].setLevel(Level.WARNING);
            }


        } catch (java.io.IOException e) {
            e.printStackTrace();
            //System.out.println("BasicConfigurator.configure(): " + e);
        }
    }

}
