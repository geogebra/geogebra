// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.font.truetype;

import java.io.IOException;

import org.geogebra.common.util.Charsets;

/**
 * OS/2 Table.
 * 
 * @author Simon Fischer
 * @version $Id: TTFOS_2Table.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public class TTFOS_2Table extends TTFVersionTable {

	public int version;

	public short xAvgCharWidth;

	public int usWeightClass, usWidthClass;

	public short fsType;

	public short ySubscriptXSize, ySubscriptYSize, ySubscriptXOffset,
			ySubscriptYOffset;

	public short ySuperscriptXSize, ySuperscriptYSize, ySuperscriptXOffset,
			ySuperscriptYOffset;

	public short yStrikeoutSize, yStrikeoutPosition;

	public short sFamilyClass;

	public byte[] panose = new byte[10];

	public long[] ulUnicode = new long[4];

	public byte[] achVendID = new byte[4];

	public int fsSelection;

	public int usFirstCharIndex, usLastCharIndes;

	public int sTypoAscender, sTzpoDescender, sTypoLineGap;

	public int usWinAscent, usWinDescent;

	public long[] ulCodePageRange = new long[2];

	@Override
	public String getTag() {
		return "OS/2";
	}

	@Override
	public void readTable() throws IOException {

		version = ttf.readUShort();
		xAvgCharWidth = ttf.readShort();
		usWeightClass = ttf.readUShort();
		usWidthClass = ttf.readUShort();
		fsType = ttf.readShort();

		ySubscriptXSize = ttf.readShort();
		ySubscriptYSize = ttf.readShort();
		ySubscriptXOffset = ttf.readShort();
		ySubscriptYOffset = ttf.readShort();
		ySuperscriptXSize = ttf.readShort();
		ySuperscriptYSize = ttf.readShort();
		ySuperscriptXOffset = ttf.readShort();
		ySuperscriptYOffset = ttf.readShort();
		yStrikeoutSize = ttf.readShort();
		yStrikeoutPosition = ttf.readShort();

		sFamilyClass = ttf.readShort();

		ttf.readFully(panose);

		for (int i = 0; i < ulUnicode.length; i++) {
			ulUnicode[i] = ttf.readULong();
		}
		ttf.readFully(achVendID);
		fsSelection = ttf.readUShort();

		usFirstCharIndex = ttf.readUShort();
		usLastCharIndes = ttf.readUShort();

		sTypoAscender = ttf.readUShort();
		sTzpoDescender = ttf.readUShort();
		sTypoLineGap = ttf.readUShort();

		usWinAscent = ttf.readUShort();
		usWinDescent = ttf.readUShort();

		ulCodePageRange[0] = ttf.readULong();
		ulCodePageRange[1] = ttf.readULong();

	}

	public String getAchVendID() {
		return new String(achVendID, Charsets.getUtf8());
	}

	@Override
	public String toString() {
		return super.toString() + "\n  version: " + version + "\n  vendor: "
				+ getAchVendID();
	}
}
