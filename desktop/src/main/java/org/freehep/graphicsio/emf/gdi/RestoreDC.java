// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * RestoreDC TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: RestoreDC.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class RestoreDC extends EMFTag {

	private int savedDC = -1;

	public RestoreDC() {
		super(34, 1);
	}

	public RestoreDC(int savedDC) {
		this();
		this.savedDC = savedDC;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		RestoreDC tag = new RestoreDC(emf.readDWORD());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeDWORD(savedDC);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  savedDC: " + savedDC;
	}
}
