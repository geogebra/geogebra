// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The ASCIIHexOutputStream encodes binary data as ASCII Hexadecimal. The exact
 * definition of ASCII Hex encoding can be found in the PostScript Language
 * Reference (3rd ed.) chapter 3.13.3.
 * 
 * @author Mark Donszelmann
 * @version $Id: ASCIIHexOutputStream.java,v 1.3 2008-05-04 12:21:25 murkle Exp
 *          $
 */
public class ASCIIHexOutputStream extends FilterOutputStream
		implements FinishableOutputStream {

	private final static int MAX_CHARS_PER_LINE = 80;

	private int characters;

	private boolean end;

	private String newline = "\n";

	/**
	 * Create an ASCIIHex Output Stream for given stream.
	 * 
	 * @param out
	 *            output stream to use
	 */
	public ASCIIHexOutputStream(OutputStream out) {
		super(out);
		characters = MAX_CHARS_PER_LINE;
		end = false;
		try {
			newline = System.getProperty("line.separator");
		} catch (SecurityException e) {
			// ignored;
		}
	}

	@Override
	public void write(int b) throws IOException {
		String s = Integer.toHexString(b & 0x00FF);
		switch (s.length()) {
		case 1:
			writeChar('0');
			writeChar(s.charAt(0));
			break;
		case 2:
			writeChar(s.charAt(0));
			writeChar(s.charAt(1));
			break;
		default:
			throw new IOException("ASCIIHexOutputStream: byte '" + b
					+ "' was encoded in less than 1 or more than 2 chars");
		}
	}

	@Override
	public void finish() throws IOException {
		if (!end) {
			end = true;
			writeChar('>');
			writeNewLine();
			flush();
			if (out instanceof FinishableOutputStream) {
				((FinishableOutputStream) out).finish();
			}
		}
	}

	@Override
	public void close() throws IOException {
		finish();
		super.close();
	}

	private void writeChar(int b) throws IOException {
		if (characters == 0) {
			characters = MAX_CHARS_PER_LINE;
			writeNewLine();
		}
		characters--;
		super.write(b);
	}

	private void writeNewLine() throws IOException {
		// write a newline
		for (int i = 0; i < newline.length(); i++) {
			super.write(newline.charAt(i));
		}
	}
}
