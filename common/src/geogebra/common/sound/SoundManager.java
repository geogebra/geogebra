package geogebra.common.sound;

import geogebra.common.kernel.geos.GeoFunction;

public interface SoundManager {

	void pauseResumeSound(boolean b);

	void playSequenceNote(int double1, double double2, int i, int j);

	void playSequenceFromString(String string, int double1);

	void playFunction(GeoFunction geoFunction, double double1, double double2);

	/**
	 * @param string file to play. Desktop currently just supports .mid, Wed supports .mp3
	 */
	void playFile(String string);

	void playFunction(GeoFunction geoFunction, double double1, double double2,
			int double3, int double4);

}
