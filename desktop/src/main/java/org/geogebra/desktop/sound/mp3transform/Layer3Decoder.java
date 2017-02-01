/*
 * 11/19/04 1.0 moved to LGPL.
 * 
 * 18/06/01  Michael Scheerer,  Fixed bugs which causes
 *           negative indexes in method huffmann_decode and in method 
 *           dequanisize_sample.
 *
 * 16/07/01  Michael Scheerer, Catched a bug in method
 *           huffmann_decode, which causes an outOfIndexException.
 *           Cause : Indexnumber of 24 at SfBandIndex,
 *           which has only a length of 22. I have simply and dirty 
 *           fixed the index to <= 22, because I'm not really be able
 *           to fix the bug. The Indexnumber is taken from the MP3 
 *           file and the origin Ma-Player with the same code works 
 *           well.      
 * 
 * 02/19/99  Java Conversion by E.B, javalayer@javazoom.net
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

import java.io.IOException;

import org.geogebra.desktop.sound.mp3transform.Constants.SBI;

/**
 * Class Implementing Layer 3 Decoder.
 * http://www.oreilly.com/catalog/mp3/chapter/ch02.html Maximum bitreservoir is
 * 511 byte.
 * http://www.hydrogenaudio.org/forums/lofiversion/index.php/t42194.html gr:
 * granules (sub-frames)
 */
final class Layer3Decoder {
	static class GrInfo {
		int part23Length;
		int bigValues;
		int globalGain;
		int scaleFactorCompress;
		boolean windowSwitching;
		int blockType;
		boolean mixedBlock;
		int[] tableSelect = new int[3];
		int[] subblockGain = new int[3];
		int region0Count;
		int region1Count;
		int preflag;
		int scaleFactorScale;
		int count1TableSelect;
	}

	static class Channel {
		int[] scfsi = new int[4];
		GrInfo[] gr = new GrInfo[] { new GrInfo(), new GrInfo() };
	}

	static class SideInfo {
		int mainDataBegin = 0;
		Channel[] ch = new Channel[] { new Channel(), new Channel() };
	}

	static class ScaleFactor {
		int[] l = new int[23]; /* [cb] */
		int[][] s = new int[3][13]; /* [window][cb] */
	}

	private static final int SSLIMIT = 18;
	private static final int SBLIMIT = 32;
	// DOUBLE
	private static final double D43 = (4.0 / 3.0);
	private final int[] scaleFactorBuffer = new int[54];
	// TODO why +4?
	private final int[] is1d = new int[SBLIMIT * SSLIMIT + 4];
	private final double[][] ro0 = new double[SBLIMIT][SSLIMIT];
	private final double[][] ro1 = new double[SBLIMIT][SSLIMIT];
	private final double[][] lr0 = new double[SBLIMIT][SSLIMIT];
	private final double[][] lr1 = new double[SBLIMIT][SSLIMIT];
	private final double[] out1d = new double[SBLIMIT * SSLIMIT];
	private final double[][] prevBlock = new double[2][SBLIMIT * SSLIMIT];
	private final double[] k0 = new double[SBLIMIT * SSLIMIT];
	private final double[] k1 = new double[SBLIMIT * SSLIMIT];
	private final int[] nonzero = new int[2];
	private final Bitstream stream;
	private final Header header;
	private final SynthesisFilter filter1, filter2;
	private final Decoder player;
	private final BitReservoir br = new BitReservoir();
	private final SideInfo si = new SideInfo();
	private final ScaleFactor[] scaleFactors = new ScaleFactor[] {
			new ScaleFactor(), new ScaleFactor() };
	private int maxGr;
	private int frameStart;
	private int part2Start;
	private int channels;
	private int firstChannel;
	private int lastChannel;
	private int sfreq;
	private final int[] isPos = new int[576];
	private final double[] isRatio = new double[576];
	private final double[] tsOutCopy = new double[18];
	private final double[] rawout = new double[36];
	// subband samples are buffered and passed to the
	// SynthesisFilter in one go.
	private double[] samples1 = new double[32];
	private double[] samples2 = new double[32];
	private final int[] newSlen = new int[4];
	int x, y, v, w;

	public Layer3Decoder(Bitstream stream, Header header,
			SynthesisFilter filter1, SynthesisFilter filter2, Decoder player) {
		this.stream = stream;
		this.header = header;
		this.filter1 = filter1;
		this.filter2 = filter2;
		this.player = player;
		channels = (header.mode() == Header.MODE_SINGLE_CHANNEL) ? 1 : 2;
		maxGr = (header.version() == Header.VERSION_MPEG1) ? 2 : 1;
		sfreq = header.sampleFrequency()
				+ ((header.version() == Header.VERSION_MPEG1) ? 3
						: (header.version() == Header.VERSION_MPEG25_LSF) ? 6
								: 0);
		if (channels == 2) {
			firstChannel = 0;
			lastChannel = 1;
		}
		nonzero[0] = nonzero[1] = 576;
	}

	public void decodeFrame() throws IOException {
		int slots = header.slots();
		getSideInfo();
		int flushMain = br.getBitCount() & 7;
		if (flushMain != 0) {
			br.getBits(8 - flushMain);
		}
		int mainDataEnd = br.getBitCount() >>> 3; // of previous frame
		for (int i = 0; i < slots; i++) {
			br.putByte(stream.getBits(8));
		}
		int bytesToDiscard = frameStart - mainDataEnd - si.mainDataBegin;
		frameStart += slots;
		if (bytesToDiscard < 0) {
			return;
		}
		if (mainDataEnd > 4096) {
			frameStart -= 4096;
			br.rewindBytes(4096);
		}
		for (; bytesToDiscard > 0; bytesToDiscard--) {
			br.getBits(8);
		}
		for (int gr = 0; gr < maxGr; gr++) {
			for (int ch = 0; ch < channels; ch++) {
				part2Start = br.getBitCount();
				if (header.version() == Header.VERSION_MPEG1) {
					getScaleFactors(ch, gr);
				} else {
					// MPEG-2 LSF, MPEG-2.5 LSF
					getLsfScaleFactors(ch, gr);
				}
				huffmanDecode(ch, gr);
				dequantizeSample(ch == 0 ? ro0 : ro1, ch, gr);
			}
			stereo(gr);
			for (int ch = firstChannel; ch <= lastChannel; ch++) {
				reorder(ch == 0 ? lr0 : lr1, ch, gr);
				antialias(ch, gr);
				hybrid(ch, gr);
				for (int sb18 = 18; sb18 < 576; sb18 += 36) {
					// Frequency inversion
					for (int ss = 1; ss < SSLIMIT; ss += 2) {
						out1d[sb18 + ss] = -out1d[sb18 + ss];
					}
				}
				if (ch == 0) {
					for (int ss = 0; ss < SSLIMIT; ss++) {
						// Polyphase synthesis
						for (int sb18 = 0, sb = 0; sb18 < 576; sb18 += 18, sb++) {
							samples1[sb] = out1d[sb18 + ss];
						}
						filter1.calculatePcmSamples(samples1, player);
					}
				} else {
					for (int ss = 0; ss < SSLIMIT; ss++) {
						// Polyphase synthesis
						for (int sb18 = 0, sb = 0; sb18 < 576; sb18 += 18, sb++) {
							samples2[sb] = out1d[sb18 + ss];
						}
						filter2.calculatePcmSamples(samples2, player);
					}
				}
			}
		}
	}

