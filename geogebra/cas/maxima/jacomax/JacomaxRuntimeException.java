/* $Id: JacomaxRuntimeException.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax;

/**
 * Generic runtime Exception thrown to indicate an unexpected problem
 * encountered when communicating with Maxima.
 * <p>
 * This Exception is unchecked as there's nothing that can reasonably be done
 * to recover from this so ought to bubble right up to a handler near the "top"
 * of your application.
 * 
 * @see JacomaxConfigurationException
 * 
 * @author  David McKain
 * @version $Revision: 5 $
 */
public class JacomaxRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 7100573731627419599L;

    public JacomaxRuntimeException(String message) {
        super(message);
    }

    public JacomaxRuntimeException(Throwable cause) {
        super(cause);
    }

    public JacomaxRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
