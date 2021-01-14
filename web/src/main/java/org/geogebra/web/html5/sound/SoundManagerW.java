package org.geogebra.web.html5.sound;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLAudioElement;

/**
 *
 */
public class SoundManagerW implements SoundManager {
	private final AppW app;
	private final EuclidianView view;
	private boolean mp3active = true;
	private final Map<GeoAudio, HTMLAudioElement> geoAudioElements;
	private AsyncOperation<Boolean> urlCallback = null;

	/**
	 * @param app
	 *            App
	 */
	public SoundManagerW(AppW app) {
		this.app = app;
		geoAudioElements = new HashMap<>();
		view = app.getActiveEuclidianView();
	}

	@Override
	public void pauseResumeSound(boolean b) {
		FunctionSoundW.getInstance().pause(b);
		mp3active = b;
	}

	@Override
	public void playSequenceNote(int note, double duration, int instrument,
			int velocity) {
		midiNotSupported();
	}

	protected void midiNotSupported() {
		Log.warn("midi not supported on web.");
	}

	@Override
	public void playSequenceFromString(String string, int double1) {
		midiNotSupported();
	}

	@Override
	public void playFunction(GeoFunction geoFunction, double min, double max) {
		FunctionSoundW.getInstance().playFunction(geoFunction, min, max);
	}

	@Override
	public void playFile(GeoElement geoElement, String file) {
		this.mp3active = true;
		String url = file;
		// eg PlaySound["#12345"] to play material 12345 from GeoGebraTube
		boolean fmtMp3 = url.startsWith("#");
		boolean fmtMidi = url.startsWith("@");
		if (fmtMp3 || fmtMidi) {
			String id = url.substring(1);
			url = app.getURLforID(id);
		}

		if (fmtMidi || url.endsWith(".mid") || url.endsWith(".midi")) {
			midiNotSupported();
			return;
		}

		Log.debug("playing URL as MP3: " + url);
		final HTMLAudioElement audio = geoAudioElements.get(geoElement);
		if (audio != null) {
			app.invokeLater(audio::play);
		} else {
			playMP3(geoElement, url);
		}
	}

	/**
	 * Stops the sound
	 */
	public void stopCurrentSound() {
		midiNotSupported();
	}

	/**
	 *
	 * @param audio
	 *            Element that is ready to play.
	 * @param geo
	 *            The geo element.
	 */
	protected void onCanPlay(HTMLAudioElement audio, GeoElement geo) {
		if (geo instanceof GeoAudio) {
			geoAudioElements.put((GeoAudio) geo, audio);
		}
		if (mp3active) {
			audio.play();
		}
	}

	/**
	 * 'canplay', 'timeupdate' and 'ended' handler for audio element from GeoAudio.
	 * 
	 * @param audio
	 *            Element that is ready to play.
	 * @param geoAudio
	 *            Audio geo element.
	 */
	protected void onGeoAudioUpdate(HTMLAudioElement audio, GeoAudio geoAudio) {
		geoAudioElements.put(geoAudio, audio);
		view.update(geoAudio);
		view.repaintView();
	}

	private int getDuration(HTMLAudioElement audio) {
		return (int) Math.floor(audio.duration * 1000);
	}

	private int getCurrentTime(HTMLAudioElement audio) {
		return (int) Math.floor(audio.currentTime * 1000);
	}

	private void setCurrentTime(HTMLAudioElement audio, double time) {
		audio.currentTime = time / 1000;
	}

	/**
	 * @param geo
	 *            the geo element
	 */
	void playMP3(GeoElement geo, String file) {
		HTMLAudioElement audio = createHtmlAudioElement();
		audio.src = file;
		audio.oncanplay = p0 -> {
			onCanPlay(audio, geo);
			return null;
		};

		audio.load();
	}

	private HTMLAudioElement createHtmlAudioElement() {
		return (HTMLAudioElement) DomGlobal.document.createElement("audio");
	}

	private void validateAudioUrl(String url) {
		HTMLAudioElement audio = createHtmlAudioElement();
		if (audio == null) {
			return;
		}

		audio.src = url;
		audio.load();
		audio.onerror = p0 -> {
			onUrlError();
			return null;
		};

		audio.oncanplay = p0 -> {
			onUrlOK();
			return null;
		};
	}

	@Override
	public void playFunction(GeoFunction geoFunction, double min, double max,
			int sampleRate, int bitDepth) {
		FunctionSoundW.getInstance().playFunction(geoFunction, min, max, sampleRate,
				bitDepth);
	}

	private void onUrlError() {
		if (urlCallback != null) {
			urlCallback.callback(Boolean.FALSE);
		}
	}

	private void onUrlOK() {
		if (urlCallback != null) {
			urlCallback.callback(Boolean.TRUE);
		}
	}

	/**
	 * Info handler
	 *
	 * @param msg
	 *            to show
	 */
	public void onInfo(String msg) {
		ToolTipManagerW.sharedInstance().showBottomMessage(msg, true, app);
	}

	@Override
	public void loadGeoAudio(GeoAudio geo) {
		HTMLAudioElement audio = createHtmlAudioElement();
		if (audio == null) {
			return;
		}

		audio.src = geo.getSrc();
		audio.oncanplay = p0 -> {
			onGeoAudioUpdate(audio, geo);
			return null;
		};

		audio.ontimeupdate = p0 -> {
			onGeoAudioUpdate(audio, geo);
			return null;
		};

		audio.onplay = p0 -> {
			onGeoAudioUpdate(audio, geo);
			return null;
		};

		audio.onended = p0 -> {
			onGeoAudioUpdate(audio, geo);
			return null;
		};

		audio.load();
	}

	@Override
	public int getDuration(GeoAudio geoAudio) {
		final HTMLAudioElement audio = geoAudioElements.get(geoAudio);
		if (audio != null) {
			return getDuration(audio);
		}
		return -1;
	}

	@Override
	public int getCurrentTime(GeoAudio geoAudio) {
		final HTMLAudioElement audio = geoAudioElements.get(geoAudio);
		if (audio != null) {
			return getCurrentTime(audio);
		}
		return -1;
	}

	@Override
	public void setCurrentTime(GeoAudio geoAudio, int time) {
		final HTMLAudioElement audio = geoAudioElements.get(geoAudio);
		if (audio != null) {
			setCurrentTime(audio, time);
		}
	}

	@Override
	public void checkURL(String url, AsyncOperation<Boolean> callback) {
		urlCallback = callback;
		validateAudioUrl(url);
	}

	@Override
	public void play(GeoAudio geo) {
		final HTMLAudioElement audio = geoAudioToElement(geo);
		if (audio != null) {
			audio.play();
		}
	}

	protected HTMLAudioElement geoAudioToElement(GeoAudio geo) {
		return geoAudioElements.get(geo);
	}

	@Override
	public void pause(GeoAudio geo) {
		final HTMLAudioElement audio = geoAudioToElement(geo);
		if (audio != null) {
			audio.pause();
		}
	}

	@Override
	public boolean isPlaying(GeoAudio geo) {
		final HTMLAudioElement audio = geoAudioToElement(geo);
		if (audio != null) {
			return !audio.paused;
		}
		return false;
	}
}
