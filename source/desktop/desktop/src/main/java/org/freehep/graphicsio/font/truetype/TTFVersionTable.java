// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.font.truetype;

import java.io.IOException;

/**
 * VERSION Table.
 * 
 * @author Simon Fischer
 * @version $Id: TTFVersionTable.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public abstract class TTFVersionTable extends TTFTable {

	public int minorVersion;

	public int majorVersion;

	public void readVersion() throws IOException {
		majorVersion = ttf.readUShort();
		minorVersion = ttf.readUShort();
	}

	@Override
	public String toString() {
		return super.toString() + " v" + majorVersion + "." + minorVersion;
	}

}
