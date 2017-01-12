// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * Rectangle TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: EOF.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class EOF extends EMFTag {

	public EOF() {
		super(14, 1);
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		/* int[] bytes = */ emf.readUnsignedByte(len);
		EOF tag = new EOF();
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeDWORD(0); // # of palette entries
		emf.writeDWORD(0x10); // offset for palette
		// ... palette
		emf.writeDWORD(0x14); // offset to start of record
	}
}
