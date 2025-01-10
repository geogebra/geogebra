// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.awt.Color;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The FillEllipse metafile record represents a call to Graphics.FillEllipse,
 * which fills the interior of an ellipse.
 * 
 * @author Mark Donszelmann
 * @version $Id: FillEllipse.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public class FillEllipse extends EMFPlusTag {

	private int brushIndex;
	private Color brushColor;
	private float x, y, w, h;

	public FillEllipse() {
		super(14, 1);
	}

	public FillEllipse(int penIndex, int brushIndex, float x, float y, float w,
			float h) {
		this();
		flags = penIndex;
		this.brushIndex = brushIndex;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		FillEllipse tag = new FillEllipse();
		tag.flags = flags;
		if ((flags & 0x8000) > 0) {
			tag.brushColor = emf.readCOLOR();
		} else {
			tag.brushIndex = emf.readUINT();
		}
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
		// FIXME No Provision for 16 bit integer values.
		emf.writeUINT(brushIndex);
		emf.writeFLOAT(x);
		emf.writeFLOAT(y);
		emf.writeFLOAT(w);
		emf.writeFLOAT(h);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append("\n  rect: (" + x + ", " + y + ", " + w + ", " + h + ")");
		sb.append("\n  ");
		sb.append(brushColor != null ? "brushColor: " + brushColor
				: "brushIndex: " + brushIndex);
		return sb.toString();
	}
}
