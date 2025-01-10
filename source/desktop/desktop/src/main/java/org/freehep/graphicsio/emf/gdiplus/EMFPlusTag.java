// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * EMF+ specific tag, from which all other EMF Tags inherit.
 * 
 * @author Mark Donszelmann
 * @version $Id: EMFPlusTag.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public abstract class EMFPlusTag extends EMFTag {

	private static final int OFFSET = 0x00004000;

	/**
	 * Constructs an EMFPlusTag.
	 * 
	 * @param id
	 *            id of the element, which will be offset to 0x4000
	 * @param version
	 *            emf+ version in which this element was first supported, which
	 *            will be offset to 0x4000
	 */
	protected EMFPlusTag(int id, int version) {
		super(id + OFFSET, version + OFFSET);
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		len = emf.readUINT(); // intermediate length
		return read(tagID - OFFSET, flags, emf, len);
	}

	public abstract EMFPlusTag read(int tagID, int flags, EMFInputStream emf,
			int len) throws IOException;

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.pushBuffer();
		write(tagID - OFFSET, flags, emf);
		int len = emf.popBuffer();
		emf.writeUINT(len);
		emf.append();
	}

	public abstract void write(int tagID, int flags, EMFOutputStream emf)
			throws IOException;

	@Override
	public String toString() {
		int id = getTag();
		String s = "EMF+Tag " + getName() + " (" + id + ") (0x"
				+ Integer.toHexString(id) + ")\n";
		s += "  flags: 0x" + Integer.toHexString(flags);
		return s;
	}
}
