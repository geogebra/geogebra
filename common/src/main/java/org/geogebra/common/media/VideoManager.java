package org.geogebra.common.media;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.kernel.Construction;
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
	void loadGeoVideo(DrawVideo geo);

	/**
	 * Adds a video player GUI.
	 * 
	 * @param video
	 *            to add GUI for.
	 */
	void addPlayer(final DrawVideo video);

	/**
	 * Removes the player specified by GeoVideo from GUI.
	 * 
	 * @param video
	 *            to add GUI for.
	 */
	void removePlayer(final DrawVideo video);

	/**
	 * Checks if there is a player already for video.
	 * 
	 * @param video
	 *            to check.
	 * @return if has player for the video specified.
	 */
	boolean hasPlayer(DrawVideo video);

	/**
	 * Updates the player due to video.
	 * 
	 * @param video
	 *            video
	 */
	void updatePlayer(DrawVideo video);

	/**
	 * Plays/resumes GeoVideo object.
	 *
	 * @param geo
	 *            to play.
	 */
	void play(DrawVideo geo);

	/**
	 * Puts all GeoVideo objects on the background.
	 */
	void backgroundAll();

	/**
	 * Gets a preview image
	 * 
	 * @param geo
	 *            video
	 * @param cb
	 *            the preview callback.
	 */
	void createPreview(GeoVideo geo, AsyncOperation<MyImage> cb);

	/**
	 * Activates preview mode if only preview images are needed (i.e. for image
	 * export)
	 * 
	 * @param preview
	 *            true to activate preview mode, false to deactivate
	 */
	void setPreviewOnly(boolean preview);

	/**
	 * @return true if preview mode is active
	 */
	boolean isPreviewOnly();

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

	/**
	 * Gets the ID of the Mebis video
	 * 
	 * @param url
	 *            the URL of the video.
	 * @return the short ID of video, or null
	 */
	String getMebisId(String url);

	/**
	 * Removes all players from application.
	 */
	void removePlayers();

	/**
	 * Creates the proper video object based on the url.
	 * 
	 * @param c
	 *            the construction
	 * @param videoURL
	 *            the validated URL of the video.
	 *            (see {@link VideoURL})
	 * @return the proper instance of {@link GeoVideo}
	 * 
	 */
	GeoVideo createVideo(Construction c, VideoURL videoURL);
	
	/**
	 * Cache video players temporarily
	 */
	void storeVideos();

	/**
	 * Remove unused videos from cache.
	 */
	void clearStoredVideos();

	/**
	 * Called when an error occurred playing the video
	 * but is it online
	 * @param video the video.
	 */
	void onError(DrawVideo video);

	/**
	 *
	 * @param video to check
	 * @return if the player of the video is offline
	 */
	boolean isPlayerOffline(DrawVideo video);
}
