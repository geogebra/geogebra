// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.awt.Color;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The Clear metafile record represents an operation which sets the entire
 * drawing surface to a solid color.
 * 
 * @author Mark Donszelmann
 * @version $Id: Clear.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public class Clear extends EMFPlusTag {

	private Color color = null;

	public Clear() {
		super(9, 1);
	}

	public Clear(Color color) {
		this();
		flags = 0;
		this.color = color;
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		Clear tag = new Clear();
		tag.flags = flags;
		tag.color = emf.readCOLOR();
		return tag;
	}

	@Override
	public void write(int tagID, int flags, EMFOutputStream emf)
			throws IOException {
		emf.writeCOLOR(color);
	}

	@Override
	public String toString() {
		return super.toString() + "\n  color: " + color;
	}
}
