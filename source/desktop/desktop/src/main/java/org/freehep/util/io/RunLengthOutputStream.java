// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The RunLengthOutputStream encodes data as Run Length encoding. The exact
 * definition of Run Length encoding can be found in the PostScript Language
 * Reference (3rd ed.) chapter 3.13.3.
 * 
 * @author Mark Donszelmann
 * @version $Id: RunLengthOutputStream.java,v 1.3 2008-05-04 12:21:35 murkle Exp
 *          $
 */
public class RunLengthOutputStream extends FilterOutputStream
		implements RunLength, FinishableOutputStream {

	private boolean end;

	private int[] buffer = new int[LENGTH];

	private int index;

	private int last;

	private int count;

	/**
	 * Create a Run Length output stream
	 * 
	 * @param out
	 *            stream to write
	 */
	public RunLengthOutputStream(OutputStream out) {
		super(out);
		end = false;
		index = 0;
		last = -1;
		count = 1;
	}

	@Override
	public void write(int a) throws IOException {
		a &= 0x00FF;
		if (last > 0) {
			if (a == last) {
				// counting
				writeBuffer();
				count++;
				if (count >= LENGTH) {
					writeCount();
				}
			} else {
				// buffering
				if (count > 1) {
					writeCount();
				} else {
					buffer[index] = last;
					index++;
					if (index >= LENGTH) {
						writeBuffer();
					}
				}
			}
		}

		last = a;
	}

	@Override
	public void finish() throws IOException {
		if (!end) {
			end = true;
			writeCount();
			writeBuffer();

			// we may have one character left in "last" (FREEHEP-578)
			if (last >= 0) {
				super.write(0);
				super.write(last);
			}
			super.write(EOD);
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

	private void writeBuffer() throws IOException {
		if (index > 0) {
			super.write(index - 1);
			for (int i = 0; i < index; i++) {
				super.write((byte) buffer[i]);
			}
		}

		index = 0;
	}

	private void writeCount() throws IOException {
		if (count > 1) {
			super.write(257 - count);
			super.write(last);
			last = -1;
		}

		count = 1;
	}
}
