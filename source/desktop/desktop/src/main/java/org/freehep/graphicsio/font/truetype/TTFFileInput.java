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

package org.freehep.graphicsio.font.truetype;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Concrete implementation of the TrueType Input for one Table, read from a TTF
 * File.
 * 
 * Reads one table from the file.
 * 
 * @author Simon Fischer
 * @version $Id: TTFFileInput.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public class TTFFileInput extends TTFInput {

	private RandomAccessFile ttf;

	private long offset, length, checksum;

	public TTFFileInput(RandomAccessFile file, long offset, long length,
			long checksum) throws IOException {
		this.ttf = file;
		this.offset = offset;
		this.length = length;
		this.checksum = checksum;
	}

	// --------------- IO ---------------

	@Override
	public void seek(long offset) throws IOException {
		ttf.seek(this.offset + offset);
		// System.out.println("seek "+(this.offset+offset));
	}

	@Override
	long getPointer() throws IOException {
		return ttf.getFilePointer() - offset;
	}

	// ---------- Simple Data Types --------------

	@Override
	public int readByte() throws IOException {
		return ttf.readUnsignedByte();
	}

	@Override
	public int readRawByte() throws IOException {
		return ttf.readByte() & 255;
	}

	@Override
	public short readShort() throws IOException {
		return ttf.readShort();
	}

	@Override
	public int readUShort() throws IOException {
		return ttf.readUnsignedShort();
	}

	@Override
	public int readLong() throws IOException {
		return ttf.readInt();
	}

	@Override
	public long readULong() throws IOException {
		byte[] temp = new byte[4];
		ttf.readFully(temp);
		long l = 0;
		long weight = 1;
		for (int i = 0; i < temp.length; i++) {
			// l |= (temp[3-i]&255) << (8*i);
			l += (temp[3 - i] & 255) * weight;
			weight *= 256;
		}
		return l;
	}

	@Override
	public byte readChar() throws IOException {
		return ttf.readByte();
	}

	// ---------------- Arrays -------------------

	@Override
	public void readFully(byte[] b) throws IOException {
		ttf.readFully(b);
	}

	@Override
	public String toString() {
		return offset + "-" + (offset + length - 1) + " - " + checksum;
	}
}
