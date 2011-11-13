/* $Id: InteractiveOutputHandler.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax.internal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

/**
 * Base handler used by {@link MaximaInteractiveProcessImpl}
 *
 * @author  David McKain
 * @version $Revision: 5 $
 */
public abstract class InteractiveOutputHandler implements MaximaOutputHandler {
    
    private final CharsetDecoder maximaOutputDecoder;
    private final ByteBuffer decodingByteBuffer;
    private final CharBuffer decodingCharBuffer;
    
    public InteractiveOutputHandler(final ByteBuffer decodingByteBuffer,
            final CharBuffer decodingCharBuffer, final CharsetDecoder charsetDecoder) {
        this.decodingByteBuffer = decodingByteBuffer;
        this.decodingCharBuffer = decodingCharBuffer;
        this.maximaOutputDecoder = charsetDecoder;
    }
    
    public void callStarting() {
        decodingByteBuffer.clear();
        decodingCharBuffer.clear();
        maximaOutputDecoder.reset();
    }
    
    public boolean handleOutput(byte[] maximaOutputBuffer, int bytesReadFromMaxima, boolean isMaximaOutputEof)
            throws IOException {
        int stdoutBufferPos = 0;
        int stdoutBufferRemaining = bytesReadFromMaxima;
        int outputChunkSize;
        
        /* Iterate over input, filling lineByteBuffer as much as possible each time */
        while (stdoutBufferPos < bytesReadFromMaxima) {
            outputChunkSize = Math.min(decodingByteBuffer.remaining(), stdoutBufferRemaining);
            decodingByteBuffer.put(maximaOutputBuffer, stdoutBufferPos, outputChunkSize);
            stdoutBufferPos += outputChunkSize;
            stdoutBufferRemaining -= outputChunkSize;
            
            decodeByteBuffer(false);
        }
        boolean inputPromptReached = isNextInputPromptReached();
        if (isMaximaOutputEof && !inputPromptReached) {
            throw new IllegalStateException("Maxima output ended before next input prompt");
        }
        return inputPromptReached;
    }
    
    private void decodeByteBuffer(boolean endOfInput) throws IOException {
        CoderResult coderResult;
        while (true) {
            decodingByteBuffer.flip();
            coderResult = maximaOutputDecoder.decode(decodingByteBuffer, decodingCharBuffer, endOfInput);
            if (coderResult.isError()) {
                coderResult.throwException();
            }
            /* Handle decoded characters */
            decodingCharBuffer.flip();
            handleDecodedOutputChunk(decodingCharBuffer);
            decodingCharBuffer.clear();
            
            /* Compact any unencoded bytes from end of buffer */
            decodingByteBuffer.compact();
            if (coderResult.isUnderflow()) {
                /* We need more bytes */
                break;
            }
        }
    }
    
    public void callFinished() throws IOException {
        finishDecoding();
    }
    
    private void finishDecoding() throws IOException {
        decodeByteBuffer(true);
    }
    
    protected abstract void handleDecodedOutputChunk(CharBuffer buffer) throws IOException;
    
    protected abstract boolean isNextInputPromptReached();
}