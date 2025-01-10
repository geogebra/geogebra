// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * LineTo TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: LineTo.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class LineTo extends EMFTag {

	private Point point;

	public LineTo() {
		super(54, 1);
	}

	public LineTo(Point point) {
		this();
		this.point = point;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		LineTo tag = new LineTo(emf.readPOINTL());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writePOINTL(point);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  point: " + point;
	}

	public Point getPoint() {
		return point;
	}

}
