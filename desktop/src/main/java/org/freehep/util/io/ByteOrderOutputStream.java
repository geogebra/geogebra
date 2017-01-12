// Copyright 2001-2005, FreeHEP.
package org.freehep.util.io;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

/**
 * Class to write bytes and pairs of bytes in both little and big endian order.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: ByteOrderOutputStream.java,v 1.3 2008-05-04 12:20:56 murkle Exp
 *          $
 */
public class ByteOrderOutputStream extends BitOutputStream
		implements DataOutput {

	protected boolean little;

	protected int written;

	/**
	 * Create a (Big Endian) Byte Order output stream from given stream
	 * 
	 * @param out
	 *            stream to write to
	 */
	public ByteOrderOutputStream(OutputStream out) {
		this(out, false);
	}

	/**
	 * Create a Byte Order output stream from the given stream
	 * 
	 * @param out
	 *            stream to write to
	 * @param littleEndian
	 *            true if stream should be little endian
	 */
	public ByteOrderOutputStream(OutputStream out, boolean littleEndian) {
		super(out);
		little = littleEndian;
		written = 0;
	}

	/**
	 * @return number of written bytes
	 * @throws IOException
	 *             if write fails FIXME, should this throw an exception ?
	 */
	public int size() throws IOException {
		return written;
	}

	@Override
	public synchronized void write(int b) throws IOException {
		super.write(b);
		written++;
	}

	@Override
	public void writeBoolean(boolean b) throws IOException {
		if (b) {
			write(1);
		} else {
			write(0);
		}
	}

	@Override
	public void writeChar(int c) throws IOException {
		if (little) {
			write(c & 0xFF);
			write((c >>> 8) & 0xFF);
		} else {
			write((c >>> 8) & 0xFF);
			write(c & 0xFF);
		}
	}

	/**
	 * Write a signed byte.
	 */
	@Override
	public void writeByte(int b) throws IOException {

		byteAlign();
		write(b);
	}

	/**
	 * Writes array of bytes
	 * 
	 * @param bytes
	 *            byte array to be written
	 * @throws IOException
	 *             if write fails
	 */
	public void writeByte(byte[] bytes) throws IOException {

		byteAlign();
		for (int i = 0; i < bytes.length; i++) {
			write(bytes[i]);
		}
	}

	/**
	 * Write an unsigned byte.
	 * 
	 * @param ub
	 *            byte to write
	 * @throws IOException
	 *             if write fails
	 */
	public void writeUnsignedByte(int ub) throws IOException {

		byteAlign();
		write(ub);
	}

	/**
	 * Write an array of unsigned bytes.
	 * 
	 * @param bytes
	 *            int array to write as bytes
	 * @throws IOException
	 *             if write fails
	 */
	public void writeUnsignedByte(int[] bytes) throws IOException {

		byteAlign();
		for (int i = 0; i < bytes.length; i++) {
			write(bytes[i]);
		}
	}

	/**
	 * Write a signed short.
	 */
	@Override
	public void writeShort(int s) throws IOException {

		byteAlign();
		if (little) {
			write(s & 0xFF);
			write((s >>> 8) & 0xFF);
		} else {
			write((s >>> 8) & 0xFF);
			write(s & 0xFF);
		}
	}

	/**
	 * Write an array of shorts.
	 * 
	 * @param shorts
	 *            short array to write
	 * @throws IOException
	 *             if write fails
	 */
	public void writeShort(short[] shorts) throws IOException {

		for (int i = 0; i < shorts.length; i++) {
			writeShort(shorts[i]);
		}
	}

	/**
	 * Write an unsigned short.
	 * 
	 * @param s
	 *            int to write as unsigned short
	 * @throws IOException
	 *             if write fails
	 */
	public void writeUnsignedShort(int s) throws IOException {

		byteAlign();
		if (little) {
			write(s & 0xFF);
			write((s >>> 8) & 0xFF);
		} else {
			write((s >>> 8) & 0xFF);
			write(s & 0xFF);
		}
	}

	/**
	 * Write an array of unsigned shorts.
	 * 
	 * @param shorts
	 *            int array to write as unsigned shorts
	 * @throws IOException
	 *             if write fails
	 */
	public void writeUnsignedShort(int[] shorts) throws IOException {

		for (int i = 0; i < shorts.length; i++) {
			writeUnsignedShort(shorts[i]);
		}
	}

	/**
	 * Write a signed integer.
	 */
	@Override
	public void writeInt(int i) throws IOException {

		if (little) {
			write(i & 0xFF);
			write((i >>> 8) & 0xFF);
			write((i >>> 16) & 0xFF);
			write((i >>> 24) & 0xFF);
		} else {
			write((i >>> 24) & 0xFF);
			write((i >>> 16) & 0xFF);
			write((i >>> 8) & 0xFF);
			write(i & 0xFF);
		}
	}

	/**
	 * Write an array of ints
	 * 
	 * @param ints
	 *            int array to write
	 * @throws IOException
	 *             if write fails
	 */
	public void writeInt(int[] ints) throws IOException {

		for (int i = 0; i < ints.length; i++) {
			writeInt(ints[i]);
		}
	}

	/**
	 * Write an unsigned integer.
	 * 
	 * @param i
	 *            long to write as unsigned int
	 * @throws IOException
	 *             if write fails
	 */
	public void writeUnsignedInt(long i) throws IOException {

		if (little) {
			write((int) (i & 0xFF));
			write((int) ((i >>> 8) & 0xFF));
			write((int) ((i >>> 16) & 0xFF));
			write((int) ((i >>> 24) & 0xFF));
		} else {
			write((int) ((i >>> 24) & 0xFF));
			write((int) ((i >>> 16) & 0xFF));
			write((int) ((i >>> 8) & 0xFF));
			write((int) (i & 0xFF));
		}
	}

	/**
	 * Write an array of unsigned ints
	 * 
	 * @param ints
	 *            long array to write as unsigned ints
	 * @throws IOException
	 *             if write fails
	 */
	public void writeUnsignedInt(long[] ints) throws IOException {

		for (int i = 0; i < ints.length; i++) {
			writeUnsignedInt(ints[i]);
		}
	}

	@Override
	public void writeLong(long l) throws IOException {
		if (little) {
			write((int) (l & 0xFF));
			write((int) ((l >>> 8) & 0xFF));
			write((int) ((l >>> 16) & 0xFF));
			write((int) ((l >>> 24) & 0xFF));
			write((int) ((l >>> 32) & 0xFF));
			write((int) ((l >>> 40) & 0xFF));
			write((int) ((l >>> 48) & 0xFF));
			write((int) ((l >>> 56) & 0xFF));
		} else {
			write((int) ((l >>> 56) & 0xFF));
			write((int) ((l >>> 48) & 0xFF));
			write((int) ((l >>> 40) & 0xFF));
			write((int) ((l >>> 32) & 0xFF));
			write((int) ((l >>> 24) & 0xFF));
			write((int) ((l >>> 16) & 0xFF));
			write((int) ((l >>> 8) & 0xFF));
			write((int) (l & 0xFF));
		}
	}

	@Override
	public void writeFloat(float f) throws IOException {
		writeInt(Float.floatToIntBits(f));
	}

	@Override
	public void writeDouble(double d) throws IOException {
		writeLong(Double.doubleToLongBits(d));
	}

	@Override
	public void writeBytes(String s) throws IOException {
		for (int i = 0; i < s.length(); i++) {
			writeByte(s.charAt(i));
		}
	}

	@Override
	public void writeChars(String s) throws IOException {
		for (int i = 0; i < s.length(); i++) {
			writeChar(s.charAt(i));
		}
	}

	/**
	 * Write a string (UTF)
	 * 
	 * @param s
	 *            string to write
	 * @throws IOException
	 *             if write fails
	 */
	public void writeString(String s) throws IOException {
		writeUTF(s);
	}

	@Override
	public void writeUTF(String s) throws IOException {
		writeUTF(s, this);
	}

	/**
	 * Write an ascii-z (0 terminated c-string).
	 * 
	 * @param s
	 *            string to write
	 * @throws IOException
	 *             if write fails
	 */
	public void writeAsciiZString(String s) throws IOException {
		writeBytes(s);
		writeByte(0);
	}

	/**
	 * Write a UTF string to the data output stream. This method should have
	 * been in DataOutputStream, but is not visible.
	 * 
	 * @param s
	 *            string to write
	 * @param dos
	 *            stream to write to
	 * @throws IOException
	 *             if write fails
	 */
	//
	public static void writeUTF(String s, DataOutput dos) throws IOException {
		int strlen = s.length();
		int utflen = 0;
		char[] charr = new char[strlen];
		int c, count = 0;

		s.getChars(0, strlen, charr, 0);

		for (int i = 0; i < strlen; i++) {
			c = charr[i];
			if ((c >= 0x0001) && (c <= 0x007F)) {
				utflen++;
			} else if (c > 0x07FF) {
				utflen += 3;
			} else {
				utflen += 2;
			}
		}

		if (utflen > 65535) {
			throw new UTFDataFormatException();
		}

		byte[] bytearr = new byte[utflen + 2];
		bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
		bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);
		for (int i = 0; i < strlen; i++) {
			c = charr[i];
			if ((c >= 0x0001) && (c <= 0x007F)) {
				bytearr[count++] = (byte) c;
			} else if (c > 0x07FF) {
				bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
				bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			} else {
				bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
				bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			}
		}
		dos.write(bytearr);
	}
}
