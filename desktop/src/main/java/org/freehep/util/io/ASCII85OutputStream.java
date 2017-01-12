// Copyright 2001-2007, FreeHEP.
package org.freehep.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The ASCII85InputStream encodes binary data as ASCII base-85 encoding. The
 * exact definition of ASCII base-85 encoding can be found in the PostScript
 * Language Reference (3rd ed.) chapter 3.13.3.
 * 
 * @author Mark Donszelmann
 * @version $Id: ASCII85OutputStream.java,v 1.3 2008-05-04 12:20:55 murkle Exp $
 */
public class ASCII85OutputStream extends FilterOutputStream
		implements ASCII85, FinishableOutputStream {

	private boolean end;

	private int characters;

	private int b[] = new int[4];

	private int bIndex;

	private int c[] = new int[5];

	private String newline = "\n";

	/**
	 * Create an ASCII85 Output Stream from given stream
	 * 
	 * @param out
	 *            output stream to use
	 */
	public ASCII85OutputStream(OutputStream out) {
		super(out);
		characters = MAX_CHARS_PER_LINE;
		end = false;
		bIndex = 0;
		try {
			newline = System.getProperty("line.separator");
		} catch (SecurityException e) {
			// ignored;
		}
	}

	@Override
	public void write(int a) throws IOException {
		b[bIndex] = a & 0x00FF;
		bIndex++;
		if (bIndex >= b.length) {
			writeTuple();
			bIndex = 0;
		}
	}

	@Override
	public void finish() throws IOException {
		if (!end) {
			end = true;
			if (bIndex > 0) {
				writeTuple();
			}
			writeEOD();
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

	private void writeTuple() throws IOException {
		// fill the rest
		for (int i = bIndex; i < b.length; i++) {
			b[i] = 0;
		}

		// convert
		long d = ((b[0] << 24) | (b[1] << 16) | (b[2] << 8) | b[3])
				& 0x00000000FFFFFFFFL;

		c[0] = (int) (d / a85p4 + '!');
		d = d % a85p4;
		c[1] = (int) (d / a85p3 + '!');
		d = d % a85p3;
		c[2] = (int) (d / a85p2 + '!');
		d = d % a85p2;
		c[3] = (int) (d / a85p1 + '!');
		c[4] = (int) (d % a85p1 + '!');

		// convert !!!!! to z
		if ((bIndex >= b.length) && (c[0] == '!') && (c[1] == '!')
				&& (c[2] == '!') && (c[3] == '!') && (c[4] == '!')) {
			writeChar('z');
		} else {
			for (int i = 0; i < bIndex + 1; i++) {
				writeChar(c[i]);
			}
		}
	}

	// Fix for IO-7
	private void writeEOD() throws IOException {
		if (characters <= 1) {
			characters = MAX_CHARS_PER_LINE;
			writeNewLine();
		}
		characters -= 2;
		super.write('~');
		super.write('>');
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
