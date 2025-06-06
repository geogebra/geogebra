/*
 * This program is free software; you can redistribute it and/or modify
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
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.sound.PauseControl;

public class Decoder {
	public static final int BUFFER_SIZE = 2 * 1152;
	public static final int MAX_CHANNELS = 2;
	private static final boolean BENCHMARK = false;

	protected final int[] bufferPointer = new int[MAX_CHANNELS];
	protected int channels;
	private SynthesisFilter filter1;
	private SynthesisFilter filter2;
	private Layer3Decoder l3decoder;
	private boolean initialized;

	private SourceDataLine line;
	private final byte[] buffer = new byte[BUFFER_SIZE * 2];

	public void decodeFrame(Header header, Bitstream stream)
			throws IOException {
		if (!initialized) {
			double scaleFactor = 32700.0f;
			int mode = header.mode();
			int channels1 = mode == Header.MODE_SINGLE_CHANNEL ? 1 : 2;
			filter1 = new SynthesisFilter(0, scaleFactor);
			if (channels1 == 2) {
				filter2 = new SynthesisFilter(1, scaleFactor);
			}
			initialized = true;
		}
		if (l3decoder == null) {
			l3decoder = new Layer3Decoder(stream, header, filter1, filter2,
					this);
		}
		l3decoder.decodeFrame();
		writeBuffer();
	}

	protected void initOutputBuffer(SourceDataLine line1,
			int numberOfChannels) {
		this.line = line1;
		channels = numberOfChannels;
		for (int i = 0; i < channels; i++) {
			bufferPointer[i] = i + i;
		}
	}

	public void appendSamples(int channel, double[] f) {
		int p = bufferPointer[channel];
		for (int i = 0; i < 32; i++) {
			double sample = f[i];
			int s = (int) ((sample > 32767.0f) ? 32767
					: ((sample < -32768.0f) ? -32768 : sample));
			buffer[p] = (byte) (s >> 8);
			buffer[p + 1] = (byte) (s & 0xff);
			p += 4;
		}
		bufferPointer[channel] = p;
	}

	protected void writeBuffer() {
		if (line != null) {
			line.write(buffer, 0, bufferPointer[0]);
		}
		for (int i = 0; i < channels; i++) {
			bufferPointer[i] = i + i;
		}
	}

	public void play(String name, InputStream in, PauseControl control) throws IOException {
		int frameCount = Integer.MAX_VALUE;

		// int testing;
		// frameCount = 100;
		Bitstream stream = new Bitstream(in);
		SourceDataLine line1 = null;
		int error = 0;
		for (int frame = 0; frame < frameCount; frame++) {
			if (control.pause) {
				if (line1 != null) {
					line1.stop();
					while (control.pause) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// ignore
						}
					}
					line1.flush();
					line1.start();
				}
			}
			try {
				Header header = stream.readFrame();
				if (header == null) {
					break;
				}
				if (channels == 0) {
					int channels1 = (header
							.mode() == Header.MODE_SINGLE_CHANNEL)
							? 1 : 2;
					float sampleRate = header.frequency();
					int sampleSize = 16;
					AudioFormat format = new AudioFormat(
							AudioFormat.Encoding.PCM_SIGNED, sampleRate,
							sampleSize, channels1, channels1 * (sampleSize / 8),
							sampleRate, true);
					// big endian
					SourceDataLine.Info info = new DataLine.Info(
							SourceDataLine.class, format);
					line1 = (SourceDataLine) AudioSystem.getLine(info);
					if (BENCHMARK) {
						initOutputBuffer(null, channels1);
					} else {
						initOutputBuffer(line1, channels1);
					}
					// TODO sometimes the line can not be opened (maybe not
					// enough system resources?): display error message
					// System.out.println(line.getFormat().toString());
					line1.open(format);
					line1.start();
				}
				if (line1 != null) {
					while (line1.available() < 100) {
						Thread.yield();
						Thread.sleep(200);
					}
				}
				decodeFrame(header, stream);
			} catch (Exception e) {
				if (error++ > 1000) {
					break;
				}
				// TODO should not write directly
				Log.debug("Error at: " + name + " Frame: " + frame + " Error: "
						+ e.toString());
				// e.printStackTrace();
			} finally {
				stream.closeFrame();
			}
		}
		if (error > 0) {
			Log.debug("errors: " + error);
		}
		in.close();
		if (line1 != null) {
			line1.stop();
			line1.close();
			line1 = null;
		}
	}

}
