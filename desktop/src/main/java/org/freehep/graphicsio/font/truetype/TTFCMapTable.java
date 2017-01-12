// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.font.truetype;

import java.io.IOException;

/**
 * CMAP Table.
 * 
 * @author Simon Fischer
 * @version $Id: TTFCMapTable.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public class TTFCMapTable extends TTFTable {

	public class EncodingTable {
		public int platformID;

		public int encodingID;

		public long offset;

		public int format;

		public int length;

		public int version;

		public TableFormat tableFormat;

		public void readHeader() throws IOException {
			platformID = ttf.readUShort();
			encodingID = ttf.readUShort();
			offset = ttf.readULong();
		}

		public void readBody() throws IOException {
			ttf.seek(offset);
			format = ttf.readUShort();
			length = ttf.readUShort();
			version = ttf.readUShort();
			switch (format) {
			case 0:
				tableFormat = new TableFormat0();
				break;
			case 4:
				tableFormat = new TableFormat4();
				break;
			case 2:
			case 6:
				System.err.println(
						"Unimplementet encoding table format: " + format);
				break;
			default:
				System.err.println(
						"Illegal value for encoding table format: " + format);
				break;
			}
			if (tableFormat != null) {
				tableFormat.read();
			}
		}

		@Override
		public String toString() {
			String str = "[encoding] PID:" + platformID + " EID:" + encodingID
					+ " format:" + format + " v" + version
					+ (tableFormat != null ? tableFormat.toString()
							: " [no data read]");
			return str;
		}
	}

	public abstract class TableFormat {
		public abstract void read() throws IOException;

		public abstract int getGlyphIndex(int character);
	}

	public class TableFormat0 extends TableFormat {

		public int[] glyphIdArray = new int[256];

		@Override
		public void read() throws IOException {
			for (int i = 0; i < glyphIdArray.length; i++) {
				glyphIdArray[i] = ttf.readByte();
			}
		}

		@Override
		public String toString() {
			String str = "";
			for (int i = 0; i < glyphIdArray.length; i++) {
				if (i % 16 == 0) {
					str += "\n    " + Integer.toHexString(i / 16) + "x: ";
				}
				String number = glyphIdArray[i] + "";
				while (number.length() < 3) {
					number = " " + number;
				}
				str += number + " ";
			}
			return str;
		}

		@Override
		public int getGlyphIndex(int character) {
			return glyphIdArray[character];
		}
	}

	public class TableFormat4 extends TableFormat {

		public int segCount;

		public int[] endCount, startCount, idRangeOffset;

		public short[] idDelta; // could be int (ushort) as well

		@Override
		public void read() throws IOException {
			segCount = ttf.readUShort() / 2;
			// dump the next three ushorts to /dev/null as they guy
			// who invented them really must have drunk a lot
			ttf.readUShort();
			ttf.readUShort();
			ttf.readUShort();

			// endCount = readFFFFTerminatedUShortArray();
			endCount = ttf.readUShortArray(segCount);
			int reservedPad = ttf.readUShort();
			if (reservedPad != 0) {
				System.err
						.println("reservedPad not 0, but " + reservedPad + ".");
			}

			startCount = ttf.readUShortArray(endCount.length);
			// the deltas should be unsigned, but due to
			// modulo arithmetic it makes no difference
			idDelta = ttf.readShortArray(endCount.length);
			idRangeOffset = ttf.readUShortArray(endCount.length);
		}

		@Override
		public String toString() {
			String str = "\n   " + endCount.length + " sections:";
			for (int i = 0; i < endCount.length; i++) {
				str += "\n    " + startCount[i] + " to " + endCount[i] + " : "
						+ idDelta[i] + " (" + idRangeOffset[i] + ")";
			}
			return str;
		}

		@Override
		public int getGlyphIndex(int character) {
			return 0;
		}
	}

	public int version;

	public EncodingTable encodingTable[];

	@Override
	public String getTag() {
		return "cmap";
	}

	@Override
	public void readTable() throws IOException {
		version = ttf.readUShort();
		encodingTable = new EncodingTable[ttf.readUShort()];
		for (int i = 0; i < encodingTable.length; i++) {
			encodingTable[i] = new EncodingTable();
			encodingTable[i].readHeader();
		}
		for (int i = 0; i < encodingTable.length; i++) {
			encodingTable[i].readBody();
		}
	}

	@Override
	public String toString() {
		String str = super.toString() + " v" + version;
		for (int i = 0; i < encodingTable.length; i++) {
			str += "\n  " + encodingTable[i];
		}
		return str;
	}
}
