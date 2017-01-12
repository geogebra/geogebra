// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetBrushOrgEx TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetBrushOrgEx.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SetBrushOrgEx extends EMFTag {

	private Point point;

	public SetBrushOrgEx() {
		super(13, 1);
	}

	public SetBrushOrgEx(Point point) {
		this();
		this.point = point;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		SetBrushOrgEx tag = new SetBrushOrgEx(emf.readPOINTL());
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
}
