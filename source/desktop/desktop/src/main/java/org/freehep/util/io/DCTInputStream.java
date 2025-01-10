// Copyright 2003, FreeHEP.
package org.freehep.util.io;

import java.awt.Image;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * Reads images from a JPEG Stream, but only images.
 *
 * @author Mark Donszelmann
 * @version $Id: DCTInputStream.java,v 1.3 2008-05-04 12:22:17 murkle Exp $
 */
/**
 * @author duns
 * 
 */
public class DCTInputStream extends FilterInputStream {

	/**
	 * Creates a DCT input stream from the given input stream
	 * 
	 * @param input
	 *            stream to read from
	 */
	public DCTInputStream(InputStream input) {
		super(input);
	}

	/**
	 * Read is not supported, only readImage.
	 * 
	 * @see java.io.FilterInputStream#read()
	 */
	@Override
	public int read() throws IOException {
		throw new IOException(
				getClass() + ": read() not implemented, use readImage().");
	}

	/**
	 * @return image read
	 * @throws IOException
	 *             if read fails
	 */
	public Image readImage() throws IOException {
		return ImageIO.read(new NoCloseInputStream(this));
	}
}
