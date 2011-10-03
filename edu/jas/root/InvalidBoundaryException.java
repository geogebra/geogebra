/*
 * $Id: InvalidBoundaryException.java 2925 2009-12-25 17:50:01Z kredel $
 */

package edu.jas.root;


/**
 * Invalid boundary exception class. Exception to be thrown when a valid
 * boundary cannot be constructed.
 * @author Heinz Kredel
 */

public class InvalidBoundaryException extends Exception {


    public InvalidBoundaryException() {
        super("InvalidBoundaryException");
    }


    public InvalidBoundaryException(String c) {
        super(c);
    }


    public InvalidBoundaryException(String c, Throwable t) {
        super(c, t);
    }


    public InvalidBoundaryException(Throwable t) {
        super("InvalidBoundaryException", t);
    }

}