	/**
	 * Reads the side info from the stream, assuming the entire frame has been
	 * read already. Mono : 136 bits (= 17 bytes) Stereo : 256 bits (= 32 bytes)
	 */
	private void getSideInfo() throws IOException {
		if (header.version() == Header.VERSION_MPEG1) {
			si.mainDataBegin = stream.getBits(9);
			if (channels == 1) {
				stream.getBits(5);
			} else {
				stream.getBits(3);
			}
			for (int ch = 0; ch < channels; ch++) {
				Channel c = si.ch[ch];
				c.scfsi[0] = stream.getBits(1);
				c.scfsi[1] = stream.getBits(1);
				c.scfsi[2] = stream.getBits(1);
				c.scfsi[3] = stream.getBits(1);
			}
			for (int gr = 0; gr < 2; gr++) {
				for (int ch = 0; ch < channels; ch++) {
					GrInfo gi = si.ch[ch].gr[gr];
					gi.part23Length = stream.getBits(12);
					gi.bigValues = stream.getBits(9);
					gi.globalGain = stream.getBits(8);
					gi.scaleFactorCompress = stream.getBits(4);
					gi.windowSwitching = stream.getBits(1) != 0;
					if (gi.windowSwitching) {
						gi.blockType = stream.getBits(2);
						gi.mixedBlock = stream.getBits(1) != 0;
						gi.tableSelect[0] = stream.getBits(5);
						gi.tableSelect[1] = stream.getBits(5);
						gi.subblockGain[0] = stream.getBits(3);
						gi.subblockGain[1] = stream.getBits(3);
						gi.subblockGain[2] = stream.getBits(3);
						// Set regionCount: implicit in this case
						if (gi.blockType == 0) {
							throw new IOException(
									"Side info bad: blockType == 0 in split block");
						} else if (gi.blockType == 2 && !gi.mixedBlock) {
							gi.region0Count = 8;
						} else {
							gi.region0Count = 7;
						}
						gi.region1Count = 20 - gi.region0Count;
					} else {
						gi.tableSelect[0] = stream.getBits(5);
						gi.tableSelect[1] = stream.getBits(5);
						gi.tableSelect[2] = stream.getBits(5);
						gi.region0Count = stream.getBits(4);
						gi.region1Count = stream.getBits(3);
						gi.blockType = 0;
					}
					gi.preflag = stream.getBits(1);
					gi.scaleFactorScale = stream.getBits(1);
					gi.count1TableSelect = stream.getBits(1);
				}
			}
		} else { // MPEG-2 LSF, MPEG-2.5 LSF
			si.mainDataBegin = stream.getBits(8);
			if (channels == 1) {
				stream.getBits(1);
			} else {
				stream.getBits(2);
			}
			for (int ch = 0; ch < channels; ch++) {
				GrInfo gi = si.ch[ch].gr[0];
				gi.part23Length = stream.getBits(12);
				gi.bigValues = stream.getBits(9);
				gi.globalGain = stream.getBits(8);
				gi.scaleFactorCompress = stream.getBits(9);
				gi.windowSwitching = stream.getBits(1) != 0;
				if (gi.windowSwitching) {
					gi.blockType = stream.getBits(2);
					gi.mixedBlock = stream.getBits(1) != 0;
					gi.tableSelect[0] = stream.getBits(5);
					gi.tableSelect[1] = stream.getBits(5);
					gi.subblockGain[0] = stream.getBits(3);
					gi.subblockGain[1] = stream.getBits(3);
					gi.subblockGain[2] = stream.getBits(3);
					// Set regionCount: implicit in this case
					if (gi.blockType == 0) {
						throw new IOException(
								"Side info bad: blockType == 0 in split block");
					} else if (gi.blockType == 2 && !gi.mixedBlock) {
						gi.region0Count = 8;
					} else {
						gi.region0Count = 7;
						gi.region1Count = 20 - gi.region0Count;
					}
				} else {
					gi.tableSelect[0] = stream.getBits(5);
					gi.tableSelect[1] = stream.getBits(5);
					gi.tableSelect[2] = stream.getBits(5);
					gi.region0Count = stream.getBits(4);
					gi.region1Count = stream.getBits(3);
					gi.blockType = 0;
				}
				gi.scaleFactorScale = stream.getBits(1);
				gi.count1TableSelect = stream.getBits(1);
			}
		}
	}

