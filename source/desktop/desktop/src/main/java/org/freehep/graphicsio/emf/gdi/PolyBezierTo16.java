// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * PolyBezierTo16 TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: PolyBezierTo16.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PolyBezierTo16 extends EMFTag {

	private Rectangle bounds;

	private int numberOfPoints;

	private Point[] points;

	public PolyBezierTo16() {
		super(88, 1);
	}

	public PolyBezierTo16(Rectangle bounds, int numberOfPoints,
			Point[] points) {
		this();
		this.bounds = bounds;
		this.numberOfPoints = numberOfPoints;
		this.points = points;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		Rectangle r = emf.readRECTL();
		int n = emf.readDWORD();
		PolyBezierTo16 tag = new PolyBezierTo16(r, n, emf.readPOINTS(n));
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeRECTL(bounds);
		emf.writeDWORD(numberOfPoints);
		emf.writePOINTS(numberOfPoints, points);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  bounds: " + bounds + "\n"
				+ "  #points: " + numberOfPoints;
	}
}
