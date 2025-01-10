// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * ExtTextOutA TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: ExtTextOutA.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class ExtTextOutA extends EMFTag implements EMFConstants {

	private Rectangle bounds;

	private int mode;

	private float xScale, yScale;

	private Text text;

	public ExtTextOutA() {
		super(83, 1);
	}

	public ExtTextOutA(Rectangle bounds, int mode, float xScale, float yScale,
			Text text) {
		this();
		this.bounds = bounds;
		this.mode = mode;
		this.xScale = xScale;
		this.yScale = yScale;
		this.text = text;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		ExtTextOutA tag = new ExtTextOutA(emf.readRECTL(), emf.readDWORD(),
				emf.readFLOAT(), emf.readFLOAT(), new Text(emf));
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeRECTL(bounds);
		emf.writeDWORD(mode);
		emf.writeFLOAT(xScale);
		emf.writeFLOAT(yScale);
		text.write(emf);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  bounds: " + bounds + "\n"
				+ "  mode: " + mode + "\n" + "  xScale: " + xScale + "\n"
				+ "  yScale: " + yScale + "\n" + text.toString();
	}
}
