// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * RoundRect TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: RoundRect.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class RoundRect extends EMFTag {

	private Rectangle bounds;

	private Dimension corner;

	public RoundRect() {
		super(44, 1);
	}

	public RoundRect(Rectangle bounds, Dimension corner) {
		this();
		this.bounds = bounds;
		this.corner = corner;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		RoundRect tag = new RoundRect(emf.readRECTL(), emf.readSIZEL());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeRECTL(bounds);
		emf.writeSIZEL(corner);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  bounds: " + bounds + "\n"
				+ "  corner: " + corner;
	}
}
