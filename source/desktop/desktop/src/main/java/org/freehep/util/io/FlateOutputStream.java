// Copyright 2001-2005, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

/**
 * The FlateOutputStream uses the Deflate mechanism to compress data. The exact
 * definition of Deflate encoding can be found in the PostScript Language
 * Reference (3rd ed.) chapter 3.13.3.
 * 
 * @author Mark Donszelmann
 * @version $Id: FlateOutputStream.java,v 1.3 2008-05-04 12:21:46 murkle Exp $
 */
public class FlateOutputStream extends DeflaterOutputStream
		implements FinishableOutputStream {

	/**
	 * Creates a (In-)Flate output stream.
	 * 
	 * @param out
	 *            stream to write to
	 */
	public FlateOutputStream(OutputStream out) {
		super(out);
	}

	@Override
	public void finish() throws IOException {
		super.finish();
		if (out instanceof FinishableOutputStream) {
			((FinishableOutputStream) out).finish();
		}
	}
}
