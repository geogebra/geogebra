package org.geogebra.web.web.util;

import java.io.OutputStream;

/**
 * GWT replacement for {@link java.io.ByteArrayOutputStream}.
 */
public class GwtByteArrayOutputStream extends OutputStream {

	private byte[] buf;
	private int count;

	public GwtByteArrayOutputStream(int initialCapacity) {
		buf = new byte[initialCapacity];
	}

	// @Override is not allowed here because only the default constructor
	// of java.io.OutputStream is JRE white-listed in GWT
	public void write(int b) {
		int newcount = count + 1;
		if (newcount > buf.length) {
			buf = copyOf(buf, Math.max(buf.length << 1, newcount));
		}
		buf[count] = (byte) b;
		count = newcount;
	}

	public byte[] toByteArray() {
		return copyOf(buf, count);
	}

	// Private Helper Methods
	// Copied from java.lang.Arrays
	private static byte[] copyOf(byte[] original, int newLength) {
		byte[] copy = new byte[newLength];
		System.arraycopy(original, 0, copy, 0,
		        Math.min(original.length, newLength));
		return copy;
	}

}
