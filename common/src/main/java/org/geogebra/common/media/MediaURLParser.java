package org.geogebra.common.media;

import java.util.HashMap;
import java.util.Map;

import org.gwtproject.regexp.shared.MatchResult;
import org.gwtproject.regexp.shared.RegExp;

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
	private static final String MEBIS_PARAM_DOC = "doc";
	private static final String MEBIS_DOC_RECORD = "record";
	private static final String MEBIS_DOC_PROVIDE_VIDEO = "provideVideo";
	private static final String MEBIS_DOC_EMBEDDED_OBJECT = "embeddedObject";

	private static final String MEBIS_PARAM_ID = "id";
	private static final String MEBIS_PARAM_IDENTIFIER = "identifier";

	private static final String MEBIS_PARAM_TYPE = "type";
	private static final String MEBIS_TYPE_VIDEO = "video";

	private static final String MEBIS_PARAM_TIME = "#t";

	/** Mebis site base URL */
	public static final String MEBIS_BASE_URL = "https://mediathek.mebis.bayern.de/";

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
	 * @return parsed URL type
	 */
	public static VideoURL checkVideo(String url) {
		boolean youtube = getYouTubeId(url) != null && !"".equals(getYouTubeId(url));
		boolean mp4 = getMP4Url(url) != null && !"".equals(getMP4Url(url));
		MediaFormat fmt = MediaFormat.NONE;
		if (youtube) {
			fmt = MediaFormat.VIDEO_YOUTUBE;
		} else if (mp4) {
			fmt = MediaFormat.VIDEO_HTML5;
		}
		MebisURL mUrl = packUrl(url);
		if (mUrl.getError() != MebisError.BASE_MISMATCH) {
			return mUrl;
		}

		if (!youtube && !mp4) {
			return VideoURL.createError(url, fmt);
		}
		return VideoURL.createOK(url, fmt);
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

	/**
	 * @param rawUrl input URL
	 * @return embeddable URL
	 */
	public static String toEmbeddableUrl(String rawUrl) {
		String url = rawUrl.replace("+", "%2B");
		RegExp regex = RegExp.compile("^https://(bavarikon.de|www.bavarikon.de)/object/");
		MatchResult result = regex.exec(url);
		if (result != null) {
			String separator = url.contains("?") ? "&" : "?";
			return url + separator + "mebisembedding=true";
		}
		return url;
	}

	/**
	 * Gets the ID of the Mebis video
	 * 
	 * @param url
	 *            the URL of the video.
	 * @return the short ID of video, or null
	 */
	public static String getMebisId(String url) {
		String substring = getQuery(url);
		Map<String, String> params = extractParams(substring);
		String doc = params.get(MEBIS_PARAM_DOC);
		String ret = getMebisIdFromParams(params, doc);
		return ret;
	}

	private static String getMebisIdFromParams(Map<String, String> params, String doc) {
		if (MEBIS_DOC_EMBEDDED_OBJECT.equals(doc)) {
			if (params.containsKey(MEBIS_PARAM_ID)) {
				return params.get(MEBIS_PARAM_ID);
			}
		} else if (MEBIS_DOC_PROVIDE_VIDEO.equals(doc) || MEBIS_DOC_RECORD.equals(doc)) {
			if (params.containsKey(MEBIS_PARAM_IDENTIFIER)) {
				return params.get(MEBIS_PARAM_IDENTIFIER);
			}
		}
		return null;
	}

	/**
	 * Transforms possible Mebis URL to a packed, standardized one. Result
	 * contains an error code if original url is not a Mebis URL. format is:
	 * https://mediathek.mebis.bayern.de/?doc=provideVideo&identifier=BY-00072140&type=video&#t=60,120
	 * 
	 * @param url
	 *            to transform.
	 * @return the packed URL with error if any.
	 */
	public static MebisURL packUrl(String url) {
		if (url == null || !url.contains(MEBIS_BASE_URL)) {
			return new MebisURL(null, MebisError.BASE_MISMATCH);
		}
		String substring = getQuery(url);
		Map<String, String> params = extractParams(substring);
		String id = null;
		String doc = params.get(MEBIS_PARAM_DOC);
		boolean docValid = MEBIS_DOC_EMBEDDED_OBJECT.equals(doc)
				|| MEBIS_DOC_PROVIDE_VIDEO.equals(doc) || MEBIS_DOC_RECORD.equals(doc);
		if (!docValid) {
			return new MebisURL(null, MebisError.DOC);
		}

		boolean typeRequired = !MEBIS_DOC_RECORD.equals(doc);

		if (typeRequired && (!params.containsKey(MEBIS_PARAM_TYPE)
				|| !MEBIS_TYPE_VIDEO.equals(params.get(MEBIS_PARAM_TYPE)))) {
			return new MebisURL(null, MebisError.TYPE);
		}

		id = getMebisIdFromParams(params, doc);

		if (id == null) {
			return new MebisURL(null, MebisError.ID);
		}

		StringBuilder sb = new StringBuilder(MEBIS_BASE_URL);
		sb.append("?");
		sb.append(MEBIS_PARAM_DOC);
		sb.append("=");
		sb.append(MEBIS_DOC_PROVIDE_VIDEO);
		sb.append("&");
		sb.append(MEBIS_PARAM_IDENTIFIER);
		sb.append("=");
		sb.append(id);
		sb.append("&");
		sb.append(MEBIS_PARAM_TYPE);
		sb.append("=");
		sb.append(MEBIS_TYPE_VIDEO);

		int timeIndex = url.indexOf(MEBIS_PARAM_TIME);
		if (timeIndex > -1) {
			String time = url.substring(timeIndex);
			int endIdx = time.indexOf("&");
			if (endIdx > -1) {
				time = time.substring(0, endIdx);
			}
			if (time.matches("#t=[0-9]+(,[0-9]+)?")) {
				sb.append(time);
			}
		}

		return new MebisURL(sb.toString(), MebisError.NONE);
	}

	private static String getQuery(String url) {
		if (!url.contains("?")) {
			return "";
		}
		String stem = url.substring(url.indexOf("?") + 1);
		if (stem.contains("#")) {
			stem = stem.substring(0, stem.indexOf("#"));
		}
		return stem;
	}

	private static Map<String, String> extractParams(String query) {
		Map<String, String> params = new HashMap<>();
		for (String item : query.split("&")) {
			if (item.contains("=")) {
				params.put(item.substring(0, item.indexOf("=")),
						item.substring(item.indexOf("=") + 1));
			}
		}
		return params;
	}
}
