// Copyright 2003, FreeHEP.
package org.freehep.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * The NoCloseReader ignores the close so that one can keep reading from the
 * underlying stream.
 * 
 * @author Mark Donszelmann
 * @version $Id: NoCloseReader.java,v 1.3 2008-05-04 12:21:29 murkle Exp $
 */
public class NoCloseReader extends BufferedReader {

	/**
	 * Creates a No Close Reader.
	 * 
	 * @param reader
	 *            reader to read from
	 */
	public NoCloseReader(Reader reader) {
		super(reader);
	}

	/**
	 * Creates a No Close Reader.
	 * 
	 * @param reader
	 *            reader to read from
	 * @param size
	 *            buffer size
	 */
	public NoCloseReader(Reader reader, int size) {
		super(reader, size);
	}

	@Override
	public void close() throws IOException {
	}

	/**
	 * Closes the reader (close is ignored).
	 * 
	 * @throws IOException
	 *             if the close fails
	 */
	public void realClose() throws IOException {
		super.close();
	}
}
