package org.geogebra.common.kernel.geos;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.media.MebisError;
import org.geogebra.common.media.MediaFormat;
import org.geogebra.common.media.PackedUrl;

/**
 * GeoElement to handle videos from Mebis.
 * 
 * @author laszlo
 *
 */
public class GeoMebisVideo extends GeoMP4Video {
	/** Mebis site base URL */
	public static final String BASE_URL = "https://mediathek.mebis.bayern.de/?";
	private String mebisId = null;

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
	public static PackedUrl packUrl(String url) {
		if (url == null || !url.contains(BASE_URL)) {
			return new PackedUrl(null, MebisError.BASE_MISMATCH);
		}
		String substring = url.replace(BASE_URL, "");
		Map<String, String> params = extractParams(substring);
		String id = null;
		String doc = params.get("doc");
		boolean docValid = "enmbeddedObject".equals(doc) || "provideVideo".equals(doc)
				|| "record".equals(doc);
		if (!docValid) {
			return new PackedUrl(null, MebisError.DOC);
		}

		boolean typeRequired = !"record".equals(doc);
		
		if (typeRequired && (!params.containsKey("type") || !"video".equals(params.get("type")))) {
			return new PackedUrl(null, MebisError.TYPE);
		}

		if ("enmbeddedObject".equals(doc)) {
			if (params.containsKey("id")) {
				id = params.get("id");
			}
		} else if ("provideVideo".equals(doc) || "record".equals(doc)) {
			if (params.containsKey("identifier")) {
				id = params.get("identifier");
			}
		}
		String start = params.containsKey("start") ? params.get("start") : null;
		if (id == null) {
			return new PackedUrl(null, MebisError.ID);
		}

		StringBuilder sb = new StringBuilder(BASE_URL);
		sb.append("doc=provideVideo&identifier=");
		sb.append(id);
		sb.append("&type=video");
		if (start != null) {
			sb.append("&#t=");
			sb.append(start);
		}
		return new PackedUrl(sb.toString(), MebisError.NONE);
	}

	private static Map<String, String> extractParams(String url) {
		if (!url.contains("&")) {
			return null;
		}

		Map<String, String> params = new HashMap<>();
		for (String item : url.split("&")) {
			String[] p = item.split("=");
			params.put(p[0], p[1]);
		}
		return params;
	}

}
