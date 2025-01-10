// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The SetClipPath metafile record represents a call to Graphics.SetClip, with a
 * GraphicsPath parameter, which sets the clipping region of the drawing
 * surface.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetClipPath.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public class SetClipPath extends EMFPlusTag {

	public final static int REPLACE = 0;
	public final static int INTERSECT = 1;
	public final static int UNION = 2;
	public final static int XOR = 3;
	public final static int EXCLUDE = 4;
	public final static int COMPLEMENT = 5; // (Exclude From)

	public SetClipPath() {
		super(51, 1);
	}

	public SetClipPath(int clipIndex, int mode) {
		this();
		flags = clipIndex | (mode << 8);
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		SetClipPath tag = new SetClipPath();
		tag.flags = flags;
		return tag;
	}

	@Override
	public void write(int tagID, int flags, EMFOutputStream emf)
			throws IOException {
		// nop
	}

}
