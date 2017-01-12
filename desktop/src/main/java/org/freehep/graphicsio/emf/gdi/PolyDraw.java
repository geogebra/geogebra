// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * PolyDraw TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: PolyDraw.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PolyDraw extends EMFTag implements EMFConstants {

	private Rectangle bounds;

	private Point[] points;

	private byte[] types;

	public PolyDraw() {
		super(56, 1);
	}

	public PolyDraw(Rectangle bounds, Point[] points, byte[] types) {
		this();
		this.bounds = bounds;
		this.points = points;
		this.types = types;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		int n;
		PolyDraw tag = new PolyDraw(emf.readRECTL(),
				emf.readPOINTL(n = emf.readDWORD()), emf.readBYTE(n));
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeRECTL(bounds);
		emf.writeDWORD(points.length);
		emf.writePOINTL(points);
		emf.writeBYTE(types);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  bounds: " + bounds + "\n"
				+ "  #points: " + points.length;
	}
}
