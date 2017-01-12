// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * AbortPath TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: AbortPath.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class AbortPath extends EMFTag {

	public AbortPath() {
		super(68, 1);
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		return this;
	}
}
