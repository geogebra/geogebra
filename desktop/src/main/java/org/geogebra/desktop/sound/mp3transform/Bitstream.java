/*
 * 11/19/04 1.0 moved to LGPL.
 * 
 * 11/17/04 Uncomplete frames discarded. E.B, javalayer@javazoom.net 
 *
 * 12/05/03 ID3v2 tag returned. E.B, javalayer@javazoom.net 
 *
 * 12/12/99 Based on Ibitstream. Exceptions thrown on errors, Temporary removed seek functionality. mdm@techie.com
 *
 * 02/12/99 : Java Conversion by E.B , javalayer@javazoom.net
 *
 * 04/14/97 : Added function prototypes for new syncing and seeking
 * mechanisms. Also made this file portable. Changes made by Jeff Tsay
 *
 *  @(#) ibitstream.h 1.5, last edit: 6/15/94 16:55:34
 *  @(#) Copyright (C) 1993, 1994 Tobias Bading (bading@cs.tu-berlin.de)
 *  @(#) Berlin University of Technology
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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * This class is responsible for parsing an MPEG audio bitstream.
 */
public final class Bitstream {
	/**
	 * Synchronization control constant for the initial synchronization to the
	 * start of a frame.
	 */
	static final byte INITIAL_SYNC = 0;
	/**
	 * Synchronization control constant for non-initial frame synchronizations.
	 */
	static final byte STRICT_SYNC = 1;
	/**
	 * Maximum size of the frame buffer. max. 1730 bytes per frame: 144 *
	 * 384kbit/s / 32000 Hz + 2 Bytes CRC
	 */
	private static final int BUFFER_INT_SIZE = 433;
	/**
	 * The frame buffer that holds the data for the current frame.
	 */
	private final int[] frameBuffer = new int[BUFFER_INT_SIZE];
	/**
	 * Number of valid bytes in the frame buffer.
	 */
	private int frameSize;
	/**
	 * The bytes read from the stream.
	 */
	private final byte[] frameBytes = new byte[BUFFER_INT_SIZE * 4];
	// Index into frameBuffer where the next bits are retrieved.
	private int wordPointer;
	/**
	 * Number (0-31, from MSB to LSB) of next bit for getBits()
	 */
	private int bitIndex;
	private int syncWord;
	private boolean singleChMode;
	private static final int[] BITMASK = { 0, // dummy
			0x00000001, 0x00000003, 0x00000007, 0x0000000F, 0x0000001F,
			0x0000003F, 0x0000007F, 0x000000FF, 0x000001FF, 0x000003FF,
			0x000007FF, 0x00000FFF, 0x00001FFF, 0x00003FFF, 0x00007FFF,
			0x0000FFFF, 0x0001FFFF };
	private final PushbackInputStream source;
	private final Header header = new Header();
	private final byte[] syncBuffer = new byte[4];
	private byte[] rawID3v2 = null;
	private boolean firstFrame = true;

	public Bitstream(InputStream in) {
		source = new PushbackInputStream(in, BUFFER_INT_SIZE * 4);
		loadID3v2();
		closeFrame();
	}

	private void loadID3v2() {
		int size = -1;
		try {
			// read ID3v2 header
			source.mark(10);
			size = readID3v2Header();
		} catch (IOException e) {
			// ignore
		} finally {
			try {
				// unread ID3v2 header
				source.reset();
			} catch (IOException e) {
				// ignore
			}
		}
		// load ID3v2 tags.
		try {
			if (size > 0) {
				rawID3v2 = new byte[size];
				this.readBytes(rawID3v2, 0, rawID3v2.length);
			}
		} catch (IOException e) {
			// ignore
		}
	}

	/**
	 * Parse ID3v2 tag header to find out size of ID3v2 frames.
	 * 
	 * @param in
	 *            MP3 InputStream
	 * @return size of ID3v2 frames + header
	 * @throws IOException
	 * @author JavaZOOM
	 */
	private int readID3v2Header() throws IOException {
		byte[] buff = new byte[4];
		int size = -10;
		readBytes(buff, 0, 3);
		if (buff[0] == 'I' && buff[1] == 'D' && buff[2] == '3') {
			readBytes(buff, 0, 3);
			readBytes(buff, 0, 4);
			size = (buff[0] << 21) + (buff[1] << 14) + (buff[2] << 7) + buff[3];
		}
		return size + 10;
	}

