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
 * PolyDraw16 TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: PolyDraw16.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PolyDraw16 extends EMFTag implements EMFConstants {

	private Rectangle bounds;

	private Point[] points;

	private byte[] types;

	public PolyDraw16() {
		super(92, 1);
	}

	public PolyDraw16(Rectangle bounds, Point[] points, byte[] types) {
		this();
		this.bounds = bounds;
		this.points = points;
		this.types = types;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		int n;
		PolyDraw16 tag = new PolyDraw16(emf.readRECTL(),
				emf.readPOINTS(n = emf.readDWORD()), emf.readBYTE(n));
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeRECTL(bounds);
		emf.writeDWORD(points.length);
		emf.writePOINTS(points);
		emf.writeBYTE(types);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  bounds: " + bounds + "\n"
				+ "  #points: " + points.length;
	}
}
