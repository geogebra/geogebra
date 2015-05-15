package org.geogebra.web.html5.sound;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;

/**
 * @author micro_000
 *
 */
public class SoundManagerW implements SoundManager {

	private AppW app;

	/**
	 * @param app
	 *            App
	 */
	public SoundManagerW(AppW app) {
		this.app = app;
	}

	public void pauseResumeSound(boolean b) {
		App.debug("unimplemented 1");

	}

	public void playSequenceNote(int note, double duration, int instrument,
			int velocity) {

		stopCurrentSound();
		getMidiSound().playSequenceNote(instrument, note, velocity, duration);
	}

	public void playSequenceFromString(String string, int double1) {
		App.debug("playSequenceFromString");

	}

	public void playFunction(GeoFunction geoFunction, double double1,
	        double double2) {
		App.debug("unimplemented 2");
	}

	public void playFile(String url) {

		// eg PlaySound["#12345"] to play material 12345 from GeoGebraTube
		if (url.startsWith("#")) {
			String id = url.substring(1);

			if (app.has(Feature.TUBE_BETA)) {
				url = GeoGebraConstants.GEOGEBRATUBE_WEBSITE_BETA;
			} else {
				url = GeoGebraConstants.GEOGEBRATUBE_WEBSITE;
			}

			// something like
			// http://tube-beta.geogebra.org/material/download/format/file/id/1123077

			// TODO: check format of URL is correct
			url = url + "material/download/format/file/id/" + id;

			// dummy file for testing
			// TODO: remove
			url = "http://tube-beta.geogebra.org/files/material-1125945.mp3";
		}

		// TODO check extension, play MIDI .mid files

		if (!url.endsWith(".mp3")) {
			Log.warn("assuming MP3 file: " + url);
		}
		playMP3(url);

	}

	MidiSoundW getMidiSound() {
		return MidiSoundW.INSTANCE;
	}
	
	FunctionSoundW getFunctionSound() {
		return FunctionSoundW.INSTANCE;
	}

	public void stopCurrentSound() {
		getMidiSound().stop();
	}

	public void playFunction(GeoFunction geoFunction, double min, double max,
			int sampleRate, int bitDepth) {
		FunctionSoundW.INSTANCE.playFunction(geoFunction, min, max);
	}

	/**
	 * @param url
	 *            eg
	 *            http://www.geogebra.org/static/spelling/spanish/00/00002.mp3
	 */
	native void playMP3(String url) /*-{
		var audioElement = $doc.createElement('audio');
		audioElement.setAttribute('src', url);
		audioElement.load();
		audioElement.addEventListener("canplay", function() {
			audioElement.play();
		});

	}-*/;
	

}
