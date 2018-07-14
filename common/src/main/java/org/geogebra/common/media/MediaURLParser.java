package org.geogebra.common.media;

import org.geogebra.common.kernel.geos.GeoMebisVideo;
import org.geogebra.common.util.AsyncOperation;

/**
 * Helper class for parsing video URLs.
 */
public class MediaURLParser {
	/**
	 * Head of a regular YouTube URL.
	 */
	public static final String YOUTUBE = "youtube.com/";
	/**
	 * Head of a short form of YouTube URL.
	 */
	public static final String YOUTUBE_SHORT = "youtu.be/";

	/**
	 * regular start of YouTube ID
	 */
	public static final String ID_PARAM_1 = "v=";
	/**
	 * alternative start of YouTube ID
	 */
	public static final String ID_PARAM_2 = "v/";
	/**
	 * embed start of YouTube ID
	 */
	public static final String EMBED = "embed/";

	/**
	 * Gets the ID of the YouTube video ie from
	 * https://www.youtube.com/watch?v=E4uvbaTR7mw gets 'E4uvbaTR7mw'.
	 * 
	 * @param url
	 *            the URL of the video.
	 * @return the short ID of video, or null if URL is not from YouTube.
	 */
	public static String getYouTubeId(String url) {
		String id = null;
		int startIdx;
		String subString = null;

		if (url.contains(YOUTUBE)) {
			if (url.contains(ID_PARAM_1) || url.contains(ID_PARAM_2)) {
				startIdx = url.indexOf(ID_PARAM_1) != -1 ? url.indexOf(ID_PARAM_1)
						: url.indexOf(ID_PARAM_2);
				subString = url.substring(startIdx + ID_PARAM_1.length());
			} else if (url.contains(EMBED)) {
				startIdx = url.indexOf(EMBED);
				subString = url.substring(startIdx + EMBED.length());
			}
		} else if (url.contains(YOUTUBE_SHORT)) {
			startIdx = url.indexOf(YOUTUBE_SHORT);
			subString = url.substring(startIdx + YOUTUBE_SHORT.length());
		}

		if (subString != null) {
			int endIdx = subString.indexOf("?") != -1 ? subString.indexOf("?")
					: (subString.indexOf("&") != -1 ? subString.indexOf("&")
							: (subString.indexOf("\"") != -1 ? subString.indexOf("\"") : -1));
			if (endIdx != -1) {
				id = subString.substring(0, endIdx);
			} else {
				id = subString;
			}
		}
		return id;
	}

	/**
	 * 
	 * @param url
	 *            to check if it is a valid Video file.
	 * @param callback
	 *            to process the result.
	 */
	public static void checkVideo(String url, AsyncOperation<VideoURL> callback) {
		boolean youtube = getYouTubeId(url) != null && !"".equals(getYouTubeId(url));
		boolean mp4 = getMP4Url(url) != null && !"".equals(getMP4Url(url));
		MediaFormat fmt = MediaFormat.NONE;
		if (youtube) {
			fmt = MediaFormat.VIDEO_YOUTUBE;
		} else if (mp4) {
			fmt = MediaFormat.VIDEO_HTML5;
		}

		if (checkMebisVideo(url, callback)) {
			return;
		}

		if (!youtube && !mp4) {
			callback.callback(VideoURL.createError(url, fmt));
		} else {
			callback.callback(VideoURL.createOK(url, fmt));
		}
	}

	private static boolean checkMebisVideo(String url, AsyncOperation<VideoURL> callback) {
		MebisURL mUrl = GeoMebisVideo.packUrl(url);
		if (mUrl.getError() == MebisError.BASE_MISMATCH) {
			return false;
		}
		callback.callback(mUrl);
		return true;
	}

	private static String getMP4Url(String url) {
		if (url == null) {
			return null;
		}
		if (url.endsWith(".m4v") || url.contains(".mp4")) {
			return url;
		}
		return null;
	}
}
