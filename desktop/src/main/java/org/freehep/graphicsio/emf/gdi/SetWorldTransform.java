// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.geom.AffineTransform;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetWorldTransform TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetWorldTransform.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class SetWorldTransform extends EMFTag {

	private AffineTransform transform;

	public SetWorldTransform() {
		super(35, 1);
	}

	public SetWorldTransform(AffineTransform transform) {
		this();
		this.transform = transform;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		SetWorldTransform tag = new SetWorldTransform(emf.readXFORM());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeXFORM(transform);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  transform: " + transform;
	}
}