	private void getScaleFactors(int ch, int gr) {
		int sfb, window;
		GrInfo gi = si.ch[ch].gr[gr];
		int scaleComp = gi.scaleFactorCompress;
		int[][] slen = Constants.SLEN;
		int length0 = slen[0][scaleComp];
		int length1 = slen[1][scaleComp];
		ScaleFactor sfc = scaleFactors[ch];
		int[] sfl = sfc.l;
		int[][] sfs = sfc.s;
		if (gi.windowSwitching && gi.blockType == 2) {
			if (gi.mixedBlock) {
				for (sfb = 0; sfb < 8; sfb++) {
					sfl[sfb] = br.getBits(slen[0][gi.scaleFactorCompress]);
				}
				for (sfb = 3; sfb < 6; sfb++) {
					for (window = 0; window < 3; window++) {
						sfs[window][sfb] = br
								.getBits(slen[0][gi.scaleFactorCompress]);
					}
				}
				for (sfb = 6; sfb < 12; sfb++) {
					for (window = 0; window < 3; window++) {
						sfs[window][sfb] = br
								.getBits(slen[1][gi.scaleFactorCompress]);
					}
				}
				for (sfb = 12, window = 0; window < 3; window++) {
					sfs[window][sfb] = 0;
				}
			} else { // SHORT
				sfs[0][0] = br.getBits(length0);
				sfs[1][0] = br.getBits(length0);
				sfs[2][0] = br.getBits(length0);
				sfs[0][1] = br.getBits(length0);
				sfs[1][1] = br.getBits(length0);
				sfs[2][1] = br.getBits(length0);
				sfs[0][2] = br.getBits(length0);
				sfs[1][2] = br.getBits(length0);
				sfs[2][2] = br.getBits(length0);
				sfs[0][3] = br.getBits(length0);
				sfs[1][3] = br.getBits(length0);
				sfs[2][3] = br.getBits(length0);
				sfs[0][4] = br.getBits(length0);
				sfs[1][4] = br.getBits(length0);
				sfs[2][4] = br.getBits(length0);
				sfs[0][5] = br.getBits(length0);
				sfs[1][5] = br.getBits(length0);
				sfs[2][5] = br.getBits(length0);
				sfs[0][6] = br.getBits(length1);
				sfs[1][6] = br.getBits(length1);
				sfs[2][6] = br.getBits(length1);
				sfs[0][7] = br.getBits(length1);
				sfs[1][7] = br.getBits(length1);
				sfs[2][7] = br.getBits(length1);
				sfs[0][8] = br.getBits(length1);
				sfs[1][8] = br.getBits(length1);
				sfs[2][8] = br.getBits(length1);
				sfs[0][9] = br.getBits(length1);
				sfs[1][9] = br.getBits(length1);
				sfs[2][9] = br.getBits(length1);
				sfs[0][10] = br.getBits(length1);
				sfs[1][10] = br.getBits(length1);
				sfs[2][10] = br.getBits(length1);
				sfs[0][11] = br.getBits(length1);
				sfs[1][11] = br.getBits(length1);
				sfs[2][11] = br.getBits(length1);
				sfs[0][12] = 0;
				sfs[1][12] = 0;
				sfs[2][12] = 0;
			} // SHORT
		} else { // LONG types 0,1,3
			if ((si.ch[ch].scfsi[0] == 0) || (gr == 0)) {
				sfl[0] = br.getBits(length0);
				sfl[1] = br.getBits(length0);
				sfl[2] = br.getBits(length0);
				sfl[3] = br.getBits(length0);
				sfl[4] = br.getBits(length0);
				sfl[5] = br.getBits(length0);
			}
			if ((si.ch[ch].scfsi[1] == 0) || (gr == 0)) {
				sfl[6] = br.getBits(length0);
				sfl[7] = br.getBits(length0);
				sfl[8] = br.getBits(length0);
				sfl[9] = br.getBits(length0);
				sfl[10] = br.getBits(length0);
			}
			if ((si.ch[ch].scfsi[2] == 0) || (gr == 0)) {
				sfl[11] = br.getBits(length1);
				sfl[12] = br.getBits(length1);
				sfl[13] = br.getBits(length1);
				sfl[14] = br.getBits(length1);
				sfl[15] = br.getBits(length1);
			}
			if ((si.ch[ch].scfsi[3] == 0) || (gr == 0)) {
				sfl[16] = br.getBits(length1);
				sfl[17] = br.getBits(length1);
				sfl[18] = br.getBits(length1);
				sfl[19] = br.getBits(length1);
				sfl[20] = br.getBits(length1);
			}
			sfl[21] = 0;
			sfl[22] = 0;
		}
	}

	private void getLsfScaleData(int ch, int gr) {
		int scaleFactorComp, intScalefacComp;
		int modeExt = header.modeExtension();
		int blockTypeNumber;
		int blockNumber = 0;
		GrInfo gi = si.ch[ch].gr[gr];
		scaleFactorComp = gi.scaleFactorCompress;
		if (gi.blockType == 2) {
			if (!gi.mixedBlock) {
				blockTypeNumber = 1;
			} else {
				blockTypeNumber = 2;
			}
		} else {
			blockTypeNumber = 0;
		}
		if (!(((modeExt == 1) || (modeExt == 3)) && (ch == 1))) {
			if (scaleFactorComp < 400) {
				newSlen[0] = (scaleFactorComp >>> 4) / 5;
				newSlen[1] = (scaleFactorComp >>> 4) % 5;
				newSlen[2] = (scaleFactorComp & 0xF) >>> 2;
				newSlen[3] = (scaleFactorComp & 3);
				si.ch[ch].gr[gr].preflag = 0;
				blockNumber = 0;
			} else if (scaleFactorComp < 500) {
				newSlen[0] = ((scaleFactorComp - 400) >>> 2) / 5;
				newSlen[1] = ((scaleFactorComp - 400) >>> 2) % 5;
				newSlen[2] = (scaleFactorComp - 400) & 3;
				newSlen[3] = 0;
				si.ch[ch].gr[gr].preflag = 0;
				blockNumber = 1;
			} else if (scaleFactorComp < 512) {
				newSlen[0] = (scaleFactorComp - 500) / 3;
				newSlen[1] = (scaleFactorComp - 500) % 3;
				newSlen[2] = 0;
				newSlen[3] = 0;
				si.ch[ch].gr[gr].preflag = 1;
				blockNumber = 2;
			}
		}
		if ((((modeExt == 1) || (modeExt == 3)) && (ch == 1))) {
			intScalefacComp = scaleFactorComp >>> 1;
			if (intScalefacComp < 180) {
				newSlen[0] = intScalefacComp / 36;
				newSlen[1] = (intScalefacComp % 36) / 6;
				newSlen[2] = (intScalefacComp % 36) % 6;
				newSlen[3] = 0;
				si.ch[ch].gr[gr].preflag = 0;
				blockNumber = 3;
			} else if (intScalefacComp < 244) {
				newSlen[0] = ((intScalefacComp - 180) & 0x3F) >>> 4;
				newSlen[1] = ((intScalefacComp - 180) & 0xF) >>> 2;
				newSlen[2] = (intScalefacComp - 180) & 3;
				newSlen[3] = 0;
				si.ch[ch].gr[gr].preflag = 0;
				blockNumber = 4;
			} else if (intScalefacComp < 255) {
				newSlen[0] = (intScalefacComp - 244) / 3;
				newSlen[1] = (intScalefacComp - 244) % 3;
				newSlen[2] = 0;
				newSlen[3] = 0;
				si.ch[ch].gr[gr].preflag = 0;
				blockNumber = 5;
			}
		}
		for (int x1 = 0; x1 < 45; x1++) {
			// TODO: why 45, not 54?
			scaleFactorBuffer[x1] = 0;
		}
		for (int i = 0, m = 0; i < 4; i++) {
			int len = Constants.NR_OF_SFB_BLOCK[blockNumber][blockTypeNumber][i];
			for (int j = 0; j < len; j++) {
				scaleFactorBuffer[m] = (newSlen[i] == 0) ? 0
						: br.getBits(newSlen[i]);
				m++;
			}
		}
	}

