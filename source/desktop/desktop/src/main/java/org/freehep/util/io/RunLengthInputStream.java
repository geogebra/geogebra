// Copyright 2001-2005, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * The RunLengthStream decodes Run Length encoding. The exact definition of Run
 * Length encoding can be found in the PostScript Language Reference (3rd ed.)
 * chapter 3.13.3.
 * 
 * IMPORTANT: inherits from InputStream rather than FilterInputStream so that
 * the correct read(byte[], int, int) method is used.
 * 
 * @author Mark Donszelmann
 * @version $Id: RunLengthInputStream.java,v 1.3 2008-05-04 12:21:33 murkle Exp
 *          $
 */
public class RunLengthInputStream extends InputStream implements RunLength {

	private int[] buffer = new int[LENGTH];

	private int index;

	private int count;

	private InputStream in;

	/**
	 * Create a Run Length input stream
	 * 
	 * @param input
	 *            stream to read from
	 */
	public RunLengthInputStream(InputStream input) {
		super();
		in = input;
		index = 0;
		count = 0;
	}

	@Override
	public int read() throws IOException {

		if ((index >= count) || (index > 128)) {
			if (!fillBuffer()) {
				return -1;
			}
		}

		int b = buffer[index];
		index++;
		return b & 0x00FF;
	}

	private boolean fillBuffer() throws IOException {
		count = in.read();

		if (end(count)) {
			return false;
		}

		if (count < 128) {
			// buffered
			count++;
			for (int i = 0; i < count; i++) {
				buffer[i] = in.read();
				if (end(buffer[i])) {
					return false;
				}
			}
		} else {
			// counted
			count = 257 - count;
			int b = in.read();
			if (end(b)) {
				return false;
			}

			for (int i = 0; i < count; i++) {
				buffer[i] = b;
			}
		}
		index = 0;
		return true;
	}

	private static boolean end(int b) {
		if ((b < 0) || (b == EOD)) {
			return true;
		}
		return false;
	}
}
