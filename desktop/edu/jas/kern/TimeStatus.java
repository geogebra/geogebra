/*
 * $Id: TimeStatus.java 3203 2010-07-02 15:54:32Z kredel $
 */

package edu.jas.kern;

import java.util.concurrent.Callable;


/**
 * Run-time status, 
 * defines global status and handling for run time limits.
 * @author Heinz Kredel
 */

public class TimeStatus {


    /**
     * Global status flag.
     */
    private static boolean allowTime = false;


    /**
     * Global run-time limit in milliseconds.
     */
    private static long limitTime = Long.MAX_VALUE;


    /**
     * Global run-time limit in milliseconds.
     */
    private static long startTime = System.currentTimeMillis();


    /**
     * Call back method.
     * true means continue, false means throw exception.
     */
    private static Callable<Boolean> callBack = null;


    /**
     * No public constructor.
     */
    protected TimeStatus() {
    }


    /**
     * isActive.
     * @return true, if run-time interruption is active, else false.
     */
    public static boolean isActive() {
        return allowTime;
    }


    /**
     * setAllow,
     * set run-time interruption to allowed status.
     */
    public static void setActive() {
        allowTime = true;
    }


    /**
     * setNotActive,
     * set run-time interruption to not active status.
     */
    public static void setNotActive() {
        allowTime = false;
    }


    /**
     * setLimit,
     * set run-time limit in milliseconds.
     */
    public static void setLimit(long t) {
        limitTime = t;
    }


    /**
     * Restart timer,
     * set run-time to current time.
     */
    public static void restart() {
        startTime = System.currentTimeMillis();
    }


    /**
     * set call back,
     * set the Callabe object.
     */
    public static void setCallBack(Callable<Boolean> cb) {
        callBack = cb;
    }


    /**
     * Check for exceeded time,
     * test if time has exceeded and throw an exception if so.
     * @param msg the message to be send with the exception.
     */
    public static void checkTime(String msg) {
        if ( ! allowTime ) {
            return;
        }
        if ( limitTime == Long.MAX_VALUE ) {
            return;
        }
        long tt = (System.currentTimeMillis() - startTime - limitTime);
        //System.out.println("tt  = " + tt);
        if ( tt <= 0L ) {
            return;
        }
        if ( callBack != null ) {
            try {
                boolean t = callBack.call();
                if ( t ) {
                    return;
                }
            } catch ( Exception e ) {
            }
        }
        if ( msg == null ) {
            msg = "";
        }
        throw new TimeExceededException(msg + " over time = " + tt);
    }

}
