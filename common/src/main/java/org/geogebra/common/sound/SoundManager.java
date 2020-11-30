package org.geogebra.common.sound;

import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.util.AsyncOperation;

public interface SoundManager {

	void pauseResumeSound(boolean b);

	void playSequenceNote(int double1, double double2, int i, int j);

	void playSequenceFromString(String string, int double1);

	void playFunction(GeoFunction geoFunction, double double1, double double2);

	/**
	 * @param geoAudible
	 * 			  audio geo element
	 * @param url
	 *            file to play. Desktop currently just supports .mid, Wed
	 *            supports .mp3
	 */
	void playFile(GeoElement geoAudible, String url);

	void playFunction(GeoFunction geoFunction, double double1, double double2,
			int double3, int double4);

	/**
	 * Loads audio resource represented by GeoAudio object for further operations.
	 * 
	 * @param geo
	 *            to load.
	 */
	void loadGeoAudio(GeoAudio geo);

	/**
	 * Gets the length of the audio given by its URL.
	 * 
	 * @param geoAudio
	 *           Audio geo element.
	 * @return the duration of the audio resource.
	 */
	int getDuration(GeoAudio geoAudio);

	/**
	 * Gets the current time elapsed of the audio given by its URL.
	 * 
	 * @param geoAudio
	 *            Audio geo element.
	 * @return the duration of the audio resource.
	 */
	int getCurrentTime(GeoAudio geoAudio);

	/**
	 * Sets the current time position of the audio given by its URL.
	 * 
	 * @param geoAudio
	 *            Audio geo element.
	 * @param pos
	 *            to set.
	 */
	void setCurrentTime(GeoAudio geoAudio, int pos);

	/**
	 * 
	 * @param url
	 *            to check if it is a valid audio file.
	 * @param callback
	 *            to process the result.
	 */
	void checkURL(String url, AsyncOperation<Boolean> callback);

	/**
	 * Plays/resumes GeoAudio object.
	 * 
	 * @param geo
	 *            to play.
	 */
	void play(GeoAudio geo);

	/**
	 * Pauses GeoAudio object.
	 * 
	 * @param geo
	 *            to pause.
	 */
	void pause(GeoAudio geo);

	/**
	 * 
	 * @param geo
	 *            audio object to check.
	 * @return if GeoAudio object is playing now.
	 */
	boolean isPlaying(GeoAudio geo);
}
