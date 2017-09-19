// Copyright 2001-2003, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class to write bits to a Stream, allowing for byte synchronization. Signed,
 * Unsigned, Booleans and Floats can be written.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: BitOutputStream.java,v 1.4 2008-08-07 18:33:54 murkle Exp $
 */
public class BitOutputStream extends CompressableOutputStream
		implements FinishableOutputStream {

	private int bits;

	private int bitPos;

	/**
	 * Create a Bit output stream from given stream
	 * 
	 * @param out
	 *            stream to write to
	 */
	public BitOutputStream(OutputStream out) {
		super(out);

		bits = 0;
		bitPos = 0;
	}

	@Override
	public void write(int b) throws IOException {
		// Application.debug(Integer.toHexString(b));
		super.write(b);
	}

	@Override
	public void finish() throws IOException {

		flushByte();
		if (out instanceof FinishableOutputStream) {
			((FinishableOutputStream) out).finish();
		}
	}

	@Override
	public void close() throws IOException {

		finish();
		super.close();
	}

	/**
	 * A utility method to flush the next byte
	 * 
	 * @throws IOException
	 *             if write fails
	 */
	protected void flushByte() throws IOException {

		if (bitPos == 0) {
			return;
		}

		write(bits);
		bits = 0;
		bitPos = 0;
	}

	/**
	 * A utility to force the next write to be byte-aligned.
	 * 
	 * @throws IOException
	 *             if write fails
	 */
	public void byteAlign() throws IOException {
		flushByte();
	}

	/**
	 * Write a bit to the output stream. A 1-bit is true; a 0-bit is false.
	 * 
	 * @param bit
	 *            value to write
	 * @throws IOException
	 *             if write fails
	 */
	public void writeBitFlag(boolean bit) throws IOException {

		writeUBits((bit) ? 1 : 0, 1);
	}

	/**
	 * Write a signed value of n-bits to the output stream.
	 * 
	 * @param value
	 *            value to write
	 * @param n
	 *            number of bits to write
	 * @throws IOException
	 *             if write fails
	 */
	public void writeSBits(long value, int n) throws IOException {

		long tmp = value & 0x7FFFFFFF;

		if (value < 0) {
			tmp |= (1L << (n - 1));
		}

		writeUBits(tmp, n);
	}

	/**
	 * Write a float value of n-bits to the stream.
	 * 
	 * @param value
	 *            value to write
	 * @param n
	 *            number of bits to write
	 * @throws IOException
	 *             if write fails
	 */
	public void writeFBits(float value, int n) throws IOException {

		if (n == 0) {
			return;
		}
		long tmp = (long) (value * 0x10000);
		writeSBits(tmp, n);
	}

	/**
	 * Write an unsigned value of n-bits to the output stream.
	 * 
	 * @param value
	 *            value to write
	 * @param n
	 *            number of bits to write
	 * @throws IOException
	 *             if write fails
	 */
	public void writeUBits(long value, int n) throws IOException {

		if (n == 0) {
			return;
		}
		if (bitPos == 0) {
			bitPos = 8;
		}

		int bitNum = n;

		while (bitNum > 0) {
			while ((bitPos > 0) && (bitNum > 0)) {
				long or = (value & (1L << (bitNum - 1)));
				int shift = bitPos - bitNum;
				if (shift < 0) {
					or >>= -shift;
				} else {
					or <<= shift;
				}
				bits |= or;

				bitNum--;
				bitPos--;
			}

			if (bitPos == 0) {
				write(bits);
				bits = 0;
				if (bitNum > 0) {
					bitPos = 8;
				}
			}
		}
	}

	/**
	 * calculates the minumum number of bits necessary to write number.
	 * 
	 * @param number
	 *            number
	 * @return minimum number of bits to store number
	 */
	public static int minBits(float number) {
		return minBits((int) number, true) + 16;
	}

	/**
	 * @param number
	 *            value to calculate bits for
	 * @return number of bits needed to store value
	 */
	public static int minBits(long number) {
		return minBits(number, number < 0);
	}

	/**
	 * @param number
	 *            value to calculate bits for
	 * @param signed
	 *            true if the value if signed (&lt; 0)
	 * @return number of bits needed to store value
	 */
	public static int minBits(long number, boolean signed) {
		number = Math.abs(number);

		long x = 1;
		int i;

		for (i = 1; i <= 64; i++) {
			x <<= 1;
			if (x > number) {
				break;
			}
		}

		return i + ((signed) ? 1 : 0);
	}
}
