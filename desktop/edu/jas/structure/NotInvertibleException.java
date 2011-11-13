/*
 * $Id: NotInvertibleException.java 1663 2008-02-05 17:32:07Z kredel $
 */

package edu.jas.structure;


/**
 * NotInvertibleException class.
 * Runtime Exception to be thrown for not invertible monoid elements.
 * @author Heinz Kredel
 */

public class NotInvertibleException extends RuntimeException {


    public NotInvertibleException() {
     super("NotInvertibleException");
    }


    public NotInvertibleException(String c) {
     super(c);
    }


    public NotInvertibleException(String c, Throwable t) {
     super(c,t);
    }


    public NotInvertibleException(Throwable t) {
     super("NotInvertibleException",t);
    }

}
