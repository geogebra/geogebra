// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetMiterLimit TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetMiterLimit.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SetMiterLimit extends EMFTag {

	private int limit;

	public SetMiterLimit() {
		super(58, 1);
	}

	public SetMiterLimit(int limit) {
		this();
		this.limit = limit;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		SetMiterLimit tag = new SetMiterLimit(emf.readDWORD());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeDWORD(limit);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  limit: " + limit;
	}
}
