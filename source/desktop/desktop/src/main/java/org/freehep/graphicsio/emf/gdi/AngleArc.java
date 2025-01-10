// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * AngleArc TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: AngleArc.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class AngleArc extends EMFTag {

	private Point center;

	private int radius;

	private float startAngle, sweepAngle;

	public AngleArc() {
		super(41, 1);
	}

	public AngleArc(Point center, int radius, float startAngle,
			float sweepAngle) {
		this();
		this.center = center;
		this.radius = radius;
		this.startAngle = startAngle;
		this.sweepAngle = sweepAngle;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		AngleArc tag = new AngleArc(emf.readPOINTL(), emf.readDWORD(),
				emf.readFLOAT(), emf.readFLOAT());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writePOINTL(center);
		emf.writeDWORD(radius);
		emf.writeFLOAT(startAngle);
		emf.writeFLOAT(sweepAngle);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  center: " + center + "\n"
				+ "  radius: " + radius + "\n" + "  startAngle: " + startAngle
				+ "\n" + "  sweepAngle: " + sweepAngle;
	}
}