	/**
	 * Reads and parses the next frame from the input source.
	 * 
	 * @return the Header describing details of the frame read, or null if the
	 *         end of the stream has been reached.
	 * @throws IOException
	 */
	public Header readFrame() throws IOException {
		try {
			Header result = readNextFrame();
			if (firstFrame) {
				result.parseVBR(frameBytes);
				firstFrame = false;
			}
			return result;
		} catch (EOFException e) {
			return null;
		}
	}

	private Header readNextFrame() throws IOException {
		if (frameSize == -1) {
			while (true) {
				boolean ok = header.readHeader(this);
				if (ok) {
					break;
				}
				closeFrame();
			}
		}
		return header;
	}

	void unreadFrame() throws IOException {
		if (wordPointer == -1 && bitIndex == -1 && frameSize > 0) {
			source.unread(frameBytes, 0, frameSize);
		}
	}

	public void closeFrame() {
		frameSize = -1;
		wordPointer = -1;
		bitIndex = -1;
	}

	/**
	 * Determines if the next 4 bytes of the stream represent a frame header.
	 */
	boolean isSyncCurrentPosition(int syncMode) throws IOException {
		int read = readBytes(syncBuffer, 0, 4);
		int headerString = ((syncBuffer[0] << 24) & 0xFF000000)
				| ((syncBuffer[1] << 16) & 0x00FF0000)
				| ((syncBuffer[2] << 8) & 0x0000FF00)
				| ((syncBuffer[3] << 0) & 0x000000FF);
		try {
			source.unread(syncBuffer, 0, read);
		} catch (IOException ex) {
			// ignore
		}
		if (read == 0) {
			return true;
		} else if (read == 4) {
			return isSyncMark(headerString, syncMode, syncWord);
		} else {
			return false;
		}
	}

	/**
	 * Get next 32 bits from bitstream. They are stored in the headerstring.
	 * syncmod allows Synchro flag ID The returned value is False at the end of
	 * stream.
	 * 
	 * @param syncMode
	 */
	int syncHeader(byte syncMode) throws IOException {
		boolean sync;
		int headerString;
		// read additional 2 bytes
		int bytesRead = readBytes(syncBuffer, 0, 3);
		if (bytesRead != 3) {
			throw new EOFException();
		}
		headerString = ((syncBuffer[0] << 16) & 0x00FF0000)
				| ((syncBuffer[1] << 8) & 0x0000FF00)
				| ((syncBuffer[2] << 0) & 0x000000FF);
		do {
			headerString <<= 8;
			if (readBytes(syncBuffer, 3, 1) != 1) {
				throw new EOFException();
			}
			headerString |= (syncBuffer[3] & 0x000000FF);
			sync = isSyncMark(headerString, syncMode, syncWord);
		} while (!sync);
		return headerString;
	}

	private boolean isSyncMark(int headerString, int syncMode, int word) {
		boolean sync = false;
		if (syncMode == INITIAL_SYNC) {
			sync = ((headerString & 0xFFE00000) == 0xFFE00000); // SZD: MPEG 2.5
		} else {
			sync = ((headerString & 0xFFF80C00) == word) && (((headerString
					& 0x000000C0) == 0x000000C0) == singleChMode);
		}
		// filter out invalid sample rate
		if (sync) {
			sync = (((headerString >>> 10) & 3) != 3);
		}
		// filter out invalid layer
		if (sync) {
			sync = (((headerString >>> 17) & 3) != 0);
		}
		// filter out invalid version
		if (sync) {
			sync = (((headerString >>> 19) & 3) != 1);
		}
		return sync;
	}

