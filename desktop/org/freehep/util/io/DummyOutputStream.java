package org.freehep.util.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Equivalent to writing to /dev/nul
 * 
 * @author tonyj
 * @version $Id: DummyOutputStream.java,v 1.3 2008-05-04 12:21:00 murkle Exp $
 */
public class DummyOutputStream extends OutputStream {
    /**
     * Creates a Dummy output steram.
     */
    public DummyOutputStream() {
    }

    public void write(int b) throws IOException {
    }

    public void write(byte[] b) throws IOException {
    }

    public void write(byte[] b, int off, int len) throws IOException {
    }
}
