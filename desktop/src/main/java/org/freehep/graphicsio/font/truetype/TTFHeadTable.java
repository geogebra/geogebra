// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.font.truetype;

import java.awt.Rectangle;
import java.io.IOException;

/**
 * HEAD Table.
 * 
 * @author Simon Fischer
 * @version $Id: TTFHeadTable.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public class TTFHeadTable extends TTFVersionTable {

	public static final int FDH_MIXED = 0;

	public static final int FDH_LEFT_TO_RIGHT = 1;

	public static final int FDH_LEFT_TO_RIGHT_NEUTRAL = 2;

	public static final int FDH_RIGHT_TO_LEFT = -1;

	public static final int FDH_RIGHT_TO_LEFT_NEUTRAL = -2;

	public static final int ITLF_SHORT = 0;

	public static final int ITLF_LONG = 1;

	public int fontRevisionMinor, fontRevisionMajor;

	public long checkSumAdjustment;

	public long magicNumber;

	public boolean baseline0, sidebearing0, instrDependOnSize, forcePPEM2Int,
			instrAlterAdvance;

	public int unitsPerEm;

	public byte[] created = new byte[8];

	public byte[] modified = new byte[8];

	public short xMin, yMin, xMax, yMax;

	public boolean macBold, macItalic;

	public int lowestRecPPEM;

	public short fontDirectionHint;

	public short indexToLocFormat, glyphDataFormat;

	@Override
	public String getTag() {
		return "head";
	}

	@Override
	public void readTable() throws IOException {
		readVersion();

		fontRevisionMajor = ttf.readUShort();
		fontRevisionMinor = ttf.readUShort();

		checkSumAdjustment = ttf.readULong();
		magicNumber = ttf.readULong();

		ttf.readUShortFlags(); // flags
		baseline0 = ttf.flagBit(0);
		sidebearing0 = ttf.flagBit(1);
		instrDependOnSize = ttf.flagBit(2);
		forcePPEM2Int = ttf.flagBit(3);
		instrAlterAdvance = ttf.flagBit(4);

		unitsPerEm = ttf.readUShort();

		ttf.readFully(created);
		ttf.readFully(modified);

		xMin = ttf.readShort();
		yMin = ttf.readShort();
		xMax = ttf.readShort();
		yMax = ttf.readShort();

		ttf.readUShortFlags(); // macstyle
		macBold = ttf.flagBit(0);
		macItalic = ttf.flagBit(1);

		lowestRecPPEM = ttf.readUShort();
		fontDirectionHint = ttf.readShort();
		indexToLocFormat = ttf.readShort();
		if ((indexToLocFormat != ITLF_LONG) && (indexToLocFormat != ITLF_SHORT)) {
			System.err.println(
					"Unknown value for indexToLocFormat: " + indexToLocFormat);
		}
		glyphDataFormat = ttf.readShort();
	}

	@Override
	public String toString() {
		String str = super.toString() + "\n" + "  magicNumber: 0x"
				+ Integer.toHexString((int) magicNumber) + " ("
				+ (magicNumber == 0x5f0f3cf5 ? "ok" : "wrong") + ")\n";
		str += "  indexToLocFormat: " + indexToLocFormat + " ";
		if (indexToLocFormat == ITLF_LONG) {
			str += " (long)\n";
		} else if (indexToLocFormat == ITLF_SHORT) {
			str += "(short)\n";
		} else {
			str += "(illegal value)\n";
		}
		str += "  bbox: (" + xMin + "," + yMin + ") : (" + xMax + "," + yMax
				+ ")";
		return str;
	}

	public Rectangle getMaxCharBounds() {
		return new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
	}
}
