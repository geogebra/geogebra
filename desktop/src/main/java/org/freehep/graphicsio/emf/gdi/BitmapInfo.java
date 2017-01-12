// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * EMF BitmapInfo
 * 
 * @author Mark Donszelmann
 * @version $Id: BitmapInfo.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class BitmapInfo {

	private BitmapInfoHeader header;

	public BitmapInfo(BitmapInfoHeader header) {
		this.header = header;
	}

	public BitmapInfo(EMFInputStream emf) throws IOException {
		header = new BitmapInfoHeader(emf);
		// colormap not necessary for true color image
	}

	public void write(EMFOutputStream emf) throws IOException {
		header.write(emf);
		// colormap not necessary for true color image
	}

	@Override
	public String toString() {
		return "  BitmapInfo\n" + header.toString();
	}
}
