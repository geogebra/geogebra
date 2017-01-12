// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The CountedByteOutputStream counts the number of bytes written.
 * 
 * @author Mark Donszelmann
 * @version $Id: CountedByteOutputStream.java,v 1.3 2008-05-04 12:21:04 murkle
 *          Exp $
 */
public class CountedByteOutputStream extends FilterOutputStream {

	private int count;

	/**
	 * Creates a Counted Bytes output stream from the given stream.
	 * 
	 * @param out
	 *            stream to write to
	 */
	public CountedByteOutputStream(OutputStream out) {
		super(out);
		count = 0;
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
		count++;
	}

	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
		count += b.length;
	}

	@Override
	public void write(byte[] b, int offset, int len) throws IOException {
		out.write(b, offset, len);
		count += len;
	}

	/**
	 * @return number of bytes written.
	 */
	public int getCount() {
		return count;
	}
}
