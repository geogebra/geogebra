// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * IntersectClipRect TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: IntersectClipRect.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class IntersectClipRect extends EMFTag {

	private Rectangle bounds;

	public IntersectClipRect() {
		super(30, 1);
	}

	public IntersectClipRect(Rectangle bounds) {
		this();
		this.bounds = bounds;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		IntersectClipRect tag = new IntersectClipRect(emf.readRECTL());
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
