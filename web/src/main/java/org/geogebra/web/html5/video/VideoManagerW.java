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

	/**
	 * Head of a regular YouTube URL.
	 */
	public static final String YOUTUBE = "https://www.youtube.com/watch?v=";

	/**
	 * Head of a short form of YouTube URL.
	 */
	public static final String YOUTUBE_SHORT = "https://youtu.be/";

	/**
	 * Head of the preview image of YouTube URL.
	 */
	public static final String YOUTUBE_PREV = "https://img.youtube.com/vi/%ID%/0.jpg";

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

	@Override
	public String getYouTubeId(String url) {
		String id = null;
		if (url.startsWith(YOUTUBE)) {
			id = url.replace(YOUTUBE, "");
		} else if (url.startsWith(YOUTUBE_SHORT)) {
			id = url.replace(YOUTUBE_SHORT, "");
		}

		int idx = id.indexOf("&");
		if (idx != -1) {
			return id.substring(0, idx);
		}
		return id;
	}

	@Override
	public void getPreview(GeoVideo geo) {
		// TODO implement this
	}

}
