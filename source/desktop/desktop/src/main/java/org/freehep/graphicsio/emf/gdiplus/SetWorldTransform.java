// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.awt.geom.AffineTransform;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The SetWorldTransform metafile record represents a change in the
 * Graphics.Transform property, which sets the world transformation matrix of
 * the graphics object.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetWorldTransform.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public class SetWorldTransform extends EMFPlusTag {

	private AffineTransform transform;

	public SetWorldTransform() {
		super(42, 1);
	}

	public SetWorldTransform(AffineTransform transform) {
		this();
		flags = 0;
		this.transform = transform;
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		SetWorldTransform tag = new SetWorldTransform();
		tag.flags = flags;
		tag.transform = new AffineTransform(emf.readFLOAT(), emf.readFLOAT(),
				emf.readFLOAT(), emf.readFLOAT(), emf.readFLOAT(),
				emf.readFLOAT());
		return tag;
	}

	@Override
	public void write(int tagID, int flags, EMFOutputStream emf)
			throws IOException {
		GDIPlusObject.writeTransform(emf, transform);
	}

	@Override
	public String toString() {
		return super.toString() + "\n  " + transform;
	}
}
