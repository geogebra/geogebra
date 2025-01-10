// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetMapMode TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetMapMode.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SetMapMode extends EMFTag implements EMFConstants {

	private int mode;

	public SetMapMode() {
		super(17, 1);
	}

	public SetMapMode(int mode) {
		this();
		this.mode = mode;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		SetMapMode tag = new SetMapMode(emf.readDWORD());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeDWORD(mode);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  mode: " + mode;
	}
}
