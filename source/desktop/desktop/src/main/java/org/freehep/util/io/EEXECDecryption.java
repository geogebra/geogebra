// Copyright 2001 freehep
package org.freehep.util.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Decrypts using the EEXEC form (Used by Type 1 fonts).
 * 
 * @author Simon Fischer
 * @version $Id: EEXECDecryption.java,v 1.3 2008-05-04 12:22:11 murkle Exp $
 */
public class EEXECDecryption extends InputStream implements EEXECConstants {

	private int n, c1, c2, r;

	private InputStream in;

	private boolean first = true;

	/**
	 * Creates an EEXECDecryption from the given stream
	 * 
	 * @param in
	 *            stream to read from
	 */
	public EEXECDecryption(InputStream in) {
		this(in, EEXEC_R, N);
	}

	/**
	 * Creates an EEXECDecryption from the given stream
	 * 
	 * @param in
	 *            stream to read from
	 * @param r
	 * @param n
	 */
	public EEXECDecryption(InputStream in, int r, int n) {
		this.in = in;
		this.r = r;
		this.n = n;
		this.c1 = C1;
		this.c2 = C2;
	}

	private int decrypt(int cipher) {
		int plain = (cipher ^ (r >>> 8)) % 256;
		r = ((cipher + r) * c1 + c2) % 65536;
		return plain;
	}

	@Override
	public int read() throws IOException {
		if (first) {
			byte[] bytes = new byte[n];
			boolean notHex = false;
			for (int i = 0; i < bytes.length; i++) {
				int c = in.read();
				bytes[i] = (byte) c;
				if (!Character.isDigit((char) c) && !((c >= 'a') && (c <= 'f'))
						&& !((c >= 'A') && (c <= 'F'))) {
					notHex = true;
				}
			}
			if (notHex) {
				for (int i = 0; i < bytes.length; i++) {
					decrypt(bytes[i] & 0x00ff);
				}
			} else {
				InputStream tempIn = new ASCIIHexInputStream(
						new ByteArrayInputStream(bytes), true);
				int asciiDecoded;
				int byteCount = 0;
				while ((asciiDecoded = tempIn.read()) >= 0) {
					decrypt(asciiDecoded);
					byteCount++;
				}
				tempIn.close();

				in = new ASCIIHexInputStream(in, true);
				while (byteCount < n) {
					decrypt(in.read());
					byteCount++;
				}

			}
			first = false;
		}

		int b = in.read();
		if (b == -1) {
			return -1;
		}
		return decrypt(b);
	}

	@Override
	public void close() throws IOException {
		super.close();
		in.close();
	}
}
