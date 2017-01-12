// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetTextAlign TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetTextAlign.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SetTextAlign extends EMFTag implements EMFConstants {

	private int mode;

	public SetTextAlign() {
		super(22, 1);
	}

	public SetTextAlign(int mode) {
		this();
		this.mode = mode;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		SetTextAlign tag = new SetTextAlign(emf.readDWORD());
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
