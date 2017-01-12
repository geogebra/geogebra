// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.awt.geom.AffineTransform;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The MultiplyWorldTransform metafile record represents a call to
 * Graphics.MultiplyTransform, which multiplies the current transformation
 * matrix by a given matrix value.
 * 
 * @author Mark Donszelmann
 * @version $Id: MultiplyWorldTransform.java,v 1.1 2009-08-17 21:44:44 murkle
 *          Exp $
 */
public class MultiplyWorldTransform extends EMFPlusTag {

	private static final int CONCATENATE = 0x2000;
	private AffineTransform transform;

	public MultiplyWorldTransform() {
		super(44, 1);
	}

	public MultiplyWorldTransform(AffineTransform transform, boolean prepend) {
		this();
		flags = prepend ? 0x0000 : CONCATENATE;
		this.transform = transform;
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		MultiplyWorldTransform tag = new MultiplyWorldTransform();
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
}
