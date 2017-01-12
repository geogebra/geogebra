// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.font.truetype;

import java.io.IOException;

/**
 * HMTX Table.
 * 
 * @author Simon Fischer
 * @version $Id: TTFHMtxTable.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public class TTFHMtxTable extends TTFTable {

	public int[] advanceWidth;

	public short[] leftSideBearing;

	public short[] leftSideBearing2;

	@Override
	public String getTag() {
		return "hmtx";
	}

	@Override
	public void readTable() throws IOException {
		int numberOfHMetrics = ((TTFHHeaTable) getTable(
				"hhea")).numberOfHMetrics;
		int numGlyphs = ((TTFMaxPTable) getTable("maxp")).numGlyphs;

		advanceWidth = new int[numberOfHMetrics];
		leftSideBearing = new short[numberOfHMetrics];
		for (int i = 0; i < numberOfHMetrics; i++) {
			advanceWidth[i] = ttf.readUFWord();
			leftSideBearing[i] = ttf.readFWord();
		}

		leftSideBearing2 = ttf.readShortArray(numGlyphs - numberOfHMetrics);
	}

	@Override
	public String toString() {
		String str = super.toString();
		str += "\n  hMetrics[" + advanceWidth.length + "] = {";
		for (int i = 0; i < advanceWidth.length; i++) {
			if (i % 8 == 0) {
				str += "\n    ";
			}
			str += "(" + advanceWidth[i] + "," + leftSideBearing[i] + ") ";
		}
		str += "\n  }";
		str += "\n  lsb[" + leftSideBearing2.length + "] = {";
		for (int i = 0; i < leftSideBearing2.length; i++) {
			if (i % 16 == 0) {
				str += "\n    ";
			}
			str += leftSideBearing2[i] + " ";
		}
		str += "\n  }";
		return str;
	}
}
