// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Dimension;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetWindowExtEx TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetWindowExtEx.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SetWindowExtEx extends EMFTag {

	private Dimension size;

	public SetWindowExtEx() {
		super(9, 1);
	}

	public SetWindowExtEx(Dimension size) {
		this();
		this.size = size;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		SetWindowExtEx tag = new SetWindowExtEx(emf.readSIZEL());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeSIZEL(size);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  size: " + size;
	}
}
