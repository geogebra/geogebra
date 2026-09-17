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

/**
 * Stores single bits as a word in the buffer. If a bit is set, the
 * corresponding word in the buffer will be non-zero. If a bit is clear, the
 * corresponding word is zero. Although this may seem waseful, this can be a
 * factor of two quicker than packing 8 bits to a byte and extracting.
 */
public class BitReservoir {

	private static final int BUFFER_SIZE = 4096 * 8;
	private static final int BUFFER_SIZE_MASK = BUFFER_SIZE - 1;
	private int offset, bitCount, bufferIndex;
	private final int[] buffer = new int[BUFFER_SIZE];

	int getBitCount() {
		return bitCount;
	}

	int getBits(int count) {
		int n = count;
		bitCount += n;
		int val = 0;
		int pos = bufferIndex;
		if (bufferIndex + n < BUFFER_SIZE) {
			while (n-- > 0) {
				val <<= 1;
				val |= ((buffer[pos++] != 0) ? 1 : 0);
			}
		} else {
			while (n-- > 0) {
				val <<= 1;
				val |= ((buffer[pos] != 0) ? 1 : 0);
				pos = (pos + 1) & BUFFER_SIZE_MASK;
			}
		}

		bufferIndex = pos;
		return val;
	}

	int getOneBit() {
		bitCount++;
		int val = buffer[bufferIndex];
		bufferIndex = (bufferIndex + 1) & BUFFER_SIZE_MASK;
		return val;
	}

	void putByte(int val) {
		int ofs = offset;
		buffer[ofs++] = val & 0x80;
		buffer[ofs++] = val & 0x40;
		buffer[ofs++] = val & 0x20;
		buffer[ofs++] = val & 0x10;
		buffer[ofs++] = val & 0x08;
		buffer[ofs++] = val & 0x04;
		buffer[ofs++] = val & 0x02;
		buffer[ofs++] = val & 0x01;
		if (ofs == BUFFER_SIZE) {
			offset = 0;
		} else {
			offset = ofs;
		}
	}

	void rewindBits(int n) {
		bitCount -= n;
		bufferIndex -= n;
		if (bufferIndex < 0) {
			bufferIndex += BUFFER_SIZE;
		}
	}

	void rewindBytes(int n) {
		int bits = n << 3;
		bitCount -= bits;
		bufferIndex -= bits;
		if (bufferIndex < 0) {
			bufferIndex += BUFFER_SIZE;
		}
	}
}
