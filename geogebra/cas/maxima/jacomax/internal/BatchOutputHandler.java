/* $Id: BatchOutputHandler.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax.internal;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Handler used when running Maxima in batch mode.
 * 
 * @see MaximaBatchProcessImpl
 *
 * @author  David McKain
 * @version $Revision: 5 $
 */
public class BatchOutputHandler implements MaximaOutputHandler {
    
    private final OutputStream batchOutputStream;
    
    public BatchOutputHandler(OutputStream outputStream) {
        this.batchOutputStream = outputStream;
    }
    
    public void callStarting() {
        /* (Nothing to do) */
    }
    
    public boolean handleOutput(byte[] maximaOutputBuffer, int bytesReadFromMaxima, boolean outputFinished) throws IOException {
        batchOutputStream.write(maximaOutputBuffer, 0, bytesReadFromMaxima);
        return outputFinished;
    }
    
    public void callFinished() throws IOException {
        batchOutputStream.close();
    }
}