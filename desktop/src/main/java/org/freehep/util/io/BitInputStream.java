// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class to read bits from a Stream, allowing for byte synchronization. Signed,
 * Unsigned, Booleans and Floats can be read.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: BitInputStream.java,v 1.3 2008-05-04 12:22:06 murkle Exp $
 */
public class BitInputStream extends DecompressableInputStream {

	final protected static int MASK_SIZE = 8;

	final protected static int ZERO = 0;

	final protected static int ONES = ~0;

	final protected static int[] BIT_MASK = new int[MASK_SIZE];

	final protected static int[] FIELD_MASK = new int[MASK_SIZE];

	// Generate the needed masks for various bit fields and for
	// individual bits.
	static {
		int tempBit = 1;
		int tempField = 1;
		for (int i = 0; i < MASK_SIZE; i++) {

			// Set the masks.
			BIT_MASK[i] = tempBit;
			FIELD_MASK[i] = tempField;

			// Update the temporary values.
			tempBit <<= 1;
			tempField <<= 1;
			tempField++;
		}
	}

	/**
	 * This is a prefetched byte used to construct signed and unsigned numbers
	 * with an arbitrary number of significant bits.
	 */
	private int bits;

	/**
	 * The number of valid bits remaining in the bits field.
	 */
	private int validBits;

	/**
	 * Create a Bit input stream from viven input
	 * 
	 * @param in
	 *            stream to read from
	 */
	public BitInputStream(InputStream in) {
		super(in);

		bits = 0;
		validBits = 0;
	}

	/**
	 * A utility method to fetch the next byte in preparation for constructing a
	 * bit field. There is no protection for this method; ensure that it is only
	 * called when a byte must be fetched.
	 * 
	 * @throws IOException
	 *             if read fails
	 */
	protected void fetchByte() throws IOException {

		bits = read();
		if (bits < 0) {
			throw new EOFException();
		}
		validBits = MASK_SIZE;
	}

	/**
	 * A utility to force the next read to be byte-aligned.
	 */
	public void byteAlign() {
		validBits = 0;
	}

	/**
	 * Read a bit from the input stream and interpret this as a boolean value. A
	 * 1-bit is true; a 0-bit is false.
	 * 
	 * @return true if read bit was 1
	 * @throws IOException
	 *             if read fails
	 */
	public boolean readBitFlag() throws IOException {

		if (validBits == 0) {
			fetchByte();
		}
		return ((bits & BIT_MASK[--validBits]) != 0);
	}

	/**
	 * Read a signed value of n-bits from the input stream.
	 * 
	 * @param n
	 *            number of bits to read
	 * @return value made up of read bits
	 * @throws IOException
	 *             if read fails
	 */
	public long readSBits(int n) throws IOException {

		if (n == 0) {
			return 0;
		}
		int value = (readBitFlag()) ? ONES : ZERO;
		value <<= (--n);
		return (value | readUBits(n));
	}

	/**
	 * Read a float value of n-bits from the stream.
	 * 
	 * @param n
	 *            number of bits to read
	 * @return value made up of read bits
	 * @throws IOException
	 *             if read fails
	 */
	public float readFBits(int n) throws IOException {

		if (n == 0) {
			return 0.0f;
		}
		return ((float) readSBits(n)) / 0x1000;
	}

	/**
	 * Read an unsigned value of n-bits from the input stream.
	 * 
	 * @param n
	 *            number of bits to read
	 * @return value made up of read bits
	 * @throws IOException
	 *             if read fails
	 */
	public long readUBits(int n) throws IOException {

		long value = ZERO;
		while (n > 0) {

			// Take the needed bits or the number which are valid
			// whichever is less.
			if (validBits == 0) {
				fetchByte();
			}
			int nbits = (n > validBits) ? validBits : n;

			// Take the bits and update the counters.
			int temp = ((bits >> (validBits - nbits)) & FIELD_MASK[nbits - 1]);
			validBits -= nbits;
			n -= nbits;

			// Shift the value up to accomodate new bits.
			value <<= nbits;
			value |= temp;
		}
		return value;
	}
}
