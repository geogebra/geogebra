package org.freehep.graphicsio.font.truetype;

import java.io.IOException;

/**
 * HHEA Table.
 * 
 * @author Simon Fischer
 * @version $Id: TTFHHeaTable.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public class TTFHHeaTable extends TTFVersionTable {

	public short ascender, descender, lineGap;

	public int advanceWidthMax;

	public short minLeftSideBearing, minRightSideBearing;

	public short xMaxExtent;

	public short caretSlopeRise, caretSlopeRun;

	public short metricDataFormat;

	public int numberOfHMetrics;

	@Override
	public String getTag() {
		return "hhea";
	}

	@Override
	public void readTable() throws IOException {
		readVersion();

		ascender = ttf.readFWord();
		descender = ttf.readFWord();
		lineGap = ttf.readFWord();

		advanceWidthMax = ttf.readUFWord();
		minLeftSideBearing = ttf.readFWord();
		minRightSideBearing = ttf.readFWord();

		xMaxExtent = ttf.readFWord();

		caretSlopeRise = ttf.readShort();
		caretSlopeRun = ttf.readShort();

		for (int i = 0; i < 5; i++) {
			ttf.checkShortZero();
		}

		metricDataFormat = ttf.readShort();
		numberOfHMetrics = ttf.readUShort();
	}

	@Override
	public String toString() {
		String str = super.toString();
		str += "\n  asc:" + ascender + " desc:" + descender + " lineGap:"
				+ lineGap + " maxAdvance:" + advanceWidthMax;
		str += "\n  metricDataFormat:" + metricDataFormat + " #HMetrics:"
				+ numberOfHMetrics;
		return str;
	}
}
