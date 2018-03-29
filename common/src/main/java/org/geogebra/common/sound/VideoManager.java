package org.geogebra.common.sound;

import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.util.AsyncOperation;

/**
 * Interface to manage video objects.
 * 
 * @author laszlo.
 *
 */
public interface VideoManager {

	/**
	 * Loads video resource represented by GeoVideo object for further operations.
	 * 
	 * @param geo
	 *            to load.
	 */
	void loadGeoVideo(GeoVideo geo);

	/**
	 * Gets the length of the Video given by its URL.
	 * 
	 * @param url
	 *            URL of the Video resource.
	 * @return the duration of the Video resource.
	 */
	int getDuration(String url);

	/**
	 * Gets the current time elapsed of the Video given by its URL.
	 * 
	 * @param url
	 *            URL of the Video resource.
	 * @return the duration of the Video resource.
	 */
	int getCurrentTime(String url);

	/**
	 * 
	 * @param url
	 *            to check if it is a valid Video file.
	 * @param callback
	 *            to process the result.
	 */
	void checkURL(String url, AsyncOperation<Boolean> callback);

	/**
	 * Plays/resumes GeoVideo object.
	 * 
	 * @param geo
	 *            to play.
	 */
	void play(GeoVideo geo);

	/**
	 * Pauses GeoVideo object.
	 * 
	 * @param geo
	 *            to pause.
	 */
	void pause(GeoVideo geo);

	/**
	 * 
	 * @param geo
	 *            Video object to check.
	 * @return if GeoVideo object is playing now.
	 */
	boolean isPlaying(GeoVideo geo);

	/**
	 * Gets a preview image
	 * 
	 * @param geo
	 *            the video object.
	 */
	void getPreview(GeoVideo geo);

	/**
	 * Gets the ID of the YouTube video
	 * ie from https://www.youtube.com/watch?v=E4uvbaTR7mw
	 * gets 'E4uvbaTR7mw'.
	 * 
	 * @param url
	 *            the URL of the video.
	 * @return the short ID of video,
	 *         or null if URL is not from YouTube.
	 */
	String getYouTubeId(String url);

}

