package org.geogebra.desktop.sound;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.App;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.sound.mp3transform.Decoder;

/**
 * Class to handle GeoGebra sound features. Calls to midi and streaming audio
 * methods are managed from here.
 * 
 * @author G. Sturr
 * 
 */
public class SoundManagerD implements SoundManager {

	private App app;
	private MidiSoundD midiSound;
	private FunctionSoundD functionSound;

	private static final int SOUNDTYPE_NONE = -1;
	private static final int SOUNDTYPE_MIDI = 0;
	private static final int SOUNDTYPE_FUNCTION = 1;
	int currentSoundType = SOUNDTYPE_NONE;

	private boolean isRunning = false;
	private boolean isPaused = false;
	private List<PauseControl> controls = new ArrayList<>();

	/**
	 * Constructor
	 * 
	 * @param app application
	 */
	public SoundManagerD(App app) {
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
		if (midiSound == null) {
			try {
				midiSound = new MidiSoundD(app);
			} catch (Exception e) {
				Log.debug(e);
			}
		}
		return midiSound;
	}

	/**
	 * Retrieves field functionSound. Creates a new instance of FunctionSound if
	 * none exists.
	 */
	private FunctionSoundD getFunctionSound() {
		if (functionSound == null) {
			try {
				functionSound = new FunctionSoundD();
			} catch (Exception e) {
				Log.error("Problem in getFunctionSound(): " + e.getMessage());
			}
		}
		return functionSound;
	}

	// ====================================
	// Sound playing methods
	// ====================================

	@Override
	public void playSequenceNote(final int note, final double duration,
			final int instrument, final int velocity) {
		try {
			stopCurrentSound();
			currentSoundType = SOUNDTYPE_MIDI;
			getMidiSound().playSequenceNote(note, duration, instrument);
		} catch (Exception e) {
			Log.debug(e);
		}
	}

	/**
	 * Plays an audio file with the .mid / .mp3
	 * 
	 * @param fileName filename
	 */
	@Override
	public void playFile(GeoElement geoElement, final String fileName) {

		SwingUtilities.invokeLater(() -> {
			try {
				if (fileName.startsWith("data:") || fileName.startsWith("#")
						|| !(fileName.endsWith(".midi")
						&& fileName.endsWith(".mid"))) {

					PauseControl control = new PauseControl();
					controls.add(control);
					Thread thread = new Thread(playMp3(fileName, control));
					thread.start();

					return;

				}

				// not .mp3, must be .mid
				stopCurrentSound();
				currentSoundType = SOUNDTYPE_MIDI;
				getMidiSound().playMidiFile(fileName);

			} catch (Exception e) {
				Log.debug(e);
			}

		});

	}

	private Runnable playMp3(String fileName, PauseControl control) {
		return () -> {
			try (InputStream is = openStream(fileName)) {
				new Decoder().play(fileName, is, control);
			} catch (Exception e) {
				Log.debug(e);
			}
			controls.remove(control);
		};
	}

	private InputStream openStream(String fileName) {
		try {
			if (fileName.startsWith(StringUtil.mp3Marker)) {
				String mp3base64 = fileName
						.substring(StringUtil.mp3Marker.length());
				byte[] mp3 = Base64.decode(mp3base64);
				return new ByteArrayInputStream(mp3);
			} else if (fileName.startsWith("#")) {
				// eg PlaySound["#12345"] to play from GeoGebraTube
				String id = fileName.substring(1);
				String url = app.getURLforID(id);

				// #5094
				return new URL(url).openStream();
			} else if (fileName.startsWith("http:")
					|| fileName.startsWith("https:")) {

				return new URL(fileName).openStream();
			} else {
				// assume local file
				// eg c:\
				// eg file://
				return new FileInputStream(fileName);
			}
		} catch (Exception ex) {
			Log.debug(ex);
		}
		return InputStream.nullInputStream();
	}

	@Override
	public void playSequenceFromString(String noteString, int instrument) {
		try {
			stopCurrentSound();
			currentSoundType = SOUNDTYPE_MIDI;
			getMidiSound().playSequenceFromJFugueString(noteString, instrument);
		} catch (Exception e) {
			Log.debug(e);
		}
	}

	@Override
	public void playFunction(final GeoFunction geoFunction, final double min,
			final double max, final int sampleRate, final int bitDepth) {
		try {
			stopCurrentSound();
			currentSoundType = SOUNDTYPE_FUNCTION;
			getFunctionSound().playFunction(geoFunction, min, max, sampleRate, bitDepth);
		} catch (Exception e) {
			Log.debug(e);
		}
	}

	@Override
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
		if (midiSound != null) {
			midiSound.stop();
		}
		if (functionSound != null) {
			functionSound.pause(true);
		}
	}

	/**
	 * Pauses/resumes current sound.
	 * 
	 * @param resume
	 *            : true = resume play, false = pause
	 */
	@Override
	public void pauseResumeSound(boolean resume) {

		if (currentSoundType == SOUNDTYPE_MIDI && midiSound != null) {
			midiSound.pause(!resume);
		}

		if (currentSoundType == SOUNDTYPE_FUNCTION && functionSound != null) {
			functionSound.pause(!resume);
		}
		for (PauseControl pauseControl: controls) {
			pauseControl.pause = !resume;
		}

		isPaused = !resume;
	}

	@Override
	public void loadGeoAudio(GeoAudio geo) {
		// not implemented here.
	}

	@Override
	public int getDuration(GeoAudio geoAudio) {
		// not implemented here.
		return 0;
	}

	@Override
	public int getCurrentTime(GeoAudio geoAudio) {
		// not implemented here.
		return 0;
	}

	@Override
	public void checkURL(String url, AsyncOperation<Boolean> callback) {
		// not implemented here.
	}

	@Override
	public void play(GeoAudio geo) {
		playFile(geo, geo.getSrc());
	}

	@Override
	public void pause(GeoAudio geo) {
		// not implemented here.
	}

	@Override
	public boolean isPlaying(GeoAudio geo) {
		// not implemented here.
		return false;
	}

	@Override
	public void setCurrentTime(GeoAudio geoAudio, int pos) {
		// not implemented here.
	}
}
