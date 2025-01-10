// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The ResetClip metafile record specifies that the clipping region is set back
 * to an infinite area.
 * 
 * @author Mark Donszelmann
 * @version $Id: ResetClip.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public class ResetClip extends EMFPlusTag {

	public ResetClip() {
		super(49, 1);
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		return new ResetClip();
	}

	@Override
	public void write(int tagID, int flags, EMFOutputStream emf)
			throws IOException {
		// nop
	}

}
