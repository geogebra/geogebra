/* $Id: MaximaTimeoutException.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax;

/**
 * Exception thrown when Maxima takes too long to complete an operation. Possible
 * reasons for this might be:
 * <ul>
 *   <li>The command genuinely took too long to run</li>
 *   <li>The command was ill-formed and left Maxima expecting more input</li>
 *   <li>The command resulted in Maxima expecting interactive communication</li>
 * </ul>
 * 
 * @author  David McKain
 * @version $Revision: 5 $
 */
public final class MaximaTimeoutException extends Exception {
    
    private static final long serialVersionUID = 6077105489157609103L;

    private final int timeoutSeconds;

    public MaximaTimeoutException(final int timeoutSeconds) {
        super("Timeout of " + timeoutSeconds + "s exceeded waiting for response from Maxima");
        this.timeoutSeconds = timeoutSeconds;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }
}
