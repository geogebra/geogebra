// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The NoCloseOutputStream ignores the close so that one can keep writing to the
 * underlying stream.
 * 
 * @author Mark Donszelmann
 * @version $Id: NoCloseOutputStream.java,v 1.3 2008-05-04 12:21:45 murkle Exp $
 */
public class NoCloseOutputStream extends BufferedOutputStream {

	/**
	 * Creates a No Close output stream.
	 * 
	 * @param stream
	 *            stream to write to
	 */
	public NoCloseOutputStream(OutputStream stream) {
		super(stream);
	}

	/**
	 * Creates a No Close output stream.
	 * 
	 * @param stream
	 *            stream to write to
	 * @param size
	 *            buffer size
	 */
	public NoCloseOutputStream(OutputStream stream, int size) {
		super(stream, size);
	}

	@Override
	public void close() throws IOException {
		flush();
	}

	/**
	 * Closes the stream (the close method is ignored).
	 * 
	 * @throws IOException
	 *             if the close fails
	 */
	public void realClose() throws IOException {
		super.close();
	}
}