	private void getLsfScaleFactors(int ch, int gr) {
		int m = 0;
		int sfb, window;
		GrInfo gi = si.ch[ch].gr[gr];
		getLsfScaleData(ch, gr);
		ScaleFactor sf = scaleFactors[ch];
		if (gi.windowSwitching && (gi.blockType == 2)) {
			if (gi.mixedBlock) {
				for (sfb = 0; sfb < 8; sfb++) {
					sf.l[sfb] = scaleFactorBuffer[m];
					m++;
				}
				for (sfb = 3; sfb < 12; sfb++) {
					for (window = 0; window < 3; window++) {
						sf.s[window][sfb] = scaleFactorBuffer[m];
						m++;
					}
				}
				for (window = 0; window < 3; window++) {
					sf.s[window][12] = 0;
				}
			} else { // SHORT
				for (sfb = 0; sfb < 12; sfb++) {
					for (window = 0; window < 3; window++) {
						sf.s[window][sfb] = scaleFactorBuffer[m];
						m++;
					}
				}
				for (window = 0; window < 3; window++) {
					sf.s[window][12] = 0;
				}
			}
		} else { // LONG types 0,1,3
			for (sfb = 0; sfb < 21; sfb++) {
				sf.l[sfb] = scaleFactorBuffer[m];
				m++;
			}
			sf.l[21] = 0;
			sf.l[22] = 0;
		}
	}

	private void huffmanDecode(final int ch, final int gr) {
		GrInfo gi = si.ch[ch].gr[gr];
		x = y = v = w = 0;
		int part23End = part2Start + gi.part23Length;
		int region1Start;
		int region2Start;
		int buf, buf1;
		Huffman huffman;
		// Find region boundary for short block case
		if (gi.windowSwitching && (gi.blockType == 2)) {
			// Region2.
			// MS: Extrahandling for 8KHZ
			region1Start = (sfreq == 8) ? 72 : 36; // sfb[9/3]*3=36 or in case
			// 8KHZ = 72
			region2Start = 576; // No Region2 for short block case
		} else { // Find region boundary for long block case
			buf = gi.region0Count + 1;
			buf1 = buf + gi.region1Count + 1;
			if (buf1 > Constants.SF_BAND_INDEX[sfreq].l.length - 1) {
				buf1 = Constants.SF_BAND_INDEX[sfreq].l.length - 1;
			}
			region1Start = Constants.SF_BAND_INDEX[sfreq].l[buf];
			region2Start = Constants.SF_BAND_INDEX[sfreq].l[buf1]; /* MI */
		}
		int index = 0;
		for (int i = 0; i < (gi.bigValues << 1); i += 2) {
			if (i < region1Start) {
				huffman = Huffman.HUFFMAN[gi.tableSelect[0]];
			} else if (i < region2Start) {
				huffman = Huffman.HUFFMAN[gi.tableSelect[1]];
			} else {
				huffman = Huffman.HUFFMAN[gi.tableSelect[2]];
			}
			huffman.decode(this, br);
			is1d[index++] = x;
			is1d[index++] = y;
		}
		// Read count1 area
		huffman = Huffman.HUFFMAN[gi.count1TableSelect + 32];
		int numBits = br.getBitCount();
		while ((numBits < part23End) && (index < 576)) {
			huffman.decode(this, br);
			is1d[index++] = v;
			is1d[index++] = w;
			is1d[index++] = x;
			is1d[index++] = y;
			numBits = br.getBitCount();
		}
		if (numBits > part23End) {
			br.rewindBits(numBits - part23End);
			index -= 4;
		}
		numBits = br.getBitCount();
		// Dismiss stuffing bits
		if (numBits < part23End) {
			br.getBits(part23End - numBits);
		}
		// Zero out rest
		if (index < 576) {
			nonzero[ch] = index;
		} else {
			nonzero[ch] = 576;
		}
		if (index < 0) {
			index = 0;
		}
		// may not be necessary
		for (; index < 576; index++) {
			is1d[index] = 0;
		}
	}

	private void iStereoKValues(int pos, int type, int i) {
		if (pos == 0) {
			k0[i] = 1.0f;
			k1[i] = 1.0f;
		} else if ((pos & 1) != 0) {
			k0[i] = Constants.IO[type][(pos + 1) >>> 1];
			k1[i] = 1.0f;
		} else {
			k0[i] = 1.0f;
			k1[i] = Constants.IO[type][pos >>> 1];
		}
	}

	private static double getT43(int abv, double globalGain) {
		switch (abv) {
		case 0:
			return 0.0f;
		case 1:
			return globalGain;
		case -1:
			return -globalGain;
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			return globalGain * Constants.T43[abv];
		case -2:
		case -3:
		case -4:
		case -5:
		case -6:
			return -globalGain * Constants.T43[-abv];
		default:
			if (abv > 0) {
				if (abv < Constants.T43_SIZE) {
					return globalGain * Constants.T43[abv];
				}
				return globalGain * Math.pow(abv, D43);
			}
			if (-abv < Constants.T43_SIZE) {
				return -globalGain * Constants.T43[-abv];
			}
			return -globalGain * Math.pow(-abv, D43);
		}
	}

	private void dequantizeSample(double[][] xr, int ch, int gr) {
		GrInfo gi = si.ch[ch].gr[gr];
		int nextCb; // next critical band boundary
		Constants.SBI sbif = Constants.SF_BAND_INDEX[sfreq];
		int[] s = sbif.s;
		int[] l = sbif.l;
		int cbWidth = 0;
		int len = nonzero[ch];
		// Compute overall (global) scaling
		double globalGain = Constants.POW2[gi.globalGain];
		int i = 0;
		for (int sb = 0; sb < SBLIMIT; sb++) {
			for (int ss = 0; ss < SSLIMIT; ss++, i++) {
				if (i >= len) {
					break;
				}
				xr[sb][ss] = getT43(is1d[i], globalGain);
			}
		}
		// choose correct scalefactor band per block type, initalize boundary
		if (gi.windowSwitching && (gi.blockType == 2)) {
			if (gi.mixedBlock) {
				nextCb = l[1];
				// LONG blocks: 0,1,3
			} else {
				cbWidth = s[1];
				nextCb = (cbWidth << 2) - cbWidth;
			}
		} else {
			nextCb = l[1];
			// LONG blocks: 0,1,3
		}
		int cb = 0;
		int cbBegin = 0;
		int index = 0;
		// apply formula per block type
		for (int j = 0; j < len; j++) {
			if (index == nextCb) {
				// adjust critical band boundary
				if (gi.windowSwitching && gi.blockType == 2) {
					if (gi.mixedBlock) {
						if (index == l[8]) {
							nextCb = s[4];
							nextCb = (nextCb << 2) - nextCb;
							cb = 3;
							cbWidth = s[4] - s[3];
							cbBegin = s[3];
							cbBegin = (cbBegin << 2) - cbBegin;
						} else if (index < l[8]) {
							nextCb = l[(++cb) + 1];
						} else {
							nextCb = s[(++cb) + 1];
							nextCb = (nextCb << 2) - nextCb;
							cbBegin = s[cb];
							cbWidth = s[cb + 1] - cbBegin;
							cbBegin = (cbBegin << 2) - cbBegin;
						}
					} else {
						nextCb = s[(++cb) + 1];
						nextCb = (nextCb << 2) - nextCb;
						cbBegin = s[cb];
						cbWidth = s[cb + 1] - cbBegin;
						cbBegin = (cbBegin << 2) - cbBegin;
					}
				} else { // long blocks
					nextCb = l[(++cb) + 1];
				}
			}
			int sb = j / SSLIMIT;
			int ss = j - sb * SSLIMIT; // % SSLIMIT
			// Do long/short dependent scaling operations
			int idx;
			if (gi.windowSwitching && gi.blockType == 2
					&& (!gi.mixedBlock || j >= 36)) {
				int ti = (index - cbBegin) / cbWidth;
				idx = scaleFactors[ch].s[ti][cb] << gi.scaleFactorScale;
				idx += (gi.subblockGain[ti] << 2);
			} else {
				// LONG block types 0,1,3 & 1st 2 subbands of switched blocks
				idx = scaleFactors[ch].l[cb];
				if (gi.preflag != 0) {
					idx += Constants.PRETAB[cb];
				}
				idx = idx << gi.scaleFactorScale;
			}
			xr[sb][ss] *= Constants.TWO_TO_NEGATIVE_HALF_POW[idx];
			index++;
		}
		for (int j = len; j < 576; j++) {
			int sb = j / SSLIMIT;
			int ss = j - sb * SSLIMIT; // % SSLIMIT
			xr[sb][ss] = 0.0f;
		}
		return;
	}

