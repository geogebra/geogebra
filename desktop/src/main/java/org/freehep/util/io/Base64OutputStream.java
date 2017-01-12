// Copyright 2003, FreeHEP.
package org.freehep.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The Base64OutputStream encodes binary data according to RFC 2045.
 * 
 * @author Mark Donszelmann
 * @version $Id: Base64OutputStream.java,v 1.4 2008-08-07 18:33:54 murkle Exp $
 */
public class Base64OutputStream extends FilterOutputStream
		implements FinishableOutputStream {

	private int MAX_LINE_LENGTH = 74;

	private int position;

	private byte[] buffer;

	private int lineLength;

	private static final char intToBase64[] = { 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', // 0 - 7
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', // 8 - 15
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', // 16 - 23
			'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', // 24 - 31
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', // 32 - 39
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', // 40 - 47
			'w', 'x', 'y', 'z', '0', '1', '2', '3', // 48 - 55
			'4', '5', '6', '7', '8', '9', '+', '/' // 56 - 63
	};

	private String newline = "\n";

	/**
	 * Creates a Base64 output stream for given output
	 * 
	 * @param out
	 *            stream to write to
	 */
	public Base64OutputStream(OutputStream out) {
		super(out);
		buffer = new byte[3];
		position = 0;
		lineLength = 0;
		try {
			newline = System.getProperty("line.separator");
		} catch (SecurityException e) {
			// ignored;
		}
	}

	@Override
	public void write(int a) throws IOException {
		buffer[position++] = (byte) a;
		if (position < buffer.length) {
			return;
		}

		// System.out.print(Integer.toHexString(buffer[0])+",
		// "+Integer.toHexString(buffer[1])+",
		// "+Integer.toHexString(buffer[2])+" ");
		writeTuple();
		// writeNewLine();

		lineLength += 4;
		if (lineLength >= MAX_LINE_LENGTH) {
			writeNewLine();
			lineLength = 0;
		}

		position = 0;
	}

	@Override
	public void finish() throws IOException {
		writeTuple();
		flush();
		if (out instanceof FinishableOutputStream) {
			((FinishableOutputStream) out).finish();
		}
	}

	@Override
	public void close() throws IOException {
		finish();
		super.close();
	}

	private void writeTuple() throws IOException {
		int data = (position > 0 ? (buffer[0] << 16) & 0x00FF0000 : 0)
				| (position > 1 ? (buffer[1] << 8) & 0x0000FF00 : 0)
				| (position > 2 ? (buffer[2]) & 0x000000FF : 0);

		// Application.debug(Integer.toHexString(data));
		switch (position) {
		case 3:
			// Application.debug("\n*** "+((data >> 18) & 0x3f) +", "+((data >>
			// 12) & 0x3f)+", "+
			// ((data >> 6) & 0x3f) +", "+((data ) & 0x3f));
			out.write(intToBase64[(data >> 18) & 0x3f]);
			out.write(intToBase64[(data >> 12) & 0x3f]);
			out.write(intToBase64[(data >> 6) & 0x3f]);
			out.write(intToBase64[(data) & 0x3f]);
			return;

		case 2:
			out.write(intToBase64[(data >> 18) & 0x3f]);
			out.write(intToBase64[(data >> 12) & 0x3f]);
			out.write(intToBase64[(data >> 6) & 0x3f]);
			out.write('=');
			return;

		case 1:
			out.write(intToBase64[(data >> 18) & 0x3f]);
			out.write(intToBase64[(data >> 12) & 0x3f]);
			out.write('=');
			out.write('=');
			return;

		default:
			return;
		}
	}

	private void writeNewLine() throws IOException {
		// write a newline
		for (int i = 0; i < newline.length(); i++) {
			out.write(newline.charAt(i));
		}
	}
}
