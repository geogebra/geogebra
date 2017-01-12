// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Color;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * EMF TriVertex
 * 
 * @author Mark Donszelmann
 * @version $Id: TriVertex.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class TriVertex {

	private int x, y;

	private Color color;

	public TriVertex(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public TriVertex(EMFInputStream emf) throws IOException {
		x = emf.readLONG();
		y = emf.readLONG();
		color = emf.readCOLOR16();
	}

	public void write(EMFOutputStream emf) throws IOException {
		emf.writeLONG(x);
		emf.writeLONG(y);
		emf.writeCOLOR16(color);
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "] " + color;
	}
}
