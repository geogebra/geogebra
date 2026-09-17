/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.sound.mp3transform;

final class Huffman {
	private static final int MX_OFF = 250;
	private static final int HUFFMAN_COUNT = 34;

	@SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
	static final Huffman[] HUFFMAN;

	private final char tableName0;
	private final char tableName1;
	private final int xlen; // max. x-index+
	private final int ylen; // max. y-index+
	private final int linbits; // number of linbits
	private final int[] val0; // decoder tree
	private final int[] val1; // decoder tree
	private final int treeLen; // length of decoder tree

	private Huffman(String name, int xlen, int ylen, int linbits, int[][] val, int treeLen) {
		tableName0 = name.charAt(0);
		tableName1 = name.charAt(1);
		this.xlen = xlen;
		this.ylen = ylen;
		this.linbits = linbits;
		val0 = new int[val.length];
		val1 = new int[val.length];
		for (int i = 0; i < val.length; i++) {
			val0[i] = val[i][0];
			val1[i] = val[i][1];
		}
		this.treeLen = treeLen;
	}

	/**
	 * Do the huffman-decoding. For counta, countb - the 4 bit value is returned
	 * in y, discard x.
	 */
	int decode(Layer3Decoder decoder, BitReservoir br) {
		// array of all huffcodtable headers
		// 0..31 Huffman code table 0..31
		// 32,33 count1-tables

		// table 0 needs no bits
		if (treeLen == 0) {
			decoder.x = decoder.y = 0;
			return 0;
		}
		int level = 1 << ((4 * 8) - 1);
		int point = 0;
		int error = 1;
		// Lookup in Huffman table
		do {
			if (val0[point] == 0) {
				// end of tree
				decoder.x = val1[point] >>> 4;
				decoder.y = val1[point] & 0xf;
				error = 0;
				break;
			}
			if (br.getOneBit() != 0) {
				while (val1[point] >= MX_OFF) {
					point += val1[point];
				}
				point += val1[point];
			} else {
				while (val0[point] >= MX_OFF) {
					point += val0[point];
				}
				point += val0[point];
			}
			level >>>= 1;
		} while (level != 0);
		// Process sign encodings for quadruples tables
		if (tableName0 == '3' && (tableName1 == '2' || tableName1 == '3')) {
			decoder.v = (decoder.y >> 3) & 1;
			decoder.w = (decoder.y >> 2) & 1;
			decoder.x = (decoder.y >> 1) & 1;
			decoder.y = decoder.y & 1;
			// v, w, x and y are reversed in the bitstream
			// switch them around to make test bitstream work.
			if (decoder.v != 0) {
				if (br.getOneBit() != 0) {
					decoder.v = -decoder.v;
				}
			}
			if (decoder.w != 0) {
				if (br.getOneBit() != 0) {
					decoder.w = -decoder.w;
				}
			}
			if (decoder.x != 0) {
				if (br.getOneBit() != 0) {
					decoder.x = -decoder.x;
				}
			}
			if (decoder.y != 0) {
				if (br.getOneBit() != 0) {
					decoder.y = -decoder.y;
				}
			}
		} else {
			// Process sign and escape encodings for dual tables.
			// x and y are reversed in the test bitstream.
			// Reverse x and y here to make test bitstream work.
			if (linbits != 0) {
				if ((xlen - 1) == decoder.x) {
					decoder.x += br.getBits(linbits);
				}
			}
			if (decoder.x != 0) {
				if (br.getOneBit() != 0) {
					decoder.x = -decoder.x;
				}
			}
			if (linbits != 0) {
				if ((ylen - 1) == decoder.y) {
					decoder.y += br.getBits(linbits);
				}
			}
			if (decoder.y != 0) {
				if (br.getOneBit() != 0) {
					decoder.y = -decoder.y;
				}
			}
		}
		return error;
	}

