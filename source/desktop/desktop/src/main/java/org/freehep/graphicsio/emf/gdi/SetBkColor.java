// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Color;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetBkColor TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetBkColor.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SetBkColor extends EMFTag {

	private Color color;

	public SetBkColor() {
		super(25, 1);
	}

	public SetBkColor(Color color) {
		this();
		this.color = color;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		SetBkColor tag = new SetBkColor(emf.readCOLORREF());
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
}
