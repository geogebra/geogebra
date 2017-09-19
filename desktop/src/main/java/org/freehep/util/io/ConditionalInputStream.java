// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The ConditionalInputStream reads a stream and filters certain parts depending
 * of properties and statements in the input.
 * <P>
 * The following statements, all start with the
 * 
 * &#64;-sign, are allowed:
 * 
 * <UL>
 * <LI><B>@ifdef property</B>, reads everything up to the next
 * 
 * &#64;statement if the property is defined.
 *            <LI><B>@ifndef property</B>, reads everything up to the next
 * &#64;statement if the property is not defined.
 *            <LI><B>@else</B>, corresponding else statement
 *            <LI><B>@endif</B>, corresponging endif statement
 *            </UL>
 * 
 *            The &#64;-sign itself must be escaped by a backslash, if used in
 *            the text followed by any of the keywords described above and no
 *            action should be taken.
 * 
 *            <P>
 *            IMPORTANT: inherits from InputStream rather than FilterInputStream
 *            so that the correct read(byte[], int, int) method is used.
 * 
 * @author Mark Donszelmann
 * @version $Id: ConditionalInputStream.java,v 1.3 2008-05-04 12:22:09 murkle
 *          Exp $
 */
public class ConditionalInputStream extends InputStream {

	private int[] buffer = new int[4096];

	private int index;

	private int len;

	private InputStream in;

	private Properties defines;

	private int nesting;

	private boolean[] ok = new boolean[50];

	private boolean escape;

	/**
	 * Creates a Conditional Input Stream from given stream.
	 * 
	 * @param input
	 *            stream to read from
	 * @param defines
	 *            set of properties to be used in ifdefs
	 */
	public ConditionalInputStream(InputStream input, Properties defines) {
		super();
		in = input;
		this.defines = defines;
		nesting = 0;
		escape = false;
		index = 0;
		len = 0;
	}

	@Override
	public int read() throws IOException {

		int b;
		int n;

		// read from buffer if possible
		if (index < len) {
			b = buffer[index];
			index++;
		} else {
			b = in.read();
		}

		// return if End Of Stream
		if (b < 0) {
			return -1;
		}

		// escape \@-signs
		if (b == '\\') {
			n = in.read();
			if (n == '@') {
				b = ' ';
				escape = true;
			}
			buffer[0] = n;
			index = 0;
			len = 1;
		}

		// check on @ sign
		if (b == '@') {
			if (escape) {
				escape = false;
			} else {
				// read keyword (ifdef, ifndef, else, endif
				index = 0;
				StringBuffer s = new StringBuffer();
				n = in.read();
				while ((n >= 0) && !Character.isWhitespace((char) n)) {
					s.append((char) n);
					buffer[index] = n;
					n = in.read();
					index++;
				}
				buffer[index] = n;
				index++;
				b = ' ';

				// check on keyword
				String keyword = s.toString();
				if (keyword.equals("ifdef") || keyword.equals("ifndef")) {

					// skip whitespace and read property
					s = new StringBuffer();
					n = in.read();
					while ((n >= 0) && Character.isWhitespace((char) n)) {
						buffer[index] = n;
						n = in.read();
						index++;
					}
					while ((n >= 0) && !Character.isWhitespace((char) n)) {
						s.append((char) n);
						buffer[index] = n;
						n = in.read();
						index++;
					}
					buffer[index] = n;
					index++;

					// check on property
					String property = s.toString();
					if (defines.getProperty(property) != null) {
						ok[nesting] = (nesting > 0 ? ok[nesting - 1] : true)
								&& keyword.equals("ifdef");
					} else {
						ok[nesting] = (nesting > 0 ? ok[nesting - 1] : true)
								&& keyword.equals("ifndef");
					}
					nesting++;
					replaceBufferWithWhitespace(index);
				} else if (keyword.equals("else")) {
					// FIXME one could have multiple elses without endifs...
					// calculate inclusion based on ifdef nesting
					if (nesting <= 0) {
						throw new RuntimeException(
								"@else without corresponding @ifdef");
					}
					ok[nesting - 1] = (nesting > 1 ? ok[nesting - 2] : true)
							&& !ok[nesting - 1];
					replaceBufferWithWhitespace(index);
				} else if (keyword.equals("endif")) {
					// calculate inclusion based on ifdef nesting
					if (nesting <= 0) {
						throw new RuntimeException(
								"@endif without corresponding @ifdef");
					}
					nesting--;
					replaceBufferWithWhitespace(index);
				} else {
					// not an known @
					b = '@';
				}
				len = index;
				index = 0;
			}
		}

		if ((nesting > 0) && !ok[nesting - 1]) {
			if (!Character.isWhitespace((char) b)) {
				b = ' ';
			}
		}
		return b & 0x00FF;
	}

	private void replaceBufferWithWhitespace(int size) {
		for (int i = 0; i < size; i++) {
			if (!Character.isWhitespace((char) buffer[i])) {
				buffer[i] = ' ';
			}
		}
	}
}
