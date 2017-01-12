// Copyright 2003, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * The WriterOutputStream makes a Writer look like an OutputStream.
 * 
 * @author Mark Donszelmann
 * @version $Id: WriterOutputStream.java,v 1.3 2008-05-04 12:22:15 murkle Exp $
 */
public class WriterOutputStream extends OutputStream {

	private Writer writer;

	/**
	 * Create an Output Stream from given Writer.
	 * 
	 * @param writer
	 *            writer to write to
	 */
	public WriterOutputStream(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void write(int b) throws IOException {
		writer.write(b & 0xFF);
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			writer.write(b[off + i]);
		}
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}
}
