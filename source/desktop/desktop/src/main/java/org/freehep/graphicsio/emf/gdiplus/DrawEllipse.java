// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The DrawEllipse metafile record represents a call to Graphics.DrawEllipse,
 * which draws the border of an ellipse.
 * 
 * @author Mark Donszelmann
 * @version $Id: DrawEllipse.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public class DrawEllipse extends EMFPlusTag {

	private float x, y, w, h;

	public DrawEllipse() {
		super(15, 1);
	}

	public DrawEllipse(int penIndex, float x, float y, float w, float h) {
		this();
		flags = penIndex;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		DrawEllipse tag = new DrawEllipse();
		tag.flags = flags;
		if ((flags & 0x4000) > 0) {
			tag.x = emf.readWORD();
			tag.y = emf.readWORD();
			tag.w = emf.readWORD();
			tag.h = emf.readWORD();
		} else {
			tag.x = emf.readFLOAT();
			tag.y = emf.readFLOAT();
			tag.w = emf.readFLOAT();
			tag.h = emf.readFLOAT();
		}
		return tag;
	}

	@Override
	public void write(int tagID, int flags, EMFOutputStream emf)
			throws IOException {
		// No Provision for 16 bit integer values.
		emf.writeFLOAT(x);
		emf.writeFLOAT(y);
		emf.writeFLOAT(w);
		emf.writeFLOAT(h);
	}

	@Override
	public String toString() {
		return super.toString() + "\n  rect: (" + x + ", " + y + ", " + w + ", "
				+ h + ")";
	}
}
