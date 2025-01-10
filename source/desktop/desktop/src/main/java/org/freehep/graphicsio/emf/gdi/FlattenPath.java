// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * FlattenPath TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: FlattenPath.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class FlattenPath extends EMFTag {

	public FlattenPath() {
		super(65, 1);
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		return this;
	}

}
