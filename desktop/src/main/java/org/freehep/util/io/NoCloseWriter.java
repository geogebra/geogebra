// Copyright 2003, FreeHEP.
package org.freehep.util.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * The NoCloseWriter ignores the close so that one can keep writing to the
 * underlying stream.
 * 
 * @author Mark Donszelmann
 * @version $Id: NoCloseWriter.java,v 1.3 2008-05-04 12:21:24 murkle Exp $
 */
public class NoCloseWriter extends BufferedWriter {

	/**
	 * Creates a No Close Writer
	 * 
	 * @param writer
	 *            writer to write to
	 */
	public NoCloseWriter(Writer writer) {
		super(writer);
	}

	/**
	 * Creates a No Close Writer
	 * 
	 * @param writer
	 *            writer to write to
	 * @param size
	 *            buffer size
	 */
	public NoCloseWriter(Writer writer, int size) {
		super(writer, size);
	}

	@Override
	public void close() throws IOException {
		flush();
	}

	/**
	 * Closes the writer (close is ignored).
	 * 
	 * @throws IOException
	 *             if the close fails
	 */
	public void realClose() throws IOException {
		super.close();
	}
}
