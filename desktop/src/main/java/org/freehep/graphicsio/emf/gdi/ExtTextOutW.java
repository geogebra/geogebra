// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * ExtTextOutW TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: ExtTextOutW.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class ExtTextOutW extends EMFTag implements EMFConstants {

	private Rectangle bounds;

	private int mode;

	private float xScale, yScale;

	private TextW text;

	public ExtTextOutW() {
		super(84, 1);
	}

	public ExtTextOutW(Rectangle bounds, int mode, float xScale, float yScale,
			TextW text) {
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

		ExtTextOutW tag = new ExtTextOutW(emf.readRECTL(), emf.readDWORD(),
				emf.readFLOAT(), emf.readFLOAT(), new TextW(emf));
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
