package org.geogebra.common.sound;

import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.util.AsyncOperation;

public interface SoundManager {

	void pauseResumeSound(boolean b);

	void playSequenceNote(int double1, double double2, int i, int j);

	void playSequenceFromString(String string, int double1);

	void playFunction(GeoFunction geoFunction, double double1, double double2);

	/**
	 * @param string
	 *            file to play. Desktop currently just supports .mid, Wed
	 *            supports .mp3
	 */
	void playFile(String string);

	void playFunction(GeoFunction geoFunction, double double1, double double2,
			int double3, int double4);

	void loadGeoAudio(GeoAudio geo);

	int getDuration(String url);

	int getCurrentTime(String url);

	/**
	 * 
	 * @param url
	 *            to check if it is a valid audio file.
	 * @param hadler
	 *            the error handler.
	 */
	void checkURL(String url, AsyncOperation<Boolean> callback);

}
