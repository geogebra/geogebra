package org.geogebra.web.html5.sound;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoAudio;
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
	private final Map<String, HTMLAudioElement> audioElements;
	private final Map<String, GeoAudio> geoAudios;
	private AsyncOperation<Boolean> urlCallback = null;

	/**
	 * @param app
	 *            App
	 */
	public SoundManagerW(AppW app) {
		this.app = app;
		audioElements = new HashMap<>();
		geoAudios = new HashMap<>();
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
	public void playFile(String url0) {
		this.mp3active = true;
		String url = url0;
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
		final HTMLAudioElement audio = audioElements.get(url);
		if (audio != null) {
			app.invokeLater(audio::play);
		} else {
			playMP3(url);
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
	 * @param url
	 *            The url of the audio element.
	 */
	protected void onCanPlay(HTMLAudioElement audio, String url) {
		audioElements.put(url, audio);
		if (mp3active) {
			audio.play();
		}
	}

	/**
	 * 'canplay', 'timeupdate' and 'ended' handler for audio element from GeoAudio.
	 * 
	 * @param audio
	 *            Element that is ready to play.
	 * @param url
	 *            The url of the audio element.
	 */
	protected void onGeoAudioUpdate(HTMLAudioElement audio, String url) {
		audioElements.put(url, audio);
		view.update(geoAudios.get(url));
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
	 * @param url
	 *            eg
	 *            http://www.geogebra.org/static/spelling/spanish/00/00002.mp3
	 */
	void playMP3(String url) {
		HTMLAudioElement audio = createHtmlAudioElement();
		audio.src = url;
		audio.oncanplay = p0 -> {
			onCanPlay(audio, url);
			return null;
		};

		audio.load();
	}

	private void loadGeoAudio(String url) {
		HTMLAudioElement audio = createHtmlAudioElement();
		if (audio == null) {
			return;
		}

		audio.src = url;
		audio.oncanplay = p0 -> {
			onGeoAudioUpdate(audio, url);
			return null;
		};

		audio.ontimeupdate = p0 -> {
			onGeoAudioUpdate(audio, url);
			return null;
		};

		audio.onplay = p0 -> {
			onGeoAudioUpdate(audio, url);
			return null;
		};

		audio.onended = p0 -> {
			onGeoAudioUpdate(audio, url);
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
		geoAudios.put(geo.getSrc(), geo);
		loadGeoAudio(geo.getSrc());
	}

	@Override
	public int getDuration(String url) {
		final HTMLAudioElement audio = audioElements.get(url);
		if (audio != null) {
			return getDuration(audio);
		}
		return -1;
	}

	@Override
	public int getCurrentTime(String url) {
		final HTMLAudioElement audio = audioElements.get(url);
		if (audio != null) {
			return getCurrentTime(audio);
		}
		return -1;
	}

	@Override
	public void setCurrentTime(String url, int time) {
		final HTMLAudioElement audio = audioElements.get(url);
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
		return audioElements.get(geo.getSrc());
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
