/*
 * $Id: PreemptingException.java 1663 2008-02-05 17:32:07Z kredel $
 */

package edu.jas.kern;


/**
 * Preempting Exception class.
 * Runtime Exception to be thrown when a thread is interrupted.
 * @author Heinz Kredel
 */

public class PreemptingException extends RuntimeException {


    public PreemptingException() {
     super("PreemptingException");
    }


    public PreemptingException(String c) {
     super(c);
    }


    public PreemptingException(String c, Throwable t) {
     super(c,t);
    }


    public PreemptingException(Throwable t) {
     super("PreemptingException",t);
    }

}
