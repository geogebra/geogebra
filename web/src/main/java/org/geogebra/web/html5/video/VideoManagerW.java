package org.geogebra.web.html5.video;

import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.sound.VideoManager;
import org.geogebra.common.util.AsyncOperation;

/**
 * Class for managing audio content.
 * 
 * @author laszlo
 *
 */
public class VideoManagerW implements VideoManager {

	@Override
	public void loadGeoVideo(GeoVideo geo) {
		// TODO implement this
	}

	@Override
	public int getDuration(String url) {
		// TODO implement this
		return 0;
	}

	@Override
	public int getCurrentTime(String url) {
		// TODO implement this
		return 0;
	}

	@Override
	public void checkURL(String url, AsyncOperation<Boolean> callback) {
		// TODO implement this

	}

	@Override
	public void play(GeoVideo geo) {
		// TODO implement this
	}

	@Override
	public void pause(GeoVideo geo) {
		// TODO implement this
	}

	@Override
	public boolean isPlaying(GeoVideo geo) {
		// TODO implement this
		return false;
	}

}