	static {
		HUFFMAN = new Huffman[HUFFMAN_COUNT];
		HUFFMAN[0] = new Huffman("0 ", 0, 0, 0, Constants.VAL_TAB_0, 0);
		HUFFMAN[1] = new Huffman("1 ", 2, 2, 0, Constants.VAL_TAB_1, 7);
		HUFFMAN[2] = new Huffman("2 ", 3, 3, 0, Constants.VAL_TAB_2, 17);
		HUFFMAN[3] = new Huffman("3 ", 3, 3, 0, Constants.VAL_TAB_3, 17);
		HUFFMAN[4] = new Huffman("4 ", 0, 0, 0, Constants.VAL_TAB_4, 0);
		HUFFMAN[5] = new Huffman("5 ", 4, 4, 0, Constants.VAL_TAB_5, 31);
		HUFFMAN[6] = new Huffman("6 ", 4, 4, 0, Constants.VAL_TAB_6, 31);
		HUFFMAN[7] = new Huffman("7 ", 6, 6, 0, Constants.VAL_TAB_7, 71);
		HUFFMAN[8] = new Huffman("8 ", 6, 6, 0, Constants.VAL_TAB_8, 71);
		HUFFMAN[9] = new Huffman("9 ", 6, 6, 0, Constants.VAL_TAB_9, 71);
		HUFFMAN[10] = new Huffman("10", 8, 8, 0, Constants.VAL_TAB_10, 127);
		HUFFMAN[11] = new Huffman("11", 8, 8, 0, Constants.VAL_TAB_11, 127);
		HUFFMAN[12] = new Huffman("12", 8, 8, 0, Constants.VAL_TAB_12, 127);
		HUFFMAN[13] = new Huffman("13", 16, 16, 0, Constants.VAL_TAB_13, 511);
		HUFFMAN[14] = new Huffman("14", 0, 0, 0, Constants.VAL_TAB_14, 0);
		HUFFMAN[15] = new Huffman("15", 16, 16, 0, Constants.VAL_TAB_15, 511);
		HUFFMAN[16] = new Huffman("16", 16, 16, 1, Constants.VAL_TAB_16, 511);
		HUFFMAN[17] = new Huffman("17", 16, 16, 2, Constants.VAL_TAB_16, 511);
		HUFFMAN[18] = new Huffman("18", 16, 16, 3, Constants.VAL_TAB_16, 511);
		HUFFMAN[19] = new Huffman("19", 16, 16, 4, Constants.VAL_TAB_16, 511);
		HUFFMAN[20] = new Huffman("20", 16, 16, 6, Constants.VAL_TAB_16, 511);
		HUFFMAN[21] = new Huffman("21", 16, 16, 8, Constants.VAL_TAB_16, 511);
		HUFFMAN[22] = new Huffman("22", 16, 16, 10, Constants.VAL_TAB_16, 511);
		HUFFMAN[23] = new Huffman("23", 16, 16, 13, Constants.VAL_TAB_16, 511);
		HUFFMAN[24] = new Huffman("24", 16, 16, 4, Constants.VAL_TAB_24, 512);
		HUFFMAN[25] = new Huffman("25", 16, 16, 5, Constants.VAL_TAB_24, 512);
		HUFFMAN[26] = new Huffman("26", 16, 16, 6, Constants.VAL_TAB_24, 512);
		HUFFMAN[27] = new Huffman("27", 16, 16, 7, Constants.VAL_TAB_24, 512);
		HUFFMAN[28] = new Huffman("28", 16, 16, 8, Constants.VAL_TAB_24, 512);
		HUFFMAN[29] = new Huffman("29", 16, 16, 9, Constants.VAL_TAB_24, 512);
		HUFFMAN[30] = new Huffman("30", 16, 16, 11, Constants.VAL_TAB_24, 512);
		HUFFMAN[31] = new Huffman("31", 16, 16, 13, Constants.VAL_TAB_24, 512);
		HUFFMAN[32] = new Huffman("32", 1, 16, 0, Constants.VAL_TAB_32, 31);
		HUFFMAN[33] = new Huffman("33", 1, 16, 0, Constants.VAL_TAB_33, 31);
	}
}
