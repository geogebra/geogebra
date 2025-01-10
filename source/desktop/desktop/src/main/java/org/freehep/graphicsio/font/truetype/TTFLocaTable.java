// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.font.truetype;

import java.io.IOException;

/**
 * LOCA Table.
 * 
 * @author Simon Fischer
 * @version $Id: TTFLocaTable.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public class TTFLocaTable extends TTFTable {

	public long offset[];

	@Override
	public String getTag() {
		return "loca";
	}

	@Override
	public void readTable() throws IOException {
		short format = ((TTFHeadTable) getTable("head")).indexToLocFormat;
		int numGlyphs = ((TTFMaxPTable) getTable("maxp")).numGlyphs + 1;
		offset = new long[numGlyphs];
		for (int i = 0; i < numGlyphs; i++) {
			offset[i] = (format == TTFHeadTable.ITLF_LONG ? ttf.readULong()
					: ttf.readUShort() * 2);
		}
	}

	@Override
	public String toString() {
		String str = super.toString();
		for (int i = 0; i < offset.length; i++) {
			if (i % 16 == 0) {
				str += "\n  ";
			}
			str += offset[i] + " ";
		}
		return str;
	}
}
