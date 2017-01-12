// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.awt.Color;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The FillPath metafile record represents a call to Graphics.FillPath, which
 * fills the interior of a GraphicsPath object.
 * 
 * @author Mark Donszelmann
 * @version $Id: FillPath.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public class FillPath extends EMFPlusTag {

	private Color brushColor = null;
	private int brushIndex;

	public FillPath() {
		super(20, 1);
	}

	public FillPath(int pathIndex, int brushIndex) {
		this();
		flags = pathIndex;
		this.brushIndex = brushIndex;
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		FillPath tag = new FillPath();
		tag.flags = flags;
		if ((flags & 0x8000) > 0) {
			tag.brushColor = emf.readCOLOR();
		} else {
			tag.brushIndex = emf.readUINT();
		}
		return tag;
	}

	@Override
	public void write(int tagID, int flags, EMFOutputStream emf)
			throws IOException {
		if (brushColor != null) {
			emf.writeCOLOR(brushColor);
		} else {
			emf.writeUINT(brushIndex);
		}
	}

	@Override
	public String toString() {
		return super.toString() + "\n  " + (brushColor != null
				? "brushColor: " + brushColor : "brushIndex: " + brushIndex);
	}
}
