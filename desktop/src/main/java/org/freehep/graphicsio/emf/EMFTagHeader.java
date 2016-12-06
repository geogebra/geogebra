// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf;

import org.freehep.util.io.TagHeader;

/**
 * Special TagHeader for EMF to include flags.
 * 
 * @author duns
 * @version $Id: EMFTagHeader.java,v 1.1 2009-08-17 21:44:45 murkle Exp $
 */
public class EMFTagHeader extends TagHeader {

	private int flags;

	public EMFTagHeader(int tagID, long len, int flags) {
		super(tagID, len);
		this.flags = flags;
	}

	public int getFlags() {
		return flags;
	}
}
