// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * DeleteObject TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: DeleteObject.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class DeleteObject extends EMFTag {

	private int index;

	public DeleteObject() {
		super(40, 1);
	}

	public DeleteObject(int index) {
		this();
		this.index = index;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		DeleteObject tag = new DeleteObject(emf.readDWORD());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeDWORD(index);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  index: 0x"
				+ Integer.toHexString(index);
	}

	public int getIndex() {
		return index;
	}
}
