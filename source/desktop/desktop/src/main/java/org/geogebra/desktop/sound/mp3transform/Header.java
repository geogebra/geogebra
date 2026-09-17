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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Class for extracting information from a frame header.
 */
public final class Header {
	private static final int[][] FREQUENCIES = {
		{22050, 24000, 16000, 1}, {44100, 48000, 32000, 1}, {11025, 12000, 8000, 1}
	};
	static final int VERSION_MPEG2_LSF = 0;
	static final int VERSION_MPEG25_LSF = 2;
	static final int VERSION_MPEG1 = 1;
	static final int MODE_JOINT_STEREO = 1;
	public static final int MODE_SINGLE_CHANNEL = 3;
	private static final int SAMPLE_FREQUENCY_FOURTYEIGHT = 1;
	private static final int SAMPLE_FREQUENCY_THIRTYTWO = 2;
	private boolean protectionBit, paddingBit;
	private int bitrateIndex, modeExtension;
	private int version;
	private int mode;
	private int sampleFrequency;
	private int numberOfSubbands, intensityStereoBound;
	private byte syncMode = Bitstream.INITIAL_SYNC;
	private int frameSize;
	private boolean vbr;
	private int slots;

	boolean readHeader(Bitstream stream) throws IOException {
		while (true) {
			int headerString = stream.syncHeader(syncMode);
			if (syncMode == Bitstream.INITIAL_SYNC) {
				version = (headerString >>> 19) & 1;
				if (((headerString >>> 20) & 1) == 0) {
					if (version == VERSION_MPEG2_LSF) {
						version = VERSION_MPEG25_LSF;
					} else {
						throw new IOException("Unsupported version: " + version);
					}
				}
				sampleFrequency = (headerString >>> 10) & 3;
				if (sampleFrequency == 3) {
					throw new IOException("Unsupported sampleFrequency: " + sampleFrequency);
				}
			}
			int layer = 4 - (headerString >>> 17) & 3;
			if (layer != 3) {
				throw new IOException("Unsupported layer: " + layer);
			}
			protectionBit = ((headerString >>> 16) & 1) != 0;
			bitrateIndex = (headerString >>> 12) & 0xF;
			paddingBit = ((headerString >>> 9) & 1) != 0;
			mode = (headerString >>> 6) & 3;
			modeExtension = (headerString >>> 4) & 3;
			if (mode == MODE_JOINT_STEREO) {
				intensityStereoBound = (modeExtension << 2) + 4;
			} else {
				intensityStereoBound = 0; // should never be used
			}
			// calculate number of subbands:
			int channelBitrate = bitrateIndex;
			// calculate bitrate per channel:
			if (mode != MODE_SINGLE_CHANNEL) {
				if (channelBitrate == 4) {
					channelBitrate = 1;
				} else {
					channelBitrate -= 4;
				}
			}
			if (channelBitrate == 1 || channelBitrate == 2) {
				if (sampleFrequency == SAMPLE_FREQUENCY_THIRTYTWO) {
					numberOfSubbands = 12;
				} else {
					numberOfSubbands = 8;
				}
			} else if (sampleFrequency == SAMPLE_FREQUENCY_FOURTYEIGHT
					|| (channelBitrate >= 3 && channelBitrate <= 5)) {
				numberOfSubbands = 27;
			} else {
				numberOfSubbands = 30;
			}
			if (intensityStereoBound > numberOfSubbands) {
				intensityStereoBound = numberOfSubbands;
			}
			calculateFramesize();
			int frameSizeLoaded = stream.readFrameData(frameSize);
			if (frameSize >= 0 && frameSizeLoaded != frameSize) {
				// Data loaded does not match to expected framesize,
				// it might be an ID3v1 Tag
				return false;
			}
			if (stream.isSyncCurrentPosition(syncMode)) {
				if (syncMode == Bitstream.INITIAL_SYNC) {
					syncMode = Bitstream.STRICT_SYNC;
					stream.setSyncWord(headerString & 0xFFF80CC0);
				}
				break;
			}
			stream.unreadFrame();
		}
		stream.parseFrame();
		if (!protectionBit) {
			// frame contains a crc checksum
			stream.getBits(16);
		}
		return true;
	}

	void parseVBR(byte[] firstFrame) throws IOException {
		// trying Xing header
		byte[] tmp = new byte[4];
		int offset;
		if (version == VERSION_MPEG1) {
			if (mode == MODE_SINGLE_CHANNEL) {
				offset = 21 - 4;
			} else {
				offset = 36 - 4;
			}
		} else {
			if (mode == MODE_SINGLE_CHANNEL) {
				offset = 13 - 4;
			} else {
				offset = 21 - 4;
			}
		}
		try {
			System.arraycopy(firstFrame, offset, tmp, 0, 4);
			if ("Xing".equals(new String(tmp, StandardCharsets.UTF_8))) {
				vbr = true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IOException("Corrupt Xing VBR header", e);
		}
		offset = 36 - 4;
		try {
			System.arraycopy(firstFrame, offset, tmp, 0, 4);
			if ("VBRI".equals(new String(tmp, StandardCharsets.UTF_8))) {
				vbr = true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IOException("Corrupt VBRI VBR header", e);
		}
	}

	int version() {
		return version;
	}

	int sampleFrequency() {
		return sampleFrequency;
	}

	public int frequency() {
		return FREQUENCIES[version][sampleFrequency];
	}

	public int mode() {
		return mode;
	}

	int slots() {
		return slots;
	}

	boolean vbr() {
		return vbr;
	}

	int modeExtension() {
		return modeExtension;
	}

	private void calculateFramesize() {
		frameSize = 144 * Constants.BITRATES[version][bitrateIndex] / frequency();
		if (version == VERSION_MPEG2_LSF || version == VERSION_MPEG25_LSF) {
			frameSize >>= 1;
		}
		if (paddingBit) {
			frameSize++;
		}
		// subtract header size
		frameSize -= 4;
		// side info size, crc size, header sidze
		if (version == VERSION_MPEG1) {
			slots = (mode == MODE_SINGLE_CHANNEL) ? 17 : 32;
		} else {
			slots = (mode == MODE_SINGLE_CHANNEL) ? 9 : 17;
		}
		slots = frameSize - slots - (protectionBit ? 0 : 2);
	}
}
