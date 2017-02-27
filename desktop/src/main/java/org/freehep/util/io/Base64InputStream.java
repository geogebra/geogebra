// Copyright 2003, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.InputStream;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * The Base64InputStream decodes binary data according to RFC 2045.
 * 
 * IMPORTANT: inherits from InputStream rather than FilterInputStream so that
 * the correct read(byte[], int, int) method is used.
 * 
 * @author Mark Donszelmann
 * @version $Id: Base64InputStream.java,v 1.3 2008-05-04 12:22:12 murkle Exp $
 */
public class Base64InputStream extends InputStream {

	private int b[] = new int[3];

	private int bIndex;

	private int bLength;

	private boolean endReached;

	private int lineNo;

	private InputStream in;

	private static final int ILLEGAL = -1;

	// private static final int WHITESPACE = -2;
	private static final int LINEFEED = -3;

	private static final int CARRIAGERETURN = -4;

	private static final int EQUALS = -5;

	private static final byte base64toInt[] = { -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -2, -3, -1, -1, -4, -1, -1, // Tab, LineFeed, CarriageReturn
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, // Space,
																		// Plus,
																		// Slash
			52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -5, -1, -1, // 0
																			// -
																			// 9,
																			// =
			-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, // A-Z
			15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1,
			26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // a-z
			41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };

	/**
	 * Creates a Base64 input stream for given input
	 * 
	 * @param input
	 *            to be read from
	 */
	public Base64InputStream(InputStream input) {
		super();
		in = input;
		endReached = false;
		bIndex = 0;
		bLength = 0;
		lineNo = 1;
	}

	@Override
	public int read() throws IOException {

		if (bIndex >= bLength) {
			if (endReached) {
				return -1;
			}
			bLength = readTuple();
			if (bLength <= 0) {
				return -1;
			}
			bIndex = 0;
		}
		int a = b[bIndex];
		bIndex++;

		return a;
	}

	/**
	 * @return current line number
	 */
	public int getLineNo() {
		return lineNo;
	}

	@SuppressFBWarnings({ "SF_SWITCH_FALLTHROUGH",
			"missing break is deliberate" })
	private int readTuple() throws IOException, EncodingException {
		byte c[] = new byte[4];
		int cIndex = 0;
		int encoding = 0;
		int prevEncoding = 0;
		int i = 0;

		if (endReached) {
			return 0;
		}

		while (i < c.length) {
			int ch = in.read();
			if (ch < 0) {
				endReached = true;
				if (cIndex == 0) {
					// still a proper end.
					return 0;
				}
				throw new EncodingException("Improperly padded Base64 Input.");
			}

			prevEncoding = encoding;
			encoding = base64toInt[ch & 0x7f];
			switch (encoding) {
			case ILLEGAL:
				// removed, ch can't be negative here
				// if (ch < 0)
				// throw new EncodingException(
				// "Illegal character in Base64 encoding '" + ch
				// + "'.");

				// fall through
			case EQUALS:
				// ignore, but keep reading padding
				i++;
				break;
			case CARRIAGERETURN:
				lineNo++;
				break;
			case LINEFEED:
				if (prevEncoding != CARRIAGERETURN) {
					lineNo++;
				}
				break;
			default:
				c[cIndex] = (byte) (encoding & 0xFF);
				cIndex++;
				i++;
				break;
			}
		}

		int data;
		switch (cIndex) {
		case 2:
			data = (c[0] << 18) | (c[1] << 12);

			b[0] = data >>> 16;
			return 1;

		case 3:
			data = (c[0] << 18) | (c[1] << 12) | (c[2] << 6);

			b[0] = data >>> 16;
			b[1] = data >>> 8;
			return 2;

		case 4:
			data = (c[0] << 18) | (c[1] << 12) | (c[2] << 6) | (c[3]);

			b[0] = data >>> 16;
			b[1] = data >>> 8;
			b[2] = data;
			return 3;

		default:
			throw new EncodingException("Base64InputStream: internal error.");
		}
	}
}