	private void reorder(double[][] xr, int ch, int gr) {
		GrInfo gi = si.ch[ch].gr[gr];
		if (gi.windowSwitching && gi.blockType == 2) {
			for (int index = 0; index < 576; index++) {
				out1d[index] = 0.0f;
			}
			if (gi.mixedBlock) {
				// NO REORDER FOR LOW 2 SUBBANDS
				for (int index = 0; index < 36; index++) {
					int sb = index / SSLIMIT;
					int ss = index - sb * SSLIMIT; // % SSLIMIT
					out1d[index] = xr[sb][ss];
				}
				// REORDERING FOR REST SWITCHED SHORT
				for (int sfb = 3; sfb < 13; sfb++) {
					int sfbStart = Constants.SF_BAND_INDEX[sfreq].s[sfb];
					int sfbLines = Constants.SF_BAND_INDEX[sfreq].s[sfb + 1]
							- sfbStart;
					int sfbStart3 = (sfbStart << 2) - sfbStart;
					for (int freq = 0, freq3 = 0; freq < sfbLines; freq++, freq3 += 3) {
						int srcLine = sfbStart3 + freq;
						int desLine = sfbStart3 + freq3;
						int sb = srcLine / SSLIMIT;
						int ss = srcLine - sb * SSLIMIT; // % SSLIMIT
						out1d[desLine] = xr[sb][ss];
						srcLine += sfbLines;
						desLine++;
						sb = srcLine / SSLIMIT;
						ss = srcLine - sb * SSLIMIT; // % SSLIMIT
						out1d[desLine] = xr[sb][ss];
						srcLine += sfbLines;
						desLine++;
						sb = srcLine / SSLIMIT;
						ss = srcLine - sb * SSLIMIT; // % SSLIMIT
						out1d[desLine] = xr[sb][ss];
					}
				}
			} else {
				// pure short
				int[] reorder = Constants.REORDER_TABLE[sfreq];
				for (int index = 0; index < 576; index++) {
					int j = reorder[index];
					int sb = j / SSLIMIT;
					int ss = j - sb * SSLIMIT; // % SSLIMIT
					out1d[index] = xr[sb][ss];
				}
			}
		} else {
			// long blocks
			for (int i = 0, sb = 0; sb < SBLIMIT; sb++) {
				for (int ss = 0; ss < SSLIMIT; ss++, i++) {
					out1d[i] = xr[sb][ss];
				}
			}
		}
	}

