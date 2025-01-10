// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class to read bytes and pairs of bytes in both little and big endian order.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: ByteOrderInputStream.java,v 1.3 2008-05-04 12:21:49 murkle Exp
 *          $
 */
public class ByteOrderInputStream extends BitInputStream implements DataInput {

	protected boolean little;

	/**
	 * Create a byte order (big-endian) input stream from given stream.
	 * 
	 * @param in
	 *            stream to read from
	 */
	public ByteOrderInputStream(InputStream in) {
		this(in, false);
	}

	/**
	 * Create a byte order input stream from given stream.
	 * 
	 * @param in
	 *            stream to read from
	 * @param littleEndian
	 *            true if stream should be little endian.
	 */
	public ByteOrderInputStream(InputStream in, boolean littleEndian) {
		super(in);
		little = littleEndian;
	}

	@Override
	public void readFully(byte b[]) throws IOException {

		readFully(b, 0, b.length);
	}

	@Override
	public void readFully(byte b[], int off, int len) throws IOException {

		if (len < 0) {
			throw new IndexOutOfBoundsException();
		}
		int n = 0;
		while (n < len) {
			int count = read(b, off + n, len - n);
			if (count < 0) {
				throw new EOFException();
			}
			n += count;
		}
	}

	@Override
	public int skipBytes(int n) throws IOException {
		int total = 0;
		int cur = 0;

		while ((total < n) && ((cur = (int) skip(n - total)) > 0)) {
			total += cur;
		}

		return total;
	}

	@Override
	public boolean readBoolean() throws IOException {
		int b = readUnsignedByte();
		return (b != 0);
	}

	@Override
	public char readChar() throws IOException {
		int b1 = readUnsignedByte();
		int b2 = readUnsignedByte();
		return (little) ? (char) ((b1 << 8) + b2) : (char) ((b2 << 8) + b1);
	}

	/**
	 * Read a signed byte.
	 */
	@Override
	public byte readByte() throws IOException {

		byteAlign();
		int b = read();
		if (b < 0) {
			throw new EOFException();
		}
		return (byte) b;
	}

	/**
	 * Read n bytes and return in byte array.
	 * 
	 * @param n
	 *            number of bytes to read
	 * @return byte array
	 * @throws IOException
	 *             if read fails
	 */
	public byte[] readByte(int n) throws IOException {

		byteAlign();
		byte[] bytes = new byte[n];
		for (int i = 0; i < n; i++) {
			int b = read();
			if (b < 0) {
				throw new EOFException();
			}
			bytes[i] = (byte) b;
		}
		return bytes;
	}

	/**
	 * Read an unsigned byte.
	 */
	@Override
	public int readUnsignedByte() throws IOException {

		byteAlign();
		int ub = read();
		if (ub < 0) {
			throw new EOFException();
		}
		return ub;
	}

	/**
	 * Read n unsigned bytes and return in int array.
	 * 
	 * @param n
	 *            number of bytes to read
	 * @return int array
	 * @throws IOException
	 *             if read fails
	 */
	public int[] readUnsignedByte(int n) throws IOException {

		byteAlign();
		int[] bytes = new int[n];
		for (int i = 0; i < n; i++) {
			int ub = read();
			if (ub < 0) {
				throw new EOFException();
			}
			bytes[i] = ub;
		}
		return bytes;
	}

	/**
	 * Read a signed short.
	 */
	@Override
	public short readShort() throws IOException {

		int i1 = readUnsignedByte();
		int i2 = readUnsignedByte();
		return (little) ? (short) ((i2 << 8) + i1) : (short) ((i1 << 8) + i2);
	}

	/**
	 * Read n shorts and return in short array
	 * 
	 * @param n
	 *            number of shorts to read
	 * @return short array
	 * @throws IOException
	 *             if read fails
	 */
	public short[] readShort(int n) throws IOException {

		short[] shorts = new short[n];
		for (int i = 0; i < n; i++) {
			shorts[i] = readShort();
		}
		return shorts;
	}

	/**
	 * Read an unsigned short.
	 */
	@Override
	public int readUnsignedShort() throws IOException {

		byteAlign();
		int i1 = readUnsignedByte();
		int i2 = readUnsignedByte();
		return (little) ? (i2 << 8) + i1 : (i1 << 8) + i2;
	}

	/**
	 * Read n unsigned shorts and return in int array
	 * 
	 * @param n
	 *            number of shorts to read
	 * @return int array
	 * @throws IOException
	 *             if read fails
	 */
	public int[] readUnsignedShort(int n) throws IOException {

		int[] shorts = new int[n];
		for (int i = 0; i < n; i++) {
			shorts[i] = readUnsignedShort();
		}
		return shorts;
	}

	/**
	 * Read a signed integer.
	 */
	@Override
	public int readInt() throws IOException {

		int i1 = readUnsignedByte();
		int i2 = readUnsignedByte();
		int i3 = readUnsignedByte();
		int i4 = readUnsignedByte();
		return (little) ? (i4 << 24) + (i3 << 16) + (i2 << 8) + i1
				: (i1 << 24) + (i2 << 16) + (i3 << 8) + i4;
	}

	/**
	 * Read n ints and return in int array.
	 * 
	 * @param n
	 *            number of ints to read
	 * @return int array
	 * @throws IOException
	 *             if read fails
	 */
	public int[] readInt(int n) throws IOException {

		int[] ints = new int[n];
		for (int i = 0; i < n; i++) {
			ints[i] = readInt();
		}
		return ints;
	}

	/**
	 * Read an unsigned integer.
	 * 
	 * @return long
	 * @throws IOException
	 *             if read fails
	 */
	public long readUnsignedInt() throws IOException {

		long i1 = readUnsignedByte();
		long i2 = readUnsignedByte();
		long i3 = readUnsignedByte();
		long i4 = readUnsignedByte();
		return (little) ? (i4 << 24) + (i3 << 16) + (i2 << 8) + i1
				: (i1 << 24) + (i2 << 16) + (i3 << 8) + i4;
	}

	/**
	 * Read n unsigned ints and return in long array.
	 * 
	 * @param n
	 *            number of ints to read
	 * @return long array
	 * @throws IOException
	 *             if read fails
	 */
	public long[] readUnsignedInt(int n) throws IOException {

		long[] ints = new long[n];
		for (int i = 0; i < n; i++) {
			ints[i] = readUnsignedInt();
		}
		return ints;
	}

	@Override
	public long readLong() throws IOException {
		long i1 = readInt();
		long i2 = readInt();
		return (little) ? (i2 << 32) + (i1 & 0xFFFFFFFFL)
				: (i1 << 32) + (i2 & 0xFFFFFFFFL);
	}

	@Override
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	/**
	 */
	@Override
	public String readLine() throws IOException {
		throw new IOException(
				"ByteOrderInputStream.readLine() is not implemented.");
	}

	/**
	 * Read a string (UTF).
	 * 
	 * @return string
	 * @throws IOException
	 *             if read fails
	 */
	public String readString() throws IOException {
		return readUTF();
	}

	@Override
	public String readUTF() throws IOException {
		return DataInputStream.readUTF(this);
	}

	/**
	 * Read an ascii-z (0 terminated c-string).
	 * 
	 * @return string
	 * @throws IOException
	 *             if read fails
	 */
	public String readAsciiZString() throws IOException {
		StringBuffer buffer = new StringBuffer();
		char c = (char) readUnsignedByte();
		while (c != 0) {
			buffer.append(c);
			c = (char) readUnsignedByte();
		}
		return buffer.toString();
	}
}
