/*
 * $Id: NoConvergenceException.java 3019 2010-03-05 18:42:41Z kredel $
 */

package edu.jas.root;


/**
 * No convergence exception class. Exception to be thrown when an
 * iteration does not converge.
 * @author Heinz Kredel
 */

public class NoConvergenceException extends Exception {


    public NoConvergenceException() {
        super("NoConvergenceException");
    }


    public NoConvergenceException(String c) {
        super(c);
    }


    public NoConvergenceException(String c, Throwable t) {
        super(c, t);
    }


    public NoConvergenceException(Throwable t) {
        super("NoConvergenceException", t);
    }

}
