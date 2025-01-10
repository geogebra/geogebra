// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.InputStream;

import org.geogebra.common.util.debug.Log;

/**
 * The input buffer can be limited to less than the number of bytes of the
 * underlying buffer. Only one real input stream exists, which is where the
 * reads take place. A buffer is limited by some length. If more is read, -1 is
 * returned. Multiple limits can be set by calling pushBuffer. If bytes are left
 * in the buffer when popBuffer is called, they are returned in an array.
 * Otherwise null is returned.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: ByteCountInputStream.java,v 1.6 2008-10-23 19:04:01 hohenwarter
 *          Exp $
 */
public class ByteCountInputStream extends ByteOrderInputStream {

	private int index;

	private int[] size;

	private long len;

	/**
	 * Create a Byte Count input stream from given stream
	 * 
	 * @param in
	 *            stream to read from
	 * @param littleEndian
	 *            true if stream should be little endian
	 * @param stackDepth
	 *            maximum number of buffers used while reading
	 */
	public ByteCountInputStream(InputStream in, boolean littleEndian,
			int stackDepth) {
		super(in, littleEndian);
		size = new int[stackDepth];
		index = -1;
		len = 0;
	}

	@Override
	public int read() throws IOException {
		// original stream
		if (index == -1) {
			len++;
			return super.read();
		}

		// end of buffer
		if (size[index] <= 0) {
			return -1;
		}

		// decrease counter
		size[index]--;

		len++;
		return super.read();
	}

	/**
	 * Push the current buffer to the stack
	 * 
	 * @param len
	 *            number of bytes that can be read from the current buffer
	 */
	public void pushBuffer(int len) {
		if (index >= size.length - 1) {
			Log.debug(
					"ByteCountInputStream: trying to push more buffers than stackDepth: "
							+ size.length);
			return;
		}

		if (index >= 0) {
			if (size[index] < len) {
				Log.debug("ByteCountInputStream: trying to set a length: " + len
						+ ", longer than the underlying buffer: "
						+ size[index]);
				return;
			}
			size[index] -= len;
		}
		index++;
		size[index] = len;
	}

	/**
	 * Pops the buffer from the stack and returns leftover bytes in a byte array
	 * 
	 * @return null if buffer was completely read. Otherwise rest of buffer is
	 *         read and returned.
	 * @throws IOException
	 *             if read fails
	 */
	public byte[] popBuffer() throws IOException {
		if (index >= 0) {
			int len = size[index];
			if (len > 0) {
				return readByte(len);
			} else if (len < 0) {
				Log.debug("ByteCountInputStream: Internal Error");
			}
			index--;
		}
		return null;
	}

	/**
	 * @return number of bytes that can be read from the current buffer
	 */
	public long getLength() {
		return (index >= 0) ? size[index] : len;
	}
}
