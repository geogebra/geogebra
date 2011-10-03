/* $Id: MaximaProcessTerminatedException.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax;

/**
 * This (unchecked) Exception is thrown if you attempt to execute a Maxima call using
 * {@link MaximaInteractiveProcess} after the process has been terminated.
 * <p>
 * You should normally ensure this never happens by using your {@link MaximaInteractiveProcess}
 * correctly.
 * 
 * @author  David McKain
 * @version $Revision: 5 $
 */
public final class MaximaProcessTerminatedException extends IllegalStateException {
    
    private static final long serialVersionUID = -3471501531982835288L;
    
    public MaximaProcessTerminatedException() {
        super("Maxima process has been terminated");
    }
}
