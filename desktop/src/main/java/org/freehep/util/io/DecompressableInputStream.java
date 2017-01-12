// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

/**
 * Special stream that can be used to read uncompressed first and compressed
 * from a certain byte.
 * 
 * IMPORTANT: inherits from InputStream rather than FilterInputStream so that
 * the correct read(byte[], int, int) method is used.
 * 
 * @author Mark Donszelmann
 * @version $Id: DecompressableInputStream.java,v 1.3 2008-05-04 12:21:17 murkle
 *          Exp $
 */
public class DecompressableInputStream extends InputStream {

	private boolean decompress;

	private InflaterInputStream iis;

	private InputStream in;

	/**
	 * Creates a Decompressable input stream from given stream.
	 * 
	 * @param input
	 *            stream to read from.
	 */
	public DecompressableInputStream(InputStream input) {
		super();
		in = input;
		decompress = false;
	}

	@Override
	public int read() throws IOException {
		return (decompress) ? iis.read() : in.read();
	}

	@Override
	public long skip(long n) throws IOException {
		return (decompress) ? iis.skip(n) : in.skip(n);
	}

	/**
	 * Start reading in compressed mode from the next byte.
	 * 
	 * @throws IOException
	 *             if read fails.
	 */
	public void startDecompressing() throws IOException {
		decompress = true;
		iis = new InflaterInputStream(in);
	}
}
