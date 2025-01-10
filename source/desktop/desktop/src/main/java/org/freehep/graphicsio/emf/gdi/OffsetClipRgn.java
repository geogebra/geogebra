// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * OffsetClipRgn TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: OffsetClipRgn.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class OffsetClipRgn extends EMFTag {

	private Point offset;

	public OffsetClipRgn() {
		super(26, 1);
	}

	public OffsetClipRgn(Point offset) {
		this();
		this.offset = offset;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		OffsetClipRgn tag = new OffsetClipRgn(emf.readPOINTL());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writePOINTL(offset);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  offset: " + offset;
	}
}
