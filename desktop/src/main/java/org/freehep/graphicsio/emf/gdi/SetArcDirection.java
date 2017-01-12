// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetArcDirection TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetArcDirection.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SetArcDirection extends EMFTag implements EMFConstants {

	private int direction;

	public SetArcDirection() {
		super(57, 1);
	}

	public SetArcDirection(int direction) {
		this();
		this.direction = direction;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		SetArcDirection tag = new SetArcDirection(emf.readDWORD());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeDWORD(direction);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  direction: " + direction;
	}
}
