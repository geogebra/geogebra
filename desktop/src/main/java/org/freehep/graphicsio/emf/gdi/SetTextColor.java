// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Color;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetTextColor TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetTextColor.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SetTextColor extends EMFTag {

	private Color color;

	public SetTextColor() {
		super(24, 1);
	}

	public SetTextColor(Color color) {
		this();
		this.color = color;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		SetTextColor tag = new SetTextColor(emf.readCOLORREF());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeCOLORREF(color);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  color: " + color;
	}

	public Color getColor() {
		return color;
	}
}
