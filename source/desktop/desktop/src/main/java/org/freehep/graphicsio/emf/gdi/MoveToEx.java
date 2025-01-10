// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * MoveToEx TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: MoveToEx.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class MoveToEx extends EMFTag {

	private Point point;

	public MoveToEx() {
		super(27, 1);
	}

	public MoveToEx(Point point) {
		this();
		this.point = point;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		MoveToEx tag = new MoveToEx(emf.readPOINTL());
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
