// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The DrawLines metafile record represents a call to Graphics.DrawLines, which
 * draws a series of straight lines connecting successive points.
 * 
 * No Provision for 16 bit integer values.
 * 
 * @author Mark Donszelmann
 * @version $Id: DrawLines.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public class DrawLines extends EMFPlusTag {

	private float[] x, y;

	public DrawLines() {
		super(13, 1);
	}

	public DrawLines(int penIndex, float[] x, float[] y) {
		this();
		flags = penIndex;
		this.x = x;
		this.y = y;
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		DrawLines tag = new DrawLines();
		tag.flags = flags;
		int n = emf.readUINT();
		tag.x = new float[n];
		tag.y = new float[n];
		for (int i = 0; i < n; i++) {
			tag.x[i] = emf.readFLOAT();
			tag.y[i] = emf.readFLOAT();
		}
		return tag;
	}

	@Override
	public void write(int tagID, int flags, EMFOutputStream emf)
			throws IOException {
		emf.writeUINT(x.length);
		for (int i = 0; i < x.length; i++) {
			emf.writeFLOAT(x[i]);
			emf.writeFLOAT(y[i]);
		}
	}
}
