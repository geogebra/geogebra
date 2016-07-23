package org.geogebra.desktop.sound;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.SwingUtilities;

import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.sound.mp3transform.Decoder;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Class to handle GeoGebra sound features. Calls to midi and streaming audio
 * methods are managed from here.
 * 
 * @author G. Sturr
 * 
 */
public class SoundManagerD implements SoundManager {

	private AppD app;
	private MidiSoundD midiSound;
	private FunctionSoundD functionSound;

	private static final int SOUNDTYPE_NONE = -1;
	private static final int SOUNDTYPE_MIDI = 0;
	private static final int SOUNDTYPE_FUNCTION = 1;
	int currentSoundType = SOUNDTYPE_NONE;

	private boolean isRunning = false;
	private boolean isPaused = false;

	/**
	 * Constructor
	 * 
	 * @param app
	 */
	public SoundManagerD(AppD app) {
		this.app = app;
	}

	// ====================================
	// Getters/setters
	// ====================================

	public boolean isRunning() {
		return isRunning;
	}

	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Retrieves field midiSound. Creates a new instance of MidiSound if none
	 * exists.
	 */
	MidiSoundD getMidiSound() {
		if (midiSound == null)
			try {
				midiSound = new MidiSoundD(app);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return midiSound;
	}

	/**
	 * Retrieves field functionSound. Creates a new instance of FunctionSound if
	 * none exists.
	 */
	private FunctionSoundD getFunctionSound() {
		if (functionSound == null)
			try {
				functionSound = new FunctionSoundD(app);
			} catch (Exception e) {
				Log.error("Problem in getFunctionSound(): " + e.getMessage());
			}
		return functionSound;
	}

	// ====================================
	// Sound playing methods
	// ====================================

	/**
	 * Plays a single note using the midi sequencer.
	 * 
	 * @param note
	 * @param duration
	 * @param instrument
	 * @param velocity
	 */
	public void playSequenceNote(final int note, final double duration,
			final int instrument, final int velocity) {
		try {
			stopCurrentSound();
			currentSoundType = SOUNDTYPE_MIDI;
			getMidiSound().playSequenceNote(note, duration, instrument,
					velocity);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * decoder to play MP3s
	 */
	Decoder decoder;

	/**
	 * Plays an audio file with the .mid / .mp3
	 * 
	 * @param fileName
	 */
	public void playFile(final String fileName) {



		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				try {
					if (fileName.startsWith("#") || !(fileName.endsWith(".midi")
							&& fileName.endsWith(".mid"))) {

						InputStream is;

						if (fileName.startsWith("#")) {
							// eg PlaySound["#12345"] to play from GeoGebraTube
							String id = fileName.substring(1);

							String url = app.getURLforID(id);

							// #5094
							is = new URL(url)
									.openStream();

						} else if (fileName.startsWith("http:")
								|| fileName.startsWith("https:")) {

							is = new URL(fileName).openStream();

						} else {
							// assume local file
							// eg c:\
							// eg file://
							is = new FileInputStream(fileName);

						}

						if (decoder == null) {
							decoder = new Decoder();
						}

						Thread thread = new Thread(
								new PlayMP3Thread(decoder, fileName, is));
						thread.start();

						return;

					}

					// not .mp3, must be .mid
					stopCurrentSound();
					currentSoundType = SOUNDTYPE_MIDI;
					getMidiSound().playMidiFile(fileName);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});





	}

	// http://stackoverflow.com/questions/13789063/get-sound-from-a-url-with-java
	private void playMP3(final String url) {

		try {

			// Create the JavaFX Panel for the WebView
			JFXPanel fxPanel = new JFXPanel();
			fxPanel.setLocation(new Point(0, 0));

			// Initialize the webView in a JavaFX-Thread
			Platform.runLater(new Runnable() {
				public void run() {
					MediaPlayer player = new MediaPlayer(new Media(url));
					player.play();
				}
			});



			if (true)
				return;

			AudioInputStream in = AudioSystem.getAudioInputStream(new URL(url));
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			AudioInputStream din = AudioSystem.getAudioInputStream(
					decodedFormat, in);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class,
					decodedFormat);
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			if (line != null) {
				line.open(decodedFormat);
				byte[] data = new byte[4096];
				// Start
				line.start();

				int nBytesRead;
				while ((nBytesRead = din.read(data, 0, data.length)) != -1) {
					line.write(data, 0, nBytesRead);
				}
				// Stop
				line.drain();
				line.stop();
				line.close();
				din.close();
			}
		} catch (Exception e) {
			Log.debug("playing MP3 failed " + url + " " + e.toString());
		}
	}

	/**
	 * Plays a sequence of notes generated by the string noteSring using the
	 * midi sequencer.
	 * 
	 * @param noteString
	 * @param instrument
	 */
	public void playSequenceFromString(String noteString, int instrument) {
		try {
			stopCurrentSound();
			currentSoundType = SOUNDTYPE_MIDI;
			getMidiSound().playSequenceFromJFugueString(noteString, instrument);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays a tone generated by the time-valued input function f(t) for t = min
	 * to t = max seconds. Also allows adjustment of the sampling rate and bit
	 * depth.
	 * 
	 * @param f
	 * @param min
	 * @param max
	 * @param sampleRate
	 * @param bitDepth
	 */
	public void playFunction(final GeoFunction f, final double min,
			final double max, final int sampleRate, final int bitDepth) {
		try {
			stopCurrentSound();
			currentSoundType = SOUNDTYPE_FUNCTION;
			getFunctionSound().playFunction(f, min, max, sampleRate, bitDepth);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays a tone generated by the time-valued input function f(t) for t = min
	 * to t = max seconds.
	 * 
	 * @param f
	 * @param min
	 * @param max
	 */
	public void playFunction(final GeoFunction f, final double min,
			final double max) {
		try {
			stopCurrentSound();
			currentSoundType = SOUNDTYPE_FUNCTION;
			getFunctionSound().playFunction(f, min, max);
		} catch (Exception e) {
			Log.error("Problem in playFunction(): " + e.getMessage());
		}
	}

	// ====================================
	// Control methods
	// ====================================

	/**
	 * Stops all sound creation and closes all sound-related resources.
	 */
	public void stopCurrentSound() {
		if (midiSound != null)
			midiSound.stop();
		if (functionSound != null)
			functionSound.pause(true);
	}

	/**
	 * Pauses/resumes current sound.
	 * 
	 * @param doResume
	 *            : true = resume play, false = pause
	 */
	public void pauseResumeSound(boolean doResume) {

		if (currentSoundType == SOUNDTYPE_MIDI && midiSound != null) {
			midiSound.pause(!doResume);
		}

		if (currentSoundType == SOUNDTYPE_FUNCTION && functionSound != null)
			functionSound.pause(!doResume);

		isPaused = !doResume;
	}

}
