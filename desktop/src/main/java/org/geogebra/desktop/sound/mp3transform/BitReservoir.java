/*
 * 11/19/04 1.0 moved to LGPL.
 * 
 * 12/12/99 0.0.7 Implementation stores single bits as ints for better performance. mdm@techie.com.
 *
 * 02/28/99 0.0     Java Conversion by E.B, javalayer@javazoom.net
 *
 *                  Adapted from the public c code by Jeff Tsay.
 *
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
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

	int getBits(int n) {
		bitCount += n;
		int val = 0;
		int pos = bufferIndex;
		if (pos + n < BUFFER_SIZE) {
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
		int bits = (n << 3);
		bitCount -= bits;
		bufferIndex -= bits;
		if (bufferIndex < 0) {
			bufferIndex += BUFFER_SIZE;
		}
	}

}
