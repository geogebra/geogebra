/*
 * $Id: PreemptStatus.java 1249 2007-07-29 10:05:58Z kredel $
 */

package edu.jas.kern;


/**
 * PreemptStatus,
 * defines global status for preemtive interruption handling.
 * @author Heinz Kredel
 */

public class PreemptStatus {

    /**
     * Global status flag.
     */
    private static boolean allowPreempt = true;


    /**
     * No public constructor.
     */
    protected PreemptStatus() {
    }


    /**
     * isAllowed.
     * @return true, preemtive interruption is allowed, else false.
     */
    public static boolean isAllowed() {
        return allowPreempt;
    }


    /**
     * setAllow,
     * set preemtive interruption to allowed status.
     */
    public static void setAllow() {
        allowPreempt = true;
    }


    /**
     * setNotAllow,
     * set preemtive interruption to not allowed status.
     */
    public static void setNotAllow() {
        allowPreempt = false;
    }

}
