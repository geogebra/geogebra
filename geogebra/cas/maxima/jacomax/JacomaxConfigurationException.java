/* $Id: JacomaxConfigurationException.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax;

/**
 * Runtime Exception thrown to indicate a problem with the configuration of
 * Maxima, such as a bad path or environment.
 * 
 * @author  David McKain
 * @version $Revision: 5 $
 */
public final class JacomaxConfigurationException extends JacomaxRuntimeException {

    private static final long serialVersionUID = 7100573731627419599L;

    public JacomaxConfigurationException(String message) {
        super(message);
    }

    public JacomaxConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
