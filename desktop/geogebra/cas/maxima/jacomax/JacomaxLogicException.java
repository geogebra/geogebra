/* $Id: JacomaxLogicException.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax;

/**
 * Runtime Exception thrown if a logic problem occurs in the Maxima Connector code, indicating
 * a bug within this code that will need looked at and fixed!
 * 
 * @author  David McKain
 * @version $Revision: 5 $
 */
public class JacomaxLogicException extends JacomaxRuntimeException {

    private static final long serialVersionUID = 7100573731627419599L;

    public JacomaxLogicException(String message) {
        super(message);
    }

    public JacomaxLogicException(Throwable cause) {
        super(cause);
    }

    public JacomaxLogicException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static JacomaxLogicException unexpectedException(Throwable cause) {
        return new JacomaxLogicException("Unexpected Exception", cause);
    }
}