	/**
	 * Reads the data for the next frame. The frame is not parsed until parse
	 * frame is called.
	 */
	int readFrameData(int byteSize) throws IOException {
		int numread = 0;
		numread = readFully(frameBytes, 0, byteSize);
		frameSize = byteSize;
		wordPointer = -1;
		bitIndex = -1;
		return numread;
	}

	/**
	 * Parses the data previously read with readFrameData().
	 */
	void parseFrame() {
		// Convert bytes read to int
		int b = 0;
		byte[] byteRead = frameBytes;
		int byteSize = frameSize;
		for (int k = 0; k < byteSize; k = k + 4) {
			byte b0 = 0;
			byte b1 = 0;
			byte b2 = 0;
			byte b3 = 0;
			b0 = byteRead[k];
			if (k + 1 < byteSize) {
				b1 = byteRead[k + 1];
			}
			if (k + 2 < byteSize) {
				b2 = byteRead[k + 2];
			}
			if (k + 3 < byteSize) {
				b3 = byteRead[k + 3];
			}
			frameBuffer[b++] = ((b0 << 24) & 0xFF000000)
					| ((b1 << 16) & 0x00FF0000) | ((b2 << 8) & 0x0000FF00)
					| (b3 & 0x000000FF);
		}
		wordPointer = 0;
		bitIndex = 0;
	}

	/**
	 * Read bits from buffer into the lower bits of an unsigned int. The LSB
	 * contains the latest read bit of the stream. (1 <= numberOfBits <= 16)
	 */
	int getBits(int numberOfBits) {
		int returnValue = 0;
		int sum = bitIndex + numberOfBits;
		// TODO There is a problem here, wordpointer could be -1 ?!
		if (wordPointer < 0) {
			System.out.println("wordPointer < 0");
			wordPointer = 0;
		}
		if (sum <= 32) {
			// all bits contained in *wordpointer
			returnValue = (frameBuffer[wordPointer] >>> (32 - sum))
					& BITMASK[numberOfBits];
			bitIndex += numberOfBits;
			if (bitIndex == 32) {
				bitIndex = 0;
				wordPointer++;
			}
			return returnValue;
		}
		int right = (frameBuffer[wordPointer] & 0x0000FFFF);
		wordPointer++;
		int left = (frameBuffer[wordPointer] & 0xFFFF0000);
		returnValue = ((right << 16) & 0xFFFF0000)
				| ((left >>> 16) & 0x0000FFFF);
		returnValue >>>= 48 - sum;
		returnValue &= BITMASK[numberOfBits];
		bitIndex = sum - 32;
		return returnValue;
	}

	/**
	 * Set the word we want to sync the header to. In Big-Endian byte order
	 */
	void setSyncWord(int s) {
		syncWord = s & 0xFFFFFF3F;
		singleChMode = ((s & 0x000000C0) == 0x000000C0);
	}

	/**
	 * Reads the exact number of bytes from the source input stream into a byte
	 * array.
	 * 
	 * @param b
	 *            The byte array to read the specified number of bytes into.
	 * @param offs
	 *            The index in the array where the first byte read should be
	 *            stored.
	 * @param len
	 *            the number of bytes to read.
	 * 
	 * @exception Exception
	 *                is thrown if the specified number of bytes could not be
	 *                read from the stream.
	 */
	private int readFully(byte[] b, int offs, int len) throws IOException {
		// TODO does not in fact throw an exception, probably return not
		// required
		int read = 0;
		while (len > 0) {
			int bytesRead = source.read(b, offs, len);
			if (bytesRead == -1) {
				while (len-- > 0) {
					b[offs++] = 0;
				}
				break;
			}
			read = read + bytesRead;
			offs += bytesRead;
			len -= bytesRead;
		}
		return read;
	}

	/**
	 * Simlar to readFully, but doesn't throw exception when EOF is reached.
	 */
	private int readBytes(byte[] b, int offs, int len) throws IOException {
		int totalBytesRead = 0;
		while (len > 0) {
			int bytesRead = source.read(b, offs, len);
			if (bytesRead == -1) {
				break;
			}
			totalBytesRead += bytesRead;
			offs += bytesRead;
			len -= bytesRead;
		}
		return totalBytesRead;
	}
}
