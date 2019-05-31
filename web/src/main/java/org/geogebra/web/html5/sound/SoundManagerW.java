package org.geogebra.web.html5.sound;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.ExternalAccess;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;

/**
 *
 */
public class SoundManagerW implements SoundManager /* , MidiSoundListenerW */ {
	private AppW app;
	private boolean mp3active = true;
	private Map<String, Element> urlToAudio = new HashMap<>();
	private Map<String, GeoAudio> urlToGeoAudio = new HashMap<>();
	private AsyncOperation<Boolean> urlCallback = null;

	/**
	 * @param app
	 *            App
	 */
	public SoundManagerW(AppW app) {
		this.app = app;
		// getMidiSound().setListener(this);
	}

	@Override
	public void pauseResumeSound(boolean b) {
		FunctionSoundW.INSTANCE.pause(b);
		mp3active = b;
	}

	@Override
	public void playSequenceNote(int note, double duration, int instrument,
			int velocity) {
		stopCurrentSound();
		// getMidiSound().playSequenceNote(instrument, note, velocity,
		// duration);
	}

	@Override
	public void playSequenceFromString(String string, int double1) {
		Log.debug("playSequenceFromString");
	}

	@Override
	public void playFunction(GeoFunction geoFunction, double min, double max) {
		FunctionSoundW.INSTANCE.playFunction(geoFunction, min, max);
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
			Log.debug("MIDI not supported");
			return;
		}

		Log.debug("playing URL as MP3: " + url);
		final Element audio = urlToAudio.get(url);
		if (audio != null) {
			app.invokeLater(new Runnable() {

				@Override
				public void run() {
					playAudioElement(audio);
				}
			});
		} else {
			playMP3(url);
		}
		// TODO check extension, play MIDI .mid files

