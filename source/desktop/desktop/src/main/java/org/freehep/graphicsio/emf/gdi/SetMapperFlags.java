// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetMapperFlags TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetMapperFlags.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class SetMapperFlags extends EMFTag {

	private int flags;

	public SetMapperFlags() {
		super(16, 1);
	}

	public SetMapperFlags(int flags) {
		this();
		this.flags = flags;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		SetMapperFlags tag = new SetMapperFlags(emf.readDWORD());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeDWORD(flags);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  flags: " + flags;
	}
}
