package org.geogebra.common.kernel.geos;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.media.MebisError;
import org.geogebra.common.media.MebisURL;
import org.geogebra.common.media.MediaFormat;
import org.geogebra.common.plugin.GeoClass;

/**
 * GeoElement to handle videos from Mebis.
 * 
 * @author laszlo
 *
 */
public class GeoMebisVideo extends GeoMP4Video {
	private static final String PARAM_DOC = "doc";
	private static final String DOC_RECORD = "record";
	private static final String DOC_PROVIDE_VIDEO = "provideVideo";
	private static final String DOC_EMBEDDED_OBJECT = "embeddedObject";

	private static final String PARAM_ID = "id";
	private static final String PARAM_IDENTIFIER = "identifier";

	private static final String PARAM_TYPE = "type";
	private static final String TYPE_VIDEO = "video";

	private static final String PARAM_TIME = "#t";

	/** Mebis site base URL */
	public static final String BASE_URL = "https://mediathek.mebis.bayern.de/?";
	// private String mebisId = null;

	/**
	 * 
	 * @param c
	 *            construction
	 */
	public GeoMebisVideo(Construction c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor.
	 *
	 * @param c
	 *            the construction.
	 * @param url
	 *            the URL of the video.
	 */
	public GeoMebisVideo(Construction c, String url) {
		super(c, url);
	}

	@Override
	protected void constructIds() {
		// TODO: implement if needed
	}

	@Override
	protected void createPreview() {
		// TODO: implement this
	}

	@Override
	public MediaFormat getFormat() {
		return MediaFormat.VIDEO_MEBIS;
	}

	/**
	 * Transforms possible Mebis URL to a packed, standardized one.
	 * Result contains an error code if original url is not a
	 * Mebis URL.
	 * format is:
	 * https://mediathek.mebis.bayern.de/?doc=provideVideo&identifier=BY-00072140&type=video&#t=60,120
	 * 
	 * @param url
	 *            to transform.
	 * @return the packed URL with error if any.
	 */
	public static MebisURL packUrl(String url) {
		if (url == null || !url.contains(BASE_URL)) {
			return new MebisURL(null, MebisError.BASE_MISMATCH);
		}
		String substring = getQuery(url);
		Map<String, String> params = extractParams(substring);
		String id = null;
		String doc = params.get(PARAM_DOC);
		boolean docValid = DOC_EMBEDDED_OBJECT.equals(doc) || DOC_PROVIDE_VIDEO.equals(doc)
				|| DOC_RECORD.equals(doc);
		if (!docValid) {
			return new MebisURL(null, MebisError.DOC);
		}

		boolean typeRequired = !DOC_RECORD.equals(doc);
		
		if (typeRequired && (!params.containsKey(PARAM_TYPE)
				|| !TYPE_VIDEO.equals(params.get(PARAM_TYPE)))) {
			return new MebisURL(null, MebisError.TYPE);
		}

		if (DOC_EMBEDDED_OBJECT.equals(doc)) {
			if (params.containsKey(PARAM_ID)) {
				id = params.get(PARAM_ID);
			}
		} else if (DOC_PROVIDE_VIDEO.equals(doc) || DOC_RECORD.equals(doc)) {
			if (params.containsKey(PARAM_IDENTIFIER)) {
				id = params.get(PARAM_IDENTIFIER);
			}
		}
		if (id == null) {
			return new MebisURL(null, MebisError.ID);
		}

		StringBuilder sb = new StringBuilder(BASE_URL);
		sb.append(PARAM_DOC);
		sb.append("=");
		sb.append(DOC_PROVIDE_VIDEO);
		sb.append("&");
		sb.append(PARAM_IDENTIFIER);
		sb.append("=");
		sb.append(id);
		sb.append("&");
		sb.append(PARAM_TYPE);
		sb.append("=");
		sb.append(TYPE_VIDEO);

		int timeIndex = url.indexOf(PARAM_TIME);
		if (timeIndex > -1) {
			String time = url.substring(timeIndex);
			if (time.matches("#t=[0-9]+(,[0-9]+)?")) {
				sb.append(time);
			}
		}

		return new MebisURL(sb.toString(), MebisError.NONE);
	}

	private static String getQuery(String url) {
		String stem = url.replace(BASE_URL, "");
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

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.MEBIS_VIDEO;
	}
}
