// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * EMF GradientRectangle
 * 
 * @author Mark Donszelmann
 * @version $Id: GradientRectangle.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class GradientRectangle extends Gradient {

	private int upperLeft, lowerRight;

	public GradientRectangle(int upperLeft, int lowerRight) {
		this.upperLeft = upperLeft;
		this.lowerRight = lowerRight;
	}

	public GradientRectangle(EMFInputStream emf) throws IOException {
		upperLeft = emf.readULONG();
		lowerRight = emf.readULONG();
	}

	@Override
	public void write(EMFOutputStream emf) throws IOException {
		emf.writeULONG(upperLeft);
		emf.writeULONG(lowerRight);
	}

	@Override
	public String toString() {
		return "  GradientRectangle: " + upperLeft + ", " + lowerRight;
	}
}
