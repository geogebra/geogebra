/* $Id: InteractiveStartupOutputHandler.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax.internal;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;

/**
 * Handler for Maxima output generated during interactive startup.
 *
 * @author  David McKain
 * @version $Revision: 5 $
 */
public class InteractiveStartupOutputHandler extends InteractiveOutputHandler {
    
    private final StringBuilder lastOutputLineBuilder;
    
    public InteractiveStartupOutputHandler(final ByteBuffer decodingByteBuffer,
            final CharBuffer decodingCharBuffer, final CharsetDecoder charsetDecoder) {
        super(decodingByteBuffer, decodingCharBuffer, charsetDecoder);
        this.lastOutputLineBuilder = new StringBuilder();
    }
    
    @Override
    public void callStarting() {
        super.callStarting();
    }
    
    @Override
    protected void handleDecodedOutputChunk(CharBuffer buffer) {
        /* Build up current line so we can check when we're at the required terminator */
        while (buffer.hasRemaining()) {
            char c = buffer.get();
            if (c=='\n' || c=='\r') {
                lastOutputLineBuilder.setLength(0);
            }
            else {
                lastOutputLineBuilder.append(c);
            }
        }
    }
    
    @Override
    protected boolean isNextInputPromptReached() {
        return "(%i1) ".equals(lastOutputLineBuilder.toString());
    }
}