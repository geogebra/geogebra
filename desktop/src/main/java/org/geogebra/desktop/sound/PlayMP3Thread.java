package org.geogebra.desktop.sound;

import java.io.InputStream;

import org.geogebra.desktop.sound.mp3transform.Decoder;

/**
 * Play MP3 in a Thread
 *
 */
public class PlayMP3Thread implements Runnable {

	private String fileName;
	private Decoder decoder;
	private InputStream is;

	/**
	 * @param decoder decoder
	 * @param fileName mp3 filename
	 * @param is stream
	 */
	public PlayMP3Thread(Decoder decoder, String fileName, InputStream is) {
		this.decoder = decoder;
		this.fileName = fileName;
		this.is = is;
	}

	@Override
	public void run() {

		try {
			decoder.play(fileName, is);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
