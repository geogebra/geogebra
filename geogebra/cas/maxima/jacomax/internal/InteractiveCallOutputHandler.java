/* $Id: InteractiveCallOutputHandler.java 20 2010-04-15 13:33:05Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;



/**
 * Handler for the outputs of each call made with {@link MaximaInteractiveProcessImpl}
 *
 * @author  David McKain
 * @version $Revision: 20 $
 */
public class InteractiveCallOutputHandler extends InteractiveOutputHandler {
    
    private static final DummyLogger logger = new DummyLogger();//LoggerFactory.getLogger(InteractiveCallOutputHandler.class);
    
    private final Appendable outputBuilder;
    
    private final String terminator;
    
    private final StringBuilder lastOutputLineBuilder;
    
    /** This flag gets set at the end of the line containing the required terminator */
    private boolean lineContainingTerminatorEnded;
    
    public InteractiveCallOutputHandler(final Appendable outputBuilder,
            final String terminator, final ByteBuffer decodingByteBuffer,
            final CharBuffer decodingCharBuffer, final CharsetDecoder charsetDecoder) {
        super(decodingByteBuffer, decodingCharBuffer, charsetDecoder);
        this.lastOutputLineBuilder = new StringBuilder();
        this.outputBuilder = outputBuilder;
        this.terminator = terminator;
    }
    
    @Override
    public void callStarting() {
        super.callStarting();
        lastOutputLineBuilder.setLength(0);
        lineContainingTerminatorEnded = false;
    }
    
    @Override
    protected void handleDecodedOutputChunk(CharBuffer charBuffer) throws IOException {
        /* Build up current line so we can check when we're at the required terminator */
        while (charBuffer.hasRemaining()) {
            char c = charBuffer.get();
            
            /* NB: On the Windows/GCL platform that I've tested, Maxima terminates lines
             * with a single newline, rather than the platform default.
             * 
             * So, the logic follows basically ignores carriage returns (which I've never
             * actually seen output) and uses newlines to indicate the end of a line.
             */
            if (c=='\n') {
                /* See if we have received the required terminator on this line */
                int terminatorPosition = lastOutputLineBuilder.indexOf(terminator);
                if (terminatorPosition != -1) {
                    /* Found required input prompt, so terminate */
                    logger.trace("Found terminator; will stop reading on next line, which will be input prompt");
                    lineContainingTerminatorEnded = true;
                    
                    /* (Record anything that came just before the terminator) */
                    if (outputBuilder!=null && terminatorPosition > 0) {
                        outputBuilder.append(lastOutputLineBuilder, 0, terminatorPosition);
                    }
                }
                else if (outputBuilder!=null) {
                    /* Add line just read to output (if being built) */
                    outputBuilder.append(lastOutputLineBuilder).append(c);
                }
                /* Reset for reading next line in */
                lastOutputLineBuilder.setLength(0);
            }
            else if (c=='\r') {
                continue;
            }
            else {
                lastOutputLineBuilder.append(c);
            }
        }
    }
    
    @Override
    public boolean isNextInputPromptReached() {
        return lineContainingTerminatorEnded && lastOutputLineBuilder.toString().endsWith(") ");
    }
}