		// if (url.endsWith(".mid") || url.endsWith(".midi")) {
		// getMidiSound().playMidiFile(url);
		// } else {
		// playMP3(url);
		// }
	}

	// MidiSoundW getMidiSound() {
	// return MidiSoundW.INSTANCE;
	// }

	/**
	 *
	 * @return the FuctionSound instance
	 */
	FunctionSoundW getFunctionSound() {
		return FunctionSoundW.INSTANCE;
	}

	/**
	 * Stops the sound
	 */
	public void stopCurrentSound() {
		// getMidiSound().stop();
	}

	/**
	 * 'canplay' audio element handler called from JSNI
	 * 
	 * @param audio
	 *            Element that is ready to play.
	 * @param url
	 *            The url of the audio element.
	 */
	protected void onCanPlay(Element audio, String url) {
		urlToAudio.put(url, audio);
		if (mp3active) {
			playAudioElement(audio);
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
	protected void onGeoAudioUpdate(Element audio, String url) {
		urlToAudio.put(url, audio);
		GeoAudio geo = urlToGeoAudio.get(url);
		if (geo != null) {
			geo.updateRepaint();
		}
	}

	private native void playAudioElement(Element audio) /*-{
		audio.play();
	}-*/;

	private native void pauseAudioElement(Element audio) /*-{
		audio.pause();
	}-*/;

	private native boolean isAudioElementPlaying(Element audio) /*-{
		return !audio.paused;
	}-*/;

	private native int getDuration(Element audio) /*-{
		return Math.floor(audio.duration * 1000)
	}-*/;

	private native int getCurrentTime(Element audio) /*-{
		return Math.floor(audio.currentTime * 1000);
	}-*/;

	private native void setCurrentTime(Element audio, double time) /*-{
		audio.currentTime = time / 1000;
	}-*/;

	/**
	 * @param url
	 *            eg
	 *            http://www.geogebra.org/static/spelling/spanish/00/00002.mp3
	 */
	native void playMP3(String url) /*-{
		var audioElement = $doc.createElement('audio');
		var that = this;
		audioElement.setAttribute('src', url);
		audioElement.load();
		audioElement
				.addEventListener(
						"canplay",
						function() {
							that.@org.geogebra.web.html5.sound.SoundManagerW::
								onCanPlay(Lcom/google/gwt/dom/client/Element;Ljava/lang/String;)
								(audioElement,url);
						});
	}-*/;

	private native void loadGeoAudio(String url) /*-{
		var audioElement = $doc.createElement('audio');
		var that = this;
		audioElement.setAttribute('src', url);
		audioElement.load();
		audioElement
		.addEventListener(
		"canplay",
		function() {
		that.@org.geogebra.web.html5.sound.SoundManagerW::onGeoAudioUpdate(
			Lcom/google/gwt/dom/client/Element;Ljava/lang/String;)(audioElement, url);
		});
		audioElement
		.addEventListener(
		"timeupdate",
		function() {
		that.@org.geogebra.web.html5.sound.SoundManagerW::onGeoAudioUpdate(
			Lcom/google/gwt/dom/client/Element;Ljava/lang/String;)(audioElement, url);
		});
		
		audioElement
		.addEventListener(
		"play",
		function() {
		that.@org.geogebra.web.html5.sound.SoundManagerW::onGeoAudioUpdate(
			Lcom/google/gwt/dom/client/Element;Ljava/lang/String;)(audioElement, url);
		});
		
		audioElement
		.addEventListener(
		"ended",
		function() {
		that.@org.geogebra.web.html5.sound.SoundManagerW::onGeoAudioUpdate(
			Lcom/google/gwt/dom/client/Element;Ljava/lang/String;)(audioElement, url);
		});
		
		}-*/;

	private native void checkAudio(String url) /*-{
		var audioElement = $doc.createElement('audio');
		var that = this;
		audioElement.setAttribute('src', url);
		audioElement.load();
		audioElement.addEventListener("error", function() {
			that.@org.geogebra.web.html5.sound.SoundManagerW::onUrlError()();
		});
		audioElement.addEventListener("canplay", function() {
			that.@org.geogebra.web.html5.sound.SoundManagerW::onUrlOK()(url);
		});
	}-*/;

	@Override
	public void playFunction(GeoFunction geoFunction, double min, double max,
			int sampleRate, int bitDepth) {
		FunctionSoundW.INSTANCE.playFunction(geoFunction, min, max, sampleRate,
				bitDepth);
	}

	/**
	 * Error handler
	 */
	public void onError(/* int errorCode */) {
		// if (errorCode == MidiSoundW.MIDI_ERROR_PORT) {
		// ToolTipManagerW.sharedInstance().showBottomMessage(
		// "No valid MIDI output port was found.", true, (AppW) app);
		// }
	}

	@ExternalAccess
	private void onUrlError() {
		if (urlCallback != null) {
			urlCallback.callback(Boolean.FALSE);
		}
	}

	@ExternalAccess
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
		urlToGeoAudio.put(geo.getSrc(), geo);
		loadGeoAudio(geo.getSrc());
	}

	@Override
	public int getDuration(String url) {
		final Element audio = urlToAudio.get(url);
		if (audio != null) {
			return getDuration(audio);
		}
		return -1;
	}

	@Override
	public int getCurrentTime(String url) {
		final Element audio = urlToAudio.get(url);
		if (audio != null) {
			return getCurrentTime(audio);
		}
		return -1;
	}

	@Override
	public void setCurrentTime(String url, int time) {
		final Element audio = urlToAudio.get(url);
		if (audio != null) {
			setCurrentTime(audio, time);
		}
	}

	@Override
	public void checkURL(String url, AsyncOperation<Boolean> callback) {
		urlCallback = callback;
		checkAudio(url);
	}

	@Override
	public void play(GeoAudio geo) {
		final Element audio = urlToAudio.get(geo.getSrc());
		if (audio != null) {
			playAudioElement(audio);
		}
	}

	@Override
	public void pause(GeoAudio geo) {
		final Element audio = urlToAudio.get(geo.getSrc());
		if (audio != null) {
			pauseAudioElement(audio);
		}
	}

	@Override
	public boolean isPlaying(GeoAudio geo) {
		final Element audio = urlToAudio.get(geo.getSrc());
		if (audio != null) {
			return isAudioElementPlaying(audio);
		}
		return false;
	}
}
