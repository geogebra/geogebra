/* $Id: MaximaOutputHandler.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax.internal;

import java.io.IOException;

/**
 * Internal interface for handling chunks of data returned via Maxima's STDOUT.
 * 
 * @see InteractiveCallOutputHandler
 * @see InteractiveStartupOutputHandler
 * @see BatchOutputHandler
 *
 * @author  David McKain
 * @version $Revision: 5 $
 */
public interface MaximaOutputHandler {
    
    /** This is called when a Maxima call is about to commence */
    void callStarting() throws IOException;
    
    /**
     * This is called when a chunk of output has been read from the Maxima process.
     * Implementations should do whatever is required with this and return true
     * if no more Maxima output is expected, false otherwise. 
     * 
     * @param maximaOutputBuffer buffer containing bytes read from Maxima. The contents of
     *   this buffer will change once this method has completed. The buffer will
     *   be filled from index zero until index bytesReadFromMaxima
     * @param bytesReadFromMaxima number of bytes read from Maxima, which will be positive
     *   and less than or equal to the size of maximaOutputBuffer. 
     * @param isMaximaOutputEof true if this is the EOF of Maxima output, false otherwise.
     */
    boolean handleOutput(byte[] maximaOutputBuffer, int bytesReadFromMaxima, boolean isMaximaOutputEof) 
        throws IOException;
    
    /** This is called when a Maxima call has completed (or failed) */
    void callFinished() throws IOException;
    
}