	private void stereo(int gr) {
		if (channels == 1) { // mono , bypass xr[0][][] to lr[0][][]
			for (int sb = 0; sb < SBLIMIT; sb++) {
				for (int ss = 0; ss < SSLIMIT; ss += 3) {
					lr0[sb][ss] = ro0[sb][ss];
					lr0[sb][ss + 1] = ro0[sb][ss + 1];
					lr0[sb][ss + 2] = ro0[sb][ss + 2];
				}
			}
			return;
		}
		GrInfo gi = si.ch[0].gr[gr];
		int modeExt = header.modeExtension();
		int sfb;
		int temp, temp2;
		boolean msStereo = ((header.mode() == Header.MODE_JOINT_STEREO)
				&& ((modeExt & 0x2) != 0));
		boolean iStereo = ((header.mode() == Header.MODE_JOINT_STEREO)
				&& ((modeExt & 0x1) != 0));
		boolean lsf = ((header.version() == Header.VERSION_MPEG2_LSF
				|| header.version() == Header.VERSION_MPEG25_LSF));
		int ioType = (gi.scaleFactorCompress & 1);
		for (int i = 0; i < 576; i++) {
			isPos[i] = 7;
			isRatio[i] = 0.0f;
		}
		if (iStereo) {
			SBI sbif = Constants.SF_BAND_INDEX[sfreq];
			int[] s = sbif.s;
			int[] l = sbif.l;
			if (gi.windowSwitching && gi.blockType == 2) {
				if (gi.mixedBlock) {
					int maxSfb = 0;
					for (int j = 0; j < 3; j++) {
						int sfbcnt = 2;
						for (sfb = 12; sfb >= 3; sfb--) {
							int i = s[sfb];
							int lines = s[sfb + 1] - i;
							i = (i << 2) - i + (j + 1) * lines - 1;
							while (lines > 0) {
								if (ro1[i / 18][i % 18] != 0.0f) {
									sfbcnt = sfb;
									sfb = -10;
									lines = -10;
								}
								lines--;
								i--;
							}
						}
						sfb = sfbcnt + 1;
						if (sfb > maxSfb) {
							maxSfb = sfb;
						}
						while (sfb < 12) {
							temp = s[sfb];
							int sb = s[sfb + 1] - temp;
							int i = (temp << 2) - temp + j * sb;
							for (; sb > 0; sb--) {
								isPos[i] = scaleFactors[1].s[j][sfb];
								if (isPos[i] != 7) {
									if (lsf) {
										iStereoKValues(isPos[i], ioType, i);
									} else {
										isRatio[i] = Constants.TAN12[isPos[i]];
									}
								}
								i++;
							}
							sfb++;
						}
						sfb = s[10];
						int sb = s[11] - sfb;
						sfb = (sfb << 2) - sfb + j * sb;
						temp = s[11];
						sb = s[12] - temp;
						int i = (temp << 2) - temp + j * sb;
						for (; sb > 0; sb--) {
							isPos[i] = isPos[sfb];
							if (lsf) {
								k0[i] = k0[sfb];
								k1[i] = k1[sfb];
							} else {
								isRatio[i] = isRatio[sfb];
							}
							i++;
						}
					}
					if (maxSfb <= 3) {
						int i = 2;
						int ss = 17;
						int sb = -1;
						while (i >= 0) {
							if (ro1[i][ss] != 0.0f) {
								sb = (i << 4) + (i << 1) + ss;
								i = -1;
							} else {
								ss--;
								if (ss < 0) {
									i--;
									ss = 17;
								}
							}
						}
						i = 0;
						while (l[i] <= sb) {
							i++;
						}
						sfb = i;
						i = l[i];
						for (; sfb < 8; sfb++) {
							sb = l[sfb + 1] - l[sfb];
							for (; sb > 0; sb--) {
								isPos[i] = scaleFactors[1].l[sfb];
								if (isPos[i] != 7) {
									if (lsf) {
										iStereoKValues(isPos[i], ioType, i);
									} else {
										isRatio[i] = Constants.TAN12[isPos[i]];
									}
								}
								i++;
							}
						}
					}
				} else {
					for (int j = 0; j < 3; j++) {
						int sfbcnt;
						sfbcnt = -1;
						for (sfb = 12; sfb >= 0; sfb--) {
							temp = s[sfb];
							int lines = s[sfb + 1] - temp;
							int i = (temp << 2) - temp + (j + 1) * lines - 1;
							while (lines > 0) {
								if (ro1[i / 18][i % 18] != 0.0f) {
									sfbcnt = sfb;
									sfb = -10;
									lines = -10;
								}
								lines--;
								i--;
							}
						}
						sfb = sfbcnt + 1;
						while (sfb < 12) {
							temp = s[sfb];
							int sb = s[sfb + 1] - temp;
							int i = (temp << 2) - temp + j * sb;
							for (; sb > 0; sb--) {
								isPos[i] = scaleFactors[1].s[j][sfb];
								if (isPos[i] != 7) {
									if (lsf) {
										iStereoKValues(isPos[i], ioType, i);
									} else {
										isRatio[i] = Constants.TAN12[isPos[i]];
									}
								}
								i++;
							} // for (; sb>0 ...
							sfb++;
						} // while (sfb<12)
						temp = s[10];
						temp2 = s[11];
						int sb = temp2 - temp;
						sfb = (temp << 2) - temp + j * sb;
						sb = s[12] - temp2;
						int i = (temp2 << 2) - temp2 + j * sb;
						for (; sb > 0; sb--) {
							isPos[i] = isPos[sfb];
							if (lsf) {
								k0[i] = k0[sfb];
								k1[i] = k1[sfb];
							} else {
								isRatio[i] = isRatio[sfb];
							}
							i++;
						}
					}
				}
			} else {
				int i = 31;
				int ss = 17;
				int sb = 0;
				while (i >= 0) {
					if (ro1[i][ss] != 0.0f) {
						sb = (i << 4) + (i << 1) + ss;
						i = -1;
					} else {
						ss--;
						if (ss < 0) {
							i--;
							ss = 17;
						}
					}
				}
				i = 0;
				while (l[i] <= sb) {
					i++;
				}
				sfb = i;
				i = l[i];
				for (; sfb < 21; sfb++) {
					sb = l[sfb + 1] - l[sfb];
					for (; sb > 0; sb--) {
						isPos[i] = scaleFactors[1].l[sfb];
						if (isPos[i] != 7) {
							if (lsf) {
								iStereoKValues(isPos[i], ioType, i);
							} else {
								isRatio[i] = Constants.TAN12[isPos[i]];
							}
						}
						i++;
					}
				}
				sfb = l[20];
				for (sb = 576 - l[21]; (sb > 0) && (i < 576); sb--) {
					isPos[i] = isPos[sfb]; // error here : i >=576
					if (lsf) {
						k0[i] = k0[sfb];
						k1[i] = k1[sfb];
					} else {
						isRatio[i] = isRatio[sfb];
					}
					i++;
				}
			}
		}
		for (int sb = 0, i = 0; sb < SBLIMIT; sb++) {
			for (int ss = 0; ss < SSLIMIT; ss++) {
				if (isPos[i] == 7) {
					if (msStereo) {
						lr0[sb][ss] = (ro0[sb][ss] + ro1[sb][ss])
								* 0.707106781f;
						lr1[sb][ss] = (ro0[sb][ss] - ro1[sb][ss])
								* 0.707106781f;
					} else {
						lr0[sb][ss] = ro0[sb][ss];
						lr1[sb][ss] = ro1[sb][ss];
					}
				} else if (iStereo) {
					if (lsf) {
						lr0[sb][ss] = ro0[sb][ss] * k0[i];
						lr1[sb][ss] = ro0[sb][ss] * k1[i];
					} else {
						lr1[sb][ss] = ro0[sb][ss] / (1 + isRatio[i]);
						lr0[sb][ss] = lr1[sb][ss] * isRatio[i];
					}
				}
				i++;
			}
		}
	}

	private void antialias(int ch, int gr) {
		int sb18, ss, sb18lim;
		GrInfo gi = si.ch[ch].gr[gr];
		// 31 alias-reduction operations between each pair of sub-bands
		// with 8 butterflies between each pair
		if (gi.windowSwitching && (gi.blockType == 2) && !gi.mixedBlock) {
			return;
		}
		if (gi.windowSwitching && gi.mixedBlock && (gi.blockType == 2)) {
			sb18lim = 18;
		} else {
			sb18lim = 558;
		}
		for (sb18 = 0; sb18 < sb18lim; sb18 += 18) {
			for (ss = 0; ss < 8; ss++) {
				int srcIdx1 = sb18 + 17 - ss;
				int srcIdx2 = sb18 + 18 + ss;
				double bu = out1d[srcIdx1];
				double bd = out1d[srcIdx2];
				out1d[srcIdx1] = (bu * Constants.CS[ss])
						- (bd * Constants.CA[ss]);
				out1d[srcIdx2] = (bd * Constants.CS[ss])
						+ (bu * Constants.CA[ss]);
			}
		}
	}

