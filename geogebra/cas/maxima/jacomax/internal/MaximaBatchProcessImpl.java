/* $Id: MaximaBatchProcessImpl.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax.internal;

import geogebra.cas.maxima.jacomax.JacomaxRuntimeException;
import geogebra.cas.maxima.jacomax.MaximaTimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;




/**
 * Internal implementation of batch process functionality.
 * 
 * @author  David McKain
 * @version $Revision: 5 $
 */
public final class MaximaBatchProcessImpl {

    private static final DummyLogger logger = new DummyLogger();//LoggerFactory.getLogger(MaximaBatchProcessImpl.class);
    
    private final MaximaProcessController maximaProcessController;
    private final InputStream batchInputStream;
    private final OutputStream batchOutputStream;
    
    public MaximaBatchProcessImpl(MaximaProcessController maximaProcessController,
            InputStream batchInputStream, OutputStream batchOutputStream) {
        this.maximaProcessController = maximaProcessController;
        this.batchInputStream = batchInputStream;
        this.batchOutputStream = batchOutputStream;
    }
    
    public int run(int timeout) throws MaximaTimeoutException {
        logger.info("Running Maxima process in batch mode");
        BatchOutputHandler writerOutputHandler = new BatchOutputHandler(batchOutputStream);
        int returnCode;
        try {
            maximaProcessController.doMaximaCall(batchInputStream, true, writerOutputHandler, timeout);
        }
        finally {
            try {
                returnCode = maximaProcessController.terminate();
            }
            finally {
                try {
                    batchOutputStream.close();
                }
                catch (IOException e) {
                    throw new JacomaxRuntimeException("Could not close batchOutputStream", e);
                }
            }
        }
        return returnCode;
    }
}
