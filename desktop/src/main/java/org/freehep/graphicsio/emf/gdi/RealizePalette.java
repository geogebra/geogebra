// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * RealizePalette TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: RealizePalette.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class RealizePalette extends EMFTag {

	public RealizePalette() {
		super(52, 1);
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		return this;
	}

}