	private void hybrid(int ch, int gr) {
		GrInfo gi = si.ch[ch].gr[gr];
		for (int sb18 = 0; sb18 < 576; sb18 += 18) {
			int bt = (gi.windowSwitching && gi.mixedBlock && (sb18 < 36)) ? 0
					: gi.blockType;
			double[] tsOut = out1d;
			double[] r = rawout;
			for (int cc = 0; cc < 18; cc++) {
				tsOutCopy[cc] = tsOut[cc + sb18];
			}
			fastInvMdct(tsOutCopy, r, bt);
			for (int cc = 0; cc < 18; cc++) {
				tsOut[cc + sb18] = tsOutCopy[cc];
			}
			// overlap addition
			double[] p = prevBlock[ch];
			tsOut[0 + sb18] = r[0] + p[sb18 + 0];
			p[sb18 + 0] = r[18];
			tsOut[1 + sb18] = r[1] + p[sb18 + 1];
			p[sb18 + 1] = r[19];
			tsOut[2 + sb18] = r[2] + p[sb18 + 2];
			p[sb18 + 2] = r[20];
			tsOut[3 + sb18] = r[3] + p[sb18 + 3];
			p[sb18 + 3] = r[21];
			tsOut[4 + sb18] = r[4] + p[sb18 + 4];
			p[sb18 + 4] = r[22];
			tsOut[5 + sb18] = r[5] + p[sb18 + 5];
			p[sb18 + 5] = r[23];
			tsOut[6 + sb18] = r[6] + p[sb18 + 6];
			p[sb18 + 6] = r[24];
			tsOut[7 + sb18] = r[7] + p[sb18 + 7];
			p[sb18 + 7] = r[25];
			tsOut[8 + sb18] = r[8] + p[sb18 + 8];
			p[sb18 + 8] = r[26];
			tsOut[9 + sb18] = r[9] + p[sb18 + 9];
			p[sb18 + 9] = r[27];
			tsOut[10 + sb18] = r[10] + p[sb18 + 10];
			p[sb18 + 10] = r[28];
			tsOut[11 + sb18] = r[11] + p[sb18 + 11];
			p[sb18 + 11] = r[29];
			tsOut[12 + sb18] = r[12] + p[sb18 + 12];
			p[sb18 + 12] = r[30];
			tsOut[13 + sb18] = r[13] + p[sb18 + 13];
			p[sb18 + 13] = r[31];
			tsOut[14 + sb18] = r[14] + p[sb18 + 14];
			p[sb18 + 14] = r[32];
			tsOut[15 + sb18] = r[15] + p[sb18 + 15];
			p[sb18 + 15] = r[33];
			tsOut[16 + sb18] = r[16] + p[sb18 + 16];
			p[sb18 + 16] = r[34];
			tsOut[17 + sb18] = r[17] + p[sb18 + 17];
			p[sb18 + 17] = r[35];
		}
	}

