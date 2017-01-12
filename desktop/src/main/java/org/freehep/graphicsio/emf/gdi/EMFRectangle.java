// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * Rectangle TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: EMFRectangle.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class EMFRectangle extends EMFTag {

	private Rectangle bounds;

	public EMFRectangle() {
		super(43, 1);
	}

	public EMFRectangle(Rectangle bounds) {
		this();
		this.bounds = bounds;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		EMFRectangle tag = new EMFRectangle(emf.readRECTL());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeRECTL(bounds);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  bounds: " + bounds;
	}
}
