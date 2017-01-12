// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * The ASCIIHexOutputStream decodes ASCII Hexadecimal. The exact definition of
 * ASCII Hex encoding can be found in the PostScript Language Reference (3rd
 * ed.) chapter 3.13.3.
 * 
 * IMPORTANT: inherits from InputStream rather than FilterInputStream so that
 * the correct read(byte[], int, int) method is used.
 * 
 * @author Mark Donszelmann
 * @version $Id: ASCIIHexInputStream.java,v 1.3 2008-05-04 12:21:59 murkle Exp $
 */
public class ASCIIHexInputStream extends InputStream {

	private boolean ignoreIllegalChars;

	private boolean endReached;

	private int prev;

	private int lineNo;

	private InputStream in;

	/**
	 * Create an ASCIIHex Input Stream from given stream
	 * 
	 * @param input
	 *            input stream to use
	 */
	public ASCIIHexInputStream(InputStream input) {
		this(input, false);
	}

	/**
	 * Create an ASCIIHex Input Stream from given stream and have it optionally
	 * ignore illegal characters
	 * 
	 * @param input
	 *            input stream to use
	 * @param ignore
	 *            ignore illegal characters
	 */
	public ASCIIHexInputStream(InputStream input, boolean ignore) {
		super();
		in = input;
		ignoreIllegalChars = ignore;
		endReached = false;
		prev = -1;
		lineNo = 1;
	}

	@Override
	public int read() throws IOException {
		if (endReached) {
			return -1;
		}

		int b0 = readPart();
		if (b0 == -1) {
			return -1;
		}

		int b1 = readPart();
		if (b1 == -1) {
			b1 = 0;
		}

		int d = (b0 << 4 | b1) & 0x00FF;
		return d;
	}

	/**
	 * @return current line number
	 */
	public int getLineNo() {
		return lineNo;
	}

	private int readPart() throws IOException, EncodingException {
		while (true) {
			int b = in.read();
			switch (b) {
			case -1:
				endReached = true;
				if (!ignoreIllegalChars) {
					throw new EncodingException(
							"missing '>' at end of ASCII HEX stream");
				}
				return -1;
			case '>':
				endReached = true;
				return -1;
			case '\r':
				lineNo++;
				prev = b;
				break;
			case '\n':
				if (prev != '\r') {
					lineNo++;
				}
				prev = b;
				break;
			case ' ':
			case '\t':
			case '\f':
			case 0:
				// skip whitespace
				prev = b;
				break;
			case '0':
				return 0;
			case '1':
				return 1;
			case '2':
				return 2;
			case '3':
				return 3;
			case '4':
				return 4;
			case '5':
				return 5;
			case '6':
				return 6;
			case '7':
				return 7;
			case '8':
				return 8;
			case '9':
				return 9;
			case 'A':
			case 'a':
				return 10;
			case 'B':
			case 'b':
				return 11;
			case 'C':
			case 'c':
				return 12;
			case 'D':
			case 'd':
				return 13;
			case 'E':
			case 'e':
				return 14;
			case 'F':
			case 'f':
				return 15;
			default:
				if (!ignoreIllegalChars) {
					throw new EncodingException(
							"Illegal char " + b + " in HexStream");
				}
				prev = b;
				break;
			}
		}
	}
}