	private static void fastInvMdct(double[] in, double[] out, int blockType) {
		double t0, t1, t2, t3, t4, t5, t6, t7, t8, t9;
		double t10, t11, t12, t13, t14, t15, t16, t17;
		if (blockType == 2) {
			for (int p = 0; p < 36; p += 9) {
				out[p] = out[p
						+ 1] = out[p + 2] = out[p + 3] = out[p + 4] = 0.0f;
				out[p + 5] = out[p + 6] = out[p + 7] = out[p + 8] = 0.0f;
			}
			int sixI = 0;
			for (int i = 0; i < 3; i++) {
				// 12 point IMDCT
				// Begin 12 point IDCT
				// Input aliasing for 12 pt IDCT
				in[15 + i] += in[12 + i];
				in[12 + i] += in[9 + i];
				in[9 + i] += in[6 + i];
				in[6 + i] += in[3 + i];
				in[3 + i] += in[0 + i];
				// Input aliasing on odd indices (for 6 point IDCT)
				in[15 + i] += in[9 + i];
				in[9 + i] += in[3 + i];
				// 3 point IDCT on even indices
				double pp1, pp2, sum;
				pp2 = in[12 + i] * 0.500000000f;
				pp1 = in[6 + i] * 0.866025403f;
				sum = in[0 + i] + pp2;
				t1 = in[0 + i] - in[12 + i];
				t0 = sum + pp1;
				t2 = sum - pp1;
				// End 3 point IDCT on even indices
				// 3 point IDCT on odd indices (for 6 point IDCT)
				pp2 = in[15 + i] * 0.500000000f;
				pp1 = in[9 + i] * 0.866025403f;
				sum = in[3 + i] + pp2;
				t4 = in[3 + i] - in[15 + i];
				t5 = sum + pp1;
				t3 = sum - pp1;
				// End 3 point IDCT on odd indices
				// Twiddle factors on odd indices (for 6 point IDCT)
				t3 *= 1.931851653f;
				t4 *= 0.707106781f;
				t5 *= 0.517638090f;
				// Output butterflies on 2 3 point IDCT's (for 6 point IDCT)
				double save = t0;
				t0 += t5;
				t5 = save - t5;
				save = t1;
				t1 += t4;
				t4 = save - t4;
				save = t2;
				t2 += t3;
				t3 = save - t3;
				// End 6 point IDCT
				// Twiddle factors on indices (for 12 point IDCT)
				t0 *= 0.504314480f;
				t1 *= 0.541196100f;
				t2 *= 0.630236207f;
				t3 *= 0.821339815f;
				t4 *= 1.306562965f;
				t5 *= 3.830648788f;
				// End 12 point IDCT
				// Shift to 12 point modified IDCT, multiply by window type 2
				t8 = -t0 * 0.793353340f;
				t9 = -t0 * 0.608761429f;
				t7 = -t1 * 0.923879532f;
				t10 = -t1 * 0.382683432f;
				t6 = -t2 * 0.991444861f;
				t11 = -t2 * 0.130526192f;
				t0 = t3;
				t1 = t4 * 0.382683432f;
				t2 = t5 * 0.608761429f;
				t3 = -t5 * 0.793353340f;
				t4 = -t4 * 0.923879532f;
				t5 = -t0 * 0.991444861f;
				t0 *= 0.130526192f;
				out[sixI + 6] += t0;
				out[sixI + 7] += t1;
				out[sixI + 8] += t2;
				out[sixI + 9] += t3;
				out[sixI + 10] += t4;
				out[sixI + 11] += t5;
				out[sixI + 12] += t6;
				out[sixI + 13] += t7;
				out[sixI + 14] += t8;
				out[sixI + 15] += t9;
				out[sixI + 16] += t10;
				out[sixI + 17] += t11;
				sixI += 6;
			}
		} else {
			// 36 point IDCT
			// input aliasing for 36 point IDCT
			in[17] += in[16];
			in[16] += in[15];
			in[15] += in[14];
			in[14] += in[13];
			in[13] += in[12];
			in[12] += in[11];
			in[11] += in[10];
			in[10] += in[9];
			in[9] += in[8];
			in[8] += in[7];
			in[7] += in[6];
			in[6] += in[5];
			in[5] += in[4];
			in[4] += in[3];
			in[3] += in[2];
			in[2] += in[1];
			in[1] += in[0];
			// 18 point IDCT for odd indices
			// input aliasing for 18 point IDCT
			in[17] += in[15];
			in[15] += in[13];
			in[13] += in[11];
			in[11] += in[9];
			in[9] += in[7];
			in[7] += in[5];
			in[5] += in[3];
			in[3] += in[1];
			double tmp0, tmp1, tmp2, tmp3, tmp4, tmp0b, tmp1b, tmp2b, tmp3b;
			double tmp0o, tmp1o, tmp2o, tmp3o, tmp4o, tmp0ob, tmp1ob, tmp2ob,
					tmp3ob;
			// Fast 9 Point Inverse Discrete Cosine Transform
			//
			// By Francois-Raymond Boyer
			// mailto:boyerf@iro.umontreal.ca
			// http://www.iro.umontreal.ca/~boyerf
			//
			// The code has been optimized for Intel processors
			// (takes a lot of time to convert double to and from iternal FPU
			// representation)
			//
			// It is a simple "factorization" of the IDCT matrix.
			// 9 point IDCT on even indices
			// 5 points on odd indices (not really an IDCT)
			double i00 = in[0] + in[0];
			double iip12 = i00 + in[12];
			tmp0 = iip12 + in[4] * 1.8793852415718f + in[8] * 1.532088886238f
					+ in[16] * 0.34729635533386f;
			tmp1 = i00 + in[4] - in[8] - in[12] - in[12] - in[16];
			tmp2 = iip12 - in[4] * 0.34729635533386f - in[8] * 1.8793852415718f
					+ in[16] * 1.532088886238f;
			tmp3 = iip12 - in[4] * 1.532088886238f + in[8] * 0.34729635533386f
					- in[16] * 1.8793852415718f;
			tmp4 = in[0] - in[4] + in[8] - in[12] + in[16];
			// 4 points on even indices
			double i6s = in[6] * 1.732050808f; // Sqrt[3]
			tmp0b = in[2] * 1.9696155060244f + i6s + in[10] * 1.2855752193731f
					+ in[14] * 0.68404028665134f;
			tmp1b = (in[2] - in[10] - in[14]) * 1.732050808f;
			tmp2b = in[2] * 1.2855752193731f - i6s - in[10] * 0.68404028665134f
					+ in[14] * 1.9696155060244f;
			tmp3b = in[2] * 0.68404028665134f - i6s + in[10] * 1.9696155060244f
					- in[14] * 1.2855752193731f;
			// 9 point IDCT on odd indices
			// 5 points on odd indices (not really an IDCT)
			double i0 = in[0 + 1] + in[0 + 1];
			double i0p12 = i0 + in[12 + 1];
			tmp0o = i0p12 + in[4 + 1] * 1.8793852415718f
					+ in[8 + 1] * 1.532088886238f
					+ in[16 + 1] * 0.34729635533386f;
			tmp1o = i0 + in[4 + 1] - in[8 + 1] - in[12 + 1] - in[12 + 1]
					- in[16 + 1];
			tmp2o = i0p12 - in[4 + 1] * 0.34729635533386f
					- in[8 + 1] * 1.8793852415718f
					+ in[16 + 1] * 1.532088886238f;
			tmp3o = i0p12 - in[4 + 1] * 1.532088886238f
					+ in[8 + 1] * 0.34729635533386f
					- in[16 + 1] * 1.8793852415718f;
			tmp4o = (in[0 + 1] - in[4 + 1] + in[8 + 1] - in[12 + 1]
					+ in[16 + 1]) * 0.707106781f; // Twiddled
			// 4 points on even indices
			double i7s = in[6 + 1] * 1.732050808f; // Sqrt[3]
			tmp0ob = in[2 + 1] * 1.9696155060244f + i7s
					+ in[10 + 1] * 1.2855752193731f
					+ in[14 + 1] * 0.68404028665134f;
			tmp1ob = (in[2 + 1] - in[10 + 1] - in[14 + 1]) * 1.732050808f;
			tmp2ob = in[2 + 1] * 1.2855752193731f - i7s
					- in[10 + 1] * 0.68404028665134f
					+ in[14 + 1] * 1.9696155060244f;
			tmp3ob = in[2 + 1] * 0.68404028665134f - i7s
					+ in[10 + 1] * 1.9696155060244f
					- in[14 + 1] * 1.2855752193731f;
			// Twiddle factors on odd indices and
			// Butterflies on 9 point IDCT's and
			// twiddle factors for 36 point IDCT
			double e, o;
			e = tmp0 + tmp0b;
			o = (tmp0o + tmp0ob) * 0.501909918f;
			t0 = e + o;
			t17 = e - o;
			e = tmp1 + tmp1b;
			o = (tmp1o + tmp1ob) * 0.517638090f;
			t1 = e + o;
			t16 = e - o;
			e = tmp2 + tmp2b;
			o = (tmp2o + tmp2ob) * 0.551688959f;
			t2 = e + o;
			t15 = e - o;
			e = tmp3 + tmp3b;
			o = (tmp3o + tmp3ob) * 0.610387294f;
			t3 = e + o;
			t14 = e - o;
			t4 = tmp4 + tmp4o;
			t13 = tmp4 - tmp4o;
			e = tmp3 - tmp3b;
			o = (tmp3o - tmp3ob) * 0.871723397f;
			t5 = e + o;
			t12 = e - o;
			e = tmp2 - tmp2b;
			o = (tmp2o - tmp2ob) * 1.183100792f;
			t6 = e + o;
			t11 = e - o;
			e = tmp1 - tmp1b;
			o = (tmp1o - tmp1ob) * 1.931851653f;
			t7 = e + o;
			t10 = e - o;
			e = tmp0 - tmp0b;
			o = (tmp0o - tmp0ob) * 5.736856623f;
			t8 = e + o;
			t9 = e - o;
			// end 36 point IDCT */
			// shift to modified IDCT
			double[] win = Constants.WIN[blockType];
			out[0] = -t9 * win[0];
			out[1] = -t10 * win[1];
			out[2] = -t11 * win[2];
			out[3] = -t12 * win[3];
			out[4] = -t13 * win[4];
			out[5] = -t14 * win[5];
			out[6] = -t15 * win[6];
			out[7] = -t16 * win[7];
			out[8] = -t17 * win[8];
			out[9] = t17 * win[9];
			out[10] = t16 * win[10];
			out[11] = t15 * win[11];
			out[12] = t14 * win[12];
			out[13] = t13 * win[13];
			out[14] = t12 * win[14];
			out[15] = t11 * win[15];
			out[16] = t10 * win[16];
			out[17] = t9 * win[17];
			out[18] = t8 * win[18];
			out[19] = t7 * win[19];
			out[20] = t6 * win[20];
			out[21] = t5 * win[21];
			out[22] = t4 * win[22];
			out[23] = t3 * win[23];
			out[24] = t2 * win[24];
			out[25] = t1 * win[25];
			out[26] = t0 * win[26];
			out[27] = t0 * win[27];
			out[28] = t1 * win[28];
			out[29] = t2 * win[29];
			out[30] = t3 * win[30];
			out[31] = t4 * win[31];
			out[32] = t5 * win[32];
			out[33] = t6 * win[33];
			out[34] = t7 * win[34];
			out[35] = t8 * win[35];
		}
	}
}
